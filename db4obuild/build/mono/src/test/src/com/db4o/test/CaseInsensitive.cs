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

namespace com.db4o.test {

    /**
     * demonstrates a case-insensitive query using an Evaluation
     */
    public class CaseInsensitive {
    
        public String name;
    
        public CaseInsensitive() {
        }
    
        public CaseInsensitive(String name) {
            this.name = name;
        }
    
        public void store(){
            Test.store(new CaseInsensitive("HelloWorld"));
        }
    
        public void test(){
            Test.ensure(queryingCaseInsensitiveResults("heLLOworld") == 1);
        }
    
        private int queryingCaseInsensitiveResults(string name){
            ObjectContainer objectContainer = Test.objectContainer();
            Query q = objectContainer.query();
            q.constrain(typeof(CaseInsensitive));
            q.constrain(new CaseInsensitiveEvaluation(name));
            return q.execute().size();
        }
    }


    public class CaseInsensitiveEvaluation: Evaluation {

        String name;

        public CaseInsensitiveEvaluation(String name){
            this.name = name;
        }

        public void evaluate(Candidate candidate) {
            CaseInsensitive ci = (CaseInsensitive)candidate.getObject();
            candidate.include(ci.name.ToLower().Equals(name.ToLower()));
        }
    }
}
