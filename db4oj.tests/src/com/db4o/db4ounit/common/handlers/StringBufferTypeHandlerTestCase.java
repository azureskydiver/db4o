/* Copyright (C) 2004 - 2006 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.handlers.*;
import com.db4o.marshall.*;
import com.db4o.reflect.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


public class StringBufferTypeHandlerTestCase extends AbstractDb4oTestCase implements OptOutDefragSolo {
    //TODO: quick for failure in "defrag-solo" mode
    
	private final class ClassPredicate implements TypeHandlerPredicate {
		private final Class _klass;
		
		public ClassPredicate(Class klass) {
			_klass = klass;
		}
		
		public boolean match(ReflectClass classReflector, int version) {
			final ReflectClass reflectClass = classReflector.reflector().forClass(_klass);
			return classReflector == reflectClass;
		}
	}

	static final class StringBufferTypeHandler implements TypeHandler4, SecondClassTypeHandler, VariableLengthTypeHandler {

		public void defragment(DefragmentContext context) {
			throw new NotImplementedException();
		}

		public void delete(DeleteContext context) throws Db4oIOException {
			throw new NotImplementedException();
		}

		public Object read(ReadContext context) {
			Object read = stringHandler(context).read(context);
			if (null == read) {
				return null;
			}
			return new StringBuffer((String)read);
		}

		public void write(WriteContext context, Object obj) {
			stringHandler(context).write(context, obj.toString());
		}

		private TypeHandler4 stringHandler(Context context) {
			return handlers(context)._stringHandler;
		}

		private HandlerRegistry handlers(Context context) {
			return ((InternalObjectContainer) context.objectContainer()).handlers();
		}

		public PreparedComparison prepareComparison(Object obj) {
			throw new NotImplementedException();
		}
	}
	
	public static class Item {
		public StringBuffer buffer;
	
		public Item() {
		}
		
		public Item(StringBuffer contents) {
			buffer = contents;
		}
	}

	protected void configure(Configuration config) throws Exception {
	    config.exceptionsOnNotStorable(true);
		config.registerTypeHandler(
				new ClassPredicate(StringBuffer.class),
				new StringBufferTypeHandler());
	}
	
	protected void store() throws Exception {
		store(new Item(new StringBuffer("42")));
	}
	
	public void _testRetrieve() {
		Item item = retrieveItem();
		Assert.areEqual("42", item.buffer.toString());
	}
	
	public void _testTopLevelStore(){
	    Assert.expect(ObjectNotStorableException.class, new CodeBlock() {
            public void run() throws Throwable {
                store(new StringBuffer("a"));
            }
        });
	}

	private Item retrieveItem() {
		return (Item)retrieveOnlyInstance(Item.class);
	}
	
}
