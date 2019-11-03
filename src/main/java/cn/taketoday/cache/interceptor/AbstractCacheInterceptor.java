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
package cn.taketoday.cache.interceptor;

import java.io.IOException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

import javax.el.ELManager;
import javax.el.ExpressionFactory;
import javax.el.StandardELContext;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import cn.taketoday.cache.Cache;
import cn.taketoday.cache.CacheELContext;
import cn.taketoday.cache.CacheManager;
import cn.taketoday.cache.DefaultCacheKey;
import cn.taketoday.cache.NoSuchCacheException;
import cn.taketoday.cache.annotation.CacheConfig;
import cn.taketoday.cache.annotation.CacheConfiguration;
import cn.taketoday.context.AnnotationAttributes;
import cn.taketoday.context.ApplicationContext;
import cn.taketoday.context.exception.ConfigurationException;
import cn.taketoday.context.factory.InitializingBean;
import cn.taketoday.context.utils.ClassUtils;
import cn.taketoday.context.utils.ContextUtils;
import cn.taketoday.context.utils.StringUtils;

/**
 * @author TODAY <br>
 *         2019-02-27 19:03
 */
public abstract class AbstractCacheInterceptor extends CacheOperations implements MethodInterceptor, InitializingBean {

    private CacheManager cacheManager;

    public AbstractCacheInterceptor() {

    }

    public AbstractCacheInterceptor(CacheManager cacheManager) {
        setCacheManager(cacheManager);
    }

    public void setCacheManager(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    public final CacheManager getCacheManager() {
        return cacheManager;
    }

    /**
     * Prepare {@link Cache} name
     * 
     * @param method
     *            Target method
     * @param cacheName
     *            {@link CacheConfig#cacheName()}
     * @return A not empty cache name
     */
    protected String prepareCacheName(final Method method, final String cacheName) {
        // if cache name is empty use declaring class full name
        if (cacheName.isEmpty()) {
            return method.getDeclaringClass().getName();
        }
        return cacheName;
    }

    protected Cache getCache(final String name, final CacheConfig cacheConfig) {
        return getCacheManager().getCache(name, cacheConfig);
    }

    /**
     * Obtain a Target method's {@link Cache} object
     * 
     * @param method
     *            Target method
     * @param cacheConfig
     *            {@link CacheConfig}
     * @return {@link Cache}
     * @throws NoSuchCacheException
     *             If there isn't a {@link Cache}
     */
    protected final Cache obtainCache(final Method method, final CacheConfig cacheConfig) throws NoSuchCacheException {
        final String name = prepareCacheName(method, cacheConfig.cacheName());
        final Cache cache = getCache(name, cacheConfig);
        if (cache == null) {
            throw new NoSuchCacheException(name);
        }
        return cache;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        final ApplicationContext applicationContext = ContextUtils.getApplicationContext();
        if (getCacheManager() == null) {
            setCacheManager(applicationContext.getBean(CacheManager.class));
        }
        if (getCacheManager() == null) {
            throw new ConfigurationException("You must provide a 'CacheManager'");
        }

        if (getExceptionResolver() == null) {
            setExceptionResolver(applicationContext.getBean(CacheExceptionResolver.class));
        }
    }

    // ExpressionOperations
    //-----------------------------------------------------

    final static class Operations {

        protected static final String KEY_ROOT = "root";
        protected static final String KEY_RESULT = "result";

        private static final StandardELContext SHARED_EL_CONTEXT = //
                ContextUtils.getApplicationContext()
                        .getEnvironment()
                        .getELProcessor()
                        .getELManager()
                        .getELContext();
        private static final ExpressionFactory EXPRESSION_FACTORY = ELManager.getExpressionFactory();
        private static final ConcurrentMap<MethodKey, String[]> METHOD_NAMES_CACHE = new ConcurrentHashMap<>(128);
        private static final Function<MethodKey, String[]> METHOD_NAMES_FUNCTION = new Function<MethodKey, String[]>() {
            @Override
            public String[] apply(MethodKey t) {
                return ClassUtils.getMethodArgsNames(t.targetMethod);
            }
        };

        private static final ConcurrentMap<MethodKey, CacheConfiguration> CACHE_OPERATION = new ConcurrentHashMap<>(128);
        private static final Function<MethodKey, CacheConfiguration> CACHE_OPERATION_FUNCTION = new Function<MethodKey, CacheConfiguration>() {

            @Override
            public CacheConfiguration apply(MethodKey t) {

                final Method method = t.targetMethod;
                final Class<? extends Annotation> annClass = t.annotationClass;

                // Find target method [annClass] AnnotationAttributes
                AnnotationAttributes attributes = ClassUtils.getAnnotationAttributes(annClass, method);
                final Class<?> declaringClass = method.getDeclaringClass();
                if (attributes == null) {
                    attributes = ClassUtils.getAnnotationAttributes(annClass, declaringClass);
                    if (attributes == null) {
                        throw new IllegalStateException("Unexpected exception has occurred, may be it's a bug");
                    }
                }

                final CacheConfiguration configuration = //
                        ClassUtils.injectAttributes(attributes, annClass, new CacheConfiguration(annClass));

                final CacheConfig cacheConfig = ClassUtils.getAnnotation(CacheConfig.class, declaringClass);
                if (cacheConfig != null) {
                    configuration.mergeCacheConfigAttributes(cacheConfig);
                }
                return configuration;
            }
        };

        // methods
        //------------------------------------------

        /**
         * Resolve {@link Annotation} from given {@link Annotation} {@link Class}
         * 
         * @param method
         *            target method
         * @param a
         *            {@link Annotation} {@link Class}
         * @return {@link Annotation} instance
         */
        static <A extends Annotation> CacheConfiguration prepareAnnotation(final MethodKey methodKey) {
            return CACHE_OPERATION.computeIfAbsent(methodKey, CACHE_OPERATION_FUNCTION);
        }

        /**
         * @param key
         * @param context
         * @param invocation
         * @return
         */
        static Object createKey(final String key, final CacheELContext context, final MethodInvocation invocation) {

            return key.isEmpty()
                    ? new DefaultCacheKey(invocation.getArguments())
                    : EXPRESSION_FACTORY.createValueExpression(context, key, Object.class).getValue(context);
        }

        /**
         * @param condition
         * @param context
         * @return
         */
        static boolean isConditionPassing(final String condition, final CacheELContext context) {

            if (StringUtils.isEmpty(condition)) { //if its empty returns true
                return true;
            }
            return (Boolean) EXPRESSION_FACTORY.createValueExpression(context, condition, Boolean.class).getValue(context);
        }

        /**
         * @param unless
         * @param result
         * @param context
         */
        static boolean allowPutCache(final String unless, final Object result, final CacheELContext context) {

            if (StringUtils.isNotEmpty(unless)) {
                context.putBean(KEY_RESULT, result);
                return !(Boolean) EXPRESSION_FACTORY.createValueExpression(context, unless, Boolean.class).getValue(context);
            }
            return true;
        }

        /**
         * Prepare parameter names
         * 
         * @param beans
         *            The mapping
         * @param arguments
         *            Target {@link Method} parameters
         * @throws IOException
         *             When asm tool can't access to the class file
         */
        static void prepareParameterNames(final MethodKey methodKey,
                                          final Object[] arguments,
                                          final Map<String, Object> beans) //
        {
            final String[] names = METHOD_NAMES_CACHE.computeIfAbsent(methodKey, METHOD_NAMES_FUNCTION);
            for (int i = 0; i < names.length; i++) {
                beans.put(names[i], arguments[i]);
            }
        }

        static CacheELContext prepareELContext(final MethodKey methodKey, final MethodInvocation invocation) {
            final Map<String, Object> beans = new HashMap<>();
            prepareParameterNames(methodKey, invocation.getArguments(), beans);
            beans.put(KEY_ROOT, invocation);// ${root.target} for target instance ${root.method}
            return new CacheELContext(SHARED_EL_CONTEXT, beans);
        }

    }

    // MethodKey
    // -----------------------------

    static final class MethodKey implements Serializable {

        private static final long serialVersionUID = 1L;

        private final int hash;
        private final Method targetMethod;
        private final Class<? extends Annotation> annotationClass;

        public MethodKey(Method targetMethod, Class<? extends Annotation> annotationClass) {
            this.targetMethod = targetMethod;
            this.hash = targetMethod.hashCode();
            this.annotationClass = annotationClass;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj instanceof MethodKey) {
                final MethodKey other = (MethodKey) obj;
                return other.annotationClass == annotationClass && other.targetMethod.equals(this.targetMethod);
            }
            return false;
        }

        @Override
        public int hashCode() {
            return this.hash;
        }
    }

}
