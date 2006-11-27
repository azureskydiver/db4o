package com.db4o.cs.server.protocol.protocol1;

import com.db4o.cs.server.protocol.OperationHandler;
import com.db4o.cs.server.Context;
import com.db4o.cs.server.Session;
import com.db4o.cs.common.ClassMetaData;
import com.db4o.cs.common.FieldMetaData;
import com.db4o.cs.server.ClassMetaDataServer;
import com.db4o.cs.server.util.GenericObjectHelper;
import com.db4o.ObjectContainer;
import com.db4o.reflect.ReflectClass;
import com.db4o.reflect.ReflectField;
import com.db4o.reflect.generic.GenericClass;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * User: treeder
 * Date: Nov 26, 2006
 * Time: 12:32:58 PM
 */
public class ClassMetaDataOperationHandler implements OperationHandler {
	public void handle(Context context, Session session, ObjectInputStream oin, ObjectOutputStream oout) throws IOException, ClassNotFoundException {
		String className = oin.readUTF();
		short fieldCount = oin.readShort();

		ClassMetaDataServer classMetaData = new ClassMetaDataServer();
		//System.out.println("got class metadata: " + className);
		classMetaData.setClassName(className);
		for(int i = 0; i < fieldCount; i++){
			String fieldName = oin.readUTF();
			String fieldClass = oin.readUTF();
			//System.out.println("field:" + fieldName + " type:" + fieldClass);
			FieldMetaData fmd = new FieldMetaData();
			fmd.setFieldName(fieldName);
			fmd.setClassName(fieldClass);
			classMetaData.addField(fmd);
		}

		// check if already cached. Still need to swallow the stream bytes above so that's why the check is here.
		ClassMetaData cached = context.getClassMetaData(className);
		if(cached != null) return;

		// todo: evolve classes

		// cache all the Reflection stuff we'll need later, save some milliseconds
		// get ReflectClass for the object
		ObjectContainer oc = session.getObjectContainer(context);
		ReflectClass genericClass = oc.ext().reflector().forName(className);
		if(genericClass == null){
			genericClass = GenericObjectHelper.createGenericClass(classMetaData);
			oc.ext().reflector().register((GenericClass) genericClass); // does this work?
			System.out.println("Created one time GenericClass for " + className);
			// this is a workaround for a bug in db4o, supposedly fixed now
			//ReflectClass[] classes = oc.ext().reflector().knownClasses();
		}
		classMetaData.setReflectClass(genericClass);
		// now cache ReflectField's
		List fields = classMetaData.getFields();
		for (int i = 0; i < fields.size(); i++) {
			FieldMetaData fieldMetaData = (FieldMetaData) fields.get(i);
			ReflectField reflectField = genericClass.getDeclaredField(fieldMetaData.getFieldName());
			reflectField.setAccessible();
			classMetaData.addReflectField(reflectField);
		}

		context.addClassMetaData(classMetaData);
	}
}
