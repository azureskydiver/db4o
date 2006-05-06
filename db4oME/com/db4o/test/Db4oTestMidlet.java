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
                String[] stores=RecordStore.listRecordStores();
                System.out.println("Found "+(stores!=null ? stores.length : 0)+" stores");
                if(stores!=null) {
                    for (int storeIdx = 0; storeIdx < stores.length; storeIdx++) {
                        System.out.println(stores[storeIdx]);
                    }
                }
		IoAdapter io =
			new RecordStoreIoAdapter(1024);
			//new MemoryIoAdapter();
		Db4o.configure().io(io);
                Db4o.configure().reflectWith(new SelfReflector(new RegressionDogSelfReflectionRegistry()));
                Db4o.configure().objectClass(Animal.class).objectField("_name").indexed(true);
                long start=System.currentTimeMillis();
                ObjectContainer db=Db4o.openFile("test.yap");
		for(int i=0;i<100;i++) {
			db.set(new Dog("Laika"+i,i));
		}
		db.commit();
		db.close();
                System.out.println("Storing 100 dogs took "+(System.currentTimeMillis()-start)+" ms");
		db=Db4o.openFile("test.yap");
                start=System.currentTimeMillis();
		ObjectSet result=db.query(Dog.class);
		System.out.println("Found "+result.size()+" dogs");
                System.out.println("Query took "+(System.currentTimeMillis()-start)+" ms");
                start=System.currentTimeMillis();
		while(result.hasNext()) {
			result.next();
		}
                System.out.println("Activation took "+(System.currentTimeMillis()-start)+" ms");
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
