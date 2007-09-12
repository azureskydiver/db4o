/* Copyright (C) 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.internal;

import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.internal.slots.*;


import db4ounit.*;
import db4ounit.extensions.*;


public class MarshallingContextTestCase extends AbstractDb4oTestCase {
    
    public static void main(String[] arguments) {
        new MarshallingContextTestCase().runSolo();
    }
    
    public static class StringItem{
        public String _name;
        public StringItem(String name){
            _name = name;
        }
    }
    
    public static class StringIntItem{
        public String _name;
        public int _int;
        public StringIntItem(String name, int i){
            _name = name;
            _int = i;
        }
    }
    
    public static class StringIntBooleanItem{
        public String _name;
        public int _int;
        public boolean _bool;
        public StringIntBooleanItem(String name, int i, boolean bool){
            _name = name;
            _int = i;
            _bool = bool;
        }
    }
    
    public void testStringItem() {
        if(! NewTypeHandlerReading.enabled){
            return;
        }
        StringItem writtenItem = new StringItem("one");
        StringItem readItem = (StringItem) writeRead(writtenItem);
        Assert.areEqual(writtenItem._name, readItem._name);
    }
    
    public void testStringIntItem() {
        if(! NewTypeHandlerReading.enabled){
            return;
        }
        StringIntItem writtenItem = new StringIntItem("one", 777);
        StringIntItem readItem = (StringIntItem) writeRead(writtenItem);
        Assert.areEqual(writtenItem._name, readItem._name);
        Assert.areEqual(writtenItem._int, readItem._int);
    }

    public void testStringIntBooleanItem() {
        if(! NewTypeHandlerReading.enabled){
            return;
        }
        StringIntBooleanItem writtenItem = new StringIntBooleanItem("one", 777, true);
        StringIntBooleanItem readItem = (StringIntBooleanItem) writeRead(writtenItem);
        Assert.areEqual(writtenItem._name, readItem._name);
        Assert.areEqual(writtenItem._int, readItem._int);
        Assert.areEqual(writtenItem._bool, readItem._bool);
    }

    private Object writeRead(Object obj) {
        int imaginativeID = 500;
        ObjectReference ref = new ObjectReference(classMetadataForObject(obj), imaginativeID);
        ref.setObject(obj);
        ObjectMarshaller marshaller = MarshallerFamily.current()._object;
        MarshallingContext marshallingContext = new MarshallingContext(trans(), ref, Integer.MAX_VALUE, true);
        marshaller.marshall(ref.getObject(), marshallingContext);
        Pointer4 pointer = marshallingContext.allocateSlot();
        Buffer buffer = marshallingContext.ToWriteBuffer(pointer);
        
        
        buffer.offset(0);
        
//        String str = new String(buffer._buffer);
//        System.out.println(str);
        
        UnmarshallingContext unmarshallingContext = new UnmarshallingContext(trans(), ref, Const4.ADD_TO_ID_TREE, false);
        unmarshallingContext.buffer(buffer);
        unmarshallingContext.activationDepth(5);
        return unmarshallingContext.read();
    }

    private ClassMetadata classMetadataForObject(Object obj) {
        return stream().produceClassMetadata(reflector().forObject(obj));
    }

}
