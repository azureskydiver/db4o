/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.reflect.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class ClassMetadataTypehandlerTestCase extends AbstractDb4oTestCase{

	public static void main(String[] args) {
		new ClassMetadataTypehandlerTestCase().runSolo();
	}
	
	public static class Item {
		
		public String _name;
		
		public Item(String name){
			_name = name;
		}

		public boolean equals(Object obj) {
			if (! (obj instanceof Item)){
				return false;
			}
			Item other = (Item) obj;
			return _name == null ?  other._name == null : _name.equals(other._name);
		}
		
	}
	
	protected void store() throws Exception {
		store(new Item("stored"));
	}
	
	public void testHandlerForClass(){
		Assert.isNotNull(classMetadataHandler());
	}
	
    public void testReadWrite() {
        MockWriteContext writeContext = new MockWriteContext(db());
        Item expected = new Item("mock");
        classMetadataHandler().write(writeContext, expected);
        MockReadContext readContext = new MockReadContext(writeContext);
        Item actual = (Item) classMetadataHandler().read(readContext);
        Assert.areEqual(expected, actual);
    }
    
    public void testCompare() {
    	Item item = (Item) retrieveOnlyInstance(Item.class);
    	Integer id = new Integer(stream().getID(trans(), item));
    	PreparedComparison preparedComparison = classMetadataHandler().prepareComparison(id);
    	Assert.areEqual(0, preparedComparison.compareTo(id));
    }

	private TypeHandler4 classMetadataHandler() {
		return stream().handlers().handlerForClass(stream(), itemClass());
	}

	private ReflectClass itemClass() {
		return reflector().forClass(Item.class);
	}

}
