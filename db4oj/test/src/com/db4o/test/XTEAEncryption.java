package com.db4o.test;

import com.db4o.*;
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

	public void configure() {
		Db4o.configure().blockSize(1);
		Db4o.configure().io(new XTeaEncryptionFileAdapter("db4o"));
	}
	
	public void store() {
		XTEAEncryption last=null;
		for(int i=0;i<NUMSTORED;i++) {
			XTEAEncryption current=new XTEAEncryption(i,"X"+i,last);
			Test.store(current);
			last=current;
		}
	}
	
	public void test() {
		Test.ensureOccurrences(getClass(), NUMSTORED);
		Query query=Test.query();
		query.constrain(getClass());
		query.descend("id").constrain(new Integer(50));
		Test.ensure(query.execute().size()==1);
	}
}
