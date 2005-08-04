/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o.ext;
using com.db4o.reflect;

namespace com.db4o.test
{
	/// <summary>
	/// Summary description for StoredFieldValue.
	/// </summary>
	public class StoredFieldValue 
	{
		String foo;
		int bar;
		Atom[] atoms;
    
		public void storeOne()
		{
			foo = "foo";
			bar = 10;
			atoms = new Atom[2];
			atoms[0] = new Atom("one");
			atoms[1] = new Atom("two");
		}
    
		public void testOne()
		{
			ExtObjectContainer oc = Tester.objectContainer();
			StoredClass sc = oc.storedClass(this);
			StoredField[] sf = sc.getStoredFields();
			bool[] cases = new bool[3];
			for (int i = 0; i < sf.Length; i++) 
			{
				StoredField f = sf[i];
				if(f.getName().Equals("foo"))
				{
					Tester.ensure(f.get(this).Equals("foo"));
					ensureFieldType(f, typeof(string));
					cases[0] = true;
				}
				if(f.getName().Equals("bar"))
				{
					Tester.ensure(f.get(this).Equals(10));
					ensureFieldType(f, typeof(int));
					cases[1] = true;
				}
				if(f.getName().Equals("atoms"))
				{
					ensureFieldType(f, typeof(Atom));
					Tester.ensure(f.isArray());
					Atom[] at = (Atom[])f.get(this);
					Tester.ensure(at[0].name.Equals("one"));
					Tester.ensure(at[1].name.Equals("two"));
					cases[2] = true;
				}
			}
			for (int i = 0; i < cases.Length; i++) 
			{
				Tester.ensure(cases[i]);
			}
		}

		private static void ensureFieldType(StoredField f, Type expectedType)
		{
			ReflectClass fieldClass = f.getStoredType();
			ReflectClass expectedClass = fieldClass.reflector().forClass(Class.getClassForType(expectedType));
			Tester.ensureEquals(expectedClass, fieldClass);
		}
	}
}
