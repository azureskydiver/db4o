package com.db4o.cs.client.protocol.protocol1;

import com.db4o.cs.common.ClassMetaData;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;

/**
 * The Class Definition is already sent to the server at this point, so we only need to send values and nothing else.
 *
 * User: treeder
 * Date: Nov 26, 2006
 * Time: 11:48:54 AM
 */
public class ObjectMarshaller {

	public void write(ClassMetaData classMetaData, ObjectOutputStream oout, Object o) throws IOException, IllegalAccessException {
		oout.writeUTF(o.getClass().getName()); // the class definition will already be on server
		List<Field> fields = classMetaData.getReflectionFields();
		for (int i = 0; i < fields.size(); i++) {
			Field field = fields.get(i);
			// already done earlier - field.setAccessible(true);
			Object value = field.get(o);
			// todo: descend down object graph, assuming simple objects for now
			oout.writeObject(value);
		}

	}
}
