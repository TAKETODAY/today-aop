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
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Today <br>
 * 
 *         2018-08-09 18:50
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
@Advice(type = AdviceType.BEFORE)
public @interface Before {

	/**
	 * package name
	 * 
	 * @return
	 */
	String[] value() default {};
	

	/**
	 * target classes
	 * 
	 * @return
	 */
	Class<?>[] target() default {};


	/**
	 * method in class
	 * 
	 * @return
	 */
	String[] method() default {};

	/**
	 * annotated with
	 * 
	 * @return
	 */
	Class<? extends Annotation>[] annotation() default {};

}
