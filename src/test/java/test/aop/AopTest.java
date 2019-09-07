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
package test.aop;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.taketoday.context.ApplicationContext;
import cn.taketoday.context.StandardApplicationContext;
import cn.taketoday.context.cglib.core.DebuggingClassWriter;
import cn.taketoday.context.exception.NoSuchBeanDefinitionException;
import lombok.extern.slf4j.Slf4j;
import test.demo.domain.User;
import test.demo.interceptor.TestInterceptor;
import test.demo.service.UserService;

/**
 * @author Today <br>
 * 
 *         2018-08-10 21:29
 */
@Slf4j
public class AopTest {

    private long start;
    static {
        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:/debug");
    }
    @Before
    public void before() {
        start = System.currentTimeMillis();
    }

    @After
    public void over() {
        System.out.println(System.currentTimeMillis() - start + "ms");
    }

    @Test
    public void test_Login() throws NoSuchBeanDefinitionException {

        try (ApplicationContext applicationContext = new StandardApplicationContext("", "")) {

            UserService bean = applicationContext.getBean(UserService.class);
            User user = new User();
            user.setPasswd("666");

            user.setUserId("666");
            long start = System.currentTimeMillis();
            User login = bean.login(user);

//			for (int i = 0; i < 1000; i++) {
//				login = bean.login(user);
//			}

            log.debug("{}ms", System.currentTimeMillis() - start);

            log.debug("Result:[{}]", login);
            log.debug("{}ms", System.currentTimeMillis() - start);

            TestInterceptor bean2 = applicationContext.getBean(TestInterceptor.class);

            System.err.println(bean2);
        }
    }

}
