package com.db4o.test.performance;

import java.io.*;

import com.db4o.*;
import com.db4o.io.*;

/**
 * @exclude
 */
public class CreateSomeData {
	public static class SomeData {
		public int id;
		public String name;

		public SomeData(int id, String name) {
			this.id = id;
			this.name = name;
		}
	}
	
	public static void main(String[] args) {
		new File("io.log.1").delete();
		new File("somedata.yap").delete();
		Db4o.configure().io(new RecordingIoAdapter(new RandomAccessFileAdapter(),"io.log"));
		ObjectContainer db=Db4o.openFile("somedata.yap");
		long start=System.currentTimeMillis();
		for(int i=0;i<10000;i++) {
			db.set(new SomeData(i,"Data"+i));
		}
		db.commit();
		System.gc();
		ObjectSet result=db.query(SomeData.class);
		System.out.println(result.size());
		System.err.println(System.currentTimeMillis()-start);
		db.close();
	}
}
