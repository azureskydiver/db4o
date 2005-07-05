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
      
		public void store() 
		{
			Test.deleteAllInstances(this);
			TypedDerivedArray tda1 = new TypedDerivedArray();
			Molecule[] mols1 = new Molecule[1];
			mols1[0] = new Molecule("TypedDerivedArray");
			tda1.atoms = mols1;
			Test.store(tda1);
		}
      
		public void test() 
		{
			Test.forEach(new TypedDerivedArray(), new MyVisitor());
		}

		public class MyVisitor:Visitor4
		{
			public void visit(Object obj) 
			{
				TypedDerivedArray tda1 = (TypedDerivedArray)obj;
				Test.ensure(tda1.atoms is Molecule[]);
			}
		}



	}
}