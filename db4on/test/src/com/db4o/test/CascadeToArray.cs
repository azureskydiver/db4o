/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.foundation;

namespace com.db4o.test {
    public class CascadeToArray {
        Object[] objects;

        public void configure() {
            Db4o.configure().objectClass(this).cascadeOnUpdate(true);
            Db4o.configure().objectClass(this).cascadeOnDelete(true);
        }

        public void store() {
            Tester.deleteAllInstances(this);
            Tester.deleteAllInstances(new Atom());
            CascadeToArray cta = new CascadeToArray();
            cta.objects = new Object[] {new Atom("stored1"), new Atom(new Atom("storedChild1"), "stored2")};
            Tester.store(cta);
            Tester.commit();
        }

        class CheckUpdate1 : Visitor4{
            public void visit(Object obj) {
                CascadeToArray cta = (CascadeToArray) obj;
                for (int i = 0; i < cta.objects.Length; i++) {
                    Atom atom = (Atom) cta.objects[i];
                    atom.name = "updated";
                    if(atom.child != null){
                        // This one should NOT cascade
                        atom.child.name = "updated";
                    }
                }
                Tester.store(cta);
            }
        }

        class CheckUpdate2 : Visitor4{
            public void visit(Object obj) {
                CascadeToArray cta = (CascadeToArray) obj;
                for (int i = 0; i < cta.objects.Length; i++) {
                    Atom atom = (Atom) cta.objects[i];
                    Tester.ensure(atom.name.Equals("updated"));
                    if(atom.child != null){
                        Tester.ensure( ! atom.child.name.Equals("updated"));
                    }
                }
            }
        }

        public void test() {
            Tester.forEach(this, new CheckUpdate1());
            Tester.reOpen();
            Tester.forEach(this, new CheckUpdate2());
		
            // Cascade-On-Delete Tester: We only want one Atom to remain.
            Tester.commit();
            Tester.reOpen();
            Tester.deleteAllInstances(this);
            Tester.ensureOccurrences(new Atom(), 1);
        }
    }
}
