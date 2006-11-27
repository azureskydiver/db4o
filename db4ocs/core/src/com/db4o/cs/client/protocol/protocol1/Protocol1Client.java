package com.db4o.cs.client.protocol.protocol1;

import com.db4o.cs.client.protocol.ClientProtocol;
import com.db4o.cs.client.protocol.objectStream.ObjectStreamProtocolClient;
import com.db4o.cs.client.util.ReflectHelper3;
import com.db4o.cs.common.Operations;
import com.db4o.cs.common.ClassMetaData;
import com.db4o.cs.common.FieldMetaData;
import com.db4o.query.Query;

import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Field;

/**
 * User: treeder
 * Date: Nov 25, 2006
 * Time: 7:47:33 PM
 */
public class Protocol1Client extends ObjectStreamProtocolClient implements ClientProtocol {

	ClassMarshaller classMarshaller = new ClassMarshaller();
	ObjectMarshaller objectMarshaller = new ObjectMarshaller();
	Map<String,ClassMetaData> classesSent = new HashMap();
	
	public Protocol1Client(OutputStream out, InputStream in) throws IOException {
		super(out, in);
	}

	// todo: should have a single base protocol for protocol1 and objectStream that delegates marshalling
	public void set(Object o) throws IOException {
		// going to try an optimization here where it will send class information on first set of a connection only
		ClassMetaData classMetaData = sendClassMetadata(oout, o);

		oout.writeByte(Operations.SET);
		Long id = getIdForObject(o);
		oout.writeLong(id.longValue());
		try {
			objectMarshaller.write(classMetaData, oout, o);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Illegal Access Exception trying to read a field.", e);
		}
		oout.flush();
	}

	private ClassMetaData sendClassMetadata(ObjectOutputStream oout, Object o) throws IOException {
		// check if it's already been sent so we only send it once
		Class clazz = o.getClass();
		ClassMetaData classMetaData  = (ClassMetaData) classesSent.get(clazz.getName());
		if(classMetaData == null){
			classMetaData = createClassMetaData(clazz);
			oout.writeByte(Operations.CLASS_METADATA);
			classMarshaller.write(oout, clazz);
			classesSent.put(clazz.getName(), classMetaData);
		}
		return classMetaData;
	}

	private ClassMetaData createClassMetaData(Class clazz) {
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
			classMetaData.addField(md);
		}
		return classMetaData;
	}


	public List execute(Query query) throws IOException, ClassNotFoundException {
		return super.execute(query);
	}


}
