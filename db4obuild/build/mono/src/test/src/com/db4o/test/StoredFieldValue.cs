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
using com.db4o.ext;

namespace com.db4o.test
{
	/// <summary>
	/// Summary description for StoredFieldValue.
	/// </summary>
	public class StoredFieldValue {
    String foo;
    int bar;
    Atom[] atoms;
    
    public void storeOne(){
        foo = "foo";
        bar = 10;
        atoms = new Atom[2];
        atoms[0] = new Atom("one");
        atoms[1] = new Atom("two");
    }
    
    public void testOne(){
        ExtObjectContainer oc = Test.objectContainer();
        StoredClass sc = oc.storedClass(this);
        StoredField[] sf = sc.getStoredFields();
        bool[] cases = new bool[3];
        for (int i = 0; i < sf.Length; i++) {
            StoredField f = sf[i];
            if(f.getName().Equals("foo")){
                Test.ensure(f.get(this).Equals("foo"));
                Test.ensure(f.getStoredType() == typeof(String));
                cases[0] = true;
            }
            if(f.getName().Equals("bar")){
                Test.ensure(f.get(this).Equals(10));
                Test.ensure(f.getStoredType() == typeof(int));
                cases[1] = true;
            }
            if(f.getName().Equals("atoms")){
                Test.ensure(f.getStoredType() == typeof(Atom));
                Test.ensure(f.isArray());
                Atom[] at = (Atom[])f.get(this);
                Test.ensure(at[0].name.Equals("one"));
                Test.ensure(at[1].name.Equals("two"));
                cases[2] = true;
            }
        }
        for (int i = 0; i < cases.Length; i++) {
            Test.ensure(cases[i]);
        }
    }
	}
}
