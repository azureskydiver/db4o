package com.db4o.db4ounit.jre5.collections.typehandler;

import com.db4o.db4ounit.jre5.collections.typehandler.ListTypeHandlerTestVariables.*;
import com.db4o.query.*;

import db4ounit.extensions.*;

public abstract class TypeHandlerUnitTest extends TypeHandlerTestUnitBase {

	protected abstract void assertCompareItems(Object element, boolean successful);

	protected void assertQuery(boolean successful, Object element, boolean withContains) {
		Query q = newQuery(itemFactory().itemClass());
		Constraint constraint = q.descend(itemFactory().fieldName()).constrain(element);
		if(withContains) {
			constraint.contains();
		}
		assertQueryResult(q, successful);
	}
	
	public void testRetrieveInstance() {
	    Class itemClass = itemFactory().itemClass();
	    Object item = retrieveOnlyInstance(itemClass);
	    assertContent(item);
	}
	
	public void testSuccessfulQuery() throws Exception {
		assertQuery(true, elements()[0], false);
	}

	public void testFailingQuery() throws Exception {
		assertQuery(false, notContained(), false);
	}

	public void testSuccessfulContainsQuery() throws Exception {
		assertQuery(true, elements()[0], true);
	}

	public void testFailingContainsQuery() throws Exception {
		assertQuery(false, notContained(), true);
	}

	public void testCompareItems() throws Exception {
		assertCompareItems(elements()[0], true);
	}

	public void testFailingCompareItems() throws Exception {
		assertCompareItems(notContained(), false);
	}

	public void testDeletion() throws Exception {
	    assertFirstClassElementCount(elements().length);
	    Object item = retrieveOnlyInstance(itemFactory().itemClass());
	    db().delete(item);
	    db().purge();
	    Db4oAssert.persistedCount(0, itemFactory().itemClass());
	    assertFirstClassElementCount(0);
	}

	protected void assertFirstClassElementCount(int expected) {
		if(!isFirstClass(elementClass())) {
			return;
		}
		Db4oAssert.persistedCount(expected, elementClass());
	}

	private boolean isFirstClass(Class elementClass) {
		return FirstClassElement.class == elementClass;
	}

}
