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
using System.Collections;
using j4o.lang;
using j4o.util;
using com.db4o;
namespace com.db4o.test 
{

	public class CascadeToExistingArrayListMember 
	{
      
		public CascadeToExistingArrayListMember () : base() 
		{
		}
		public ArrayList vec;
      
		public void configure() 
		{
			Db4o.configure().objectClass(j4o.lang.Class.getClassForObject(this).getName()).cascadeOnUpdate(true);
		}
      
		public void store() 
		{
			Test.deleteAllInstances(new Atom());
			Test.deleteAllInstances(this);
			CascadeToExistingArrayListMember cev1 = new CascadeToExistingArrayListMember();
			cev1.vec = new ArrayList();
			Atom atom1 = new Atom("one");
			Test.store(atom1);
			cev1.vec.Add(atom1);
			Test.store(cev1);
		}
      
		public void test() 
		{
			Test.forEach(new CascadeToExistingArrayListMember(), new MyVisitorE1());
			Test.reOpen();
			Test.forEach(new CascadeToExistingArrayListMember(), new MyVisitorE2());
			Test.forEach(new CascadeToExistingArrayListMember(), new MyVisitorE3());
			Test.reOpen();
			Test.forEach(new CascadeToExistingArrayListMember(), new MyVisitorE4());
		}
	}

	public class MyVisitorE1:Visitor4
	{
		public void visit(Object obj) 
		{
			CascadeToExistingArrayListMember cev1 = (CascadeToExistingArrayListMember)obj;
			Atom atom1 = (Atom)cev1.vec[0];
			atom1.name = "two";
			Test.store(cev1);
			atom1.name = "three";
			Test.store(cev1);
		}
	}
	public class MyVisitorE2:Visitor4
	{
		public void visit(Object obj) 
		{
			CascadeToExistingArrayListMember cev1 = (CascadeToExistingArrayListMember)obj;
			Atom atom1 = (Atom)cev1.vec[0];
			Test.ensure(atom1.name.Equals("three"));
			Test.ensureOccurrences(atom1, 1);
		}
	}
	public class MyVisitorE3:Visitor4
	{
		public void visit(Object obj) 
		{
			CascadeToExistingArrayListMember cev1 = (CascadeToExistingArrayListMember)obj;
			Atom atom1 = (Atom)cev1.vec[0];
			atom1.name = "four";
			Test.store(cev1);
		}
	}

	public class MyVisitorE4:Visitor4
	{
		public void visit(Object obj) 
		{
			CascadeToExistingArrayListMember cev1 = (CascadeToExistingArrayListMember)obj;
			Atom atom1 = (Atom)cev1.vec[0];
			Test.ensure(atom1.name.Equals("four"));
			Test.ensureOccurrences(atom1, 1);
		}
	}



}