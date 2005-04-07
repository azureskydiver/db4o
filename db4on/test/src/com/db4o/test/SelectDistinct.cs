/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using com.db4o.query;

namespace com.db4o.test {

    public class SelectDistinct {
        public String name;

        public SelectDistinct() {
        }

        public SelectDistinct(String name) {
            this.name = name;
        }

        public void store() {
            Test.store(new SelectDistinct("a"));
            Test.store(new SelectDistinct("a"));
            Test.store(new SelectDistinct("a"));
            Test.store(new SelectDistinct("b"));
            Test.store(new SelectDistinct("b"));
            Test.store(new SelectDistinct("c"));
            Test.store(new SelectDistinct("c"));
            Test.store(new SelectDistinct("d"));
            Test.store(new SelectDistinct("e"));
        }

        public void test() {
            
            String[] expected = new String[]{"a", "b", "c", "d", "e"};
            
            Query q = Test.query();
            q.constrain(typeof(SelectDistinct));
            q.constrain(new DistinctEvaluation());
            
            ObjectSet objectSet = q.execute();
            while(objectSet.hasNext()) {
                SelectDistinct sd = (SelectDistinct)objectSet.next();
                bool found = false;
                for(int i = 0; i < expected.Length; i++) {
                    if(sd.name.Equals(expected[i])) {
                        expected[i] = null;
                        found = true;
                        break;
                    }
                }
                Test.ensure(found);
            }

            for(int i = 0; i < expected.Length; i++) {
                Test.ensure(expected[i] == null);
            }
        }

        public class DistinctEvaluation: Evaluation{

            private Hashtable ht = new Hashtable();

            public void evaluate(Candidate candidate){
                SelectDistinct sd = (SelectDistinct)candidate.getObject();
                bool isDistinct = ht[sd.name] == null;
                candidate.include(isDistinct);
                if(isDistinct){
                    ht[sd.name] = new Object();
                }
            }
        }
    }
}
