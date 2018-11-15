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
package cn.taketoday.aop.listener;

import cn.taketoday.aop.advice.AspectsRegistry;
import cn.taketoday.aop.annotation.Aspect;
import cn.taketoday.context.ApplicationContext;
import cn.taketoday.context.annotation.ContextListener;
import cn.taketoday.context.event.ContextRefreshEvent;
import cn.taketoday.context.factory.ObjectFactory;
import cn.taketoday.context.factory.SimpleObjectFactory;
import cn.taketoday.context.listener.ApplicationListener;
import cn.taketoday.context.utils.ClassUtils;

import java.util.Collection;

import lombok.extern.slf4j.Slf4j;

/**
 * @author TODAY <br>
 * 
 *         2018-11-10 13:20
 */
@Slf4j
@ContextListener
public class AspectsCreator implements ApplicationListener<ContextRefreshEvent> {

	@Override
	public void onApplicationEvent(ContextRefreshEvent event) {

		log.debug("Loading Aspect Objects");
		ApplicationContext applicationContext = event.getApplicationContext();
		Collection<Class<?>> aspects = ClassUtils.getAnnotatedClasses(Aspect.class);
		AspectsRegistry aspectsRegistry = AspectsRegistry.getInstance();
		ObjectFactory objectFactory = new SimpleObjectFactory();
		try {
			for (Class<?> aspect : aspects) {
				log.debug("Found Aspect: [{}]", aspect.getName());
				Object create = objectFactory.create(aspect);
				aspectsRegistry.addAspect(create);
				applicationContext.registerSingleton(aspect.getSimpleName(), create);
			}
			aspectsRegistry.sortAspects();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
