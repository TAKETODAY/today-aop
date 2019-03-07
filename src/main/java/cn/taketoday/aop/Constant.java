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
package cn.taketoday.aop;

import cn.taketoday.aop.cglib.core.Signature;
import cn.taketoday.aop.cglib.core.TypeUtils;
import cn.taketoday.context.asm.Opcodes;
import cn.taketoday.context.asm.Type;

/**
 * @author Juozas Baliuka <a href="mailto:baliuka@mwm.lt">baliuka@mwm.lt</a>
 * @author Today <br>
 *         2018-11-10 16:46
 */
public interface Constant extends cn.taketoday.context.Constant, Opcodes {

	/* Indicates the ASM API version that is used throughout cglib */
//	int			ASM_API					= ASM7;

	Class<?>[]	EMPTY_CLASS_ARRAY		= {};
	Type[]		TYPES_EMPTY				= {};

	Signature	SIG_STATIC				= TypeUtils.parseSignature("void <clinit>()");

	Type		TYPE_OBJECT_ARRAY		= TypeUtils.parseType("Object[]");
	Type		TYPE_CLASS_ARRAY		= TypeUtils.parseType("Class[]");
	Type		TYPE_STRING_ARRAY		= TypeUtils.parseType("String[]");

	Type		TYPE_TYPE				= Type.getType(Type.class);
	Type		TYPE_ERROR				= TypeUtils.parseType("Error");
	Type		TYPE_SYSTEM				= TypeUtils.parseType("System");
	Type		TYPE_LONG				= TypeUtils.parseType("Long");
	Type		TYPE_BYTE				= TypeUtils.parseType("Byte");
	Type		TYPE_CLASS				= TypeUtils.parseType("Class");
	Type		TYPE_FLOAT				= TypeUtils.parseType("Float");
	Type		TYPE_SHORT				= TypeUtils.parseType("Short");
	Type		TYPE_OBJECT				= TypeUtils.parseType("Object");
	Type		TYPE_DOUBLE				= TypeUtils.parseType("Double");
	Type		TYPE_STRING				= TypeUtils.parseType("String");
	Type		TYPE_NUMBER				= TypeUtils.parseType("Number");
	Type		TYPE_BOOLEAN			= TypeUtils.parseType("Boolean");
	Type		TYPE_INTEGER			= TypeUtils.parseType("Integer");
	Type		TYPE_CHARACTER			= TypeUtils.parseType("Character");
	Type		TYPE_THROWABLE			= TypeUtils.parseType("Throwable");
	Type		TYPE_CLASS_LOADER		= TypeUtils.parseType("ClassLoader");
	Type		TYPE_STRING_BUFFER		= TypeUtils.parseType("StringBuffer");
	Type		TYPE_BIG_INTEGER		= TypeUtils.parseType("java.math.BigInteger");
	Type		TYPE_BIG_DECIMAL		= TypeUtils.parseType("java.math.BigDecimal");
	Type		TYPE_RUNTIME_EXCEPTION	= TypeUtils.parseType("RuntimeException");
	Type		TYPE_SIGNATURE			= TypeUtils.parseType("cn.taketoday.aop.cglib.core.Signature");

	String		CONSTRUCTOR_NAME		= "<init>";
	String		STATIC_NAME				= "<clinit>";
	String		SOURCE_FILE				= "<generated>";
	String		SUID_FIELD_NAME			= "serialVersionUID";

	int			PRIVATE_FINAL_STATIC	= ACC_PRIVATE | ACC_FINAL | ACC_STATIC;

	int			SWITCH_STYLE_TRIE		= 0;
	int			SWITCH_STYLE_HASH		= 1;
	int			SWITCH_STYLE_HASHONLY	= 2;
	
	/*************************************************
	 * Parameter Types
	 */
	byte 		TYPE_NULL				= 0x00;
	byte 		TYPE_THROWING			= 0x01;
	byte 		TYPE_ARGUMENT			= 0x02;
	byte 		TYPE_ARGUMENTS			= 0x03;
	byte 		TYPE_RETURNING			= 0x04;
	byte 		TYPE_ANNOTATED			= 0x05;
	byte 		TYPE_JOIN_POINT			= 0x06;
	
}
