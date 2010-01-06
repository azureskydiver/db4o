package com.db4o.omplus.connection;

import com.db4o.*;
import com.db4o.cs.*;
import com.db4o.cs.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;

public class RemoteConnectionParams extends ConnectionParams{

	private String host;
	
	private int port;
	
	private String user;
	
	private String password;
	
	public RemoteConnectionParams(String host,int port,String user,String password) {
//		super(readOnly);
		this.host=host;
		this.port = port;
		this.user=user;
		this.password=password;
	}

	public String getPath() {
		StringBuilder sb = new StringBuilder();
//		sb.append("db4o://");
		sb.append(host);
		sb.append(":");
		sb.append(port);
		sb.append(":");
		sb.append(user);
		return sb.toString();
	}

	@Override
	public boolean isRemote() {
		// TODO Auto-generated method stub
		return true;
	}

	public String getHost() {
		return host;
	}

	public int getPort() {
		return port;
	}

	public String getUser() {
		return user;
	}

	public String getPassword() {
		return password;
	}
	
	public ClientConfiguration configure(){
		ClientConfiguration config = Db4oClientServer.newClientConfiguration();
		configureCommon(config.common());
		return config;
	}

	@Override
	public ObjectContainer connect(Function4<String, Boolean> userCallback) throws DBConnectException {
		try {
			return Db4oClientServer.openClient(configure(), getHost(), getPort(), getUser(), getPassword());
		} 
		catch (InvalidPasswordException e) {
			throw new DBConnectException(this, "Invalid User Credentials", e);
		} 
		catch (Exception e) {
			throw new DBConnectException(this, "Could not connect to remote database", e);
		}
	}

}
