package com.db4o.db4ounit.fieldindex;

import com.db4o.*;
import com.db4o.db4ounit.btree.*;
import com.db4o.query.Query;

public abstract class FieldIndexTestCaseBase extends BTreeTestCaseBase {

	public FieldIndexTestCaseBase() {
		super();
	}

	protected void configure() {
	    Db4o.configure()
	    .objectClass(FieldIndexItem.class)
	    .objectField("bar")
	    .indexed(true);
	}
	
	public abstract void store();
	
	protected void store(final int[] bars) {
		for (int i = 0; i < bars.length; i++) {
	        db().set(new FieldIndexItem(bars[i]));
	    }
	    db().commit();
	}

	protected Query createQuery(final int id) {
		Query q = createItemQuery();
		q.descend("bar").constrain(new Integer(id));
		return q;
	}

	protected Query createItemQuery() {
		Query q = db().query();
		q.constrain(FieldIndexItem.class);
		return q;
	}

	protected Query createItemQuery(Transaction trans) {
		Query q = stream().query(trans);
		q.constrain(FieldIndexItem.class);
		return q;
	}

}