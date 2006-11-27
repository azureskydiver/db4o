package com.db4o.cs.client.protocol.protocol1;

import com.db4o.cs.client.util.ReflectHelper3;

import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

/**
 * User: treeder
 * Date: Nov 26, 2006
 * Time: 12:05:10 PM
 */
public class ClassMarshaller {
	/**
	 * Writes the following:
	 * <ol>
	 * <li>class name</li>
	 * <li>field count</li>
	 * <li>for each field:</li>
	 * <ol>
	 * <li>field name</li>
	 * <li>class name</li>
	 * </ol>
	 * </ol>
	 * @param oout
	 * @param aClass
	 * @throws IOException
	 */
	public void write(ObjectOutputStream oout, Class<? extends Object> aClass) throws IOException {
		oout.writeUTF(aClass.getName());
		// todo: should send class defs for entire heirarchy, starting at the top class
		List<Field> fields = ReflectHelper3.getDeclaredFieldsInHeirarchy(aClass);
		oout.writeShort(fields.size());
		for (int i = 0; i < fields.size(); i++) {
			Field f = fields.get(i);
			Class fClass = f.getType();
			// todo: if it's a user defined type, send this too, maybe even before this class so the server has it first
			oout.writeUTF(f.getName());
			oout.writeUTF(fClass.getName());

		}
	}
}
