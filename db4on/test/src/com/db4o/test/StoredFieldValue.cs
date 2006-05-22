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
    
		public void StoreOne()
		{
			foo = "foo";
			bar = 10;
			atoms = new Atom[2];
			atoms[0] = new Atom("one");
			atoms[1] = new Atom("two");
		}
    
		public void TestOne()
		{
			ExtObjectContainer oc = Tester.ObjectContainer();
			StoredClass sc = oc.StoredClass(this);
			StoredField[] sf = sc.GetStoredFields();
			bool[] cases = new bool[3];
			for (int i = 0; i < sf.Length; i++) 
			{
				StoredField f = sf[i];
				if(f.GetName().Equals("foo"))
				{
					Tester.Ensure(f.Get(this).Equals("foo"));
					EnsureFieldType(f, typeof(string));
					cases[0] = true;
				}
				if(f.GetName().Equals("bar"))
				{
					Tester.Ensure(f.Get(this).Equals(10));
					EnsureFieldType(f, typeof(int));
					cases[1] = true;
				}
				if(f.GetName().Equals("atoms"))
				{
					EnsureFieldType(f, typeof(Atom));
					Tester.Ensure(f.IsArray());
					Atom[] at = (Atom[])f.Get(this);
					Tester.Ensure(at[0].name.Equals("one"));
					Tester.Ensure(at[1].name.Equals("two"));
					cases[2] = true;
				}
			}
			for (int i = 0; i < cases.Length; i++) 
			{
				Tester.Ensure(cases[i]);
			}
		}

		private static void EnsureFieldType(StoredField f, Type expectedType)
		{
			ReflectClass fieldClass = f.GetStoredType();
			ReflectClass expectedClass = fieldClass.Reflector().ForClass(Class.GetClassForType(expectedType));
			Tester.EnsureEquals(expectedClass, fieldClass);
		}
	}
}
