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
