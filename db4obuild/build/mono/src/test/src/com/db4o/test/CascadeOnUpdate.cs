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
