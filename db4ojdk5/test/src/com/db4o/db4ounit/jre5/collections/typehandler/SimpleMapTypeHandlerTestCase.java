/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections.typehandler;

import java.util.*;

import com.db4o.config.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;


/**
 * @exclude
 */
public class SimpleMapTypeHandlerTestCase extends AbstractDb4oTestCase {
    
    public static void main(String[] arguments) {
        new SimpleMapTypeHandlerTestCase().runSolo();
    }
    
    public static class Item {
        
        public Map map;
        
        public boolean equals(Object obj) {
            if(! (obj instanceof Item)){
                return false;
            }
            Item other = (Item) obj;
            if(map == null){
                return other.map == null;
            }
            return map.equals(other.map);
        }
        
    }
    
    public static class FirstClassElement {

        public int _id;
        
        public FirstClassElement(int id) {
            _id = id;
        }
        
        public boolean equals(Object obj) {
            if(this == obj) {
                return true;
            }
            if(obj == null || getClass() != obj.getClass()) {
                return false;
            }
            FirstClassElement other = (FirstClassElement) obj;
            return _id == other._id;
        }
        
        public int hashCode() {
            return _id;
        }
        
        public String toString() {
            return "FCE#" + _id;
        }

    }
 
    
    protected void configure(Configuration config) throws Exception {
        config.registerTypeHandler(
            new SingleClassTypeHandlerPredicate(Hashtable.class),
            new MapTypeHandler());
    }
    
    protected void store() throws Exception {
        store(storedItem());
    }

    private Item storedItem() {
        Item item = new Item();
        item.map = new Hashtable();
        Map map = item.map;
        map.put("oneKey", "oneValue");
        map.put("twoKey", "twoValue");
        map.put(new FirstClassElement(1), new FirstClassElement(1));
        return item;
    }
    
    public void test(){
        Item item = (Item) retrieveOnlyInstance(Item.class);
        Assert.areEqual(storedItem(), item);
    }

}
