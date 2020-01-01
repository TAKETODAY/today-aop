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

import java.io.IOException;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.defaults.DefaultSqlSessionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;

import com.zaxxer.hikari.HikariDataSource;

import cn.taketoday.context.annotation.Configuration;
import cn.taketoday.context.annotation.Props;
import cn.taketoday.context.annotation.Singleton;
import cn.taketoday.context.annotation.Value;
import cn.taketoday.context.factory.DisposableBean;
import cn.taketoday.context.factory.InitializingBean;
import cn.taketoday.context.utils.ContextUtils;
import test.demo.mapper.UserMapper;

/**
 * 
 * @author Today <br>
 *         2018-08-28 21:36
 */
@Configuration
@Props(prefix = "jdbc.")
public final class HikariDataSourceConfiguration extends HikariDataSource implements InitializingBean, DisposableBean {

    private String url;
    private String passwd;
    private String driver;
    private String userName;

    private int minIdle = 0;
    private int maxPoolSize = 2;
    private long idleTimeout = 600000;
    private boolean isReadOnly = false;
    private long maxLifetime = 1800000;
    private long connectionTimeout = 30000;

    public HikariDataSourceConfiguration() {

    }

    @Override
    public void afterPropertiesSet() throws Exception {
        setJdbcUrl(url);
        setPassword(passwd);
        setUsername(userName);
        setMinimumIdle(minIdle);
        setReadOnly(isReadOnly);
        setDriverClassName(driver);
        setIdleTimeout(idleTimeout);
        setMaxLifetime(maxLifetime);
        setMaximumPoolSize(maxPoolSize);
        setConnectionTimeout(connectionTimeout);
    }

    @Override
    public void destroy() throws Exception {
        close();
    }

    @Singleton
    public org.apache.ibatis.session.Configuration mybatisConfiguration(@Value(value = "#{mybatis.config}") String config)
            throws IOException {
        final org.apache.ibatis.session.Configuration parse = new XMLConfigBuilder(ContextUtils.getResourceAsStream(config),
                                                                                   "TODAY-MYBATIS").parse();

        parse.setEnvironment(new Environment("TODAY-MYBATIS", new JdbcTransactionFactory(), this));

        return parse;
    }

    @Singleton
    public SqlSessionFactory sessionFactory(org.apache.ibatis.session.Configuration configuration) {
        return new DefaultSqlSessionFactory(configuration);
    }

    @Singleton
    public SqlSession sqlSession(SqlSessionFactory sessionFactory) {
        return sessionFactory.openSession();
    }

    @Singleton
    public UserMapper userMapper(SqlSession session) {
        return session.getMapper(UserMapper.class);
    }

}
