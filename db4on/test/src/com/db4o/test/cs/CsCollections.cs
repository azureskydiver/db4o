/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using j4o.lang;

namespace com.db4o.test.cs {

    public class CsCollections {
        ArrayList arrayList;
        Hashtable hashTable;
        Queue queue;
        Stack stack;

        public CsCollections(){
        }

        public void store(){
            Test.deleteAllInstances(this);
            CsCollections csc = new CsCollections();
            csc.fill();
            Test.store(csc);
            Test.commit();
        }

        public void test(){
            CsCollections csc = (CsCollections)Test.getOne(this);
            csc.check();
        }

        private void fill(){
            
            arrayList = new ArrayList();
            fill(arrayList);
            
            hashTable = new Hashtable();
            fill(hashTable);

            queue = new Queue();
            queue.Enqueue(1);
            queue.Enqueue("hi");
            queue.Enqueue(new Atom("foo"));

            stack = new Stack();
            stack.Push(1);
            stack.Push("hi");
            stack.Push(new Atom("foo"));
        }

        private void check(){
            check(arrayList);
            check(hashTable);

            Test.ensure(queue.Dequeue().Equals(1));
            Test.ensure(queue.Dequeue().Equals("hi"));
            Test.ensure(queue.Dequeue().Equals(new Atom("foo")));

            Test.ensure(stack.Pop().Equals(new Atom("foo")));
            Test.ensure(stack.Pop().Equals("hi"));
            Test.ensure(stack.Pop().Equals(1));
        }

        private void fill(IList list){
            list.Add(1);
            list.Add(null);
            list.Add(new Atom("foo"));
            list.Add("foo");
        }


        private void check(IList list){
            Test.ensure(list[0].Equals(1));
            Test.ensure(list[1] == null);
            Test.ensure(list[2].Equals(new Atom("foo")));
            Test.ensure(list[3].Equals("foo"));
        }

        private void fill(IDictionary dict){
            dict[1] = 1;
            dict["hey"] = "ho";
            dict[new Atom("foo")] = new Atom("bar");
            dict[4] = "Yoman";
        }

        private void check(IDictionary dict){
            Test.ensure(dict[1].Equals(1));
            Test.ensure(dict["hey"].Equals("ho"));
            Test.ensure(dict[new Atom("foo")].Equals(new Atom("bar")));
            Test.ensure(dict[4].Equals("Yoman"));
        }

    }
}
