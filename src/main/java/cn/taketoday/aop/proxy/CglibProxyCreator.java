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

import java.lang.reflect.Constructor;

import cn.taketoday.aop.ProxyCreator;
import cn.taketoday.aop.cglib.proxy.Enhancer;
import cn.taketoday.aop.intercept.CglibMethodInterceptor;
import cn.taketoday.context.annotation.Autowired;
import cn.taketoday.context.exception.ConfigurationException;
import cn.taketoday.context.factory.BeanFactory;
import cn.taketoday.context.utils.ContextUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Today <br>
 *         2018-11-10 13:03
 */
@Slf4j
public class CglibProxyCreator implements ProxyCreator {

	@Override
	public Object createProxy(TargetSource targetSource, BeanFactory beanFactory) {

		log.debug("Creating Cglib Proxy, target source is: [{}]", targetSource);

		Class<?> targetClass = targetSource.getTargetClass();
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(targetClass);
		enhancer.setInterfaces(targetSource.getInterfaces());
		enhancer.setInterceptDuringConstruction(false);
		enhancer.setCallback(new CglibMethodInterceptor(targetSource));

		// fix: Superclass has no null constructors but no arguments were given
		Constructor<?>[] constructors = targetClass.getConstructors();
		if (constructors == null || constructors.length == 0) {
			throw new ConfigurationException("You must provide at least one public constructor");
		}

		for (Constructor<?> constructor : constructors) {
			if (constructor.getParameterCount() == 0) {// <init>()
				return enhancer.create();
			}
			else if (constructor.isAnnotationPresent(Autowired.class)) {
				final Object[] resolveParameter = ContextUtils.resolveParameter(constructor, beanFactory);
				return enhancer.create(constructor.getParameterTypes(), resolveParameter);
			}
		}
		throw new ConfigurationException("Your provided constructors must at least one annotated @{}", Autowired.class.getName());
	}

}
