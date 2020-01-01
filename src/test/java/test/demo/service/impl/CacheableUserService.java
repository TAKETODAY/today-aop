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

import java.util.concurrent.TimeUnit;

import cn.taketoday.cache.annotation.CacheConfig;
import cn.taketoday.cache.annotation.CachePut;
import cn.taketoday.cache.annotation.Cacheable;
import cn.taketoday.cache.annotation.EnableRedissonCaching;
import cn.taketoday.context.annotation.Autowired;
import cn.taketoday.context.annotation.Service;
import lombok.extern.slf4j.Slf4j;
import test.demo.domain.User;
import test.demo.mapper.UserMapper;
import test.demo.service.UserService;

/**
 * @author TODAY <br>
 *         2018-11-15 16:52
 */
@Slf4j
@Service
//@EnableCaching
@EnableRedissonCaching
@CacheConfig(cacheName = "loginUser")
public class CacheableUserService implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Cacheable(key = "userEmail_${user.email}", condition = "${!empty user.email}", expire = 2000,
               timeUnit = TimeUnit.MILLISECONDS, sync = true)
    //  @Cacheable(cacheName = "loginUser", sync = false)
    public User login(User user) {
        log.debug("login userMapper");

        return userMapper.login(user);
    }

    @Override
    @CachePut(cacheName = "loginUser")
    public boolean register(User user) {
        userMapper.save(user);
        return false;
    }

    @Override
    public boolean remove(User user) {
        return false;
    }

}
