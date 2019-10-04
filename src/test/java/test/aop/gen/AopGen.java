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
package test.aop.gen;

import static cn.taketoday.context.Constant.SOURCE_FILE;
import static cn.taketoday.context.asm.Opcodes.ACC_PUBLIC;
import static cn.taketoday.context.asm.Opcodes.JAVA_VERSION;
import static cn.taketoday.context.asm.Type.array;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.ProtectionDomain;

import cn.taketoday.context.ApplicationContext;
import cn.taketoday.context.Constant;
import cn.taketoday.context.StandardApplicationContext;
import cn.taketoday.context.asm.ClassVisitor;
import cn.taketoday.context.asm.Type;
import cn.taketoday.context.cglib.core.AbstractClassGenerator;
import cn.taketoday.context.cglib.core.ClassEmitter;
import cn.taketoday.context.cglib.core.CodeEmitter;
import cn.taketoday.context.cglib.core.EmitUtils;
import cn.taketoday.context.cglib.core.KeyFactory;
import cn.taketoday.context.cglib.core.MethodInfo;
import cn.taketoday.context.cglib.core.ReflectUtils;
import cn.taketoday.context.cglib.core.Signature;
import cn.taketoday.context.cglib.core.TypeUtils;
import cn.taketoday.context.utils.ClassUtils;
import cn.taketoday.context.utils.ContextUtils;
import cn.taketoday.context.utils.ObjectUtils;
import test.demo.domain.User;
import test.demo.service.UserService;

/**
 * @author TODAY <br>
 *         2019-09-04 13:50
 */
public class AopGen {

//    static {
//        System.setProperty(DebuggingClassWriter.DEBUG_LOCATION_PROPERTY, "D:/debug");
//    }

    public static void main(String[] args) {

        try (ApplicationContext applicationContext = new StandardApplicationContext("", "")) {

            UserService userService = applicationContext.getBean(UserService.class);
            User user = new User();
            user.setPasswd("666");

            user.setUserId("666");
            User login = userService.login(user);

            Generator<UserService> g = new Generator<>();

            g.setTarget(userService);
            final UserService create = g.create();

            System.err.println(create);

            System.err.println(ClassUtils.getUserClass(create).equals(ClassUtils.getUserClass(userService)));
            System.err.println(ClassUtils.getUserClass(create));

            System.err.println(userService.login(login));
            System.err.println(create.login(login));
        }
    }

    private static final AopGenKey KEY_FACTORY = //
            (AopGenKey) KeyFactory.create(AopGenKey.class, KeyFactory.CLASS_BY_NAME);

    interface AopGenKey {
        Object newInstance(Class<?> superClass);
    }

    public static class Generator<T> extends AbstractClassGenerator<T> {

        private static final Source SOURCE = new Source(AopGen.class.getSimpleName());

        private T target;
        private Class<T> targetClass;
        private Class<?>[] parameterTypes;
        private Constructor<T> targetConstructor;

        public Generator() {
            super(SOURCE);
        }

        @SuppressWarnings("unchecked")
        public void setTarget(T target) {
            this.target = target;
            setTargetClass((Class<T>) target.getClass());
        }

        public void setTargetClass(Class<T> targetClass) {
            this.targetClass = ClassUtils.getUserClass(targetClass);
        }

        @Override
        protected ClassLoader getDefaultClassLoader() {
            return targetClass.getClassLoader();
        }

        @Override
        protected ProtectionDomain getProtectionDomain() {
            return ReflectUtils.getProtectionDomain(targetClass);
        }

        @SuppressWarnings("unchecked")
        public T create() {
            setNamePrefix(targetClass.getName());
            Object key = KEY_FACTORY.newInstance(targetClass);
            return (T) super.create(key);
        }

        @Override
        protected Object firstInstance(Class<T> type) {

            if (ObjectUtils.isEmpty(parameterTypes)) {
                return ReflectUtils.newInstance(type, new Class[] { targetClass }, new Object[] { target });
            }
            Class<?>[] types = this.parameterTypes;
            final Class<?>[] copy = new Class[types.length + 1];
            System.arraycopy(types, 0, copy, 0, types.length);
            copy[types.length] = targetClass;

            final Object[] arg = ContextUtils.resolveParameter(targetConstructor, ContextUtils.getApplicationContext());
            Object[] args = new Object[parameterTypes.length + 1];
            System.arraycopy(arg, 0, args, 0, arg.length);
            args[types.length] = target;

            return ReflectUtils.newInstance(type, copy, args);
        }

        @Override
        protected Object nextInstance(Object instance) {
            return instance;
        }

        @Override
        public void generateClass(ClassVisitor v) throws NoSuchMethodException {

            final ClassEmitter ce = new ClassEmitter(v);

            final Type targetType = TypeUtils.parseType(targetClass);

            ce.beginClass(JAVA_VERSION, ACC_PUBLIC, getClassName(), targetType,
                          array(TypeUtils.getTypes(targetClass.getInterfaces())), SOURCE_FILE);

            ce.declare_field(Constant.ACC_PRIVATE | Constant.ACC_FINAL, "target", targetType, null);

            targetConstructor = ClassUtils.obtainConstructor(targetClass);

            // 父类构造器参数
            final Type[] types = TypeUtils.getTypes(parameterTypes = targetConstructor.getParameterTypes());
            {// 构造器

                final Type[] add = TypeUtils.add(types, targetType, true); // 子类构造器参数
                final Signature parseConstructor = TypeUtils.parseConstructor(add);

                final CodeEmitter cone = ce.beginMethod(ACC_PUBLIC, parseConstructor);

                cone.load_this();

                cone.dup();

                final int length = types.length;
                if (length > 0) {
                    cone.load_args(0, length);
                }
                cone.super_invoke_constructor(TypeUtils.parseConstructor(types));

                cone.load_arg(length);
                cone.putfield("target");

                cone.return_value();
                cone.end_method();
            }

            for (Method method : targetClass.getDeclaredMethods()) {

                final int modifiers = method.getModifiers();

                if ((!Modifier.isProtected(modifiers) && !Modifier.isPublic(modifiers)) || Modifier.isFinal(
                                                                                                            modifiers)) {
                    continue;
                }

                final MethodInfo methodInfo = ReflectUtils.getMethodInfo(method);

                final boolean isStatic = Modifier.isStatic(modifiers);

                if ((target == null) ^ isStatic) {
                    throw new IllegalArgumentException("Static method " + (isStatic ? "not " : Constant.BLANK)
                            + "expected");
                }

                final CodeEmitter codeEmitter = EmitUtils.beginMethod(ce, methodInfo, modifiers);

                codeEmitter.load_this();
                codeEmitter.getfield(ce.getClassInfo().getType(), "target", targetType);

                codeEmitter.load_args();
                codeEmitter.invoke(methodInfo);

                codeEmitter.return_value();
                codeEmitter.end_method();
            }

            ce.endClass();
        }

    }
}
