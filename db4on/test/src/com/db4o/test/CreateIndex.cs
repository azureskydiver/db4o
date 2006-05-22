/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;

namespace com.db4o.test {

    public class CreateIndex {

        public String i_name;
        public int i_int;

        public CreateIndex() {
        }

        public CreateIndex(String name) {
            this.i_name = name;
        }

        public CreateIndex(int a_int) {
            i_int = a_int;
        }

        public void Configure() {
            Db4o.Configure().ObjectClass(this).ObjectField("i_name").Indexed(true);
            Db4o.Configure().ObjectClass(this).ObjectField("i_int").Indexed(true);
        }

        public void Store() {
            Tester.DeleteAllInstances(this);

            Tester.Store(new CreateIndex("a"));
            Tester.Store(new CreateIndex("c"));
            Tester.Store(new CreateIndex("b"));
            Tester.Store(new CreateIndex("f"));
            Tester.Store(new CreateIndex("e"));

            Tester.Store(new CreateIndex(1));
            Tester.Store(new CreateIndex(5));
            Tester.Store(new CreateIndex(7));
            Tester.Store(new CreateIndex(3));
            Tester.Store(new CreateIndex(2));
            Tester.Store(new CreateIndex(3));

            TQueryB();
            TQueryInts(5);
        }

        public void Test() {
        
            TQueryNull(6);
            TQueryNull(6);
        
            Tester.Store(new CreateIndex("d"));
            TQueryB();
            TUpdateB();
            Tester.Store(new CreateIndex("z"));
            Tester.Store(new CreateIndex("y"));
            Tester.ReOpen();
            TQueryB();

            TQueryInts(8);
        }

        private void TQueryInts(int expectedZeroSize) {
        
            Query q = Tester.Query();
            q.Constrain(typeof(CreateIndex));
            q.Descend("i_int").Constrain(0);
            int zeroSize = q.Execute().Size();
            Tester.Ensure(zeroSize == expectedZeroSize);
        
            q = Tester.Query();
            q.Constrain(typeof(CreateIndex));
            q.Descend("i_int").Constrain(4).Greater().Equal();
            TExpectInts(q, new int[] { 5, 7 });
         
            q = Tester.Query();
            q.Constrain(typeof(CreateIndex));
            q.Descend("i_int").Constrain(4).Greater();
            TExpectInts(q, new int[] { 5, 7 });

            q = Tester.Query();
            q.Constrain(typeof(CreateIndex));
            q.Descend("i_int").Constrain(3).Greater();
            TExpectInts(q, new int[] { 5, 7 });

            q = Tester.Query();
            q.Constrain(typeof(CreateIndex));
            q.Descend("i_int").Constrain(3).Greater().Equal();
            TExpectInts(q, new int[] { 3, 3, 5, 7 });

            q = Tester.Query();
            q.Constrain(typeof(CreateIndex));
            q.Descend("i_int").Constrain(2).Greater().Equal();
            TExpectInts(q, new int[] { 2, 3, 3, 5, 7 });
            q = Tester.Query();

            q = Tester.Query();
            q.Constrain(typeof(CreateIndex));
            q.Descend("i_int").Constrain(2).Greater();
            TExpectInts(q, new int[] { 3, 3, 5, 7 });

            q = Tester.Query();
            q.Constrain(typeof(CreateIndex));
            q.Descend("i_int").Constrain(1).Greater().Equal();
            TExpectInts(q, new int[] { 1, 2, 3, 3, 5, 7 });

            q = Tester.Query();
            q.Constrain(typeof(CreateIndex));
            q.Descend("i_int").Constrain(1).Greater();
            TExpectInts(q, new int[] { 2, 3, 3, 5, 7 });

            q = Tester.Query();
            q.Constrain(typeof(CreateIndex));
            q.Descend("i_int").Constrain(4).Smaller();
            TExpectInts(q, new int[] { 1, 2, 3, 3 }, expectedZeroSize);

            q = Tester.Query();
            q.Constrain(typeof(CreateIndex));
            q.Descend("i_int").Constrain(4).Smaller().Equal();
            TExpectInts(q, new int[] { 1, 2, 3, 3 }, expectedZeroSize);

            q = Tester.Query();
            q.Constrain(typeof(CreateIndex));
            q.Descend("i_int").Constrain(3).Smaller();
            TExpectInts(q, new int[] { 1, 2 }, expectedZeroSize);

            q = Tester.Query();
            q.Constrain(typeof(CreateIndex));
            q.Descend("i_int").Constrain(3).Smaller().Equal();
            TExpectInts(q, new int[] { 1, 2, 3, 3 }, expectedZeroSize);

            q = Tester.Query();
            q.Constrain(typeof(CreateIndex));
            q.Descend("i_int").Constrain(2).Smaller().Equal();
            TExpectInts(q, new int[] { 1, 2 }, expectedZeroSize);
            q = Tester.Query();

            q = Tester.Query();
            q.Constrain(typeof(CreateIndex));
            q.Descend("i_int").Constrain(2).Smaller();
            TExpectInts(q, new int[] { 1 }, expectedZeroSize);

            q = Tester.Query();
            q.Constrain(typeof(CreateIndex));
            q.Descend("i_int").Constrain(1).Smaller().Equal();
            TExpectInts(q, new int[] { 1 }, expectedZeroSize);

            q = Tester.Query();
            q.Constrain(typeof(CreateIndex));
            q.Descend("i_int").Constrain(1).Smaller();
            TExpectInts(q, new int[] {}, expectedZeroSize);

        }

        private void TExpectInts(Query q, int[] ints, int zeroSize) {
            ObjectSet res = q.Execute();
            Tester.Ensure(res.Size() == (ints.Length + zeroSize));
            while (res.HasNext()) {
                CreateIndex ci = (CreateIndex)res.Next();
                for (int i = 0; i < ints.Length; i++) {
                    if (ints[i] == ci.i_int) {
                        ints[i] = 0;
                        break;
                    }
                }
            }
            for (int i = 0; i < ints.Length; i++) {
                Tester.Ensure(ints[i] == 0);
            }
        }

        private void TExpectInts(Query q, int[] ints) {
            TExpectInts(q, ints, 0);
        }

        private void TQueryB() {
            ObjectSet res = Query("b");
            Tester.Ensure(res.Size() == 1);
            CreateIndex ci = (CreateIndex)res.Next();
            Tester.Ensure(ci.i_name.Equals("b"));
        }
    
        private void TQueryNull(int expect) {
            ObjectSet res = Query(null);
            Tester.Ensure(res.Size() == expect);
            while(res.HasNext()){
                CreateIndex ci = (CreateIndex)res.Next();
                Tester.Ensure(ci.i_name == null);
            }
        }

        private void TUpdateB() {
            ObjectSet res = Query("b");
            CreateIndex ci = (CreateIndex)res.Next();
            ci.i_name = "j";
            Tester.ObjectContainer().Set(ci);
            res = Query("b");
            Tester.Ensure(res.Size() == 0);
            res = Query("j");
            Tester.Ensure(res.Size() == 1);
            ci.i_name = "b";
            Tester.ObjectContainer().Set(ci);
            TQueryB();
        }

        private ObjectSet Query(String n) {
            Query q = Tester.Query();
            q.Constrain(typeof(CreateIndex));
            q.Descend("i_name").Constrain(n);
            return q.Execute();
        }

    }
}