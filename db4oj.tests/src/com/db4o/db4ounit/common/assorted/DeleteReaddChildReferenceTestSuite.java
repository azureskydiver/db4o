/* Copyright (C) 2009  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.assorted;

import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.Db4oClientServerTestCase;
import db4ounit.extensions.fixtures.Db4oFixtureProvider;
import db4ounit.fixtures.FixtureTestSuiteDescription;
import db4ounit.fixtures.SubjectFixtureProvider;

/**
 * COR-1539  Readding a deleted object from a different client changes database ID in embedded mode
 */
public class DeleteReaddChildReferenceTestSuite extends FixtureTestSuiteDescription {
	
	{
		fixtureProviders(new SubjectFixtureProvider(true, false), new Db4oFixtureProvider());
		testUnits(DeleteReaddChildReferenceTestUnit.class);
	}
	
	public static class DeleteReaddChildReferenceTestUnit extends Db4oClientServerTestCase{
		
		
	    private static final String ITEM_NAME = "child";
	
		public static class ItemParent {
	    	
	    	public Item child;
	        
	    }
	    
	    public static class Item {
	        
	        public String name;
	        
	        public Item(String name_){
	            name = name_;
	        }
	    }
	    
	    @Override
	    protected void configure(Configuration config) throws Exception {
	    	if (!useIndices()) {
	    		return;
	    	}
	    	indexField(config, ItemParent.class, ITEM_NAME);
	    	indexField(config, Item.class, "name");
	    }

		private Boolean useIndices() {
			return SubjectFixtureProvider.<Boolean>value();
		}
	    
	    protected void store() throws Exception {
	        Item child = new Item(ITEM_NAME);
	        ItemParent parent = new ItemParent();
	        parent.child = child;
			store(parent);
	    }
	    
	    public void testDeleteReadd() {
	    	if (!isClientServer()){
	    		return;
	    	}
	        ExtObjectContainer client1 = db();
	        ExtObjectContainer client2 = openNewClient();
	        
			ItemParent parent1 = retrieveOnlyInstance(client1, ItemParent.class);
	        ItemParent parent2 = retrieveOnlyInstance(client2, ItemParent.class);
	        
	        client1.delete(parent1.child);        
	        
	        assertQuery(0, client1);
	        assertQuery(1, client2);
	
	        client1.commit();
	        
	        assertQuery(0, client1);
	        assertQuery(0, client2);
	
	        client2.store(parent2.child);
	        client2.commit();
	
	        assertQuery(1, client1);
	        assertQuery(1, client2);
	
	        client2.close();
	
	        ItemParent parent3 = retrieveOnlyInstance(client1, ItemParent.class);
	        db().refresh(parent3, Integer.MAX_VALUE);
	        Assert.isNotNull(parent3);
	        Assert.isNotNull(parent3.child);
	    }
	
	    private void assertQuery(int expectedCount, ExtObjectContainer queryClient) {
	    	assertParentChildQuery(expectedCount, queryClient);
	    	assertChildQuery(expectedCount, queryClient);
	    }
	    
		private void assertParentChildQuery(int expectedCount, ExtObjectContainer queryClient) {
			Query query = queryClient.query();
	        query.constrain(ItemParent.class);
	        query.descend("child").descend("name").constrain(ITEM_NAME);
			Assert.areEqual(expectedCount, query.execute().size());
		}
	
		private void assertChildQuery(int expectedCount, ExtObjectContainer queryClient) {
			Query query = queryClient.query();
	        query.constrain(Item.class);
	        query.descend("name").constrain(ITEM_NAME);
			Assert.areEqual(expectedCount, query.execute().size());
		}
	
	    public static void main(String[] arguments) {
	        new DeleteReaddChildReferenceTestUnit().runAll();
	    }
	
	}
}