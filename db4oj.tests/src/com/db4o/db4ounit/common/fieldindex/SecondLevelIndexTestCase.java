package com.db4o.db4ounit.common.fieldindex;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.diagnostic.Diagnostic;
import com.db4o.diagnostic.DiagnosticListener;
import com.db4o.diagnostic.LoadedFromClassIndex;
import com.db4o.query.Query;

import db4ounit.*;
import db4ounit.extensions.*;


public class SecondLevelIndexTestCase extends AbstractDb4oTestCase implements DiagnosticListener {
    
    public static void main(String[] arguments) {
        new SecondLevelIndexTestCase().runSolo();
    }
	
	public static class ItemPair {
	    
	    public Item item1;
	    
	    public Item item2;
	    
	    public ItemPair() {            
	    }

	    public ItemPair(Item item_, Item item2_) {
	        item1 = item_;
	        item2 = item2_;
	    }
	}
	

	public static class Item {
	    
	    public String name;
	    
	    public Item() {            
	    }
	
	    public Item(String name_) {
	        name = name_;
	    }
	}	
	
	protected void configure(Configuration config) throws Exception {
        config.diagnostic().addListener(this);
        config.objectClass(Item.class).objectField("name").indexed(true);
        config.objectClass(ItemPair.class).objectField("item1").indexed(true);
        config.objectClass(ItemPair.class).objectField("item2").indexed(true);
	    super.configure(config);
	}
	
    protected void db4oTearDownBeforeClean() throws Exception {
        Db4o.configure().diagnostic().removeAllListeners();
    }
	
	public void test() {
    	Item itemOne = new Item("one");
    	Item itemTwo = new Item("two");
		store(new ItemPair(itemOne,itemTwo));
    	Query query = newQuery(ItemPair.class);
    	query.descend("item2").descend("name").constrain("two");
        ObjectSet objectSet = query.execute();
        Assert.areEqual(((ItemPair) objectSet.next()).item1 , itemOne);
    }

	public void onDiagnostic(Diagnostic d) {
	    Assert.isFalse(d instanceof LoadedFromClassIndex);
	}
	
}
