package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.db4ounit.btree.*;
import com.db4o.query.Query;

public abstract class FieldIndexTestCaseBase extends BTreeTestCaseBase {

	public FieldIndexTestCaseBase() {
		super();
	}

	protected void configure() {
		index(FieldIndexItem.class, "bar");
	}

	protected void index(final Class clazz, final String fieldName) {
		Db4o.configure()
	    .objectClass(clazz)
	    .objectField(fieldName)
	    .indexed(true);
	}
	
	public abstract void store();
	
	protected void storeItems(final int[] bars) {
		for (int i = 0; i < bars.length; i++) {
			store(new FieldIndexItem(bars[i]));
	    }
	}

	protected void store(Object item) {
		db().set(item);
	}

	protected Query createQuery(final int id) {
		Query q = createItemQuery();
		q.descend("bar").constrain(new Integer(id));
		return q;
	}

	protected Query createItemQuery() {
		return createQuery(FieldIndexItem.class);
	}

	protected Query createQuery(Class clazz) {
		Query q = db().query();
		q.constrain(clazz);
		return q;
	}

	protected Query createItemQuery(Transaction trans) {
		Query q = stream().query(trans);
		q.constrain(FieldIndexItem.class);
		return q;
	}

}