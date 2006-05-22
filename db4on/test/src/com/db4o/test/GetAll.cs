/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test {

   public class GetAll {
      
      public GetAll() : base() {
      }
      
      public void Test() {
         int size1 = AllObjectCount();
         Tester.Store(new GetAll());
         Tester.Ensure(AllObjectCount() == size1 + 1);
         Tester.RollBack();
         Tester.Ensure(AllObjectCount() == size1);
         Tester.Store(new GetAll());
         Tester.Ensure(AllObjectCount() == size1 + 1);
         Tester.Commit();
         Tester.Ensure(AllObjectCount() == size1 + 1);
      }
      
      private int AllObjectCount() {
         return Tester.ObjectContainer().Get(null).Size();
      }
   }
}