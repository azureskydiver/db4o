package com.db4o.objectmanager.model;

import java.io.IOException;

import com.db4o.Db4o;
import com.db4o.ObjectContainer;
import com.swtworkbench.community.xswt.metalogger.Logger;

public class Db4oSocketConnectionSpec extends Db4oConnectionSpec {

	private String host;
	private int port;
	private String user;
	private String password;

	public Db4oSocketConnectionSpec(String host,int port,String user,String password,boolean readOnly) {
		super(readOnly);
		this.host=host;
		this.port=port;
		this.user=user;
		this.password=password;
	}

	public String path() {
		return "db4o://" + host + ":" + port;
	}

	protected ObjectContainer connectInternal() {
		try {
			return Db4o.openClient(host, port, user, password);
		} catch (IOException exc) {
			Logger.log().error(exc,"Could not connect to: "+path()+" as user "+user);
			return null;
		}
	}

	public String shortPath() {
		return path();
	}

}
