/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.foundation;

namespace com.db4o.test {
    public class CascadeOnUpdate {

        Object child;

        public void Configure() {
            Db4o.Configure().ObjectClass(this).CascadeOnUpdate(true);
        }

        public void Store() {
            Tester.DeleteAllInstances(this);
            Tester.DeleteAllInstances(new Atom());
            CascadeOnUpdate cou = new CascadeOnUpdate();
            cou.child = new Atom(new Atom("storedChild"), "stored");
            Tester.Store(cou);
            Tester.Commit();
        }

        class CheckUpdate1 : Visitor4{
            public void Visit(Object obj) {
                CascadeOnUpdate cou = (CascadeOnUpdate) obj;
                ((Atom)cou.child).name = "updated";
                ((Atom)cou.child).child.name = "updated";
                Tester.Store(cou);
            }
        }

        class CheckUpdate2 : Visitor4{
            public void Visit(Object obj) {
                CascadeOnUpdate cou = (CascadeOnUpdate) obj;
                Atom atom = (Atom)cou.child;
                Tester.Ensure(atom.name.Equals("updated"));
                Tester.Ensure( ! atom.child.name.Equals("updated"));
            }
        }


        public void Test() {
            Tester.ForEach(this, new CheckUpdate1());
            Tester.ReOpen();
            Tester.ForEach(this, new CheckUpdate2());
        }
    }

}
