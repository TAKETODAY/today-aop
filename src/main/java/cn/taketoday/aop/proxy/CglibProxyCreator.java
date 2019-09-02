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
import cn.taketoday.aop.intercept.CglibMethodInterceptor;
import cn.taketoday.context.annotation.Autowired;
import cn.taketoday.context.cglib.proxy.Enhancer;
import cn.taketoday.context.exception.ConfigurationException;
import cn.taketoday.context.factory.BeanFactory;
import cn.taketoday.context.utils.ContextUtils;
import cn.taketoday.context.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author TODAY <br>
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
        final Constructor<?>[] constructors = targetClass.getDeclaredConstructors();

        if (ObjectUtils.isEmpty(constructors)) {
            throw new ConfigurationException("You must provide at least one constructor");
        }

        if (constructors.length == 1) { // 只有一个构造器
            return doEnhance(beanFactory, enhancer, constructors[0]);
        }

        for (final Constructor<?> constructor : constructors) { // 多个构造器时选用标注有Autowired的
            if (constructor.isAnnotationPresent(Autowired.class)) {
                return doEnhance(beanFactory, enhancer, constructor);
            }
        }
        throw new ConfigurationException("Your provided constructors must at least one annotated @" + Autowired.class.getName());
    }

    protected Object doEnhance(final BeanFactory beanFactory, final Enhancer enhancer, final Constructor<?> constructor) {
        if (constructor.getParameterCount() == 0) {// <init>()
            return enhancer.create();
        }
        final Object[] resolveParameter = ContextUtils.resolveParameter(constructor, beanFactory);
        return enhancer.create(constructor.getParameterTypes(), resolveParameter);
    }

}
