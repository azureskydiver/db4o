package com.db4o.db4ounit.fieldindex;

import com.db4o.Db4o;
import com.db4o.query.Query;

public abstract class FieldIndexTestCaseBase extends BTreeTestCaseBase {

	protected static final int[] IDS = new int[]{3,7,9,4};

	public FieldIndexTestCaseBase() {
		super();
	}

	protected void configure() {
	    Db4o.configure()
	    .objectClass(FieldIndexItem.class)
	    .objectField("_id")
	    .indexed(true);
	}

	public void store() {
	    for (int i = 0; i < IDS.length; i++) {
	        db().set(new FieldIndexItem(IDS[i]));
	    }
	    db().commit();
	}

	protected Query createQuery(final int id) {
		Query q = db().query();
		q.constrain(FieldIndexItem.class);
		q.descend("_id").constrain(new Integer(id));
		return q;
	}

}