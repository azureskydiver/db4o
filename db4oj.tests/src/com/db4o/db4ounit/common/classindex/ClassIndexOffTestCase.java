/* Copyright (C) 2007  Versant Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.classindex;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

public class ClassIndexOffTestCase extends AbstractDb4oTestCase implements OptOutCS{
    
    static String NAME = "1";
    
    public static class Holder {
        
        public Item _item;
        
        public Item _nullItem;
        
        public Holder(Item item){
            _item = item;
        }
    }
	
	public static class Item {
	    
		public String _name;

		public Item(String name) {
			_name = name;
		}
	}
	
	public static void main(String[] args) {
		new ClassIndexOffTestCase().runSolo();
	}
	
	protected void configure(Configuration config) throws Exception {
		super.configure(config);
		config.objectClass(Item.class).indexed(false);
	}
	
	protected void store() throws Exception {
        Item item = new Item(NAME);
        store(new Holder(item));
	}
	
	public void testNoItemInIndex(){
		
		StoredClass storedClass = db().storedClass(Item.class);
		Assert.isFalse(storedClass.hasClassIndex());
		
		assertNoItemFoundByQuery();
		
		db().commit();
		assertNoItemFoundByQuery();
	}
	
	private void assertNoItemFoundByQuery(){
		Query q = db().query();
		q.constrain(Item.class);
		Assert.areEqual(0, q.execute().size());
	}
	
	public void testRetrievalThroughHolder(){
	    assertData();
	}

    private void assertData() {
        Holder holder = (Holder) retrieveOnlyInstance(Holder.class);
	    Assert.isNotNull(holder._item);
	    Assert.areEqual(NAME, holder._item._name);
    }
	
	public void testDefragment() throws Exception{
	    defragment();
	    assertData();
	}

}
