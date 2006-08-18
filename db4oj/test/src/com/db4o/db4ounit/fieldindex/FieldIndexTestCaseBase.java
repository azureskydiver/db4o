package com.db4o.db4ounit.fieldindex;

import com.db4o.Db4o;
import com.db4o.query.Query;

public abstract class FieldIndexTestCaseBase extends BTreeTestCaseBase {

	protected static final int[] BARS = new int[]{3,7,9,4};

	public FieldIndexTestCaseBase() {
		super();
	}

	protected void configure() {
	    Db4o.configure()
	    .objectClass(FieldIndexItem.class)
	    .objectField("bar")
	    .indexed(true);
	}

	public void store() {
	    for (int i = 0; i < BARS.length; i++) {
	        db().set(new FieldIndexItem(BARS[i]));
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

}