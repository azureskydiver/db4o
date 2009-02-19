package com.db4o.db4ounit.common.handlers.framework;

import static org.easymock.EasyMock.*;

import com.db4o.config.*;
import com.db4o.marshall.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;

@decaf.Remove
public class FirstClassHandlerSemanticsTestCase extends AbstractInMemoryDb4oTestCase {
	
	public static class Item {
	}

	final TypeHandler4 _typeHandlerMock = createMock(TypeHandler4.class);
	
	@Override
	protected void configure(Configuration config) throws Exception {
	    config.registerTypeHandler(new SingleClassTypeHandlerPredicate(Item.class), _typeHandlerMock);
	}
	
	public void testReadMustNotReturnDifferentObject() throws Exception {
	
		_typeHandlerMock.write(isA(WriteContext.class), isA(Item.class));
		expectLastCall();
		expect(_typeHandlerMock.read(isA(FirstClassReadContext.class)))
			.andReturn(new Item());
		replay(_typeHandlerMock);
		
		store(new Item());
		reopen();
		
		Assert.expect(IllegalStateException.class, new CodeBlock() { public void run() {
			retrieveOnlyInstance(Item.class);
		}});
		
		verify(_typeHandlerMock);
		
	}
}
