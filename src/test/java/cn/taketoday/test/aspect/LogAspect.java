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
package cn.taketoday.test.aspect;

import cn.taketoday.aop.annotation.After;
import cn.taketoday.aop.annotation.AfterReturning;
import cn.taketoday.aop.annotation.AfterThrowing;
import cn.taketoday.aop.annotation.Annotated;
import cn.taketoday.aop.annotation.Arguments;
import cn.taketoday.aop.annotation.Aspect;
import cn.taketoday.aop.annotation.Before;
import cn.taketoday.aop.annotation.Returning;
import cn.taketoday.aop.annotation.Throwing;
import cn.taketoday.context.Ordered;
import cn.taketoday.context.annotation.Order;
import cn.taketoday.test.domain.User;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Today <br>
 *         2018-11-06 17:52
 */
@Slf4j
@Aspect
@Order(Ordered.HIGHEST_PRECEDENCE)
public class LogAspect {

	@AfterReturning(value = "cn.taketoday.test.service.*", annotation = Logger.class, method = "login")
	public void afterReturning(@Returning Object returnValue) {
		log.debug("@AfterReturning returnValue: [{}]", returnValue);
//		int i = 1 / 0;
	}

	@AfterThrowing(value = "cn.taketoday.test.service.*", annotation = Logger.class, method = "login")
	public void afterThrowing(@Throwing Throwable throwable) {
		log.error("@AfterThrowing With Msg: [{}]", throwable.getMessage(), throwable);
	}

	@Before(value = "cn.taketoday.test.service.*", annotation = Logger.class, method = "login")
	public void before(@Annotated Logger logger) {
		log.debug("@Before method in class with logger: [{}]", logger);
	}

	@After(value = "cn.taketoday.test.service.*", annotation = Logger.class, method = "login")
	public Object after(@Returning User returnValue, @Arguments Object[] arguments) {
		log.debug("@After method in class");
		return returnValue.setSex("nv");
	}
//
//	@Around(value = "cn.taketoday.test.service.*", annotation = Logger.class, method = "login")
//	public Object around(@JoinPoint Joinpoint joinpoint) throws Throwable {
//		log.debug("@Around Before method");
//		Object proceed = joinpoint.proceed();
//		log.debug("@Around After method");
//		return proceed;
//	}

}
