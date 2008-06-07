package com.db4o.omplus.connection;

public class DbRemoteConnection extends DbConnection{

	private String host;
	
	private int port;
	
	private String user;
	
	private String password;
	
	public DbRemoteConnection(String host,String port,String user,String password,boolean readOnly) {
//		super(readOnly);
		this.host=host;
		this.port= new Integer(port).intValue();
		this.user=user;
		this.password=password;
	}

	public String getPath() {
		StringBuilder sb = new StringBuilder();
		sb.append("db4o://");
		sb.append(host);
		sb.append(":");
		sb.append(port);
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

}
