/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com */

package com.db4o.test;

import com.db4o.Db4o;
import com.db4o.ObjectSet;
import com.db4o.config.Configuration;
import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Query;

import db4ounit.extensions.ClientServerTestCase;
import db4ounit.extensions.Db4oUtil;

public class CascadeDeleteDeleted extends ClientServerTestCase {

	public String name;

	public Object untypedMember;

	public CddMember typedMember;

	public CascadeDeleteDeleted() {
	}

	public CascadeDeleteDeleted(String name) {
		this.name = name;
	}

	public void configure(Configuration config) {
		super.configure(config);
		Db4o.configure().objectClass(this).cascadeOnDelete(true);
	}

	public void store(ExtObjectContainer oc) {
		membersFirst(oc, "membersFirst commit");
		membersFirst(oc, "membersFirst");
		twoRef(oc, "twoRef");
		twoRef(oc, "twoRef commit");
		twoRef(oc, "twoRef delete");
		twoRef(oc, "twoRef delete commit");
	}

	private void membersFirst(ExtObjectContainer oc, String name) {
		CascadeDeleteDeleted cdd = new CascadeDeleteDeleted(name);
		cdd.untypedMember = new CddMember();
		cdd.typedMember = new CddMember();
		oc.set(cdd);
	}

	private void twoRef(ExtObjectContainer oc, String name) {
		CascadeDeleteDeleted cdd = new CascadeDeleteDeleted(name);
		cdd.untypedMember = new CddMember();
		cdd.typedMember = new CddMember();
		CascadeDeleteDeleted cdd2 = new CascadeDeleteDeleted(name);
		cdd2.untypedMember = cdd.untypedMember;
		cdd2.typedMember = cdd.typedMember;
		oc.set(cdd);
		oc.set(cdd2);
	}

	public void conc(ExtObjectContainer oc, int seq) {
		if (seq == 1) {
			tMembersFirst(oc, "membersFirst commit");
		} else if (seq == 2) {
			tMembersFirst(oc, "membersFirst");
		} else if (seq == 3) {
			tTwoRef(oc, "twoRef");
		} else if (seq == 4) {
			tTwoRef(oc, "twoRef commit");
		} else if (seq == 5) {
			tTwoRef(oc, "twoRef delete");
		} else if (seq == 6) {
			tTwoRef(oc, "twoRef delete commit");
		}
	}

	public void check(ExtObjectContainer oc) {
		Db4oUtil.assertOccurrences(oc, CddMember.class, 0);
	}

	private void tMembersFirst(ExtObjectContainer oc, String name) {
		boolean commit = name.indexOf("commit") > 1;
		Query q = oc.query();
		q.constrain(CascadeDeleteDeleted.class);
		q.descend("name").constrain(name);
		ObjectSet objectSet = q.execute();
		CascadeDeleteDeleted cdd = (CascadeDeleteDeleted) objectSet.next();
		oc.delete(cdd.untypedMember);
		oc.delete(cdd.typedMember);
		if(commit){
            oc.commit();
        }
        oc.delete(cdd);
        if(!commit){
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
		CascadeDeleteDeleted cdd = (CascadeDeleteDeleted) objectSet.next();
		CascadeDeleteDeleted cdd2 = (CascadeDeleteDeleted) objectSet.next();
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
