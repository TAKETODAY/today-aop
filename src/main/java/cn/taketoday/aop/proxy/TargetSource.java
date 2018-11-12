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

import cn.taketoday.aop.advice.AbstractAdvice;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author Today <br>
 *         2018-11-10 11:47
 */
@Getter
@Setter
public class TargetSource {

	private Object target;
	private Class<?> targetClass;
	private Class<?>[] interfaces;
	private Map<Method, List<AbstractAdvice>> aspectMappings;

	/**
	 * the name of this target
	 */
	private final String beanName;

	public TargetSource(Object target, Class<?> targetClass, String beanName) {
		this.target = target;
		this.targetClass = targetClass;
		this.interfaces = targetClass.getInterfaces();
		this.beanName = beanName;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\"targetClass\":\"").append(targetClass).append("\",\"interfaces\":\"")
				.append(Arrays.toString(interfaces)).append("\",\"target\":\"").append(target).append("\"}");
		return builder.toString();
	}
}
