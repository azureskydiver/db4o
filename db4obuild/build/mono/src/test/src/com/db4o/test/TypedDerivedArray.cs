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