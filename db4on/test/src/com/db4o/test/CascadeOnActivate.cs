/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
namespace com.db4o.test {

   public class CascadeOnActivate {
      
      public CascadeOnActivate() : base() {
      }
      public String name;
      public CascadeOnActivate child;
      
      public void Configure() {
         Db4o.Configure().ObjectClass(this).CascadeOnActivate(true);
      }
      
      public void Store() {
         name = "1";
         child = new CascadeOnActivate();
         child.name = "2";
         child.child = new CascadeOnActivate();
         child.child.name = "3";
         Tester.Store(this);
      }
      
      public void Test() {
         Query q1 = Tester.Query();
         q1.Constrain(j4o.lang.Class.GetClassForObject(this));
         q1.Descend("name").Constrain("1");
         ObjectSet os1 = q1.Execute();
         CascadeOnActivate coa1 = (CascadeOnActivate)os1.Next();
         CascadeOnActivate coa31 = coa1.child.child;
         Tester.Ensure(coa31.name.Equals("3"));
         Tester.ObjectContainer().Deactivate(coa1, Int32.MaxValue);
         Tester.Ensure(coa31.name == null);
         Tester.ObjectContainer().Activate(coa1, 1);
         Tester.Ensure(coa31.name.Equals("3"));
      }
   }
}