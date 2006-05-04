package com.db4o.test;

import javax.microedition.midlet.*;
import javax.microedition.rms.*;

import com.db4o.*;
import com.db4o.io.*;
import com.db4o.reflect.self.*;

public class Db4oTestMidlet extends MIDlet {

	public Db4oTestMidlet() {
		// TODO Auto-generated constructor stub
	}

	protected void destroyApp(boolean arg0) throws MIDletStateChangeException {
		// TODO Auto-generated method stub

	}

	protected void pauseApp() {
		// TODO Auto-generated method stub

	}

	protected void startApp() throws MIDletStateChangeException {
		try {
			runTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void runTest() throws ClassNotFoundException, RecordStoreException {
		try {
			RecordStore.deleteRecordStore("test.yap");
		} catch (Exception e) {
		}
		System.out.println(IntClassGetter.getIntClass());
		IoAdapter io =
			new RecordStoreIoAdapter(256);
			//new MemoryIoAdapter();
		Db4o.configure().io(io);
        Db4o.configure().reflectWith(new SelfReflector(new RegressionDogSelfReflectionRegistry()));
		ObjectContainer db=Db4o.openFile("test.yap");
		for(int i=0;i<100;i++) {
			db.set(new Dog("Laika"+i,i));
		}
		db.commit();
//		db.close();
//		db=Db4o.openFile("test.yap");
		ObjectSet result=db.query(Dog.class);
		System.out.println(result.size());
		while(result.hasNext()) {
			System.out.println(result.next());
		}
		db.close();
	}

	public static void main(String[] args) {
		try {
			runTest();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
