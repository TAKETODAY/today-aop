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
package test.demo.service.impl;

import cn.taketoday.context.annotation.Autowired;
import cn.taketoday.context.annotation.Service;
import lombok.extern.slf4j.Slf4j;
import test.aspect.Logger;
import test.aspect.Timer;
import test.demo.dao.UserDao;
import test.demo.domain.User;
import test.demo.service.UserService;

/**
 * @author TODAY <br>
 *         2018-11-11 09:25
 */
@Slf4j
@Service
public class DefaultUserService implements UserService {

    final private UserDao userDao;

    @Autowired
    public DefaultUserService(UserDao userDao) {
        this.userDao = userDao;
    }

    @Timer
    @Logger("登录")
    @Override
    public User login(User user) {
        log.debug("login");
        //		int i = 1 / 0;
        return userDao.login(user);
    }

    @Logger("注册")
    @Override
    public boolean register(User user) {
        return userDao.save(user);
    }

    @Override
    public boolean remove(User user) {

        log.debug("remove");

        return true;
    }
}
