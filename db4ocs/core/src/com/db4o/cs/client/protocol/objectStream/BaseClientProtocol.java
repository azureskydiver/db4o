package com.db4o.cs.client.protocol.objectStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.db4o.cs.client.batch.UpdateSet;
import com.db4o.cs.client.protocol.ClientProtocol;
import com.db4o.cs.client.query.ClientQuery;
import com.db4o.cs.common.Config;
import com.db4o.cs.common.Operations;
import com.db4o.cs.common.util.Log;
import com.db4o.query.Query;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 7:32:08 AM
 */
public abstract class BaseClientProtocol implements ClientProtocol {
	protected ObjectInputStream oin;
	protected ObjectOutputStream oout;

	protected Map idMap = new HashMap();

	public static final Long UNSAVED_ID = new Long(0);

	// todo: could have one single list with a bunch of operations in them
	protected List toDelete = new ArrayList();
	protected List<Object> toSet = new ArrayList<Object>();


	public BaseClientProtocol(OutputStream out, InputStream in) throws IOException {
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
		if (o == null) return;
		if (Config.BATCH_UNTIL_COMMIT) {
			toSet.add(o); // will send on commit
		} else {
			sendSet(o);
			if (Config.BLOCKING) {
				Long clientID = null;
				try {
					clientID = (Long) oin.readObject();
					setIdForObject(o, clientID.longValue());
					//Log.print("ret: " + ret);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					throw new RuntimeException("ClassNotFoundException! ", e);
				}
			}
		}
	}

	protected abstract void sendSet(Object o) throws IOException;

	private void sendSetsBatch() throws IOException, ClassNotFoundException {
		if (toSet.size() > 0) {
			List toSet2 = toSet;
			synchronized (toSet) {
				toSet = new ArrayList(); // fresh list for next round so other threads don't hit the same one
			}
			oout.writeByte(Operations.BATCH); // maybe change to Ops.BATCH
			oout.writeInt(toSet2.size());
			for (int i = 0; i < toSet2.size(); i++) {
				Object o = toSet2.get(i);
				sendSet(o);
			}
			if (Config.BLOCKING) {
				Log.print("Waiting for results...");
				// get result count
				List responses = (List) oin.readObject();
				for (int i = 0; i < responses.size(); i++) {
					Object o = responses.get(i);
					System.out.println("resp" + i + ": " + o);
					// todo: apply ids and what not
				}
			}
			toSet2 = null;
		}
	}



	private void sendDeletesBatch() throws IOException, ClassNotFoundException {
		if (toDelete.size() > 0) {
			List toDelete2 = toDelete;
			synchronized (toDelete) {
				toDelete = new ArrayList(); // fresh list for next round so other threads don't hit the same one
			}
			oout.writeByte(Operations.BATCH);
			oout.writeInt(toDelete2.size());
			for (int i = 0; i < toDelete2.size(); i++) {
				Object o = toDelete2.get(i);
				sendDelete(o);
			}
			List responses = (List) oin.readObject();
			for (int i = 0; i < responses.size(); i++) {
				Object o = responses.get(i);
				//System.out.println("resp" + i + ": " + o);
			}
			toDelete2 = null;
		}
	}

	public long getID(Object o) throws IOException {
		Long id = getIdForObject(o);
		if (id == UNSAVED_ID && Config.BATCH_UNTIL_COMMIT) {
			// send all pending changes
			try {
				sendDeletesBatch();
				sendSetsBatch();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("ClassNotFoundException! ", e);
			}
			id = getIdForObject(o).longValue();
		}
		return id;
	}
	
	protected Long getIdForObject(Object o) {
//		Long id = (Long) idMap.get(o.hashCode());
//		if (id == null) {
//			id = UNSAVED_ID;
//		}
		Set entrySet = idMap.entrySet();
		Iterator iter = entrySet.iterator();
		Entry entry;
		while(iter.hasNext()) {
			entry = (Entry)iter.next();
			if(entry.getValue() == o) {
				return (Long)entry.getKey();
			}
		}
		return UNSAVED_ID;
	}

	protected void setIdForObject(Object o, long newId) {
		// idMap.put(o.hashCode(), new Long(newId));
		idMap.put(new Long(newId), o);
	}
	
	protected Object getObjectByID(long id) {
		return idMap.get(new Long(id));
	}
	
	/**
	 * This delete will just send the object id back to save on traffic
	 *
	 * @param o
	 */
	public void delete(Object o) throws IOException {
		if (Config.BATCH_UNTIL_COMMIT) {
			toDelete.add(o); // will send on commit
		} else {
			sendDelete(o);
			if (Config.BLOCKING) {
				Object numDeleted = null;
				try {
					numDeleted = oin.readObject();
					Log.print("numDeleted: " + numDeleted);
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
					throw new RuntimeException("ClassNotFoundException! ", e);
				}
			}
		}
	}

	private void sendDelete(Object o) throws IOException {
		oout.writeByte(Operations.DELETE);
		oout.writeLong(getIdForObject(o));
		oout.flush();
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
			if (o != null) {
				// sometimes the object is null, see corresponding comment in QueryOperationHandler, probably deleted before the next thread gets here
				setIdForObject(o, objectId);
				ret.add(o);
			}
		}
		return ret;

	}

	public void batch(UpdateSet updateSet, Query query) throws IOException {
		oout.writeByte(Operations.BULK);
		oout.writeObject(query);
		oout.writeObject(updateSet);
		oout.flush();

		// todo: what gets returned?  probably touched count
	}

	public void commit() throws IOException {
		if (Config.BATCH_UNTIL_COMMIT) {
			// send all pending changes
			try {
				sendDeletesBatch();
				sendSetsBatch();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				throw new RuntimeException("ClassNotFoundException! ", e);
			}
		}
		oout.writeByte(Operations.COMMIT);
		oout.flush();
		if(Config.BLOCKING) {
			try {
				Object ret = oin.readObject();
				if(ret == Boolean.FALSE) {
					throw new IOException("server fails to commit the data");
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}



	public void close() throws IOException {
		oout.writeByte(Operations.CLOSE);
		oout.flush();
	}

	public Map getIdMap() {
		return idMap;
	}
	
	public Object getByID(long id) throws IOException {
		Object obj = getObjectByID(id);
		if (obj == null) {
			oout.writeByte(Operations.GETBYID);
			oout.writeLong(id);
			oout.flush();
			try {
				obj = oin.readObject();
				setIdForObject(obj, id);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				return null;
			}
		}
		return obj;
	}
	
}
