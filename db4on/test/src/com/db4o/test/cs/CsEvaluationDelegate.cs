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
            Tester.deleteAllInstances(this);
            name = "one";
            Tester.store(this);
            CsEvaluationDelegate se1 = new CsEvaluationDelegate();
            se1.child = new CsEvaluationDelegate();
            se1.child.name = "three";
            se1.name = "two";
            Tester.store(se1);
        }
      
        public void testStaticMethodDelegate() {
            runEvaluationDelegateTest(new EvaluationDelegate(_evaluate));
        }
        
        public void testInstanceMethodDelegate() {
            runEvaluationDelegateTest(new EvaluationDelegate(new NameCondition("three")._evaluate));
        }
        
        void runEvaluationDelegateTest(EvaluationDelegate evaluation) {
	        Query q1 = Tester.query();
            Query cq1 = q1;
            q1.constrain(j4o.lang.Class.getClassForObject(this));
            cq1 = cq1.descend("child");
            cq1.constrain(evaluation);
            ObjectSet os = q1.execute();
            Tester.ensure(os.size() == 1);
            CsEvaluationDelegate se = (CsEvaluationDelegate)os.next();
            Tester.ensure(se.name.Equals("two"));
        }

        public static void _evaluate(Candidate candidate) {
            CsEvaluationDelegate obj = ((CsEvaluationDelegate)candidate.getObject());
			candidate.include(obj.name.Equals("three"));
        }
        
        class NameCondition {
        	string _name;
        	
        	public NameCondition(string name) {
        		_name = name;
        	}
        	
        	public void _evaluate(Candidate candidate) {
        		CsEvaluationDelegate obj = ((CsEvaluationDelegate)candidate.getObject());
				candidate.include(obj.name.Equals(_name));
        	}
        }
    }
}
