/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

using System;

namespace com.db4o.test {
    public class CascadeToArray {
        Object[] objects;

        public void configure() {
            Db4o.configure().objectClass(this).cascadeOnUpdate(true);
            Db4o.configure().objectClass(this).cascadeOnDelete(true);
        }

        public void store() {
            Test.deleteAllInstances(this);
            Test.deleteAllInstances(new Atom());
            CascadeToArray cta = new CascadeToArray();
            cta.objects = new Object[] {new Atom("stored1"), new Atom(new Atom("storedChild1"), "stored2")};
            Test.store(cta);
            Test.commit();
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
                Test.store(cta);
            }
        }

        class CheckUpdate2 : Visitor4{
            public void visit(Object obj) {
                CascadeToArray cta = (CascadeToArray) obj;
                for (int i = 0; i < cta.objects.Length; i++) {
                    Atom atom = (Atom) cta.objects[i];
                    Test.ensure(atom.name.Equals("updated"));
                    if(atom.child != null){
                        Test.ensure( ! atom.child.name.Equals("updated"));
                    }
                }
            }
        }

        public void test() {
            Test.forEach(this, new CheckUpdate1());
            Test.reOpen();
            Test.forEach(this, new CheckUpdate2());
		
            // Cascade-On-Delete Test: We only want one Atom to remain.
            Test.commit();
            Test.reOpen();
            Test.deleteAllInstances(this);
            Test.ensureOccurrences(new Atom(), 1);
        }
    }
}
