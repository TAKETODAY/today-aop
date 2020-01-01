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
package cn.taketoday.cache;

import java.io.Serializable;
import java.util.Arrays;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author TODAY <br>
 *         2019-02-27 18:12
 */
@Setter
@Getter
public class DefaultCacheKey implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final DefaultCacheKey EMPTY = new DefaultCacheKey(Constant.EMPTY_OBJECT);

    private final int hash;
    private final Object[] params;

    /**
     * Create a new {@link DefaultCacheKey} instance.
     * 
     * @param params
     *            the method parameters
     */
    public DefaultCacheKey(Object... params) {
        if (params == null) {
            this.params = EMPTY.params;
        }
        else {
            this.params = new Object[params.length];
            System.arraycopy(params, 0, this.params, 0, params.length);
        }
        this.hash = Arrays.deepHashCode(this.params);
    }

    @Override
    public boolean equals(Object other) {
        return (this == other //
                || (other instanceof DefaultCacheKey && Arrays.deepEquals(this.params, ((DefaultCacheKey) other).params))//
        );
    }

    @Override
    public final int hashCode() {
        return this.hash;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + " " + Arrays.deepToString(this.params);
    }
}
