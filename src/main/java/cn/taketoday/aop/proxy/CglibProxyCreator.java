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
package cn.taketoday.aop.proxy;

import cn.taketoday.aop.ProxyCreator;
import cn.taketoday.aop.cglib.proxy.Enhancer;
import cn.taketoday.aop.intercept.DynamicAdvisedInterceptor;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @author Today <br>
 *         2018-11-10 13:03
 */
@Slf4j
public class CglibProxyCreator implements ProxyCreator {

	private TargetSource targetSource;

	public CglibProxyCreator(TargetSource targetSource) {
		this.targetSource = targetSource;
	}

	@Override
	public Object createProxy() {

		log.debug("Creating Cglib Proxy: target source is [{}]", targetSource);

		Class<?> targetClass = targetSource.getTargetClass();
		Enhancer enhancer = new Enhancer();
		enhancer.setSuperclass(targetClass);
		enhancer.setInterfaces(targetSource.getInterfaces());
		enhancer.setInterceptDuringConstruction(false);
		enhancer.setCallback(new DynamicAdvisedInterceptor(targetSource));

		return enhancer.create();
	}

}
