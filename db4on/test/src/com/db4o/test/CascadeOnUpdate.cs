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
            Test.deleteAllInstances(this);
            Test.deleteAllInstances(new Atom());
            CascadeOnUpdate cou = new CascadeOnUpdate();
            cou.child = new Atom(new Atom("storedChild"), "stored");
            Test.store(cou);
            Test.commit();
        }

        class CheckUpdate1 : Visitor4{
            public void visit(Object obj) {
                CascadeOnUpdate cou = (CascadeOnUpdate) obj;
                ((Atom)cou.child).name = "updated";
                ((Atom)cou.child).child.name = "updated";
                Test.store(cou);
            }
        }

        class CheckUpdate2 : Visitor4{
            public void visit(Object obj) {
                CascadeOnUpdate cou = (CascadeOnUpdate) obj;
                Atom atom = (Atom)cou.child;
                Test.ensure(atom.name.Equals("updated"));
                Test.ensure( ! atom.child.name.Equals("updated"));
            }
        }


        public void test() {
            Test.forEach(this, new CheckUpdate1());
            Test.reOpen();
            Test.forEach(this, new CheckUpdate2());
        }
    }

}
