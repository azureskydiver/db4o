/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o.test.soda;
using com.db4o.test.soda.engines.db4o;
namespace com.db4o.test {

   public class Soda {
      
      public Soda() : base() {
      }
      
      public void test() {
         SodaTest st1 = new SodaTest();
         if (Tester.isClientServer()) {
             st1.run(SodaTest.CLASSES, new STEngine[]{
                                                         new STDb4oClientServer()            }, true);
         } else {
             st1.run(SodaTest.CLASSES, new STEngine[]{
                                                         new STDb4o()            }, true);
         }
         Tester.ensure(SodaTest.failedClassesSize() == 0);
         Tester.assertionCount += SodaTest.testCaseCount();
      }
   }
}