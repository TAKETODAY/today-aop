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
package cn.taketoday.aop.intercept;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * 
 * @author Today <br>
 *         2018-11-10 13:14
 */
public class DefaultMethodInvocation implements MethodInvocation {

	private final Object target;
	private final Method method;
	private final Object[] arguments;
	private final MethodInterceptor[] advices;

	/**
	 * a flag show that current index of advice
	 */
	private int currentAdviceIndex = 0;
	
	private final int adviceLength;

	public DefaultMethodInvocation(Object target, Method method, Object[] arguments, MethodInterceptor[] advices) {
		this.target = target;
		this.method = method;
		this.advices = advices;
		this.arguments = arguments;
		this.adviceLength = advices.length;
	}

	@Override
	public Method getMethod() {
		return method;
	}

	@Override
	public Object[] getArguments() {
		return arguments;
	}

	@Override
	public Object proceed() throws Throwable {

		if (currentAdviceIndex == adviceLength) {
			return method.invoke(target, arguments);
		}
		return advices[currentAdviceIndex++].invoke(this);
	}

	@Override
	public Object getThis() {
		return target;
	}

	@Override
	public AccessibleObject getStaticPart() {
		return method;
	}

	@Override
	public String toString() {
		return new StringBuilder()//
				.append("{\n\t\"target\":\"").append(target)//
				.append("\",\n\t\"method\":\"").append(method)//
				.append("\",\n\t\"arguments\":\"").append(Arrays.toString(arguments))//
				.append("\",\n\t\"advices\":\"").append(Arrays.toString(advices))//
				.append("\",\n\t\"currentAdviceIndex\":\"").append(currentAdviceIndex)//
				.append("\"\n}")//
				.toString();
	}

}
