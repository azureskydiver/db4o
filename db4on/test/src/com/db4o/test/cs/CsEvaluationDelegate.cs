/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.query;

namespace com.db4o.test.cs
{
	/// <summary>
	/// Summary description for CsEvaluationDelegate.
	/// </summary>
	public class CsEvaluationDelegate
	{
        internal CsEvaluationDelegate child;
        internal String name;
      
        public void store() {
            Test.deleteAllInstances(this);
            name = "one";
            Test.store(this);
            CsEvaluationDelegate se1 = new CsEvaluationDelegate();
            se1.child = new CsEvaluationDelegate();
            se1.child.name = "three";
            se1.name = "two";
            Test.store(se1);
        }
      
        public void test() {
            Query q1 = Test.query();
            Query cq1 = q1;
            q1.constrain(j4o.lang.Class.getClassForObject(this));
            cq1 = cq1.descend("child");
            cq1.constrain(new EvaluationDelegate(evaluate));
            ObjectSet os = q1.execute();
            Test.ensure(os.size() == 1);
            CsEvaluationDelegate se = (CsEvaluationDelegate)os.next();
            Test.ensure(se.name.Equals("two"));
        }

        public void evaluate(Candidate candidate) {
            candidate.include(((CsEvaluationDelegate)candidate.getObject()).name.Equals("three"));
        }

    }
}
