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

        public void Store() {
            Tester.Store(new SelectDistinct("a"));
            Tester.Store(new SelectDistinct("a"));
            Tester.Store(new SelectDistinct("a"));
            Tester.Store(new SelectDistinct("b"));
            Tester.Store(new SelectDistinct("b"));
            Tester.Store(new SelectDistinct("c"));
            Tester.Store(new SelectDistinct("c"));
            Tester.Store(new SelectDistinct("d"));
            Tester.Store(new SelectDistinct("e"));
        }

        public void Test() {
            
            String[] expected = new String[]{"a", "b", "c", "d", "e"};
            
            Query q = Tester.Query();
            q.Constrain(typeof(SelectDistinct));
            q.Constrain(new DistinctEvaluation());
            
            ObjectSet objectSet = q.Execute();
            while(objectSet.HasNext()) {
                SelectDistinct sd = (SelectDistinct)objectSet.Next();
                bool found = false;
                for(int i = 0; i < expected.Length; i++) {
                    if(sd.name.Equals(expected[i])) {
                        expected[i] = null;
                        found = true;
                        break;
                    }
                }
                Tester.Ensure(found);
            }

            for(int i = 0; i < expected.Length; i++) {
                Tester.Ensure(expected[i] == null);
            }
        }

        public class DistinctEvaluation: Evaluation{

            private Hashtable ht = new Hashtable();

            public void Evaluate(Candidate candidate){
                SelectDistinct sd = (SelectDistinct)candidate.GetObject();
                bool isDistinct = ht[sd.name] == null;
                candidate.Include(isDistinct);
                if(isDistinct){
                    ht[sd.name] = new Object();
                }
            }
        }
    }
}
