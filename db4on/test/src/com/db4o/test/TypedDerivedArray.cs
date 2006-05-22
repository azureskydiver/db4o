/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.foundation;
using j4o.lang;
namespace com.db4o.test 
{

	public class TypedDerivedArray 
	{
      
		public TypedDerivedArray() : base() 
		{
		}
		internal Atom[] atoms;
      
		public void Store() 
		{
			Tester.DeleteAllInstances(this);
			TypedDerivedArray tda1 = new TypedDerivedArray();
			Molecule[] mols1 = new Molecule[1];
			mols1[0] = new Molecule("TypedDerivedArray");
			tda1.atoms = mols1;
			Tester.Store(tda1);
		}
      
		public void Test() 
		{
			Tester.ForEach(new TypedDerivedArray(), new MyVisitor());
		}

		public class MyVisitor:Visitor4
		{
			public void Visit(Object obj) 
			{
				TypedDerivedArray tda1 = (TypedDerivedArray)obj;
				Tester.Ensure(tda1.atoms is Molecule[]);
			}
		}



	}
}