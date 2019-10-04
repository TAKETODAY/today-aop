/**
 * Original Author -> 杨海健 (taketoday@foxmail.com) https://taketoday.cn
 * Copyright © TODAY & 2017 - 2019 All Rights Reserved.
 * 
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package cn.taketoday.aop.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.aopalliance.intercept.MethodInterceptor;

import cn.taketoday.aop.Constant;
import cn.taketoday.aop.ProxyFactory;
import cn.taketoday.aop.advice.AbstractAdvice;
import cn.taketoday.aop.advice.AspectsRegistry;
import cn.taketoday.aop.annotation.Advice;
import cn.taketoday.aop.annotation.AdviceImpl;
import cn.taketoday.aop.annotation.Aspect;
import cn.taketoday.context.ApplicationContext;
import cn.taketoday.context.exception.ConfigurationException;
import cn.taketoday.context.factory.BeanFactory;
import cn.taketoday.context.utils.ClassUtils;
import cn.taketoday.context.utils.ExceptionUtils;
import cn.taketoday.context.utils.ObjectUtils;
import cn.taketoday.context.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author TODAY <br>
 *         2018-11-10 13:13
 */
@Slf4j
public class DefaultProxyFactory implements ProxyFactory {

    private static final CglibProxyCreator CGLIB_PROXY_CREATOR = new CglibProxyCreator();;

    private final TargetSource targetSource;
    private final ApplicationContext applicationContext;
    private final Map<Method, List<MethodInterceptor>> aspectMappings = new HashMap<>(16, 1.0f);

    public DefaultProxyFactory() {
        this(null, null);
    }

    public DefaultProxyFactory(TargetSource targetSource, ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
        this.targetSource = targetSource;
    }

    @Override
    public Object getProxy() {

        List<Object> aspects = getAspects();

        try {

            boolean weaved = false;
            for (final Object aspect : aspects) {
                final Class<?> aspectClass = aspect.getClass(); // aspect class
                final Class<?> targetClass = targetSource.getTargetClass(); // target class

                if (aspect instanceof MethodInterceptor) { // 直接实现的: MethodInterceptor
                    // create interceptor chain
                    final Advice[] advices = ClassUtils.getAnnotationArray(aspectClass, Advice.class, AdviceImpl.class);

                    // matching class start
                    if (matchClass(targetClass, advices)) { // matched
                        weaved = matchMethod(aspect, null, targetClass, advices);
                    }
                }

                // matching all methods
                // ---------------------

                for (final Method aspectMethod : aspectClass.getDeclaredMethods()) {// all advice methods

                    final Advice[] advices = ClassUtils.getAnnotationArray(aspectMethod, Advice.class,
                                                                           AdviceImpl.class);
                    if (ObjectUtils.isNotEmpty(advices)) {
                        // matching class start
                        if (!matchClass(targetClass, advices)) {
                            continue;
                        }
                        // matching methods start
                        if (weaved) {
                            matchMethod(aspect, aspectMethod, targetClass, advices);
                        }
                        else {
                            weaved = matchMethod(aspect, aspectMethod, targetClass, advices);
                        }
                    }
                }
            }
            if (weaved) {
                targetSource.setAspectMappings(aspectMappings);
                return CGLIB_PROXY_CREATOR.createProxy(targetSource, applicationContext);
            }
            return targetSource.getTarget();
        }
        catch (Throwable ex) {
            ex = ExceptionUtils.unwrapThrowable(ex);
            throw new ConfigurationException(//
                                             "An Exception Occured When Creating A Target Proxy Instance With Msg: ["
                                                     + ex + "]",
                                             ex//
            );
        }
    }

    /**
     * Get all Aspects
     * 
     * @return All Aspects
     */
    protected List<Object> getAspects() {

        final AspectsRegistry instance = AspectsRegistry.getInstance();

        if (!instance.isAspectsLoaded()) {
            instance.loadAspects(applicationContext);
        }

        return instance.getAspects();
    }

    /**
     * Match method
     * 
     * @param aspect
     *            aspect instance
     * @param aspectMethod
     *            aspect method
     * @param targetClass
     *            target class
     * @param advices
     *            advice instances
     * @return
     * @throws Throwable
     */
    private boolean matchMethod(final Object aspect, final Method aspectMethod, //
                                final Class<?> targetClass, final Advice[] advices) throws Throwable //
    {
        boolean weaved = false;
        Method[] targetDeclaredMethods = targetClass.getDeclaredMethods();

        for (final Advice advice : advices) {
            Class<? extends MethodInterceptor> interceptor = advice.interceptor(); // interceptor class

            MethodInterceptor methodInterceptor = null;
            if (aspectMethod == null) { // method interceptor
                if (!(aspect instanceof MethodInterceptor)) {
                    throw new ConfigurationException("[" + aspect.getClass().getName() + //
                            "] must be implement: [" + MethodInterceptor.class.getName() + "]");
                }
                methodInterceptor = (MethodInterceptor) aspect;
            }
            else {
                methodInterceptor = getInterceptor(aspect, aspectMethod, interceptor, applicationContext);
            }
            log.trace("Found Interceptor: [{}]", methodInterceptor);

            boolean isAllMethodsWeaved = false;

            boolean traceEnabled = ClassUtils.traceEnabled;
            // annotation matching
            for (Class<? extends Annotation> annotation : advice.value()) {
                if (targetClass.isAnnotationPresent(annotation)) {
                    weaved = true;
                    isAllMethodsWeaved = true;
                    // all method matched
                    if (traceEnabled) {
                        log.trace("Class: [{}] Present An Annotation Named: [{}] All Method Will Be Weaving: [{}]", //
                                  targetClass.getName(), annotation, advice);
                    }

                    for (Method targetMethod : targetDeclaredMethods) {// all methods
                        weaving(methodInterceptor, targetMethod, aspectMappings);
                    }
                    continue;
                }
                if (traceEnabled) {
                    log.trace("Class: [{}] Not Present An Annotation Named: [{}]", targetClass.getName(), annotation);
                }
                // method annotation match start
                for (Method targetMethod : targetDeclaredMethods) {
                    if (targetMethod.isAnnotationPresent(annotation)) {
                        weaved = true;
                        weaving(methodInterceptor, targetMethod, aspectMappings);
                    }
                }
            }
            if (isAllMethodsWeaved) { // has matched all method
                continue;
            }
            if (weaved) { // has already weaved
                regexMatchMethod(targetDeclaredMethods, advice, methodInterceptor);
                continue;
            }
            // regex match method
            weaved = regexMatchMethod(targetDeclaredMethods, advice, methodInterceptor);
        }
        return weaved;
    }

    private boolean regexMatchMethod(final Method[] targetDeclaredMethods, //
                                     final Advice advice, final MethodInterceptor methodInterceptor) //
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
                    if (!aspectMappings.containsKey(targetMethod) && //
                        Pattern.matches(methodRegex, targetMethod.getName())) //
                    {
                        weaved = true;
                        weaving(methodInterceptor, targetMethod, aspectMappings);
                    }
                }
            }
        }
        return weaved;
    }

    /**
     * Match target class
     * 
     * @param targetClass
     *            target class
     * @param advices
     *            advice methods
     * @return if class matched
     */
    public static boolean matchClass(final Class<?> targetClass, final Advice[] advices) {

        for (final Advice advice : advices) {
            // target class match start
            for (final Class<?> target : advice.target()) {
                if (target == targetClass) {
                    return true;
                }
            }
            Method[] targetDeclaredMethods = targetClass.getDeclaredMethods(); // target class's methods
            // annotation match start
            for (Class<? extends Annotation> annotation : advice.value()) {
                if (targetClass.isAnnotationPresent(annotation)) {
                    return true;
                }
                for (Method targetMethod : targetDeclaredMethods) {// target class's methods
                    if (targetMethod.isAnnotationPresent(annotation)) {
                        return true;
                    }
                }
            }
            String targetClassName = targetClass.getName();
            for (String regex : advice.pointcut()) { // regex match start
                if (StringUtils.isEmpty(regex)) {
                    continue;
                }

                if (Pattern.matches(regex, targetClassName)) {
                    // class matched
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Get an advice instance
     * 
     * @param aspect
     *            aspect instance
     * @param aspectMethod
     *            current aspect method
     * @param interceptor
     *            interceptor type
     * @throws Throwable
     */
    public static MethodInterceptor getInterceptor(final Object aspect, Method aspectMethod, //
                                                   final Class<? extends MethodInterceptor> interceptor, //
                                                   final BeanFactory beanFactory) throws Throwable //
    {

        if (interceptor == AbstractAdvice.class || !MethodInterceptor.class.isAssignableFrom(interceptor)) {
            throw new ConfigurationException("You must be implement: [" + AbstractAdvice.class.getName() + //
                    "] or [" + MethodInterceptor.class.getName() + "]");
        }

        if (AbstractAdvice.class.isAssignableFrom(interceptor)) {
            return interceptor.getConstructor(Method.class, Object.class).newInstance(aspectMethod, aspect);
        }

        // fix
        if (interceptor.isAnnotationPresent(Aspect.class)) {
            MethodInterceptor bean = beanFactory.getBean(interceptor);
            if (bean != null) {
                return bean;
            }
        }
        return interceptor.getConstructor().newInstance();
    }

    /**
     * Weaving advice to target method
     * 
     * @param advice
     *            advice instance
     * @param targetMethod
     *            target method
     * @param aspectMappings
     *            aspect mappings
     */
    public static void weaving(MethodInterceptor advice, Method targetMethod, //
                               Map<Method, List<MethodInterceptor>> aspectMappings) //
    {
        List<MethodInterceptor> aspectMapping = aspectMappings.get(targetMethod);
        if (aspectMapping == null) {
            aspectMapping = new ArrayList<>();
            aspectMappings.put(targetMethod, aspectMapping);
        }
        aspectMapping.add(advice);
    }

}
