/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test {

   public class GetAll {
      
      public GetAll() : base() {
      }
      
      public void test() {
         int size1 = allObjectCount();
         Test.store(new GetAll());
         Test.ensure(allObjectCount() == size1 + 1);
         Test.rollBack();
         Test.ensure(allObjectCount() == size1);
         Test.store(new GetAll());
         Test.ensure(allObjectCount() == size1 + 1);
         Test.commit();
         Test.ensure(allObjectCount() == size1 + 1);
      }
      
      private int allObjectCount() {
         return Test.objectContainer().get(null).size();
      }
   }
}