/* Copyright (C) 2004 - 2007  db4objects Inc.  http://www.db4o.com */

package com.db4o.test.concurrency;

import com.db4o.*;
import com.db4o.config.*;
import com.db4o.ext.*;
import com.db4o.query.*;

import db4ounit.*;
import db4ounit.extensions.*;

public class CascadeDeleteDeletedTestCase extends Db4oClientServerTestCase {

	public String name;

	public Object untypedMember;

	public CddMember typedMember;
	
	public static void main(String[] args) {
		new CascadeDeleteDeletedTestCase().runConcurrency();
	}
	
	protected void db4oSetupBeforeStore() throws Exception {
		configureThreadCount(10);
	}

	public CascadeDeleteDeletedTestCase() {
	}

	public CascadeDeleteDeletedTestCase(String name) {
		this.name = name;
	}

	protected void configure(Configuration config) {
		config.objectClass(this).cascadeOnDelete(true);
	}

	public void store() {
		ExtObjectContainer oc = db();
		membersFirst(oc, "membersFirst commit");
		membersFirst(oc, "membersFirst");
		twoRef(oc, "twoRef");
		twoRef(oc, "twoRef commit");
		twoRef(oc, "twoRef delete");
		twoRef(oc, "twoRef delete commit");
	}

	private void membersFirst(ExtObjectContainer oc, String name) {
		CascadeDeleteDeletedTestCase cdd = new CascadeDeleteDeletedTestCase(name);
		cdd.untypedMember = new CddMember();
		cdd.typedMember = new CddMember();
		oc.set(cdd);
	}

	private void twoRef(ExtObjectContainer oc, String name) {
		CascadeDeleteDeletedTestCase cdd = new CascadeDeleteDeletedTestCase(name);
		cdd.untypedMember = new CddMember();
		cdd.typedMember = new CddMember();
		CascadeDeleteDeletedTestCase cdd2 = new CascadeDeleteDeletedTestCase(name);
		cdd2.untypedMember = cdd.untypedMember;
		cdd2.typedMember = cdd.typedMember;
		oc.set(cdd);
		oc.set(cdd2);
	}

	public void conc(ExtObjectContainer oc, int seq) {
		if (seq == 0) {
			tMembersFirst(oc, "membersFirst commit");
		} else if (seq == 1) {
			tMembersFirst(oc, "membersFirst");
		} else if (seq == 2) {
			tTwoRef(oc, "twoRef");
		} else if (seq == 3) {
			tTwoRef(oc, "twoRef commit");
		} else if (seq == 4) {
			tTwoRef(oc, "twoRef delete");
		} else if (seq == 5) {
			tTwoRef(oc, "twoRef delete commit");
		}
	}

	public void check(ExtObjectContainer oc) {
		Assert.areEqual(0, countOccurences(oc, CddMember.class));
	}

	private void tMembersFirst(ExtObjectContainer oc, String name) {
		boolean commit = name.indexOf("commit") > 1;
		Query q = oc.query();
		q.constrain(CascadeDeleteDeletedTestCase.class);
		q.descend("name").constrain(name);
		ObjectSet objectSet = q.execute();
		CascadeDeleteDeletedTestCase cdd = (CascadeDeleteDeletedTestCase) objectSet.next();
		oc.delete(cdd.untypedMember);
		oc.delete(cdd.typedMember);
		if (commit) {
			oc.commit();
		}
		oc.delete(cdd);
		if (!commit) {
			oc.commit();
		}
	}

	private void tTwoRef(ExtObjectContainer oc, String name) {
		boolean commit = name.indexOf("commit") > 1;
		boolean delete = name.indexOf("delete") > 1;

		Query q = oc.query();
		q.constrain(this.getClass());
		q.descend("name").constrain(name);
		ObjectSet objectSet = q.execute();
		CascadeDeleteDeletedTestCase cdd = (CascadeDeleteDeletedTestCase) objectSet.next();
		CascadeDeleteDeletedTestCase cdd2 = (CascadeDeleteDeletedTestCase) objectSet.next();
		if (delete) {
			oc.delete(cdd.untypedMember);
			oc.delete(cdd.typedMember);
		}
		oc.delete(cdd);
		if (commit) {
			oc.commit();
		}
		oc.delete(cdd2);
		if (!commit) {
			oc.commit();
		}
	}

	public static class CddMember {
		public String name;
	}

}
