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

import cn.taketoday.aop.Constant;
import cn.taketoday.aop.annotation.Annotated;
import cn.taketoday.aop.annotation.Argument;
import cn.taketoday.aop.annotation.Arguments;
import cn.taketoday.aop.annotation.JoinPoint;
import cn.taketoday.aop.annotation.Returning;
import cn.taketoday.aop.annotation.Throwing;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

import org.aopalliance.aop.Advice;
import org.aopalliance.intercept.Joinpoint;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Today <br>
 * 
 *         2018-11-10 11:26
 */
@Setter
@Getter
public abstract class AbstractAdvice implements Advice, MethodInterceptor {

	protected final Method adviceMethod;

	protected final Class<?>[] adviceParameterTypes;
	protected Object aspect;
	private Object annotation;
	private byte[] parameterTypes;
	private int parameterLength = 0;

	public AbstractAdvice(Method adviceMethod) {
		this.adviceParameterTypes = adviceMethod.getParameterTypes();
		this.adviceMethod = adviceMethod;
		this.parameterLength = adviceParameterTypes.length;
		this.parameterTypes = new byte[parameterLength];

		Parameter[] parameters = adviceMethod.getParameters();
		for (int i = 0; i < parameters.length; i++) {
			Parameter parameter = parameters[i];
			parameterTypes[i] = Constant.TYPE_NULL;
			if (parameter.isAnnotationPresent(JoinPoint.class)) {
				parameterTypes[i] = Constant.TYPE_JOIN_POINT;
			}
			if (parameter.isAnnotationPresent(Argument.class)) {
				parameterTypes[i] = Constant.TYPE_ARGUMENT;
			}
			if (parameter.isAnnotationPresent(Arguments.class)) {
				parameterTypes[i] = Constant.TYPE_ARGUMENTS;
			}
			if (parameter.isAnnotationPresent(Returning.class)) {
				parameterTypes[i] = Constant.TYPE_RETURNING;
			}
			if (parameter.isAnnotationPresent(Throwing.class)) {
				parameterTypes[i] = Constant.TYPE_THROWING;
			}
			if (parameter.isAnnotationPresent(Annotated.class)) {
				parameterTypes[i] = Constant.TYPE_ANNOTATED;
			}
		}
	}

	@Override
	public abstract Object invoke(MethodInvocation invocation) throws Throwable;

	/**
	 * 
	 * @param methodInvocation
	 * @param returnValue
	 * @param throwable
	 * @return
	 * @throws Throwable
	 */
	protected Object invokeAdviceMethod(//
			MethodInvocation methodInvocation, Object returnValue, Throwable throwable) throws Throwable //
	{
		try {

			if (parameterLength == 0) {
				return adviceMethod.invoke(aspect);
			}
			return adviceMethod.invoke(aspect, resolveParameter(methodInvocation, returnValue, throwable));
		} //
		catch (InvocationTargetException ex) {
			throw ex.getTargetException();
		}
	}

	/**
	 * 
	 * @param methodInvocation
	 *            the join point
	 * @param returnValue
	 *            the method returned value
	 * @param ex
	 *            the exception
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private final Object[] resolveParameter(MethodInvocation methodInvocation, Object returnValue, Throwable ex) {

		Object[] args = new Object[parameterLength];
		for (int i = 0; i < parameterLength; i++) {
			switch (parameterTypes[i])
			{
				case Constant.TYPE_THROWING : {
					args[i] = ex;
					break;
				}
				case Constant.TYPE_ARGUMENT : {
					if (parameterLength == 1) {
						args[i] = methodInvocation.getArguments()[0];
					}
					// for every argument matching
					for (Object argument : methodInvocation.getArguments()) {
						if (argument.getClass() == adviceParameterTypes[i]) {
							args[i] = argument;
						}
					}
					break;
				}
				case Constant.TYPE_ARGUMENTS :
					args[i] = methodInvocation.getArguments();
					break;
				case Constant.TYPE_RETURNING :
					args[i] = returnValue;
					break;
				case Constant.TYPE_ANNOTATED : {
					args[i] = resolveAnnotation(methodInvocation,
							(Class<? extends Annotation>) adviceParameterTypes[i]);
					break;
				}
				case Constant.TYPE_JOIN_POINT : {
					args[i] = methodInvocation;
					break;
				}
				default: {
					Class<?> parameterType = adviceParameterTypes[i];
					if (Joinpoint.class.isAssignableFrom(parameterType)) {
						args[i] = methodInvocation;
					}
					if (Throwable.class.isAssignableFrom(parameterType)) {
						args[i] = ex;
					}
					if (Annotation.class.isAssignableFrom(parameterType)) {
						args[i] = resolveAnnotation(methodInvocation, (Class<? extends Annotation>) parameterType);
					}
					break;
				}
			}
		}
		return args;
	}

	/**
	 * resolve an annotation
	 * 
	 * @param methodInvocation
	 *            the join point
	 * @param annotationClass
	 *            given annotation class
	 * @return
	 */
	private final Object resolveAnnotation(//
			MethodInvocation methodInvocation, Class<? extends Annotation> annotationClass) //
	{
		Method method = methodInvocation.getMethod();
		Annotation annotation = method.getAnnotation(annotationClass);
		if (annotation == null) {
			annotation = method.getDeclaringClass().getAnnotation(annotationClass);
		}
		return annotation;
	}

}
