/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.test;

import java.io.Serializable;

import com.db4o.ext.ExtObjectContainer;
import com.db4o.query.Candidate;
import com.db4o.query.Evaluation;
import com.db4o.query.Query;

import db4ounit.Assert;
import db4ounit.extensions.ClientServerTestCase;

/**
 * demonstrates a case-insensitive query using an Evaluation
 */
public class CaseInsensitive extends ClientServerTestCase implements
		Serializable {

	public String name;

	public CaseInsensitive() {
	}

	public CaseInsensitive(String name) {
		this.name = name;
	}

	public void store(ExtObjectContainer oc) {
		oc.set(new CaseInsensitive("HelloWorld"));
	}

	public void concQueryCaseInsenstive(ExtObjectContainer oc) {
		Query q = oc.query();
		q.constrain(CaseInsensitive.class);
		q.constrain(new CaseInsensitiveEvaluation("helloworld"));
		Assert.areEqual(1, q.execute().size());
	}

}

class CaseInsensitiveEvaluation implements Evaluation {
	String name;

	public CaseInsensitiveEvaluation(String name) {
		this.name = name;
	}

	public void evaluate(Candidate candidate) {
		CaseInsensitive ci = (CaseInsensitive) candidate.getObject();
		candidate.include(ci.name.toLowerCase().equals(name.toLowerCase()));
	}

}
