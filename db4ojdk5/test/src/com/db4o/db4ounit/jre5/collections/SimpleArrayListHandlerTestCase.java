/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.marshall.*;
import com.db4o.marshall.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.reflect.generic.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class SimpleArrayListHandlerTestCase extends AbstractDb4oTestCase implements OptOutDefragSolo {
	
	public static void main(String[] args) {
		new SimpleArrayListHandlerTestCase().runSolo();
	}
    
    private static final class ArrayListTypeHandler implements TypeHandler4 {

        public PreparedComparison prepareComparison(Context context, Object obj) {
            // TODO Auto-generated method stub
            return null;
        }

        public void write(WriteContext context, Object obj) {
            List list = (List)obj;
            writeClass(context, list);
            writeElementCount(context, list);
            writeElements(context, list);
        }
        
        @SuppressWarnings("unchecked")
		public Object read(ReadContext context) {
            ClassMetadata classMetadata = readClass(context);            
            List existing = (List) ((UnmarshallingContext) context).persistentObject();
            List list = 
                existing != null ? 
                    existing : 
                        (List) classMetadata.instantiateFromReflector(container(context));
            int elementCount = context.readInt();
            TypeHandler4 elementHandler = elementTypeHandler(context, list);
            for (int i = 0; i < elementCount; i++) {
                list.add(context.readObject(elementHandler));
            }
            return list;
        }
        
		private void writeElementCount(WriteContext context, List list) {
			context.writeInt(list.size());
		}

		private void writeElements(WriteContext context, List list) {
			TypeHandler4 elementHandler = elementTypeHandler(context, list);
            final Iterator elements = list.iterator();
            while (elements.hasNext()) {
                context.writeObject(elementHandler, elements.next());
            }
		}

		private void writeClass(WriteContext context, List list) {
			int classID = classID(context, list);
            context.writeInt(classID);
		}
        
        private int classID(WriteContext context, Object obj) {
            ObjectContainerBase container = container(context);
            GenericReflector reflector = container.reflector();
            ReflectClass claxx = reflector.forObject(obj);
            ClassMetadata classMetadata = container.produceClassMetadata(claxx);
            return classMetadata.getID();
        }

        private ObjectContainerBase container(Context context) {
            return ((InternalObjectContainer)context.objectContainer()).container();
        }
        
        private TypeHandler4 elementTypeHandler(Context context, List list){
            
            // TODO: If all elements in the list are of one type,
            //       it is possible to use a more specific handler
            
            return container(context).handlers().untypedObjectHandler();
        }        

		private ClassMetadata readClass(ReadContext context) {
			int classID = context.readInt();
            ClassMetadata classMetadata = container(context).classMetadataForId(classID);
			return classMetadata;
		}

        public void delete(DeleteContext context) throws Db4oIOException {
            // TODO Auto-generated method stub
      
        }

        public void defragment(DefragmentContext context) {
            // TODO Auto-generated method stub
      
        }
    }

    public static class Item {
        public List list;
    }
    
    protected void configure(Configuration config) throws Exception {
        config.registerTypeHandler(
            new SingleClassTypeHandlerPredicate(ArrayList.class), 
            new ArrayListTypeHandler());
    }
    
    @SuppressWarnings("unchecked")
	protected void store() throws Exception {
        Item item = new Item();
        item.list = new ArrayList();
        item.list.add("one");
        store(item);
    }
    
    public void _test(){
        Item item = (Item) retrieveOnlyInstance(Item.class);
        assertListContent(item);
    }
    
    public void testQuery() throws Exception {
    	Query q = newQuery(Item.class);
    	q.descend("list").constrain("one");
    	assertQueryResult(q);
	}

	private void assertListContent(Item item) {
		Assert.areEqual(item.list.size(), 1);
        Assert.areEqual("one", item.list.get(0));
	}
	
	public void testContainsQuery() throws Exception {
    	Query q = newQuery(Item.class);
    	q.descend("list").constrain("e").endsWith(false);
    	assertQueryResult(q);
	}
	
	public void testFailingContainsQuery() throws Exception {
    	Query q = newQuery(Item.class);
    	q.descend("list").constrain("g").endsWith(false);
    	assertEmptyQueryResult(q);
	}
	
	public void testCompareItems() throws Exception {
    	Query q = newQuery();
    	Item item = new Item();
    	item.list = new ArrayList();
    	item.list.add("two");
    	q.constrain(item);
    	assertEmptyQueryResult(q);
    }

	private void assertEmptyQueryResult(Query q) {
		ObjectSet set = q.execute();
		Assert.isTrue(set.isEmpty());
	}

	private void assertQueryResult(Query q) {
		ObjectSet set = q.execute();
    	
    	Assert.areEqual(1, set.size());
    	Item item = (Item)set.next();
        assertListContent(item);
	}

}
