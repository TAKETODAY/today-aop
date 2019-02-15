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
package test.demo.domain;

import java.io.Serializable;

import cn.taketoday.context.annotation.Value;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
//@Singleton
@AllArgsConstructor
//@Prototype("prototype_config")
public class Config implements Serializable {

	private static final long serialVersionUID = 2021083013784359309L;

	private Integer id;

	@Value("#{site.cdn}")
	private String cdn;

	@Value("#{site.icp}")
	private String icp;

	@Value("#{site.host}")
	private String host;

	@Value("#{site.index}")
	private String index;

	@Value("#{site.upload}")
	private String upload;

	@Value("#{site.keywords}")
	private String keywords;

	@Value("#{site.name}")
	private String siteName;

	@Value("#{site.copyright}")
	private String copyright;

	@Value("#{site.baiduCode}")
	private String baiduCode;

	@Value("#{site.server.path}")
	private String serverPath;

	@Value("#{site.description}")
	private String description;

	@Value("#{site.otherFooterInfo}")
	private String otherFooterInfo;

	public Config() {

	}

}
