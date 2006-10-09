/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.util.Date;

import com.db4o.config.ObjectClass;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

public class IndexCreateDrop extends ClientServerTestCase {

	public int myInt;

	public String myString;

	public Date myDate;

	public void store(ExtObjectContainer oc) {
		store(oc, 4);
		store(oc, 7);
		store(oc, 6);
		store(oc, 6);
		store(oc, 5);
		store(oc, 4);
		store(oc, 0);
		store(oc, 0);
	}

	public void conc1(ExtObjectContainer oc) {
		queries(oc);
	}

	public void conc2(ExtObjectContainer oc) {
		indexed(oc, true);
		queries(oc);
	}

	public void conc3(ExtObjectContainer oc, int seq) {
		indexed(oc, seq % 2 == 0 ? true : false);
		queries(oc);
	}

	private void indexed(ExtObjectContainer objectContainer, boolean flag) {
		ObjectClass oc = objectContainer.configure().objectClass(
				this.getClass());
		oc.objectField("myInt").indexed(flag);
		oc.objectField("myString").indexed(flag);
		oc.objectField("myDate").indexed(flag);
	}

	private void store(ExtObjectContainer oc, int val) {
		IndexCreateDrop icd = new IndexCreateDrop();
		icd.myInt = val;
		if (val != 0) {
			icd.myString = "" + val;
			icd.myDate = new Date(val);
		}

		oc.set(icd);
	}

	private void queries(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(IndexCreateDrop.class);
		q.descend("myInt").constrain(new Integer(6));
		Assert.areEqual(2, q.execute().size());

		q = oc.query();
		q.constrain(IndexCreateDrop.class);
		q.descend("myInt").constrain(new Integer(4)).greater();
		Assert.areEqual(4, q.execute().size());

		q = oc.query();
		q.constrain(IndexCreateDrop.class);
		q.descend("myInt").constrain(new Integer(4)).greater().equal();
		Assert.areEqual(6, q.execute().size());

		q = oc.query();
		q.constrain(IndexCreateDrop.class);
		q.descend("myInt").constrain(new Integer(7)).smaller().equal();
		Assert.areEqual(8, q.execute().size());

		q = oc.query();
		q.constrain(IndexCreateDrop.class);
		q.descend("myInt").constrain(new Integer(7)).smaller();
		Assert.areEqual(7, q.execute().size());

		q = oc.query();
		q.constrain(IndexCreateDrop.class);
		q.descend("myString").constrain("6");
		Assert.areEqual(2, q.execute().size());

		q = oc.query();
		q.constrain(IndexCreateDrop.class);
		q.descend("myString").constrain("7");
		Assert.areEqual(1, q.execute().size());

		q = oc.query();
		q.constrain(IndexCreateDrop.class);
		q.descend("myString").constrain("4");
		Assert.areEqual(2, q.execute().size());

		q = oc.query();
		q.constrain(IndexCreateDrop.class);
		q.descend("myString").constrain(null);
		Assert.areEqual(2, q.execute().size());

		q = oc.query();
		q.constrain(IndexCreateDrop.class);
		q.descend("myDate").constrain(new Date(4)).greater();
		Assert.areEqual(4, q.execute().size());

		q = oc.query();
		q.constrain(IndexCreateDrop.class);
		q.descend("myDate").constrain(new Date(4)).greater().equal();
		Assert.areEqual(6, q.execute().size());

		q = oc.query();
		q.constrain(IndexCreateDrop.class);
		q.descend("myDate").constrain(new Date(7)).smaller().equal();

		Assert.areEqual(6, q.execute().size());

		q = oc.query();
		q.constrain(IndexCreateDrop.class);
		q.descend("myDate").constrain(new Date(7)).smaller();

		Assert.areEqual(5, q.execute().size());

		q = oc.query();
		q.constrain(IndexCreateDrop.class);
		q.descend("myDate").constrain(null);
		Assert.areEqual(2, q.execute().size());
	}

}
