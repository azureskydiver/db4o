package com.db4o.db4ounit.common.handlers.framework;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.delete.*;
import com.db4o.internal.reflect.*;
import com.db4o.marshall.*;
import com.db4o.query.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class TypeHandlerCanInstantiateTestCase extends AbstractDb4oTestCase {
	
	public static class Item {
		
		private int _id;
		
		public Item(int id) {
			_id = id;
		}
		
		public int id() {
			return _id;
		}
	}
	
	public static class ItemHandler implements TypeHandler4 {

		public void defragment(DefragmentContext context) {
        }

		public void delete(DeleteContext context) throws Db4oIOException {
        }

		public Object read(ReadContext context) {
			int id = context.readInt();
			return null;
        }

		public void write(WriteContext context, Object obj) {
			context.writeInt(((Item)obj).id());
        }

		public PreparedComparison prepareComparison(Context context, Object obj) {
	        // TODO Auto-generated method stub
	        return null;
        }
		
		public boolean canHold(ReflectClass type) {
			return ReflectClasses.areEqual(Item.class, type);
	    }
		
	}
	
	@Override
	protected void configure(Configuration config) throws Exception {
	    config.registerTypeHandler(new SingleClassTypeHandlerPredicate(Item.class), new ItemHandler());
	}
	
	@Override
	protected void store() throws Exception {
		store(new Item(42));
	}
	
	public void testField() {
		
		Assert.areEqual(42, retrieveOnlyInstance(Item.class).id());
	}
	
	public void testIdentity() {
		
		Assert.areSame(retrieveOnlyInstance(Item.class), retrieveOnlyInstance(Item.class));
	}
	
	public void _testQuery() {
		Item found = itemById(42);
		Assert.areSame(retrieveOnlyInstance(Item.class), found);
	}

	private Item itemById(final int id) {
	    final Query query = newQuery(Item.class);
		query.descend("_id").constrain(id);
		final ObjectSet<Object> found = query.execute();
		return found.hasNext()
			? (Item) found.next()
			: null;
    }

}
