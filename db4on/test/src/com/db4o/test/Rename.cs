/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.config;
namespace com.db4o.test {

   public class Rename {
      
      public Rename() : base() {
      }
      
      public void test() {
         if (Tester.run == 1  && ! Tester.isClientServer()) {
            Tester.deleteAllInstances(Class.getClassForType(typeof(One)));
            Tester.store(new One("wasOne"));
            Tester.ensureOccurrences(Class.getClassForType(typeof(One)), 1);
            Tester.commit();
            ObjectClass oc1 = Db4o.configure().objectClass(Class.getClassForType(typeof(One)));
            oc1.objectField("nameOne").rename("nameTwo");
            oc1.rename(Class.getClassForType(typeof(Two)).getName());
            Tester.reOpenServer();
            Tester.ensureOccurrences(Class.getClassForType(typeof(Two)), 1);
            Tester.ensureOccurrences(Class.getClassForType(typeof(One)), 0);
            Two two1 = (Two)Tester.getOne(Class.getClassForType(typeof(Two)));
            Tester.ensure(two1.nameTwo.Equals("wasOne"));
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