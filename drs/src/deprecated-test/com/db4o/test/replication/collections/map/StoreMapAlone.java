package com.db4o.test.replication.collections.map;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.db4o.ext.Db4oUUID;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.*;


public class StoreMapAlone {
	public static void main(String[] args) {
		Db4o.configure().generateUUIDs(Integer.MAX_VALUE);
		server();
	}
	
	private static void solo() {
		final String fileName = "storemap.yap";
		
		File file = new File(fileName);
		file.deleteOnExit();
		
		
		ExtObjectContainer oc=null;
		try {
			 oc = Db4o.openFile(fileName).ext();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final Map uuids = newStore(oc);
		oc.commit();
		oc.close();
		
	}

	private static void server() {
		final String fileName = "storemap.yap";
		
		File file = new File(fileName);
		file.deleteOnExit();
		
		final ObjectServer server = Db4o.openServer(fileName, 7777);
		
		server.grantAccess("userName", "password");
		
		ExtObjectContainer oc=null;
		try {
			 oc = Db4o.openClient("localhost", 7777, "userName", "password").ext();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		final Map uuids = newStore(oc);
		oc.commit();
		oc.close();
		server.close();
		
		final ObjectServer server2 = Db4o.openServer(fileName, 7777);
		server2.grantAccess("userName", "password");
		
		ExtObjectContainer oc2=null;
		try {
			 oc2 = Db4o.openClient("localhost", 7777, "userName", "password").ext();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for (Object entry : uuids.values()) {
			final Db4oUUID uuid = (Db4oUUID) entry;
			final Object obj = oc2.getByUUID(uuid);
			if (obj== null)
				throw new RuntimeException("can't get the object back");
		}
	}

	private static Map newStore(ExtObjectContainer oc) {
		Map out = new HashMap();
		final MapKey key1 = new MapKey("key1");
		oc.set(key1);
		//oc.commit();
		final Db4oUUID uuid = oc.getObjectInfo(key1).getUUID();
		if (uuid== null)
			throw new RuntimeException("why don't you generate uuid?");
		out.put(key1, uuid);
		
		final MapKey key2 = new MapKey("key2");
		oc.set(key2);
		out.put(key2, oc.getObjectInfo(key2).getUUID());
		
		MapContent c1 = new MapContent("c1");
		oc.set(c1);
		out.put(c1, oc.getObjectInfo(c1).getUUID());
		
		MapContent c2 = new MapContent("c2");
		oc.set(c2);
		out.put(c2, oc.getObjectInfo(c2).getUUID());

		MapHolder mh = new MapHolder("h1");
		mh.put(key1, c1);
		mh.put(key2, c2);
		
		oc.set(mh);
		out.put(mh, oc.getObjectInfo(mh).getUUID());
		
		return out;

	}	
}
