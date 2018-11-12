/*
 * Copyright 2003,2004 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cn.taketoday.aop.cglib.core;

import cn.taketoday.aop.Constant;
import cn.taketoday.context.asm.ClassReader;
import cn.taketoday.context.asm.ClassVisitor;
import cn.taketoday.context.asm.ClassWriter;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.security.AccessController;
import java.security.PrivilegedAction;

@SuppressWarnings("all")
public class DebuggingClassWriter extends ClassVisitor {

	public static final String DEBUG_LOCATION_PROPERTY = "cglib.debugLocation";

	private static String debugLocation = "D:/debug";
	private static Constructor traceCtor;

	private String className;
	private String superName;

	static {
		debugLocation = System.getProperty(DEBUG_LOCATION_PROPERTY);
		if (debugLocation != null) {
			System.err.println("CGLIB debugging enabled, writing to '" + debugLocation + "'");
			try {
				Class clazz = Class.forName("cn.taketoday.aop.asm.util.TraceClassVisitor");
				traceCtor = clazz.getConstructor(new Class[] { ClassVisitor.class, PrintWriter.class });
			} catch (Throwable ignore) {
			}
		}
	}

	public DebuggingClassWriter(int flags) {
		super(Constant.ASM_API, new ClassWriter(flags));
	}

	public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
		className = name.replace('/', '.');
		this.superName = superName.replace('/', '.');
		super.visit(version, access, name, signature, superName, interfaces);
	}

	public String getClassName() {
		return className;
	}

	public String getSuperName() {
		return superName;
	}

	public byte[] toByteArray() {

		return (byte[]) AccessController.doPrivileged(new PrivilegedAction() {
			public Object run() {
				byte[] b = ((ClassWriter) DebuggingClassWriter.super.cv).toByteArray();
				if (debugLocation != null) {
					String dirs = className.replace('.', File.separatorChar);
					try {
						new File(debugLocation + File.separatorChar + dirs).getParentFile().mkdirs();

						File file = new File(new File(debugLocation), dirs + ".class");
						OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
						try {
							out.write(b);
						} finally {
							out.close();
						}

						if (traceCtor != null) {
							file = new File(new File(debugLocation), dirs + ".asm");
							out = new BufferedOutputStream(new FileOutputStream(file));
							try {
								ClassReader cr = new ClassReader(b);
								PrintWriter pw = new PrintWriter(new OutputStreamWriter(out));
								ClassVisitor tcv = (ClassVisitor) traceCtor.newInstance(new Object[] { null, pw });
								cr.accept(tcv, 0);
								pw.flush();
							} finally {
								out.close();
							}
						}
					} catch (Exception e) {
						throw new CodeGenerationException(e);
					}
				}
				return b;
			}
		});

	}
}
