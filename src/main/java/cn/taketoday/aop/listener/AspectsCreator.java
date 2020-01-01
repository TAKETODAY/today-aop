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
package cn.taketoday.aop.listener;

import cn.taketoday.aop.advice.AspectsRegistry;
import cn.taketoday.context.Ordered;
import cn.taketoday.context.annotation.ContextListener;
import cn.taketoday.context.annotation.Order;
import cn.taketoday.context.event.ContextPreRefreshEvent;
import cn.taketoday.context.listener.ApplicationListener;

/**
 * @author TODAY <br>
 *         2018-11-10 13:20
 */
@Deprecated
@ContextListener
@Order(Ordered.LOWEST_PRECEDENCE - Ordered.HIGHEST_PRECEDENCE)
public class AspectsCreator implements ApplicationListener<ContextPreRefreshEvent> {

    @Override
    public void onApplicationEvent(ContextPreRefreshEvent event) {
        final AspectsRegistry instance = AspectsRegistry.getInstance();
        if (instance.isAspectsLoaded()) {
            return;
        }
        instance.loadAspects(event.getApplicationContext());
    }

}
