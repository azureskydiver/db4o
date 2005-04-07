/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.ext;
using com.db4o.reflect.net;

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
                Test.ensure(((NetClass)f.getStoredType()).getNetType()  == typeof(String));
                cases[0] = true;
            }
            if(f.getName().Equals("bar")){
                Test.ensure(f.get(this).Equals(10));
                Test.ensure(((NetClass)f.getStoredType()).getNetType()  == typeof(int));
                cases[1] = true;
            }
            if(f.getName().Equals("atoms")){
                Test.ensure(((NetClass)f.getStoredType()).getNetType()  == typeof(Atom));
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
