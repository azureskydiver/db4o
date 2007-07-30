package com.db4o.db4ounit.common.fieldindex;

import com.db4o.ObjectSet;
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
	
	public class ItemPair {        
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
	
	public void test() {
    	int nbItems = 1000;
    	Item[] items = new Item[nbItems];
    	for (int il = 0;il < nbItems;il++) {
    		Item it = new Item("item" + il);
    		store(it);
    		items[il] = it;
    	}
    	Item foo = new Item("foo");
		store(foo);
    	for (int il = 0;il < nbItems-1;il++) {
    		store(new ItemPair(items[il],items[il+1]));
    	}
    	Item theOne = items[30];
		store(new ItemPair(theOne,foo));
    	
    	final Query query = newQuery(ItemPair.class);
    	query.descend("item2").descend("name").constrain("foo");
        ObjectSet objectSet = query.execute();
        Assert.areEqual(((ItemPair) objectSet.next()).item1 , theOne);
    }


	public void onDiagnostic(Diagnostic d) {
        System.out.println(d);
	    Assert.isFalse(d instanceof LoadedFromClassIndex);
	}
	
}
