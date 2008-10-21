package com.db4o.db4ounit.common.cs;

import java.io.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.query.*;

public class CsSchemaMigrationSourceCode {
	
	public static class Item {
		
		//update
		//assert
		/*public String _name;
		
		public String toString() {
			return "Item " + _name;
		}
		*/

	}
	
	private static final String FILE = "csmig.db4o";
	private static final int PORT = 4447;

	public static void main(String[] arguments) throws IOException {
		new CsSchemaMigrationSourceCode().run();
	}
	
	public void run(){
		
		//store
		/*new File(FILE).delete();*/
		
		Configuration conf = Db4o.newConfiguration();
		ObjectServer server = Db4o.openServer(conf, FILE, PORT);
		server.grantAccess("db4o", "db4o");
		
		//store
		/*storeItem();*/
		
		//update
		/*updateItem();*/
		
		//assert
		/*assertItem();*/
		
		server.close();
		//assert
		/*new File(FILE).delete();*/
		
	}

	private void storeItem() {
		ObjectContainer client = openClient();
		Item item = new Item();
		client.store(item);
		client.close();
		//store
		/*System.err.println("Item stored");*/
	}
	
	private void updateItem() {
		ObjectContainer client = openClient();
		Query query = client.query();
		query.constrain(Item.class);
		Item item = (Item) query.execute().next();
		//update
		//assert
		/*item._name = "one";*/
		client.store(item);
		client.close();
	}

	private ObjectContainer openClient() {
		return Db4o.openClient(Db4o.newConfiguration(), "localhost", PORT, "db4o", "db4o");
	}
	
	private void assertItem() {
		ObjectContainer client = openClient();
		Query query = client.query();
		query.constrain(Item.class);
		Item item = (Item) query.execute().next();
		System.out.println(item);
		client.close();
	}

}

