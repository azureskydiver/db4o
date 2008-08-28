/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.jre12.collections;

import java.util.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;

/**
 * @decaf.ignore.jdk11
 */
public class CollectionActivationTestCase
	extends AbstractDb4oTestCase
	implements OptOutDefragSolo, OptOutTA {
	
	public static final class Item {    
		public Item(List list) {
			this.list = list;
		}

		List list;
	}
	
	public static class CollectionActivationElement {
        public String name;

        public CollectionActivationElement(){}

        public CollectionActivationElement(String name){
            this.name = name;
        }
    }
	
	private long _elementId;
	
	/**
	 * @deprecated using deprecated apis
	 */
    protected void store() {
        Item item = new Item(db().collections().newLinkedList());        
        item.list.add(storeElement());
        store(item);
    }

	private CollectionActivationElement storeElement() {
		CollectionActivationElement cae = new CollectionActivationElement("test");
        store(cae);
        _elementId = db().getID(cae);
		return cae;
	}

    public void test() {
    	Item item = (Item)retrieveOnlyInstance(Item.class);
        db().activate(item, Integer.MAX_VALUE);
        
        CollectionActivationElement cae = (CollectionActivationElement)db().getByID(_elementId);
        Assert.isNull(cae.name);
        cae = (CollectionActivationElement)item.list.get(0);
        Assert.areEqual("test", cae.name);
    }
}
