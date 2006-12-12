package com.db4o.cs.server.protocol.protocol1;

import com.db4o.cs.server.protocol.OperationHandler;
import com.db4o.cs.server.Context;
import com.db4o.cs.server.Session;
import com.db4o.cs.server.ClassMetaDataServer;
import com.db4o.cs.common.util.Log;
import com.db4o.cs.common.util.StopWatch;
import com.db4o.cs.common.Operations;
import com.db4o.cs.common.DataTypes;
import com.db4o.cs.common.Config;
import com.db4o.reflect.ReflectField;
import com.db4o.ObjectContainer;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

/**
 * todo: Have to figure out how to get ids back to client for deep graphs, should be at the end somehow
 *
 * User: treeder
 * Date: Nov 26, 2006
 * Time: 1:02:09 PM
 */
public class SetOperationHandler implements OperationHandler {
	private Object root; // todo: i don't like this being here
	public static StopWatch stopWatchInsantiation = new StopWatch();

	public Object handle(Context context, Session session, ObjectInputStream oin, ObjectOutputStream oout) throws IOException, ClassNotFoundException {
		// collect incoming data
		try {
			byte nextDataType = oin.readByte();
			if (nextDataType == DataTypes.COLLECTION) {
				System.out.println("got root collection");
				// then just a deluge of objects incoming
				int size = oin.readInt();
				long[] newIds = new long[size];
				for (int i = 0; i < size; i++) {
					newIds[i] = readObject(oin, session, oout, context);
				}
				if (Config.BLOCKING) {
					oout.writeObject(newIds);
					oout.flush();
				}
				return newIds;
			} else {
				long id = readObject(oin, session, oout, context);
				return id;
			}
		} catch (Exception e) {
			// todo: should return an error message to the client
			e.printStackTrace();
		}
		return null;
	}

	private long readObject(ObjectInputStream oin, Session session, ObjectOutputStream oout, Context context) throws Exception {
		stopWatchInsantiation.start();
		traverse(oin, session, oout, context, null, null, null);
		// and finally save
		Log.print("finally saving object: " + root);
		// todo: move this out a method, then only call it once, currently getting called for every object
		ObjectContainer oc = session.getObjectContainer();
		oc.set(root);
		long newId = oc.ext().getID(root); // this seems to take some time, doubles duration if stop is put below this line
		stopWatchInsantiation.stop();
		return newId;

	}

	// this declaration is getting pretty weak with the collection and stuff, consider refactoring
	private void traverse(ObjectInputStream oin, Session session, ObjectOutputStream oout, Context context, Object incomingObject, ReflectField reflectField, List incomingCollection) throws Exception, ClassNotFoundException {
		byte next = oin.readByte();
		Log.print("datatype or op: " + next);
		if (next == Operations.SET_END) {
			return;
		}
		if (next == DataTypes.NULL) {
			return;
		} else if (next == DataTypes.SECOND_CLASS) {
			// then apply to field
			/**
			 * What would be really nice here is a low level call to apply changes to a particular field or to apply changes
			 * to an object without instantiating it.
			 * eg: oc.ext().setField(objectId, field, value);
			 */

			Object fieldValue = oin.readObject();
			reflectField.set(incomingObject, fieldValue);
		} else if (next == Operations.CLASS_METADATA) {
			// then new class incoming
			session.handle(next, oin, oout);
			traverse(oin, session, oout, context, incomingObject, reflectField, null);
		} else if (next == DataTypes.COLLECTION) {
			//System.out.println("incoming collection");
			int size = oin.readInt();
			List collection = new ArrayList(size); // todo: should store as the specific collection type, at least the interface
			// loop through each, add to a list, then apply to field
			for(int j = 0; j < size; j++){
				traverse(oin, session, oout, context, null, null, collection);
			}
			reflectField.set(incomingObject, collection);

		} else if (next == DataTypes.OBJECT) {
			long objectId = oin.readLong();
			String className = oin.readUTF();
			// todo: check for collection, map, etc and handle accordingly
			ClassMetaDataServer classMetaData = (ClassMetaDataServer) context.getClassMetaData(className);
			if (classMetaData == null) throw new RuntimeException("Server does not know about class: " + className);
			// todo: maybe this should eat all the incoming data for this object, then return an error response?
			Log.print("set for class: " + className);

			// if object exists, then update and save, if not, then create GenericObject and save
			ObjectContainer oc = session.getObjectContainer();
			Object o = null;
			if (objectId > 0) {
				o = oc.ext().getByID(objectId);
			}
			if (o == null) { // not doing an "else" in case there is an id problem, we'll still save it
				o = classMetaData.getReflectClass().newInstance();
			}
			if (o == null) {
				// newInstance in JdkClass swallows any exceptions, so have to check for null once again and guess what went wrong
				throw new Exception("Could not instantiate object!");
			}

			int fieldCount = classMetaData.getFieldCount();
			List fieldValues = new ArrayList();
			for (int i = 0; i < fieldCount; i++) {
				Log.print("getting field: " + i + " - " + classMetaData.getFields().get(i).getFieldName());
				/*byte type = oin.readByte();
				//should this recurse, pass the object and the Field along and have the recursion set the field, then return
				Object fieldValue = oin.readObject();
				System.out.println("fieldValue:" + fieldValue);
				fieldValues.add(fieldValue);*/
				traverse(oin, session, oout, context, o, classMetaData.getReflectFields().get(i), null);
			}

			if (incomingObject != null) {
				if (reflectField != null) {
					Log.print("setting object: " + o + " on field: " + reflectField);
					reflectField.set(incomingObject, o);
				} else {
					Log.print("BAD, no field for object!");
				}
			} else if(incomingCollection != null){
				// then add it
				incomingCollection.add(o);
			} else {
				Log.print("setting root object to: " + o);
				root = o;
			}
		}
	}

}
