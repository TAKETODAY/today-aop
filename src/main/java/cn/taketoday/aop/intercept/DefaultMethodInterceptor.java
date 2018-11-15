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
package cn.taketoday.aop.intercept;

import cn.taketoday.aop.advice.AbstractAdvice;
import cn.taketoday.aop.cglib.proxy.MethodInterceptor;
import cn.taketoday.aop.cglib.proxy.MethodProxy;
import cn.taketoday.aop.proxy.TargetSource;
import cn.taketoday.context.utils.OrderUtils;

import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Today <br>
 * 
 *         2018-11-06 19:14
 */
@Slf4j
public final class DefaultMethodInterceptor implements MethodInterceptor {

	private final Object target;
	private Map<Method, AbstractAdvice[]> aspectMappings;

	public DefaultMethodInterceptor(TargetSource targetSource) {
		this.target = targetSource.getTarget();
		Map<Method, List<AbstractAdvice>> aspectMappings_ = targetSource.getAspectMappings();
		aspectMappings = new HashMap<>(aspectMappings_.size());

		for (Entry<Method, List<AbstractAdvice>> advices : aspectMappings_.entrySet()) {
			advices.getValue().sort(Comparator.comparingInt(OrderUtils::getOrder).reversed());
			aspectMappings.put(advices.getKey(), advices.getValue().toArray(new AbstractAdvice[0]));
		}
	}

	@Override
	public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
		AbstractAdvice[] aspectMapping = aspectMappings.get(method);
		if (aspectMapping == null || aspectMapping.length == 0) {
			return proxy.invoke(target, args);
		}
		log.debug("Intercept method : [{}]", method.getName());
		return new DefaultMethodInvocation(target, method, args, aspectMapping).proceed();
	}

}
