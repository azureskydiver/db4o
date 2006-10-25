/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.db4ounit.common.soda.classes.simple;
import com.db4o.*;
import com.db4o.db4ounit.common.soda.*;
import com.db4o.query.*;




public class STStringTestCase extends com.db4o.db4ounit.common.soda.util.SodaBaseTestCase implements STInterface {
	
	public String str;

	public STStringTestCase() {
	}

	public STStringTestCase(String str) {
		this.str = str;
	}

	/** needed for STInterface test */
	public Object returnSomething() {
		return str;
	}

	public Object[] createData() {
		return new Object[] {
			new STStringTestCase(null),
			new STStringTestCase("aaa"),
			new STStringTestCase("bbb"),
			new STStringTestCase("dod")};
	}
	
	public void testEquals() {
		Query q = newQuery();
		q.constrain(_array[2]);
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, _array[2]);
	}

	public void testNotEquals() {
		Query q = newQuery();
		Constraint c = q.constrain(_array[2]);
		q.descend("str").constraints().not();
		
		expect(q, new int[] { 0, 1, 3 });
	}

	public void testDescendantEquals() {
		Query q = newQuery();
		q.constrain(new STStringTestCase());
		q.descend("str").constrain("bbb");
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, new STStringTestCase("bbb"));
	}

	public void testContains() {
		Query q = newQuery();
		Constraint c = q.constrain(new STStringTestCase("od"));
		q.descend("str").constraints().contains();
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, new STStringTestCase("dod"));
	}

	public void testNotContains() {
		Query q = newQuery();
		Constraint c = q.constrain(new STStringTestCase("od"));
		q.descend("str").constraints().contains().not();
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expect(
			q,
			new Object[] { new STStringTestCase(null), new STStringTestCase("aaa"), new STStringTestCase("bbb")});
	}

	public void testLike() {
		Query q = newQuery();
		Constraint c = q.constrain(new STStringTestCase("do"));
		q.descend("str").constraints().like();
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, new STStringTestCase("dod"));
		q = newQuery();
		c = q.constrain(new STStringTestCase("od"));
		q.descend("str").constraints().like();
        
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q,_array[3]);
	}

	public void testStartsWith() {
		Query q = newQuery();
		Constraint c = q.constrain(new STStringTestCase("do"));
		q.descend("str").constraints().startsWith(true);
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, new STStringTestCase("dod"));
		q = newQuery();
		c = q.constrain(new STStringTestCase("od"));
		q.descend("str").constraints().startsWith(true);
		expect(q, new int[] {});
	}

	public void testEndsWith() {
		Query q = newQuery();
		Constraint c = q.constrain(new STStringTestCase("do"));
		q.descend("str").constraints().endsWith(true);
		expect(q, new int[] {});
		q = newQuery();
		c = q.constrain(new STStringTestCase("od"));
		q.descend("str").constraints().endsWith(true);
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, new STStringTestCase("dod"));
		q = newQuery();
		c = q.constrain(new STStringTestCase("D"));
		q.descend("str").constraints().endsWith(false);
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, new STStringTestCase("dod"));
	}

	public void testNotLike() {
		Query q = newQuery();
		Constraint c = q.constrain(new STStringTestCase("aaa"));
		q.descend("str").constraints().like().not();
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expect(
			q,
			new Object[] { new STStringTestCase(null), new STStringTestCase("bbb"), new STStringTestCase("dod")});
		q = newQuery();
		c = q.constrain(new STStringTestCase("xxx"));
		q.descend("str").constraints().like();
		expect(q, new int[] {});
	}

	public void testIdentity() {
		Query q = newQuery();
		Constraint c = q.constrain(new STStringTestCase("aaa"));
		ObjectSet set = q.execute();
		STStringTestCase identityConstraint = (STStringTestCase) set.next();
		identityConstraint.str = "hihs";
		q = newQuery();
		q.constrain(identityConstraint).identity();
		identityConstraint.str = "aaa";
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, new STStringTestCase("aaa"));
	}

	public void testNotIdentity() {
		Query q = newQuery();
		Constraint c = q.constrain(new STStringTestCase("aaa"));
		ObjectSet set = q.execute();
		STStringTestCase identityConstraint = (STStringTestCase) set.next();
		identityConstraint.str = null;
		q = newQuery();
		q.constrain(identityConstraint).identity().not();
		identityConstraint.str = "aaa";
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expect(
			q,
			new Object[] { new STStringTestCase(null), new STStringTestCase("bbb"), new STStringTestCase("dod")});
	}

	public void testNull() {
		Query q = newQuery();
		Constraint c = q.constrain(new STStringTestCase(null));
		q.descend("str").constrain(null);
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, new STStringTestCase(null));
	}

	public void testNotNull() {
		Query q = newQuery();
		Constraint c = q.constrain(new STStringTestCase(null));
		q.descend("str").constrain(null).not();
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expect(
			q,
			new Object[] { new STStringTestCase("aaa"), new STStringTestCase("bbb"), new STStringTestCase("dod")});
	}

	public void testConstraints() {
		Query q = newQuery();
		q.constrain(new STStringTestCase("aaa"));
		q.constrain(new STStringTestCase("bbb"));
		Constraints cs = q.constraints();
		Constraint[] csa = cs.toArray();
		if (csa.length != 2) {
			db4ounit.Assert.fail("Constraints not returned");
		}
	}

	public void testEvaluation() {
		Query q = newQuery();
		q.constrain(new STStringTestCase(null));
		q.constrain(new Evaluation() {
			public void evaluate(Candidate candidate) {
				STStringTestCase sts = (STStringTestCase) candidate.getObject();
				candidate.include(sts.str.indexOf("od") == 1);
			}
		});
		com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, new STStringTestCase("dod"));
	}
	
    public void testCaseInsenstiveContains() {
        Query q = newQuery();
        q.constrain(STStringTestCase.class);
        q.constrain(new Evaluation() {
            public void evaluate(Candidate candidate) {
                STStringTestCase sts = (STStringTestCase) candidate.getObject();
                candidate.include(sts.str.toLowerCase().indexOf("od") >= 0);
            }
        });
        com.db4o.db4ounit.common.soda.util.SodaTestUtil.expectOne(q, new STStringTestCase("dod"));
    }

}
