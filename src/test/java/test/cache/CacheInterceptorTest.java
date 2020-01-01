/**
 * Original Author -> 杨海健 (taketoday@foxmail.com) https://taketoday.cn
 * Copyright © TODAY & 2017 - 2020 All Rights Reserved.
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
package test.cache;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import cn.taketoday.context.ApplicationContext;
import cn.taketoday.context.StandardApplicationContext;
import cn.taketoday.context.exception.NoSuchBeanDefinitionException;
import cn.taketoday.context.logger.Logger;
import cn.taketoday.context.logger.LoggerFactory;
import test.demo.domain.User;
import test.demo.service.UserService;
import test.demo.service.impl.CacheableUserService;
import test.demo.service.impl.DefaultUserService;

/**
 * @author Today <br>
 * 
 *         2018-12-24 22:34
 */
public class CacheInterceptorTest {

    private static final Logger log = LoggerFactory.getLogger(CacheInterceptorTest.class);

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
    public void test_Login() throws NoSuchBeanDefinitionException {

        try (ApplicationContext applicationContext = new StandardApplicationContext("", "")) {

            UserService bean = applicationContext.getBean(DefaultUserService.class);
            User user = new User();
            user.setEmail("taketoday@foxmail.com");
            user.setPassword("130447AD788ACD4E5A06BF83136E78CB");
            long start = System.currentTimeMillis();
            User login = bean.login(user);
            login = bean.login(user);

            log.debug("{}ms", System.currentTimeMillis() - start);
            log.debug("Result:[{}]", login);
            log.debug("{}ms", System.currentTimeMillis() - start);

            // CacheableUserService

            final CacheableUserService userService = applicationContext.getBean(CacheableUserService.class);

            login = userService.login(user);
            log.debug("Result:[{}]", login);
        }
    }

}
