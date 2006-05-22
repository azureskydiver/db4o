/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
namespace com.db4o.test {

   public class SodaEvaluation {
      
      public SodaEvaluation() : base() {
      }
      internal SodaEvaluation child;
      internal String name;
      
      public void Store() {
         Tester.DeleteAllInstances(this);
         name = "one";
         Tester.Store(this);
         SodaEvaluation se1 = new SodaEvaluation();
         se1.child = new SodaEvaluation();
         se1.child.name = "three";
         se1.name = "two";
         Tester.Store(se1);
      }
      
      public void Test() {
         String nameConstraint1 = "three";
         Query q1 = Tester.Query();
         Query cq1 = q1;
         q1.Constrain(j4o.lang.Class.GetClassForObject(this));
         cq1 = cq1.Descend("child");
         cq1.Constrain(new MyEvaluation());
         ObjectSet os = q1.Execute();
         Tester.Ensure(os.Size() == 1);
         SodaEvaluation se = (SodaEvaluation)os.Next();
         Tester.Ensure(se.name.Equals("two"));
      }
   }

	class MyEvaluation : Evaluation{
		String nameConstraint1 = "three";

            public void Evaluate(Candidate candidate) 
			{
               candidate.Include(((SodaEvaluation)candidate.GetObject()).name.Equals(nameConstraint1));
            }
	}


}