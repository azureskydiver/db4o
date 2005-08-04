/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.io.*;
import java.util.*;

import com.db4o.*;
import com.db4o.test.types.*;

public class MapEntries {
	
	static String FILE = "hm.yap";
	
	HashMap hm;

	public static void main(String[] args) {
		// createAndDelete();
		
		set();
		check();
		LogAll.run(FILE);
		update();
		check();
		LogAll.run(FILE);
	}
	
	static void createAndDelete(){
		new File(FILE).delete();
		ObjectContainer con = Db4o.openFile(FILE);
		HashMap map = new HashMap();
		map.put("delme", new Integer(99));
		con.set(map);
		con.close();
		con = Db4o.openFile(FILE);
		con.delete(con.get(new HashMap()).next());
		con.close();
		LogAll.run(FILE);
	}
	
	static void check(){
		ObjectContainer con = Db4o.openFile(FILE);
		System.out.println("Entry elements: " + con.get(new com.db4o.config.Entry()).size());
		con.close();
	}
	
	static void set(){
		new File(FILE).delete();
		ObjectContainer con = Db4o.openFile(FILE);
		MapEntries me = new MapEntries();
		me.hm = new HashMap();
		me.hm.put("t1", new ObjectSimplePublic());
		me.hm.put("t2", new ObjectSimplePublic());
		con.set(me);
		con.close();
	}
	
	static void update(){
		ObjectContainer con = Db4o.openFile(FILE);
		ObjectSet set = con.get(new MapEntries());
		while(set.hasNext()){
			MapEntries me = (MapEntries)set.next();
			me.hm.put("t1", new Integer(100));
			con.set(me.hm);
		}
		con.close();
	}
	
	
}
