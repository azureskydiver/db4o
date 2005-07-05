/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test.soda.engines.db4o;

import java.io.*;

import com.db4o.*;
import com.db4o.query.*;
import com.db4o.test.soda.*;

public class STDb4o implements STEngine {
	
	private final String FILE = "soda.yap";
	
	public static void main(String[] arguments) {
		System.out.println(Db4o.version());
	}
	
	private com.db4o.ObjectContainer con;
	
	public void reset(){
		new File(FILE).delete();
	}

	public Query query() {
		return con.query();
	}
	
	public void open() {
		Db4o.configure().messageLevel(-1);
		
		// a little bit more than default for 
		// the recursive ones
		Db4o.configure().activationDepth(7);
		
		con = Db4o.openFile(FILE);
	}

	public void close() {
		con.close();
	}
	
	public void store(Object obj) {
		con.set(obj);
	}
	
	public void commit(){
		con.commit();
	}

	public void delete(Object obj){
		con.delete(obj);
	}
	
}
