package com.db4o.cs.client.protocol.protocol1;

import com.db4o.cs.common.ClassMetaData;
import com.db4o.cs.common.Operations;
import com.db4o.cs.common.DataTypes;
import com.db4o.cs.common.util.Log;
import com.db4o.cs.common.util.StopWatch;
import com.db4o.cs.client.util.ReflectHelper3;
import com.db4o.cs.client.ClientContext;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * The Class Definition is already sent to the server at this point, so we only need to send values and nothing else.
 * <p/>
 * User: treeder
 * Date: Nov 26, 2006
 * Time: 11:48:54 AM
 */
public class ObjectMarshaller {

	public static StopWatch stopWatchWriteObject = new StopWatch();

	/**
	 * PERFORMANCE NOTES: This method is fast, for 60 objects, took 46 ms
	 * @param context
	 * @param classMarshaller
	 * @param oout
	 * @param o
	 * @throws IOException
	 * @throws IllegalAccessException
	 */
	public void write(ClientContext context, ClassMarshaller classMarshaller, ObjectOutputStream oout, Object o) throws IOException, IllegalAccessException {
		if (o == null) {
			oout.writeByte(DataTypes.NULL);
			return;
		}
		Class oClass = o.getClass();
		String className = oClass.getName();
		Log.print("writing object of type: " + className + " - " + o);
		if (ReflectHelper3.isSecondClass(oClass)) { // could probably preset these before hand when creating ClassMetaData
			oout.writeByte(DataTypes.SECOND_CLASS);
			oout.writeObject(o); // this is fast, problem not here, 64 ms for 40
		} else if (ReflectHelper3.isCollection(oClass)) {
			oout.writeByte(DataTypes.COLLECTION);
			Collection collection = (Collection) o;
			oout.writeInt(collection.size());
			for (Iterator iterator = collection.iterator(); iterator.hasNext();) {
				Object o1 = iterator.next();
				write(context, classMarshaller, oout, o1);
			}
		} else if (ReflectHelper3.isMap(oClass)) {
			Map map = (Map) o;
			oout.writeByte(DataTypes.MAP);
		} else {
			ClassMetaData classMetaData = context.getClassMetaData(className);
			if (classMetaData == null) {
				// optimizatiion where it will send class information only once per connection
				classMetaData = classMarshaller.write(context, oout, o.getClass());
			}
			stopWatchWriteObject.start();
			oout.writeByte(DataTypes.OBJECT);
			Long id = context.getIdForObject(o);
			oout.writeLong(id.longValue());
			oout.writeUTF(className); // the class definition will already be on server // todo: could optimize
			List<Field> fields = classMetaData.getReflectionFields();
			for (int i = 0; i < fields.size(); i++) {

				Field field = fields.get(i);
				// already done earlier - field.setAccessible(true);
				Object value = field.get(o);
				// go down tree
				write(context, classMarshaller, oout, value);
			}
			stopWatchWriteObject.stop();
		}

	}
}
