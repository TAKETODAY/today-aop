/*
 * Copyright 2003 The Apache Software Foundation
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
package cn.taketoday.aop.cglib.util;

import cn.taketoday.aop.Constant;
import cn.taketoday.aop.cglib.core.ClassEmitter;
import cn.taketoday.aop.cglib.core.CodeEmitter;
import cn.taketoday.aop.cglib.core.EmitUtils;
import cn.taketoday.aop.cglib.core.Local;
import cn.taketoday.aop.cglib.core.Signature;
import cn.taketoday.aop.cglib.core.TypeUtils;
import cn.taketoday.context.asm.ClassVisitor;
import cn.taketoday.context.asm.Type;

class ParallelSorterEmitter extends ClassEmitter {
	private static final Type PARALLEL_SORTER = TypeUtils.parseType("cn.taketoday.aop.cglib.util.ParallelSorter");
	private static final Signature CSTRUCT_OBJECT_ARRAY = TypeUtils.parseConstructor("Object[]");
	private static final Signature NEW_INSTANCE = new Signature("newInstance", PARALLEL_SORTER,
			new Type[] { Constant.TYPE_OBJECT_ARRAY });
	private static final Signature SWAP = TypeUtils.parseSignature("void swap(int, int)");

	public ParallelSorterEmitter(ClassVisitor v, String className, Object[] arrays) {
		super(v);
		begin_class(Constant.V1_2, Constant.ACC_PUBLIC, className, PARALLEL_SORTER, null, Constant.SOURCE_FILE);
		EmitUtils.null_constructor(this);
		EmitUtils.factory_method(this, NEW_INSTANCE);
		generateConstructor(arrays);
		generateSwap(arrays);
		end_class();
	}

	private String getFieldName(int index) {
		return "FIELD_" + index;
	}

	private void generateConstructor(Object[] arrays) {
		CodeEmitter e = begin_method(Constant.ACC_PUBLIC, CSTRUCT_OBJECT_ARRAY, null);
		e.load_this();
		e.super_invoke_constructor();
		e.load_this();
		e.load_arg(0);
		e.super_putfield("a", Constant.TYPE_OBJECT_ARRAY);
		for (int i = 0; i < arrays.length; i++) {
			Type type = Type.getType(arrays[i].getClass());
			declare_field(Constant.ACC_PRIVATE, getFieldName(i), type, null);
			e.load_this();
			e.load_arg(0);
			e.push(i);
			e.aaload();
			e.checkcast(type);
			e.putfield(getFieldName(i));
		}
		e.return_value();
		e.end_method();
	}

	private void generateSwap(final Object[] arrays) {
		CodeEmitter e = begin_method(Constant.ACC_PUBLIC, SWAP, null);
		for (int i = 0; i < arrays.length; i++) {
			Type type = Type.getType(arrays[i].getClass());
			Type component = TypeUtils.getComponentType(type);
			Local T = e.make_local(type);

			e.load_this();
			e.getfield(getFieldName(i));
			e.store_local(T);

			e.load_local(T);
			e.load_arg(0);

			e.load_local(T);
			e.load_arg(1);
			e.array_load(component);

			e.load_local(T);
			e.load_arg(1);

			e.load_local(T);
			e.load_arg(0);
			e.array_load(component);

			e.array_store(component);
			e.array_store(component);
		}
		e.return_value();
		e.end_method();
	}
}
