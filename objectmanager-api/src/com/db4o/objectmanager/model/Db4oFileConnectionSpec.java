package com.db4o.objectmanager.model;

import java.io.*;

import com.db4o.*;

public class Db4oFileConnectionSpec extends Db4oConnectionSpec {
	private String filePath;
	
	public Db4oFileConnectionSpec(String path,boolean readOnly) {
		super(readOnly);
		this.filePath=path;
	}

	public String getPath() {
		return filePath;
	}

	protected ObjectContainer connectInternal() {
		return Db4o.openFile(filePath);
	}

    public String toString() {
        return filePath;
    }

    public String getShortPath() {
		return new File(filePath).getName();
	}
}
