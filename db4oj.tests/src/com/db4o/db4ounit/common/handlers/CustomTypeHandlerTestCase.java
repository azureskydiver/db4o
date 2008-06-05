/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.handlers.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class CustomTypeHandlerTestCase extends AbstractDb4oTestCase{
    
    private static final int[] DATA = new int[] {1, 2};

    public static void main(String[] arguments) {
        new CustomTypeHandlerTestCase().runSolo();
    }
    
    private static boolean prepareComparisonCalled;
    
    private final class CustomItemTypeHandler implements TypeHandler4, VariableLengthTypeHandler {

        public PreparedComparison prepareComparison(Context context, Object obj) {
            prepareComparisonCalled = true;
            return new PreparedComparison() {
                public int compareTo(Object obj) {
                    return 0;
                }
            };
        }

        public void write(WriteContext context, Object obj) {
            Item item = (Item)obj;
            if(item.numbers == null){
                context.writeInt(-1);
                return;
            }
            context.writeInt(item.numbers.length);
            for (int i = 0; i < item.numbers.length; i++) {
                context.writeInt(item.numbers[i]);
            }
        }

        public Object read(ReadContext context) {
            Item item = (Item)((UnmarshallingContext) context).persistentObject();
            int elementCount = context.readInt();
            if(elementCount == -1){
                return item;
            }
            item.numbers = new int[elementCount];
            for (int i = 0; i < item.numbers.length; i++) {
                item.numbers[i] = context.readInt();
            }
            return item;
        }

        public void delete(DeleteContext context) throws Db4oIOException {
            // TODO Auto-generated method stub
      
        }

        public void defragment(DefragmentContext context) {
            // TODO Auto-generated method stub
      
        }
    }

    public static class Item {
        
        public int[] numbers;

        public Item(int[] numbers_) {
            numbers = numbers_;
        }
        
        public boolean equals(Object obj){
            if( ! (obj instanceof Item)){
                return false;
            }
            return areEqual(numbers, ((Item)obj).numbers); 
        }
        
        private boolean areEqual(int[] expected, int[] actual){
            if ( expected == null){
                return actual == null;
            }
            if(actual == null){
                return false;
            }
            if(expected.length != actual.length){
                return false;
            }
            for (int i = 0; i < expected.length; i++) {
                if(expected[i] != actual[i]){
                    return false;
                }
            }
            return true;
            
        }
        
    }
    
    public static class ItemChild extends Item {
        
        public String name;

        public ItemChild(String name_, int[] numbers_) {
            super(numbers_);
            name = name_;
        }
        
        public boolean equals(Object obj){
            if(! (obj instanceof ItemChild)){
                return false;
            }
            ItemChild other = (ItemChild) obj;
            if(name == null){
                if(other.name != null){
                    return false;
                }
                return super.equals(obj);
            }
            if(! name.equals(other.name)){
                return false;
            }
            return super.equals(obj);
        }
        
    }
    
    protected void configure(Configuration config) throws Exception {
        TypeHandler4 customTypeHandler = new CustomItemTypeHandler();
        
        final ReflectClass claxx = ((Config4Impl)config).reflector().forClass(Item.class);
        
        TypeHandlerPredicate predicate = new TypeHandlerPredicate() {
            public boolean match(ReflectClass classReflector, int version) {
                return claxx.equals(classReflector);
            }
        };
        
        config.registerTypeHandler(predicate, customTypeHandler);
    }
    
    protected void store() throws Exception {
        store(storedItem());
        store(storedItemChild());
    }
    
    public void testConfiguration(){
        ClassMetadata classMetadata = stream().classMetadataForReflectClass(itemClass());
        prepareComparisonCalled = false;
        classMetadata.prepareComparison(stream().transaction().context(), null);
        Assert.isTrue(prepareComparisonCalled);
    }
    
    public void testRetrieveOnlyInstance(){
        Query q = newQuery(Item.class);
        Item retrievedItem = (Item) q.execute().next();  
        Assert.areEqual(storedItem(), retrievedItem);
    }
    
    public void testDerivedClass(){
        Object retrievedItemChild = retrieveOnlyInstance(ItemChild.class);
        Assert.areEqual(storedItemChild(), retrievedItemChild);
    }
    
    private Item storedItem(){
        return new Item(DATA);
    }
    
    private Item storedItemChild(){
        return new ItemChild("child", DATA);
    }

    
    ReflectClass itemClass(){
        return reflector().forClass(Item.class);
    }

}
