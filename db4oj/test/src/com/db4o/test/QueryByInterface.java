package com.db4o.test;

import com.db4o.*;
import com.db4o.query.*;

public class QueryByInterface {
	
	private static class SimpleEvaluation implements Evaluation {
		public void evaluate(Candidate candidate) {
			candidate.include(((IFoo)candidate.getObject()).s().equals("A"));
		}
	}

	public static interface IFoo {
		String s();
	}
	
	public static class Bar implements IFoo {
		public int i;

		public Bar(int i) {
			this.i = i;
		}

		public String s() {
			return String.valueOf(i);
		}
	}

	public static class Baz implements IFoo {
		public String s;

		public Baz(String s) {
			this.s = s;
		}

		public String s() {
			return s;
		}
	}

	public void store() {
		//Test.objectContainer().set(new Bar(1));
		//Test.objectContainer().set(new Baz("A"));
	}
	
	public void testSODA() {
		Query query=Test.objectContainer().query();
		Constraint constraint=query.constrain(IFoo.class);
		Test.ensure(constraint!=null);
		query.descend("s").constrain("A");
		ObjectSet result=query.execute();
		Test.ensure(result.size()==1);
	}

	public void testEvaluation() {
		Query query=Test.objectContainer().query();
		Constraint constraint=query.constrain(IFoo.class);
		Test.ensure(constraint!=null);
		query.constrain(new SimpleEvaluation());
		ObjectSet result=query.execute();
		Test.ensure(result.size()==1);
	}
}
