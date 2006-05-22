/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.config;
namespace com.db4o.test {

   public class Rename {
      
      public Rename() : base() {
      }
      
      public void Test() {
         if (Tester.run == 1  && ! Tester.IsClientServer()) {
            Tester.DeleteAllInstances(Class.GetClassForType(typeof(One)));
            Tester.Store(new One("wasOne"));
            Tester.EnsureOccurrences(Class.GetClassForType(typeof(One)), 1);
            Tester.Commit();
            ObjectClass oc1 = Db4o.Configure().ObjectClass(Class.GetClassForType(typeof(One)));
            oc1.ObjectField("nameOne").Rename("nameTwo");
            oc1.Rename(Class.GetClassForType(typeof(Two)).GetName());
            Tester.ReOpenServer();
            Tester.EnsureOccurrences(Class.GetClassForType(typeof(Two)), 1);
            Tester.EnsureOccurrences(Class.GetClassForType(typeof(One)), 0);
            Two two1 = (Two)Tester.GetOne(Class.GetClassForType(typeof(Two)));
            Tester.Ensure(two1.nameTwo.Equals("wasOne"));
         }
      }
      
      public class One {
         public String nameOne;
         
         public One() : base() {
         }
         
         public One(String name) : base() {
            nameOne = name;
         }
      }
      
      public class Two {
         
         public Two() : base() {
         }
         public String nameTwo;
      }
   }
}