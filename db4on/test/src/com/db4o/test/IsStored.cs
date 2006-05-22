/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test {

   public class IsStored {
      
      public IsStored() : base() {
      }
      internal String myString;
      
      public void Test() {
         ObjectContainer con = Tester.ObjectContainer();
         Tester.DeleteAllInstances(this);
         IsStored isStored1 = new IsStored();
         isStored1.myString = "isStored";
         con.Set(isStored1);
         Tester.Ensure(con.Ext().IsStored(isStored1));
         Tester.Ensure(Tester.Occurrences(this) == 1);
         con.Delete(isStored1);
         Tester.Ensure(!con.Ext().IsStored(isStored1));
         Tester.Ensure(Tester.Occurrences(this) == 0);
         con.Commit();
         if (con.Ext().IsStored(isStored1)) {
            if (!Tester.clientServer) {
               Tester.Error();
            }
         }
         Tester.Ensure(Tester.Occurrences(this) == 0);
         con.Set(isStored1);
         Tester.Ensure(con.Ext().IsStored(isStored1));
         Tester.Ensure(Tester.Occurrences(this) == 1);
         con.Commit();
         Tester.Ensure(con.Ext().IsStored(isStored1));
         Tester.Ensure(Tester.Occurrences(this) == 1);
         con.Delete(isStored1);
         Tester.Ensure(!con.Ext().IsStored(isStored1));
         Tester.Ensure(Tester.Occurrences(this) == 0);
         con.Commit();
         if (con.Ext().IsStored(isStored1)) {
            if (!Tester.clientServer) {
               Tester.Error();
            }
         }
         Tester.Ensure(Tester.Occurrences(this) == 0);
      }
   }
}