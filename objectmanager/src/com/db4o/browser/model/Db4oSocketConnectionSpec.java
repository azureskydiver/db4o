package com.db4o.browser.model;

import java.io.*;

import com.db4o.*;
import com.swtworkbench.community.xswt.metalogger.*;

public class Db4oSocketConnectionSpec extends Db4oConnectionSpec {

	private String host;
	private int port;
	private String user;
	private String password;

	protected Db4oSocketConnectionSpec(String host,int port,String user,String password,boolean readOnly) {
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

}
