/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.foundation;

namespace com.db4o.test {
    public class CascadeToArray {
        Object[] objects;

        public void Configure() {
            Db4o.Configure().ObjectClass(this).CascadeOnUpdate(true);
            Db4o.Configure().ObjectClass(this).CascadeOnDelete(true);
        }

        public void Store() {
            Tester.DeleteAllInstances(this);
            Tester.DeleteAllInstances(new Atom());
            CascadeToArray cta = new CascadeToArray();
            cta.objects = new Object[] {new Atom("stored1"), new Atom(new Atom("storedChild1"), "stored2")};
            Tester.Store(cta);
            Tester.Commit();
        }

        class CheckUpdate1 : Visitor4{
            public void Visit(Object obj) {
                CascadeToArray cta = (CascadeToArray) obj;
                for (int i = 0; i < cta.objects.Length; i++) {
                    Atom atom = (Atom) cta.objects[i];
                    atom.name = "updated";
                    if(atom.child != null){
                        // This one should NOT cascade
                        atom.child.name = "updated";
                    }
                }
                Tester.Store(cta);
            }
        }

        class CheckUpdate2 : Visitor4{
            public void Visit(Object obj) {
                CascadeToArray cta = (CascadeToArray) obj;
                for (int i = 0; i < cta.objects.Length; i++) {
                    Atom atom = (Atom) cta.objects[i];
                    Tester.Ensure(atom.name.Equals("updated"));
                    if(atom.child != null){
                        Tester.Ensure( ! atom.child.name.Equals("updated"));
                    }
                }
            }
        }

        public void Test() {
            Tester.ForEach(this, new CheckUpdate1());
            Tester.ReOpen();
            Tester.ForEach(this, new CheckUpdate2());
		
            // Cascade-On-Delete Tester: We only want one Atom to remain.
            Tester.Commit();
            Tester.ReOpen();
            Tester.DeleteAllInstances(this);
            Tester.EnsureOccurrences(new Atom(), 1);
        }
    }
}
