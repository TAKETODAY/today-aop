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
package cn.taketoday.cache;

import cn.taketoday.context.utils.ConcurrentCache;

/**
 * @author TODAY <br>
 *         2019-12-17 12:29
 */
public class ConcurrentMapCache extends AbstractCache {

    private final ConcurrentCache<Object, Object> store;

    public ConcurrentMapCache(String name) {
        this(name, new ConcurrentCache<>(256));
    }

    protected ConcurrentMapCache(String name, ConcurrentCache<Object, Object> store) {
        this.setName(name);
        this.store = store;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, CacheCallback<T> valueLoader) throws CacheValueRetrievalException {
        return (T) store.get(key, k -> lookupValue(k, valueLoader));
    }

    @Override
    public void evict(Object key) {
        store.remove(key);
    }

    @Override
    public void clear() {
        store.clear();
    }

    @Override
    protected Object lookupValue(Object key) {
        return store.get(key);
    }

    @Override
    protected void putInternal(Object key, Object value) {
        store.put(key, value);
    }

}
