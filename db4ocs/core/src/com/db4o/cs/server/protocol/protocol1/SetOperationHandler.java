package com.db4o.cs.server.protocol.protocol1;

import com.db4o.cs.server.protocol.OperationHandler;
import com.db4o.cs.server.Context;
import com.db4o.cs.server.Session;
import com.db4o.cs.server.ClassMetaDataServer;
import com.db4o.reflect.ReflectField;
import com.db4o.ObjectContainer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * User: treeder
 * Date: Nov 26, 2006
 * Time: 1:02:09 PM
 */
public class SetOperationHandler implements OperationHandler {
	public void handle(Context context, Session session, ObjectInputStream oin, ObjectOutputStream oout) throws IOException, ClassNotFoundException {
		// collect incoming data
		long objectId = oin.readLong();
		String className = oin.readUTF();
		ClassMetaDataServer classMetaData = (ClassMetaDataServer) context.getClassMetaData(className);
		if(classMetaData == null) throw new ClassNotFoundException("Server does not know about class: " + className);
		// todo: maybe this should eat all the incoming data for this object, then return an error response?
		//System.out.println("set for class: " + className);
		int fieldCount = classMetaData.getFieldCount();
		List fieldValues = new ArrayList();
		for(int i = 0; i < fieldCount; i++){
			Object fieldValue = oin.readObject();
			//System.out.println("fieldValue:" + fieldValue);
			fieldValues.add(fieldValue);
		}

		ObjectContainer oc = session.getObjectContainer(context);

		/**
		 * What would be really nice here is a low level call to apply changes to a particular field or to apply changes
		 * to an object without instantiating it.
		 * eg: oc.ext().reflector().setField(genericClass, field, value);
		 */

		// if object exists, then update and save, if not, then create GenericObject and save
		Object o = null;
		if (objectId > 0) {
			o = oc.ext().getByID(objectId);
		}
		if(o == null){ // not doing an "else" in case there is an id problem, we'll still save it
			o = classMetaData.getReflectClass().newInstance();
		}
		if(o == null){
			// newInstance in JdkClass swallows any exceptions, so have to check for null once again and guess what went wrong
			// todo: should return an error message to the client 
			return;
		}

		// now apply values
		List<ReflectField> reflectFields = classMetaData.getReflectFields();
		for (int i = 0; i < reflectFields.size(); i++) {
			ReflectField reflectField = reflectFields.get(i);
			Object fieldValue =  fieldValues.get(i);
			//System.out.println(o + " - fieldValue: " + fieldValue);
			reflectField.set(o, fieldValue);
		}

		// and finally save
		oc.set(o);

	}

}
