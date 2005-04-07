/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test {

   public class SimplestPossible {
      
      public SimplestPossible() : base() {
      }
      public String name;
      
      public void storeOne() {
         name = "sp";
      }
      
      public void testOne() {
         Test.ensure(name.Equals("sp"));
      }
   }
}