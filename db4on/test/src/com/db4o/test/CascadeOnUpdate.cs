/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.foundation;

namespace com.db4o.test {
    public class CascadeOnUpdate {

        Object child;

        public void configure() {
            Db4o.configure().objectClass(this).cascadeOnUpdate(true);
        }

        public void store() {
            Tester.deleteAllInstances(this);
            Tester.deleteAllInstances(new Atom());
            CascadeOnUpdate cou = new CascadeOnUpdate();
            cou.child = new Atom(new Atom("storedChild"), "stored");
            Tester.store(cou);
            Tester.commit();
        }

        class CheckUpdate1 : Visitor4{
            public void visit(Object obj) {
                CascadeOnUpdate cou = (CascadeOnUpdate) obj;
                ((Atom)cou.child).name = "updated";
                ((Atom)cou.child).child.name = "updated";
                Tester.store(cou);
            }
        }

        class CheckUpdate2 : Visitor4{
            public void visit(Object obj) {
                CascadeOnUpdate cou = (CascadeOnUpdate) obj;
                Atom atom = (Atom)cou.child;
                Tester.ensure(atom.name.Equals("updated"));
                Tester.ensure( ! atom.child.name.Equals("updated"));
            }
        }


        public void test() {
            Tester.forEach(this, new CheckUpdate1());
            Tester.reOpen();
            Tester.forEach(this, new CheckUpdate2());
        }
    }

}
