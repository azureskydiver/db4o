/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.soda.collections {

   public class STElement {
      public String foo1;
      public Object foo2;
      
      public STElement() : base() {
      }
      
      public STElement(String foo1, Object foo2) : base() {
         this.foo1 = foo1;
         this.foo2 = foo2;
      }
   }
}