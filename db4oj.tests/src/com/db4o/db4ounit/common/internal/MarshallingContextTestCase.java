/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.internal;

import com.db4o.internal.*;
import com.db4o.internal.marshall.*;


import db4ounit.*;
import db4ounit.extensions.*;


public class MarshallingContextTestCase extends AbstractDb4oTestCase {
    
    public static void main(String[] arguments) {
        new MarshallingContextTestCase().runSolo();
    }
    
    public static class Item{
        
        public String _name;
        
        public Item(String name){
            _name = name;
        }
        
    }
    
    public void test(){
        if(! MarshallingSpike.enabled){
            return;
        }
        
        int imaginativeID = 500;
        
        Item item = new Item("one");
        
        ObjectReference ref = new ObjectReference(classMetadataForObject(item), imaginativeID);
        ref.setObject(item);
        
        ObjectMarshaller2Spike marshaller = new ObjectMarshaller2Spike();
        StatefulBuffer buffer = marshaller.marshallNew(trans(), ref, Integer.MAX_VALUE);
        
        buffer.offset(0);
        Item readItem = (Item)ref.read(trans(), buffer, null, imaginativeID, imaginativeID, false);
        Assert.areEqual("one", readItem._name);
    }

    private ClassMetadata classMetadataForObject(Object obj) {
        return stream().produceClassMetadata(reflector().forObject(obj));
    }

}
