/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test {

   public class NoInternalClasses {
      
      public NoInternalClasses() : base() {
      }
      
      public void Store() {
         Tester.Store(new StaticClass());
      }
      
      public void Test() {
         Tester.EnsureOccurrences(new StaticClass(), 0);
      }
   }
}