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
package cn.taketoday.aop.advice;

import cn.taketoday.context.utils.OrderUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Today <br>
 * 
 *         2018-11-10 18:48
 */
@Getter
@Setter
public final class AspectsRegistry {

	private static AspectsRegistry aspectsRegistry = new AspectsRegistry();

	private final List<Object> aspects = new ArrayList<>();

	public void addAspect(Object aspect) {
		aspects.add(aspect);
	}

	public static AspectsRegistry getInstance() {
		return aspectsRegistry;
	}

	public void sortAspects() {
		aspects.sort(Comparator.comparing(OrderUtils::getOrder).reversed());
	}

}