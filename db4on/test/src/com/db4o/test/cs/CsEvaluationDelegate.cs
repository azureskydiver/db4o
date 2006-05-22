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
      
        public void Store() {
            Tester.DeleteAllInstances(this);
            name = "one";
            Tester.Store(this);
            CsEvaluationDelegate se1 = new CsEvaluationDelegate();
            se1.child = new CsEvaluationDelegate();
            se1.child.name = "three";
            se1.name = "two";
            Tester.Store(se1);
        }
      
        public void TestStaticMethodDelegate() {
            RunEvaluationDelegateTest(new EvaluationDelegate(_evaluate));
        }
        
        public void TestInstanceMethodDelegate() {
            RunEvaluationDelegateTest(new EvaluationDelegate(new NameCondition("three")._evaluate));
        }
        
        void RunEvaluationDelegateTest(EvaluationDelegate evaluation) {
	        Query q1 = Tester.Query();
            Query cq1 = q1;
            q1.Constrain(j4o.lang.Class.GetClassForObject(this));
            cq1 = cq1.Descend("child");
            cq1.Constrain(evaluation);
            ObjectSet os = q1.Execute();
            Tester.Ensure(os.Size() == 1);
            CsEvaluationDelegate se = (CsEvaluationDelegate)os.Next();
            Tester.Ensure(se.name.Equals("two"));
        }

        public static void _evaluate(Candidate candidate) {
            CsEvaluationDelegate obj = ((CsEvaluationDelegate)candidate.GetObject());
			candidate.Include(obj.name.Equals("three"));
        }
        
        class NameCondition {
        	string _name;
        	
        	public NameCondition(string name) {
        		_name = name;
        	}
        	
        	public void _evaluate(Candidate candidate) {
        		CsEvaluationDelegate obj = ((CsEvaluationDelegate)candidate.GetObject());
				candidate.Include(obj.name.Equals(_name));
        	}
        }
    }
}
