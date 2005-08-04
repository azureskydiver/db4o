/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test {

   public class NoInternalClasses {
      
      public NoInternalClasses() : base() {
      }
      
      public void store() {
         Tester.store(new StaticClass());
      }
      
      public void test() {
         Tester.ensureOccurrences(new StaticClass(), 0);
      }
   }
}