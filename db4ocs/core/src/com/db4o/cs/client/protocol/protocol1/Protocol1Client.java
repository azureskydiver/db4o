package com.db4o.cs.client.protocol.protocol1;

import com.db4o.cs.client.protocol.ClientProtocol;
import com.db4o.cs.client.protocol.objectStream.BaseClientProtocol;
import com.db4o.cs.client.util.ReflectHelper3;
import com.db4o.cs.common.Operations;
import com.db4o.cs.common.Config;
import com.db4o.cs.common.DataTypes;
import com.db4o.cs.common.util.Log;
import com.db4o.cs.common.util.StopWatch;
import com.db4o.query.Query;

import java.io.*;
import java.util.List;
import java.util.Collection;

/**
 * User: treeder
 * Date: Nov 25, 2006
 * Time: 7:47:33 PM
 */
public class Protocol1Client extends BaseClientProtocol implements ClientProtocol {

	ClassMarshaller classMarshaller = new ClassMarshaller();
	ObjectMarshaller objectMarshaller = new ObjectMarshaller();
	private ClientContextProtocol1 context;
	public static StopWatch stopWatchForSet = new StopWatch();

	public Protocol1Client(OutputStream out, InputStream in) throws IOException {
		super(out, in);
		context = new ClientContextProtocol1();
		context.setIdMap(super.getIdMap());
	}

	protected void sendSet(Object o) throws IOException {
		oout.writeByte(Operations.SET);
		try {
			if (ReflectHelper3.isCollection(o.getClass())) {
				// very fast to send collections of objects rather than one by one
				oout.writeByte(DataTypes.COLLECTION); // maybe this should write Operations.BATCH instead
				System.out.println("collection");
				Collection c = (Collection) o;
				oout.writeInt(c.size());
				for (Object o1 : c) {
					writeObject(o1);
				}
				// retrieve all ids at the end for performance
				/*if (Config.BLOCKING) {
					long[] ids = (long[]) oin.readObject();
					System.out.println("got ids: " + ids.length);
					// todo: set id's on objects
				}*/
			} else {
				oout.writeByte(DataTypes.OBJECT);
				writeObject(o);
				/*
								ID performance:
								- Could we pre-get a set of IDs and assign them on the client?
								- or send back id's in a separate thread/connection?
								- why is this so slow anyways?
								 */
				/*if (Config.BLOCKING) {
					stopWatchForSet.start();
					long newId = oin.readLong(); // TODO: THIS TAKES 99% OF THE TIME!  EG: 9031 ms out of 9110 ms total for 20 objects
					stopWatchForSet.stop();
					Log.print("got newId: " + newId + " for " + o);
					setIdForObject(o, newId);
				}*/
			}
		} catch (IllegalAccessException e) {
			e.printStackTrace();
			throw new RuntimeException("Illegal Access Exception trying to read a field.", e);
		} /*catch (ClassNotFoundException e) {
			throw new RuntimeException("ClassNotFoundException: " + e.getMessage(), e);
		}*/
		Log.print("done marshalling");
		//oout.writeByte(Operations.SET_END);
		//oout.flush();
	}

	private void writeObject(Object o) throws IOException, IllegalAccessException {
		objectMarshaller.write(context, classMarshaller, oout, o);
		oout.flush();
	}


	public List execute(Query query) throws IOException, ClassNotFoundException {
		return super.execute(query);
	}


}
