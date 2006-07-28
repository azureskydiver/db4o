/* Copyright (C) 2004 - 2006  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import java.io.*;

import com.db4o.*;
import com.db4o.io.*;
import com.db4o.io.crypt.*;
import com.db4o.query.*;

public class XTEAEncryption {
	private static final int NUMSTORED = 100;
	public int id;
	public String name;
	public XTEAEncryption parent;
	
	public XTEAEncryption() {
		this(0,null,null);
	}
	
	public XTEAEncryption(int id, String name, XTEAEncryption parent) {
		this.id = id;
		this.name = name;
		this.parent = parent;
	}

	public void test() {
		Db4o.configure().blockSize(1);
		Db4o.configure().io(new XTeaEncryptionFileAdapter("db4o"));

		new File("encrypted.yap").delete();
		ObjectContainer db=Db4o.openFile("encrypted.yap");		
		XTEAEncryption last=null;
		for(int i=0;i<NUMSTORED;i++) {
			XTEAEncryption current=new XTEAEncryption(i,"X"+i,last);
			db.set(current);
			last=current;
		}
		db.close();
		
		db=Db4o.openFile("encrypted.yap");		
		Query query=db.query();
		query.constrain(getClass());
		query.descend("id").constrain(new Integer(50));
		Test.ensure(query.execute().size()==1);
		db.close();
		
		Db4o.configure().io(new RandomAccessFileAdapter());
	}
}
