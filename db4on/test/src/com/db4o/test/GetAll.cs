/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test {

   public class GetAll {
      
      public GetAll() : base() {
      }
      
      public void test() {
         int size1 = allObjectCount();
         Tester.store(new GetAll());
         Tester.ensure(allObjectCount() == size1 + 1);
         Tester.rollBack();
         Tester.ensure(allObjectCount() == size1);
         Tester.store(new GetAll());
         Tester.ensure(allObjectCount() == size1 + 1);
         Tester.commit();
         Tester.ensure(allObjectCount() == size1 + 1);
      }
      
      private int allObjectCount() {
         return Tester.objectContainer().get(null).size();
      }
   }
}