/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o.foundation;
using j4o.lang;
using j4o.util;
using com.db4o;
namespace com.db4o.test 
{

	public class CascadeToHashtable 
	{
      
		public CascadeToHashtable() : base() 
		{
		}
		internal System.Collections.Hashtable ht;
      
		public void Configure() 
		{
			Db4o.Configure().ObjectClass(this).CascadeOnUpdate(true);
			Db4o.Configure().ObjectClass(this).CascadeOnDelete(true);
		}
      
		public void Store() 
		{
			Tester.DeleteAllInstances(this);
			Tester.DeleteAllInstances(new Atom());
			CascadeToHashtable cth1 = new CascadeToHashtable();
			cth1.ht = new System.Collections.Hashtable();
			cth1.ht.Add("key1", new Atom("stored1"));
			cth1.ht.Add("key2", new Atom(new Atom("storedChild1"), "stored2"));
			Tester.Store(cth1);
		}
      
		public void Test() 
		{
			Tester.ForEach(this, new MyVisitorCTH1());
			Tester.ReOpen();
			Tester.ForEach(this, new MyVisitorCTH2());
			Tester.ReOpen();
			Tester.DeleteAllInstances(this);
			Tester.EnsureOccurrences(new Atom(), 1);
		}
	}

	public class MyVisitorCTH1:Visitor4
	{
		public void Visit(Object obj) 
		{
			CascadeToHashtable cth1 = (CascadeToHashtable)obj;
			cth1.ht["key1"] = new Atom("updated1");
			Atom atom1 = (Atom)cth1.ht["key2"];
			atom1.name = "updated2";
			Tester.Store(cth1);
		}
	}



	public class MyVisitorCTH2:Visitor4
	{
		public void Visit(Object obj) 
		{
			CascadeToHashtable cth1 = (CascadeToHashtable)obj;
			Atom atom1 = (Atom)cth1.ht["key1"];
			Tester.Ensure(atom1.name.Equals("updated1"));
			atom1 = (Atom)cth1.ht["key2"];
			Tester.Ensure(atom1.name.Equals("updated2"));
		}
	}
}