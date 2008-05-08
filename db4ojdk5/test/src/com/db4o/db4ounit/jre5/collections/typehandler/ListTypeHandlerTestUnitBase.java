/* Copyright (C) 2008  db4objects Inc.  http://www.db4o.com */

package com.db4o.db4ounit.jre5.collections.typehandler;

import java.util.*;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.internal.*;
import com.db4o.query.*;
import com.db4o.typehandlers.*;

import db4ounit.*;
import db4ounit.extensions.*;
import db4ounit.extensions.fixtures.*;


@SuppressWarnings("unchecked")
public abstract class ListTypeHandlerTestUnitBase extends AbstractDb4oTestCase implements OptOutDefragSolo {
	
    protected void configure(Configuration config) throws Exception {
        config.registerTypeHandler(
            new SingleClassTypeHandlerPredicate(itemFactory().listClass()),
            listTypeHandler());
        config.objectClass(itemFactory().itemClass()).cascadeOnDelete(true);
    }
    
	protected void store() throws Exception {
		ItemFactory factory = itemFactory();
        Object item = factory.newItem();
        List list = listFromItem(item);
        for (int eltIdx = 0; eltIdx < elements().length; eltIdx++) {
			list.add(elements()[eltIdx]);
		}
        list.add(null);
        store(item);
    }

	protected ItemFactory itemFactory() {
		return (ItemFactory) ListTypeHandlerTestVariables.LIST_IMPLEMENTATION.value();
	}
	
	protected TypeHandler4 listTypeHandler() {
	    return (TypeHandler4) ListTypeHandlerTestVariables.LIST_TYPEHANDER.value();
	}

	protected Object[] elements() {
		return elementsSpec()._elements;
	}

	protected Object notContained() {
		return elementsSpec()._notContained;
	}

	protected Object largeElement() {
		return elementsSpec()._largeElement;
	}

	protected Class elementClass() {
		return elementsSpec()._notContained.getClass();
	}

	protected void assertQueryResult(Query q, boolean successful) {
		if(successful) {
			assertSuccessfulQueryResult(q);
		}
		else {
			assertEmptyQueryResult(q);
		}
	}
	
	protected void assertListContent(Object item) {
		List list = listFromItem(item);
		Assert.areEqual(itemFactory().listClass(), list.getClass());
		Assert.areEqual(elements().length + 1, list.size());
		for (int eltIdx = 0; eltIdx < elements().length; eltIdx++) {
	        Assert.areEqual(elements()[eltIdx], list.get(eltIdx));
		}
		Assert.isNull(list.get(elements().length));
	}

	protected List listFromItem(Object item) {
		try {
			return (List) item.getClass().getField(ItemFactory.LIST_FIELD_NAME).get(item);
		} 
		catch (Exception exc) {
			throw new RuntimeException(exc);
		}
	}

	private void assertEmptyQueryResult(Query q) {
		ObjectSet set = q.execute();
		Assert.isTrue(set.isEmpty());
	}

	private void assertSuccessfulQueryResult(Query q) {
		ObjectSet set = q.execute();
    	Assert.areEqual(1, set.size());
    	Object item = set.next();
        assertListContent(item);
	}

	private ListTypeHandlerTestElementsSpec elementsSpec() {
		return (ListTypeHandlerTestElementsSpec) ListTypeHandlerTestVariables.ELEMENTS_SPEC.value();
	}    

}
