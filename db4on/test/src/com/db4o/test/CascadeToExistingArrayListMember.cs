/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using com.db4o.foundation;
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
      
		public void Configure() 
		{
			Db4o.Configure().ObjectClass(j4o.lang.Class.GetClassForObject(this).GetName()).CascadeOnUpdate(true);
		}
      
		public void Store() 
		{
			Tester.DeleteAllInstances(new Atom());
			Tester.DeleteAllInstances(this);
			CascadeToExistingArrayListMember cev1 = new CascadeToExistingArrayListMember();
			cev1.vec = new ArrayList();
			Atom atom1 = new Atom("one");
			Tester.Store(atom1);
			cev1.vec.Add(atom1);
			Tester.Store(cev1);
		}
      
		public void Test() 
		{
			Tester.ForEach(new CascadeToExistingArrayListMember(), new MyVisitorE1());
			Tester.ReOpen();
			Tester.ForEach(new CascadeToExistingArrayListMember(), new MyVisitorE2());
			Tester.ForEach(new CascadeToExistingArrayListMember(), new MyVisitorE3());
			Tester.ReOpen();
			Tester.ForEach(new CascadeToExistingArrayListMember(), new MyVisitorE4());
		}
	}

	public class MyVisitorE1:Visitor4
	{
		public void Visit(Object obj) 
		{
			CascadeToExistingArrayListMember cev1 = (CascadeToExistingArrayListMember)obj;
			Atom atom1 = (Atom)cev1.vec[0];
			atom1.name = "two";
			Tester.Store(cev1);
			atom1.name = "three";
			Tester.Store(cev1);
		}
	}
	public class MyVisitorE2:Visitor4
	{
		public void Visit(Object obj) 
		{
			CascadeToExistingArrayListMember cev1 = (CascadeToExistingArrayListMember)obj;
			Atom atom1 = (Atom)cev1.vec[0];
			Tester.Ensure(atom1.name.Equals("three"));
			Tester.EnsureOccurrences(atom1, 1);
		}
	}
	public class MyVisitorE3:Visitor4
	{
		public void Visit(Object obj) 
		{
			CascadeToExistingArrayListMember cev1 = (CascadeToExistingArrayListMember)obj;
			Atom atom1 = (Atom)cev1.vec[0];
			atom1.name = "four";
			Tester.Store(cev1);
		}
	}

	public class MyVisitorE4:Visitor4
	{
		public void Visit(Object obj) 
		{
			CascadeToExistingArrayListMember cev1 = (CascadeToExistingArrayListMember)obj;
			Atom atom1 = (Atom)cev1.vec[0];
			Tester.Ensure(atom1.name.Equals("four"));
			Tester.EnsureOccurrences(atom1, 1);
		}
	}



}