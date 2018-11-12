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
package cn.taketoday.test.aop;

import cn.taketoday.context.ApplicationContext;
import cn.taketoday.context.StandardApplicationContext;
import cn.taketoday.context.exception.NoSuchBeanDefinitionException;
import cn.taketoday.test.domain.User;
import cn.taketoday.test.service.UserService;
import cn.taketoday.test.service.impl.UserServiceImpl;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import lombok.extern.slf4j.Slf4j;

/**
 * @author Today <br>
 * 
 *         2018-08-10 21:29
 */
@Slf4j
public class AopTest {

	private long start;

	@Before
	public void before() {
		start = System.currentTimeMillis();
	}

	@After
	public void over() {
		System.out.println(System.currentTimeMillis() - start + "ms");
	}

	@Test
	public void start() throws NoSuchBeanDefinitionException {

		ApplicationContext applicationContext = new StandardApplicationContext();
		Object bean = applicationContext.getBean("TimerInterceptor");

		System.out.println(bean);

		applicationContext.close();
	}

	public static void main(String[] args) throws NoSuchBeanDefinitionException {

		ApplicationContext applicationContext = new StandardApplicationContext(false);

		UserService bean = applicationContext.getBean(UserServiceImpl.class);

		User user = new User();
		user.setPasswd("666");
		user.setUserId("666");

		long start = System.currentTimeMillis();
		User login = bean.login(user);
		log.debug("{}ms", System.currentTimeMillis() - start);
		for (int i = 0; i < 10; i++) {
			login = bean.login(user);
		}
		log.debug("Result:[{}]", login);
		log.debug("{}ms", System.currentTimeMillis() - start);

		applicationContext.close();
	}

}
