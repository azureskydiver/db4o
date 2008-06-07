package com.db4o.omplus.connection;

import java.io.File;

public class DbFileConnection extends DbConnection{

	private String filePath;

	public DbFileConnection(String path) {
//		super(readOnly);
		this.filePath= new File(filePath).getAbsolutePath();
	}
	
	public String getPath() {
		// TODO Auto-generated method stub
		return filePath;
	}

	public boolean isRemote() {
		return false;
	}

}
