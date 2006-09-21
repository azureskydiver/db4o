package com.db4o.db4ounit.common.fieldindex;

import com.db4o.*;
import com.db4o.query.Query;

import db4ounit.extensions.AbstractDb4oTestCase;

public abstract class FieldIndexTestCaseBase extends AbstractDb4oTestCase {

	public FieldIndexTestCaseBase() {
		super();
	}

	protected void configure() {
		index(FieldIndexItem.class, "foo");
	}

	protected void index(final Class clazz, final String fieldName) {
		Db4o.configure()
	    .objectClass(clazz)
	    .objectField(fieldName)
	    .indexed(true);
	}
	
	protected abstract void store();
	
	protected void storeItems(final int[] foos) {
		for (int i = 0; i < foos.length; i++) {
			store(new FieldIndexItem(foos[i]));
	    }
	}

	protected void store(Object item) {
		db().set(item);
	}

	protected Query createQuery(final int id) {
		Query q = createItemQuery();
		q.descend("foo").constrain(new Integer(id));
		return q;
	}

	protected Query createItemQuery() {
		return createQuery(FieldIndexItem.class);
	}
	
	protected Query createQuery(Class clazz) {
		return createQuery(trans(), clazz);
	}

	protected Query createQuery(Transaction trans, Class clazz) {
		Query q = createQuery(trans);
		q.constrain(clazz);
		return q;
	}

	protected Query createItemQuery(Transaction trans) {
		Query q = createQuery(trans);
		q.constrain(FieldIndexItem.class);
		return q;
	}

	private Query createQuery(Transaction trans) {
		return stream().query(trans);
	}
}