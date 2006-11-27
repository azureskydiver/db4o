package com.db4o.cs.client.protocol.objectStream;

import com.db4o.cs.client.protocol.ClientProtocol;
import com.db4o.cs.client.query.ClientQuery;
import com.db4o.cs.server.Entry;
import com.db4o.cs.common.Operations;
import com.db4o.query.Query;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 7:32:08 AM
 */
public class ObjectStreamProtocolClient implements ClientProtocol {
	private ObjectInputStream oin;
	protected ObjectOutputStream oout;

	Map idMap = new HashMap();

	public static final Long UNSAVED_ID = new Long(0);

	public ObjectStreamProtocolClient(OutputStream out, InputStream in) throws IOException {
		oout = new ObjectOutputStream(out);
		oin = new ObjectInputStream(in);
	}

	public void writeHeaders() throws IOException {
		//System.out.println("Writing headers...");
		oout.writeUTF("0.1");
		oout.flush();
	}

	public boolean login(String username, String password) throws IOException {
		//System.out.println("writing login data");
		oout.writeByte(Operations.LOGIN);
		oout.writeObject(username);
		oout.writeObject(password);
		oout.flush();

		boolean successful = oin.readBoolean();
		int clientId = oin.readInt();
		return successful;
	}

	public void set(Object o) throws IOException {
		oout.writeByte(Operations.SET);
		Long id = getIdForObject(o);
		oout.writeLong(id.longValue());
		oout.writeObject(o);
		oout.flush();
	}

	protected Long getIdForObject(Object o) {
		Long id = (Long) idMap.get(o.hashCode());
		if (id == null) {
			id = UNSAVED_ID;
		}
		return id;
	}

	public void commit() throws IOException {
		oout.writeByte(Operations.COMMIT);
		oout.flush();
	}


	public void close() throws IOException {
		oout.writeByte(Operations.CLOSE);
		oout.flush();
	}

	/**
	 * This delete will just send the object id back to save on traffic
	 *
	 * @param o
	 */
	public void delete(Object o) throws IOException {
		oout.writeByte(Operations.DELETE);
		oout.writeLong(getIdForObject(o));
	}

	public List query(Class aClass) throws IOException, ClassNotFoundException {
		ClientQuery q = new ClientQuery();
		q.constrain(aClass);
		return execute(q);
	}

	public List execute(Query query) throws IOException, ClassNotFoundException {
		// go through query tree and send
		oout.writeByte(Operations.QUERY);
		oout.writeObject(query);
		oout.flush();

		// now retrieve objects
		int resultsSize = oin.readInt();
		//System.out.println("results size on client: " + resultsSize);
		List ret = new ArrayList();
		for (int i = 0; i < resultsSize; i++) {
			long objectId = oin.readLong();
			Object o = oin.readObject();
			if(o != null){
				// sometimes the object is null, see corresponding comment in QueryOperationHandler, probably deleted before the next thread gets here
				idMap.put(o.hashCode(), new Long(objectId));
				ret.add(o);
			}
		}
		return ret;

	}


}
