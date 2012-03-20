/* Copyright (C) 2012 Versant Inc. http://www.db4o.com */
package com.db4o.db4ounit.jre12.assorted;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.internal.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class MissingTranslatorTestCase extends AbstractDb4oTestCase {
	
	public static void main(String[] args) {
		new MissingTranslatorTestCase().runSolo();
	}
	
    public static class Item {
        
        String name;
        
        public Item(String name){
            this.name = name;
        }

		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) 
				return false;
			if (getClass() != obj.getClass()) return false;
			
			Item other = (Item) obj;
			if (name == null) {
				if (other.name != null)
					return false;
			} else if (!name.equals(other.name))
				return false;
			return true;
		}
        
    }
    
	private ItemConstructor translator =  new ItemConstructor();

	
	private void configureTranslator(Configuration config) {
		translator =  new ItemConstructor();
		config.objectClass(Item.class).translate(translator);
	}
	
	@Override
	protected void store() throws Exception {
		configureTranslator(fixture().config());
		reopen();
		
		store(new Item("foo"));
		fixture().resetConfig();
		reopen();
	}

    public static class ItemConstructor implements ObjectConstructor{

        public Object onInstantiate(ObjectContainer container, Object storedObject) {
            callCount++;
        	return new Item((String)storedObject);
        }

        public void onActivate(ObjectContainer container, Object applicationObject, Object storedObject) {
            callCount++;
        }

        public Object onStore(ObjectContainer container, Object applicationObject) {
            callCount++;
            return ((Item)applicationObject).name;
        }

        public Class storedClass() {
            return String.class;
        }
        
        public void resetCounter() {
        	callCount = 0;
        }
        
        public int callCount;
    }

	public void testMissingTranslatorThrows() {
		
		Assert.expect(Db4oFatalException.class, new CodeBlock() {		
			@Override
			public void run() throws Throwable {
				LocalObjectContainer loc = (LocalObjectContainer) db();
				loc.classMetadataForName(Item.class.getName());
			}		
		});
		
	}
	
	public void testMissingTranslatorDoesNotThrowsInRecoveryMode() throws Exception {
		
		fixture().config().recoveryMode(true);
		reopen();
		
		LocalObjectContainer loc = (LocalObjectContainer) db();
		loc.classMetadataForName(Item.class.getName());
		Assert.isGreater(0,  translator.callCount);
		
	}
	
	public void testDbIsUsableAfterException() throws Exception {
		boolean exceptionThrown = false;
		try {
			LocalObjectContainer loc = (LocalObjectContainer) db();
			loc.classMetadataForName(Item.class.getName());
		}
		catch(Db4oFatalException ex) {
			exceptionThrown = true;
		}
		
		Assert.isTrue(exceptionThrown);
		fixture().clean();
		configureTranslator(fixture().config());
		reopen();
		
		Query query = db().query();
		query.constrain(Item.class);
		
		ObjectSet<Object> result = query.execute();
		Assert.areEqual(1, result.size());
		Assert.areEqual(new Item("foo"), result.get(0));
	}
	
	public void testTranslatorInstalled() {
		Assert.isGreater(0, translator.callCount);
	}
	
	public void testConfiguringTranslatorForExistingClass() throws Exception {
		// get rid of translator config by deleting the databse and starting fresh
		db().close();
		fixture().clean();
		reopen();
		
		store(new Item("bar - no translator"));
		fixture().clean();
		configureTranslator(fixture().config());
		reopen();
		
		Assert.expect(Db4oFatalException.class, new CodeBlock() {
			@Override
			public void run() throws Throwable {
				LocalObjectContainer loc = (LocalObjectContainer) db();
				loc.classMetadataForName(Item.class.getName());
			}			
		});
	}
	
}
