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
package cn.taketoday.aop.advice;

import cn.taketoday.aop.BeforeAdvice;
import cn.taketoday.context.annotation.Order;

import java.lang.reflect.Method;

import org.aopalliance.intercept.MethodInvocation;


/**
 * @author Today <br>
 * 
 *         2018-10-13 11:27
 */
@Order(2)
public class MethodBeforeAdvice extends AbstractAdvice implements BeforeAdvice {

	public MethodBeforeAdvice(Method method) {
		super(method);
	}
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		invokeAdviceMethod(invocation, null, null);
		return invocation.proceed();
	}

}
