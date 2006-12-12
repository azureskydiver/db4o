package com.db4o.cs.client.protocol.protocol1;

import com.db4o.cs.client.util.ReflectHelper3;
import com.db4o.cs.client.ClientContext;
import com.db4o.cs.common.ClassMetaData;
import com.db4o.cs.common.FieldMetaData;
import com.db4o.cs.common.Operations;
import com.db4o.cs.common.util.Log;

import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

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
	 *
	 * @param aClass	  @throws IOException
	 */
	public ClassMetaData write(ClientContext context, ObjectOutputStream oout, Class aClass) throws IOException {
		ClassMetaData classMetaData = context.getClassMetaData(aClass.getName());
		if (classMetaData != null) return classMetaData; // so it's only sent once
		Log.print("sending class data for: " + aClass.getName());
		classMetaData = createClassMetaData(aClass);
		context.setClassMetaData(aClass.getName(), classMetaData);
		oout.writeByte(Operations.CLASS_METADATA);
		oout.writeUTF(aClass.getName());
		List<Field> fields = classMetaData.getReflectionFields();
		List<Field> sendAfter = new ArrayList<Field>();
		oout.writeShort(fields.size());
		for (int i = 0; i < fields.size(); i++) {
			Field f = fields.get(i);
			Class fClass = f.getType();
			if (ReflectHelper3.isSecondClass(fClass) || ReflectHelper3.isCollection(fClass)
					|| ReflectHelper3.isMap(fClass)) {
				// todo: if it's a user defined type, send this too, maybe even before this class so the server has it first
				oout.writeUTF(f.getName());
				oout.writeUTF(fClass.getName());
			} else {
				oout.writeUTF(f.getName());
				oout.writeUTF(fClass.getName());
				sendAfter.add(f);
			}
		}
		oout.flush();
		for (int i = 0; i < sendAfter.size(); i++) {
			Field field = sendAfter.get(i);
			write(context, oout, field.getType());
		}
		return classMetaData;
	}

	private ClassMetaData createClassMetaData(Class clazz) {
		Log.print("create metadata");
		ClassMetaData classMetaData = new ClassMetaData();
		classMetaData.setClassName(clazz.getName());
		List<Field> fields = ReflectHelper3.getDeclaredFieldsInHeirarchy(clazz);
		classMetaData.setReflectionFields(fields);
		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);
			field.setAccessible(true);
			FieldMetaData md = new FieldMetaData();
			md.setFieldName(field.getName());
			md.setClassName(field.getType().getName());
			Log.print(i + " field: " + field.getName() + " - type:" + field.getType().getName());
			classMetaData.addField(md);
		}
		return classMetaData;
	}

}
