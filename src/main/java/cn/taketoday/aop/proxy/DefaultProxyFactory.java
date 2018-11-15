/**
 * Original Author -> 杨海健 (taketoday@foxmail.com) https://taketoday.cn
 * Copyright © Today & 2017 - 2018 All Rights Reserved.
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.taketoday.aop.proxy;

import cn.taketoday.aop.AdviceType;
import cn.taketoday.aop.Constant;
import cn.taketoday.aop.ProxyCreator;
import cn.taketoday.aop.ProxyFactory;
import cn.taketoday.aop.advice.AbstractAdvice;
import cn.taketoday.aop.advice.AroundMethodAdvice;
import cn.taketoday.aop.advice.AspectsRegistry;
import cn.taketoday.aop.advice.MethodAfterAdvice;
import cn.taketoday.aop.advice.MethodAfterReturningAdvice;
import cn.taketoday.aop.advice.MethodAfterThrowingAdvice;
import cn.taketoday.aop.advice.MethodBeforeAdvice;
import cn.taketoday.aop.annotation.Advice;
import cn.taketoday.aop.annotation.AdviceImpl;
import cn.taketoday.context.exception.ConfigurationException;
import cn.taketoday.context.utils.ClassUtils;
import cn.taketoday.context.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Today <br>
 *         2018-11-10 13:13
 */
@Slf4j
public class DefaultProxyFactory implements ProxyFactory {

	private TargetSource targetSource;
	private final Map<Method, List<AbstractAdvice>> aspectMappings = new HashMap<>();

	@Override
	public Object getProxy() {

		List<Object> aspects = AspectsRegistry.getInstance().getAspects();

		try {

			boolean weaved = false;
			for (Object aspect : aspects) {
				Class<?> aspectClass = aspect.getClass();
				Method[] aspectMethods = aspectClass.getDeclaredMethods();

				for (Method aspectMethod : aspectMethods) {
					Advice[] advices = ClassUtils.getMethodAnntation(aspectMethod, Advice.class, AdviceImpl.class);
					if (advices == null || advices.length == 0) {
						continue;
					}
					// matching start
					Class<?> targetClass = targetSource.getTargetClass();
					if (!matchClass(targetClass, advices)) {
						continue;
					}
					// match method start
					weaved = matchMethod(aspect, aspectMethod, targetClass, advices);
				}
			}
			if (weaved) {
				return createAopProxy().createProxy();
			}
			return targetSource.getTarget();
		} catch (Throwable e) {
			throw new ConfigurationException("An Exception Occured When Creating A Target Instance With Msg: [{}]",
					e.getMessage(), e);
		}
	}

	/**
	 * Match method
	 * 
	 * @param aspect
	 * @param aspectMethod
	 * @param targetClass
	 * @param advices
	 * @return
	 */
	private boolean matchMethod(Object aspect, //
			Method aspectMethod, Class<?> targetClass, Advice[] advices) //
	{
		boolean weaved = false;
		Method[] targetDeclaredMethods = targetClass.getDeclaredMethods();
		for (Advice advice : advices) {
			AdviceType adviceType = advice.type();
			Class<? extends Annotation>[] annotations = advice.value();
			AbstractAdvice abstractAdvice = getAdvice(aspect, aspectMethod, adviceType);

			boolean isAllMethodWeaving = false;
			// annotation matching
			for (Class<? extends Annotation> annotation : annotations) {
				if (targetClass.isAnnotationPresent(annotation)) {
					weaved = true;
					isAllMethodWeaving = true;
					// all method matched
					log.debug("Class Present An Annotation Named: [{}] All Method Will Be Weaving: [{}]", annotation,
							advice);
					for (Method targetMethod : targetDeclaredMethods) {// all methods
						weaving(abstractAdvice, targetMethod);
					}
					continue;
				}
				log.debug("Class Not Present An Annotation Named: [{}]", annotation);
				// method annotation match start
				for (Method targetMethod : targetDeclaredMethods) {
					if (targetMethod.isAnnotationPresent(annotation)) {
						weaved = true;
						weaving(abstractAdvice, targetMethod);
					}
				}
			}
			if (isAllMethodWeaving) { // has matched all method
				continue;
			}
			if (weaved) {
				regexMatchMethod(targetDeclaredMethods, advice, abstractAdvice);
				continue;
			}
			// regex match method
			weaved = regexMatchMethod(targetDeclaredMethods, advice, abstractAdvice);
		}
		return weaved;
	}

	/**
	 * 
	 * @param targetDeclaredMethods
	 * @param advice
	 * @param abstractAdvice
	 */
	private final boolean regexMatchMethod(Method[] targetDeclaredMethods, //
			Advice advice, AbstractAdvice abstractAdvice) //
	{
		String[] methodsStr = advice.method();
		boolean weaved = false;
		for (String methodStr : methodsStr) {
			String[] methodRegexs = methodStr.split(Constant.SPLIT_REGEXP);
			if (methodRegexs == null || methodRegexs.length == 0) {
				methodRegexs = new String[] { methodStr };
			}
			// start match method
			for (String methodRegex : methodRegexs) {
				for (Method targetMethod : targetDeclaredMethods) {
					if (!aspectMappings.containsKey(targetMethod)
							&& Pattern.matches(methodRegex, targetMethod.getName())) //
					{
						weaved = true;
						weaving(abstractAdvice, targetMethod);
					}
				}
			}
		}
		return weaved;
	}

	/**
	 * 
	 * @param targetClass
	 * @param advices
	 * @return
	 */
	private final boolean matchClass(Class<?> targetClass, Advice[] advices) {

		for (Advice advice : advices) {
			// target match start
			for (Class<?> target : advice.target()) {
				if (target == targetClass) {
					return true;
				}
			}
			Method[] targetDeclaredMethods = targetClass.getDeclaredMethods();
			// annotation match start
			for (Class<? extends Annotation> annotation : advice.value()) {
				if (targetClass.isAnnotationPresent(annotation)) {
					return true;
				}
				for (Method targetMethod : targetDeclaredMethods) {// all methods
					if (targetMethod.isAnnotationPresent(annotation)) {
						return true;
					}
				}
			}
			String name = targetClass.getName();
			for (String regex : advice.pointcut()) { // regex match start
				if (StringUtils.isEmpty(regex)) {
					return true;
				}
				if (Pattern.matches(regex, name)) {
					// class matched
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * 
	 * @param aspect
	 * @param aspectMethod
	 * @param adviceType
	 * @return
	 */
	public AbstractAdvice getAdvice(Object aspect, Method aspectMethod, AdviceType adviceType) {
		AbstractAdvice advice = null;
		switch (adviceType)
		{
			case BEFORE :
				advice = new MethodBeforeAdvice(aspectMethod);
				break;
			case AFTER :
				advice = new MethodAfterAdvice(aspectMethod);
				break;
			case AROUND :
				advice = new AroundMethodAdvice(aspectMethod);
				break;
			case AFTER_THROWING :
				advice = new MethodAfterThrowingAdvice(aspectMethod);
				break;
			case AFTER_RETURNING :
				advice = new MethodAfterReturningAdvice(aspectMethod);
				break;
		}
		if (advice != null) {
			advice.setAspect(aspect);
		}
		log.debug("Found Advice: [{}]", advice);
		return advice;
	}

	/**
	 * 
	 * @param advice
	 * @param targetMethod
	 */
	private void weaving(AbstractAdvice advice, Method targetMethod) {
		List<AbstractAdvice> aspectMapping = aspectMappings.get(targetMethod);
		if (aspectMapping == null) {
			aspectMapping = new ArrayList<>();
			aspectMappings.put(targetMethod, aspectMapping);
		}
		aspectMapping.add(advice);
	}

	public DefaultProxyFactory(TargetSource targetSource) {
		this.targetSource = targetSource;
	}

	public DefaultProxyFactory() {

	}

	protected final ProxyCreator createAopProxy() {
		targetSource.setAspectMappings(aspectMappings);
		return new CglibProxyCreator(targetSource);
	}

}
