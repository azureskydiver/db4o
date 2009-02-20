/* Copyright (C) 2004 - 2008 db4objects Inc. http://www.db4o.com */

package com.db4o.db4ounit.common.handlers;

import com.db4o.config.*;
import com.db4o.diagnostic.*;
import com.db4o.ext.*;
import com.db4o.foundation.*;
import com.db4o.internal.handlers.*;
import com.db4o.query.*;
import com.db4o.typehandlers.*;
import com.db4o.types.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class StringBufferHandlerTestCase extends AbstractDb4oTestCase {

    public static void main(String[] args) {
        new StringBufferHandlerTestCase().runAll();
    }

    public static class Item implements SecondClass {
        public StringBuffer buffer;

        public Item(StringBuffer contents) {
            buffer = contents;
        }
    }

    static String _bufferValue = "42"; //$NON-NLS-1$

    protected void configure(Configuration config) throws Exception {
        config.exceptionsOnNotStorable(true);
        config.registerTypeHandler(new SingleClassTypeHandlerPredicate(
                StringBuffer.class), new StringBufferHandler());
        config.diagnostic().addListener(new DiagnosticListener() {

            public void onDiagnostic(Diagnostic d) {
                if (d instanceof DeletionFailed)
                    throw new Db4oException();
            }
        });
    }

    protected void store() throws Exception {
        store(new Item(new StringBuffer(_bufferValue)));
    }

    public void testRetrieve() {
        Item item = retrieveItem();
        Assert.areEqual(_bufferValue, item.buffer.toString());
    }

    public void testTopLevelStore() {
        Assert.expect(ObjectNotStorableException.class, new CodeBlock() {
            public void run() throws Throwable {
                store(new StringBuffer("a")); //$NON-NLS-1$
            }
        });
    }

    public void testDelete() {
        Item item = retrieveItem();
        Assert.areEqual(_bufferValue, item.buffer.toString());
        db().delete(item);
        Query query = newQuery();
        query.constrain(Item.class);
        Assert.areEqual(0, query.execute().size());
    }

    public void testPrepareComparison() {
        StringBufferHandler handler = new StringBufferHandler();
        PreparedComparison preparedComparison = handler.prepareComparison(trans().context(), _bufferValue);
        Assert.isGreater(preparedComparison.compareTo("43"), 0); //$NON-NLS-1$
    }
    
    public void testStoringStringBufferDirectly(){
    	Assert.expect(ObjectNotStorableException.class, new CodeBlock() {
			public void run() throws Throwable {
		    	StringBuffer stringBuffer = new StringBuffer(_bufferValue);
		    	store(stringBuffer);
			}
		});
    }

    private Item retrieveItem() {
        return (Item) retrieveOnlyInstance(Item.class);
    }

}
