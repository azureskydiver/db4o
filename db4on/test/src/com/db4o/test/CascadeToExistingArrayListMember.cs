/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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