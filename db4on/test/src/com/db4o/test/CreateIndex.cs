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

        public void configure() {
            Db4o.configure().objectClass(this).objectField("i_name").indexed(true);
            Db4o.configure().objectClass(this).objectField("i_int").indexed(true);
        }

        public void store() {
            Tester.deleteAllInstances(this);

            Tester.store(new CreateIndex("a"));
            Tester.store(new CreateIndex("c"));
            Tester.store(new CreateIndex("b"));
            Tester.store(new CreateIndex("f"));
            Tester.store(new CreateIndex("e"));

            Tester.store(new CreateIndex(1));
            Tester.store(new CreateIndex(5));
            Tester.store(new CreateIndex(7));
            Tester.store(new CreateIndex(3));
            Tester.store(new CreateIndex(2));
            Tester.store(new CreateIndex(3));

            tQueryB();
            tQueryInts(5);
        }

        public void test() {
        
            tQueryNull(6);
            tQueryNull(6);
        
            Tester.store(new CreateIndex("d"));
            tQueryB();
            tUpdateB();
            Tester.store(new CreateIndex("z"));
            Tester.store(new CreateIndex("y"));
            Tester.reOpen();
            tQueryB();

            tQueryInts(8);
        }

        private void tQueryInts(int expectedZeroSize) {
        
            Query q = Tester.query();
            q.constrain(typeof(CreateIndex));
            q.descend("i_int").constrain(0);
            int zeroSize = q.execute().size();
            Tester.ensure(zeroSize == expectedZeroSize);
        
            q = Tester.query();
            q.constrain(typeof(CreateIndex));
            q.descend("i_int").constrain(4).greater().equal();
            tExpectInts(q, new int[] { 5, 7 });
         
            q = Tester.query();
            q.constrain(typeof(CreateIndex));
            q.descend("i_int").constrain(4).greater();
            tExpectInts(q, new int[] { 5, 7 });

            q = Tester.query();
            q.constrain(typeof(CreateIndex));
            q.descend("i_int").constrain(3).greater();
            tExpectInts(q, new int[] { 5, 7 });

            q = Tester.query();
            q.constrain(typeof(CreateIndex));
            q.descend("i_int").constrain(3).greater().equal();
            tExpectInts(q, new int[] { 3, 3, 5, 7 });

            q = Tester.query();
            q.constrain(typeof(CreateIndex));
            q.descend("i_int").constrain(2).greater().equal();
            tExpectInts(q, new int[] { 2, 3, 3, 5, 7 });
            q = Tester.query();

            q = Tester.query();
            q.constrain(typeof(CreateIndex));
            q.descend("i_int").constrain(2).greater();
            tExpectInts(q, new int[] { 3, 3, 5, 7 });

            q = Tester.query();
            q.constrain(typeof(CreateIndex));
            q.descend("i_int").constrain(1).greater().equal();
            tExpectInts(q, new int[] { 1, 2, 3, 3, 5, 7 });

            q = Tester.query();
            q.constrain(typeof(CreateIndex));
            q.descend("i_int").constrain(1).greater();
            tExpectInts(q, new int[] { 2, 3, 3, 5, 7 });

            q = Tester.query();
            q.constrain(typeof(CreateIndex));
            q.descend("i_int").constrain(4).smaller();
            tExpectInts(q, new int[] { 1, 2, 3, 3 }, expectedZeroSize);

            q = Tester.query();
            q.constrain(typeof(CreateIndex));
            q.descend("i_int").constrain(4).smaller().equal();
            tExpectInts(q, new int[] { 1, 2, 3, 3 }, expectedZeroSize);

            q = Tester.query();
            q.constrain(typeof(CreateIndex));
            q.descend("i_int").constrain(3).smaller();
            tExpectInts(q, new int[] { 1, 2 }, expectedZeroSize);

            q = Tester.query();
            q.constrain(typeof(CreateIndex));
            q.descend("i_int").constrain(3).smaller().equal();
            tExpectInts(q, new int[] { 1, 2, 3, 3 }, expectedZeroSize);

            q = Tester.query();
            q.constrain(typeof(CreateIndex));
            q.descend("i_int").constrain(2).smaller().equal();
            tExpectInts(q, new int[] { 1, 2 }, expectedZeroSize);
            q = Tester.query();

            q = Tester.query();
            q.constrain(typeof(CreateIndex));
            q.descend("i_int").constrain(2).smaller();
            tExpectInts(q, new int[] { 1 }, expectedZeroSize);

            q = Tester.query();
            q.constrain(typeof(CreateIndex));
            q.descend("i_int").constrain(1).smaller().equal();
            tExpectInts(q, new int[] { 1 }, expectedZeroSize);

            q = Tester.query();
            q.constrain(typeof(CreateIndex));
            q.descend("i_int").constrain(1).smaller();
            tExpectInts(q, new int[] {}, expectedZeroSize);

        }

        private void tExpectInts(Query q, int[] ints, int zeroSize) {
            ObjectSet res = q.execute();
            Tester.ensure(res.size() == (ints.Length + zeroSize));
            while (res.hasNext()) {
                CreateIndex ci = (CreateIndex)res.next();
                for (int i = 0; i < ints.Length; i++) {
                    if (ints[i] == ci.i_int) {
                        ints[i] = 0;
                        break;
                    }
                }
            }
            for (int i = 0; i < ints.Length; i++) {
                Tester.ensure(ints[i] == 0);
            }
        }

        private void tExpectInts(Query q, int[] ints) {
            tExpectInts(q, ints, 0);
        }

        private void tQueryB() {
            ObjectSet res = query("b");
            Tester.ensure(res.size() == 1);
            CreateIndex ci = (CreateIndex)res.next();
            Tester.ensure(ci.i_name.Equals("b"));
        }
    
        private void tQueryNull(int expect) {
            ObjectSet res = query(null);
            Tester.ensure(res.size() == expect);
            while(res.hasNext()){
                CreateIndex ci = (CreateIndex)res.next();
                Tester.ensure(ci.i_name == null);
            }
        }

        private void tUpdateB() {
            ObjectSet res = query("b");
            CreateIndex ci = (CreateIndex)res.next();
            ci.i_name = "j";
            Tester.objectContainer().set(ci);
            res = query("b");
            Tester.ensure(res.size() == 0);
            res = query("j");
            Tester.ensure(res.size() == 1);
            ci.i_name = "b";
            Tester.objectContainer().set(ci);
            tQueryB();
        }

        private ObjectSet query(String n) {
            Query q = Tester.query();
            q.constrain(typeof(CreateIndex));
            q.descend("i_name").constrain(n);
            return q.execute();
        }

    }
}