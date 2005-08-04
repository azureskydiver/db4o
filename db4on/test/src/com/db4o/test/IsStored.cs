/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test {

   public class IsStored {
      
      public IsStored() : base() {
      }
      internal String myString;
      
      public void test() {
         ObjectContainer con = Tester.objectContainer();
         Tester.deleteAllInstances(this);
         IsStored isStored1 = new IsStored();
         isStored1.myString = "isStored";
         con.set(isStored1);
         Tester.ensure(con.ext().isStored(isStored1));
         Tester.ensure(Tester.occurrences(this) == 1);
         con.delete(isStored1);
         Tester.ensure(!con.ext().isStored(isStored1));
         Tester.ensure(Tester.occurrences(this) == 0);
         con.commit();
         if (con.ext().isStored(isStored1)) {
            if (!Tester.clientServer) {
               Tester.error();
            }
         }
         Tester.ensure(Tester.occurrences(this) == 0);
         con.set(isStored1);
         Tester.ensure(con.ext().isStored(isStored1));
         Tester.ensure(Tester.occurrences(this) == 1);
         con.commit();
         Tester.ensure(con.ext().isStored(isStored1));
         Tester.ensure(Tester.occurrences(this) == 1);
         con.delete(isStored1);
         Tester.ensure(!con.ext().isStored(isStored1));
         Tester.ensure(Tester.occurrences(this) == 0);
         con.commit();
         if (con.ext().isStored(isStored1)) {
            if (!Tester.clientServer) {
               Tester.error();
            }
         }
         Tester.ensure(Tester.occurrences(this) == 0);
      }
   }
}