/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

import java.util.*;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;


public class SimpleArrayListHandlerTestCase extends AbstractDb4oTestCase {
    
    private static final class ArrayListTypeHandler implements VariableLengthTypeHandler {

        public PreparedComparison prepareComparison(Object obj) {
            // TODO Auto-generated method stub
            return null;
        }

        public void write(WriteContext context, Object obj) {
            List list = (List)obj;
            int classID = classID(context, list);
            context.writeInt(classID);
            int elementCount = list.size();
            context.writeInt(elementCount);
            TypeHandler4 untypedObjectHandler = elementTypeHandler(context, list);
            for (int i = 0; i < elementCount; i++) {
                context.writeObject(untypedObjectHandler, list.get(i));
            }
        }
        
        private int classID(WriteContext context, Object obj) {
            ObjectContainerBase container = container(context);
            GenericReflector reflector = container.reflector();
            ReflectClass claxx = reflector.forObject(obj);
            ClassMetadata classMetadata = container.produceClassMetadata(claxx);
            return classMetadata.getID();
        }

        private ObjectContainerBase container(Context context) {
            return (ObjectContainerBase) context.objectContainer();
        }
        
        private TypeHandler4 elementTypeHandler(Context context, List list){
            
            // TODO: If all elements in the list are of one type,
            //       it is possible to use a more specific handler
            
            return container(context).handlers().untypedObjectHandler();
        }

        public Object read(ReadContext context) {
            int classID = context.readInt();
            ObjectContainerBase container = container(context);
            ClassMetadata classMetadata = container.classMetadataForId(classID);
            List list = (List) classMetadata.instantiateFromReflector(container);
            int elementCount = context.readInt();
            TypeHandler4 untypedObjectHandler = elementTypeHandler(context, list);
            for (int i = 0; i < elementCount; i++) {
                list.add(context.readObject(untypedObjectHandler));
            }
            return list;
        }

        public void delete(DeleteContext context) throws Db4oIOException {
            // TODO Auto-generated method stub
      
        }

        public void defragment(DefragmentContext context) {
            // TODO Auto-generated method stub
      
        }
    }

    public static class Item {
        public List _list;
    }
    
    protected void configure(Configuration config) throws Exception {
        config.registerTypeHandler(
            new SingleClassTypeHandlerPredicate(ArrayList.class), 
            new ArrayListTypeHandler());
    }
    
    protected void store() throws Exception {
        Item item = new Item();
        item._list = new ArrayList();
        item._list.add("one");
        store(item);
    }
    
    public void test(){
        Item item = (Item) retrieveOnlyInstance(Item.class);
        Assert.areEqual(item._list.size(), 1);
        Assert.areEqual("one", item._list.get(0));
    }

}
