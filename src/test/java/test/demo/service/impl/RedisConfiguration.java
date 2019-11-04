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
package test.demo.service.impl;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.client.codec.Codec;
import org.redisson.codec.FstCodec;
import org.redisson.config.Config;

import cn.taketoday.context.annotation.Autowired;
import cn.taketoday.context.annotation.Configuration;
import cn.taketoday.context.annotation.Props;
import cn.taketoday.context.annotation.Singleton;

/**
 * @author TODAY <br>
 *         2018-12-21 23:11
 */
@Configuration
@Props(prefix = { "redis.pool.", "redis." })
public class RedisConfiguration {

    //    private int maxIdle;
    private int minIdle;
    private int timeout;
    private int maxTotal;

    private int database;
    private String address;

    private String password;
    private String clientName;

    private int connectTimeout;

    @Singleton("fstCodec")
    public Codec codec() {
        return new FstCodec();
    }

    /**
     * @param redisson
     * @return
     */
    //	@Singleton("limitLock")
    //	public Lock limitLock(Redisson redisson) {
    //		return redisson.getLock("limitLock");
    //	}

    @Singleton(destroyMethods = "shutdown")
    public Redisson redisson(@Autowired("fstCodec") Codec codec) {
        Config config = new Config();
        config.setCodec(codec)//
                .useSingleServer()//
                .setAddress(address)//
                .setTimeout(timeout)//
                .setPassword(password)//
                .setDatabase(database)//
                .setClientName(clientName)//
                .setConnectionPoolSize(maxTotal)//
                .setConnectTimeout(connectTimeout)//
                .setConnectionMinimumIdleSize(minIdle);
        //                .setConnectionMinimumIdleSize(maxIdle)

        final RedissonClient create = Redisson.create(config);

        create.getKeys().flushdb();

        return (Redisson) create;
    }

}
