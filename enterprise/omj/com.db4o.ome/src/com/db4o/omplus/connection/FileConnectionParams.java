package com.db4o.omplus.connection;


public class FileConnectionParams extends ConnectionParams{

	private String filePath;

	public FileConnectionParams(String path) {
		this.filePath = path;
	}
	
	public String getPath() {
		// TODO Auto-generated method stub
		return filePath;
	}

	public boolean isRemote() {
		return false;
	}
	
	public String toString() {
		return getPath();
	}

}
