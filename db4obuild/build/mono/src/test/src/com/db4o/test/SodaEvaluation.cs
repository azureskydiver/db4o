/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

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
      
      public void store() {
         Test.deleteAllInstances(this);
         name = "one";
         Test.store(this);
         SodaEvaluation se1 = new SodaEvaluation();
         se1.child = new SodaEvaluation();
         se1.child.name = "three";
         se1.name = "two";
         Test.store(se1);
      }
      
      public void test() {
         String nameConstraint1 = "three";
         Query q1 = Test.query();
         Query cq1 = q1;
         q1.constrain(j4o.lang.Class.getClassForObject(this));
         cq1 = cq1.descend("child");
         cq1.constrain(new MyEvaluation());
         ObjectSet os = q1.execute();
         Test.ensure(os.size() == 1);
         SodaEvaluation se = (SodaEvaluation)os.next();
         Test.ensure(se.name.Equals("two"));
      }
   }

	class MyEvaluation : Evaluation{
		String nameConstraint1 = "three";

            public void evaluate(Candidate candidate) 
			{
               candidate.include(((SodaEvaluation)candidate.getObject()).name.Equals(nameConstraint1));
            }
	}


}