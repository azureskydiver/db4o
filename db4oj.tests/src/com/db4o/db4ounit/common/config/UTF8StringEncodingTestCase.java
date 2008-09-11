/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.config;

import com.db4o.config.*;
import com.db4o.internal.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class UTF8StringEncodingTestCase extends AbstractDb4oTestCase {
	
	public static class Item {
		
		public Item(String name){
			_name = name;
		}
		
		public String _name;
	}
	
	protected void configure(Configuration config) throws Exception {
		config.stringEncoding().useUtf8();
	}
	
	public void testGlobalEncoderIsUtf8(){
		Assert.isInstanceOf(UTF8StringIO.class, container().stringIO());
	}
	
	public void testStoreSimpleObject() throws Exception{
		String name = "one";
		store(new Item(name));
		reopen();
		Item item = (Item) retrieveOnlyInstance(Item.class);
		Assert.areEqual(name, item._name);
	}
	
	public static void main(String[] arguments) {
		new UTF8StringEncodingTestCase().runEmbeddedClientServer();
	}

}
