package com.db4o.cs.server;

import com.db4o.ObjectContainer;
import com.db4o.Db4o;
import com.db4o.ObjectServer;
import com.db4o.cs.common.ClassMetaData;
import com.db4o.query.Query;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Iterator;

/**
 * User: treeder
 * Date: Oct 31, 2006
 * Time: 12:34:06 AM
 */
public class DefaultContext implements Context {

	private Map accessMap = new HashMap();
	private Map classMap = new HashMap();
	private int clientIdCounter;
	private ObjectServer server;
	private String file;
	private int port;

	public DefaultContext(String file, int port) {

		this.file = file;
		this.port = port;

		// startup db4o server
		getObjectServer();
	}

	public Map getAccessMap() {
		return accessMap;
	}

	public int getClientId() {
		return ++clientIdCounter;
	}

	public ObjectContainer getObjectContainer() {
		ObjectContainer ret = getObjectServer().openClient();
		//dump(ret);
		return ret;
	}

	public void addClassMetaData(ClassMetaData classMetaData) {
		classMap.put(classMetaData.getClassName(), classMetaData);
	}

	public ClassMetaData getClassMetaData(String className) {
		return (ClassMetaData) classMap.get(className);
	}

	public static int dump(ObjectContainer oc) {
		System.out.println("DUMPING: " + oc.ext().identity());
		Query q = oc.query();
		List results = q.execute();
		int counter = 0;
		for (Iterator iterator = results.iterator(); iterator.hasNext();) {
			Object o = iterator.next();
			System.out.println("o:" + o);
			counter++;
		}
		System.out.println("END DUMP: " + oc.ext().identity());
		return counter;
	}

	private synchronized ObjectServer getObjectServer() {
		if (server == null) {
			System.out.println("starting objectserver on " + (port+1));
			server = Db4o.openServer(file, port+1); // opening on non-zero port so can connect with ObjectManager while running
			server.grantAccess("test", "test");
		}
		return server;
	}

	public void setAccessMap(Map accessMap) {
		this.accessMap = accessMap;
	}
}
