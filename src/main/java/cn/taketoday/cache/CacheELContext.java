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
package cn.taketoday.cache;

import java.util.HashMap;
import java.util.Map;

import cn.taketoday.expression.BeanNameELResolver;
import cn.taketoday.expression.CompositeELResolver;
import cn.taketoday.expression.ELContext;
import cn.taketoday.expression.ELResolver;
import cn.taketoday.expression.EvaluationListener;
import cn.taketoday.expression.FunctionMapper;
import cn.taketoday.expression.StandardELContext;
import cn.taketoday.expression.VariableMapper;
import cn.taketoday.expression.lang.LocalBeanNameResolver;

/**
 * 
 * @author TODAY <br>
 *         2019-02-17 20:40
 */
public class CacheELContext extends ELContext {

    private ELResolver elResolver;
    private final Map<String, Object> beans;
    private final StandardELContext delegate;

    public CacheELContext(StandardELContext delegate) {
        this(delegate, new HashMap<>(8, 1.0f));
    }

    public CacheELContext(StandardELContext delegate, Map<String, Object> beans) {
        this.beans = beans;
        this.delegate = delegate;
    }

    @Override
    public ELResolver getELResolver() {

        if (elResolver == null) {
            ELResolver elResolver = delegate.getELResolver();
            CompositeELResolver resolver = new CompositeELResolver(2);

            resolver.add(new BeanNameELResolver(new LocalBeanNameResolver(beans)));
            resolver.add(elResolver);
            this.elResolver = resolver;
        }
        return elResolver;
    }

    /**
     * Add a bean to this context
     * 
     * @param name
     *            bean name
     * @param bean
     *            bean instance
     */
    public void putBean(final String name, final Object bean) {
        beans.put(name, bean);
    }

    @Override
    public FunctionMapper getFunctionMapper() {
        return this.delegate.getFunctionMapper();
    }

    @Override
    public VariableMapper getVariableMapper() {
        return this.delegate.getVariableMapper();
    }

    @Override
    public void setPropertyResolved(Object base, Object property) {
        setPropertyResolved(true);
    }

    @Override
    public void addEvaluationListener(EvaluationListener listener) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void notifyAfterEvaluation(String expr) {}

    @Override
    public void notifyBeforeEvaluation(String expr) {}

    @Override
    public void notifyPropertyResolved(Object base, Object property) {}

}
