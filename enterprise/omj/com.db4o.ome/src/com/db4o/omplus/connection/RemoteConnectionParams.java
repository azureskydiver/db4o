package com.db4o.omplus.connection;

public class RemoteConnectionParams extends ConnectionParams{

	private String host;
	
	private int port;
	
	private String user;
	
	private String password;
	
	public RemoteConnectionParams(String host,String port,String user,String password) {
//		super(readOnly);
		this.host=host;
		this.port= new Integer(port).intValue();
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
	
	public String toString() {
		return getPath();
	}

}
