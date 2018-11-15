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
package cn.taketoday.aop.annotation;

import cn.taketoday.aop.AdviceType;

import java.lang.annotation.Annotation;
import java.util.Arrays;

/**
 * @author Today <br>
 * 
 *         2018-11-10 13:10
 */
@SuppressWarnings("all")
public class AdviceImpl implements Advice {

	private Class<? extends Annotation>[] value;
	private String method[] = { "*" };
	private Class<?>[] target = null;
	private AdviceType type = AdviceType.BEFORE;
	private String[] pointcut = {};

	@Override
	public Class<? extends Annotation> annotationType() {
		return Advice.class;
	}

	@Override
	public Class<? extends Annotation>[] value() {
		return this.value;
	}

	@Override
	public Class<?>[] target() {
		return target;
	}

	@Override
	public String[] method() {
		return this.method;
	}

	@Override
	public String[] pointcut() {
		return this.pointcut;
	}

	@Override
	public AdviceType type() {
		return this.type;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{\n\t\"value\":\"").append(Arrays.toString(value)).append("\",\n\t\"method\":\"")
				.append(Arrays.toString(method)).append("\",\n\t\"type\":\"").append(type)
				.append("\",\n\t\"pointcut\":\"").append(Arrays.toString(pointcut)).append("\",\n\t\"target\":\"")
				.append(Arrays.toString(target)).append("\"\n}");
		return builder.toString();
	}

}
