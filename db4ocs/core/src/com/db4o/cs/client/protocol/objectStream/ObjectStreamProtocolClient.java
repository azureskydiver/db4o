package com.db4o.cs.client.protocol.objectStream;

import com.db4o.cs.client.protocol.ClientProtocol;
import com.db4o.cs.server.Entry;

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
	private ObjectOutputStream oout;

	Map idMap = new HashMap();

	public ObjectStreamProtocolClient(OutputStream out, InputStream in) throws IOException {
		oout = new ObjectOutputStream(out);
		oin = new ObjectInputStream(in);
	}

	public void writeHeaders() throws IOException {
		System.out.println("Writing headers...");
		
		oout.writeObject("0.1");
		oout.flush();
	}

	public boolean login(String username, String password) throws IOException {
		System.out.println("writing login data");
		oout.writeObject("login");
		oout.writeObject(username);
		oout.writeObject(password);
		oout.flush();

		boolean successful = oin.readBoolean();
		int clientId = oin.readInt();
		return successful;
	}

	public void set(Object o) throws IOException {
		oout.writeObject("set");
		Long id = (Long) idMap.get(o.hashCode());
		if(id == null){
			oout.writeLong(0);
		} else {
			oout.writeLong(id.longValue());
		}
		oout.writeObject(o);
		oout.flush();
	}

	public void commit() throws IOException {
		oout.writeObject("commit");
		oout.flush();
	}

	public List query(Class aClass) throws IOException, ClassNotFoundException {
		oout.writeObject("query");
		oout.writeObject(aClass.getName());
		oout.flush();

		// now retrieve objects
		int resultsSize = oin.readInt();
		System.out.println("results size on client: " + resultsSize);
		List ret = new ArrayList();
		for(int i = 0; i < resultsSize; i++){
			long objectId = oin.readLong();
			Object o = oin.readObject();
			/*Entry entry = new Entry();
			entry.setObjectId(objectId);
			entry.setObject(o);*/
			idMap.put(o.hashCode(), new Long(objectId));
			ret.add(o);
		}
		return ret;
	}

	public void close() throws IOException {
		oout.writeObject("close");
		oout.flush();
	}


}
