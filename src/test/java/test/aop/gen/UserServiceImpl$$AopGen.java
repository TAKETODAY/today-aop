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
 * along with this program.  If not, see [http://www.gnu.org/licenses/]
 */
package test.aop.gen;

import cn.taketoday.aop.intercept.StandardMethodInvocation;
import cn.taketoday.aop.intercept.StandardMethodInvocation.TargetMethodInvocation;
import test.demo.dao.UserDao;
import test.demo.domain.User;
import test.demo.service.impl.UserServiceImpl;

/**
 * @author TODAY <br>
 *         2019-10-23 12:49
 */
public class UserServiceImpl$$AopGen extends UserServiceImpl {

    private final UserServiceImpl target;
    private final TargetMethodInvocation targetInvocation;

    public UserServiceImpl$$AopGen(UserDao userDao, UserServiceImpl target, TargetMethodInvocation targetInvocation) {
        super(userDao);
        this.target = target;
        this.targetInvocation = targetInvocation;
    }

    @Override
    public User login(User user) {
        try {
            new StandardMethodInvocation(targetInvocation, user).proceed();
        }
        catch (Throwable e) {

        }
        return super.login(user);
    }

    @Override
    public boolean register(User user) {
        return super.register(user);
    }

    public UserServiceImpl getTarget() {
        return target;
    }
}
