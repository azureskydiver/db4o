/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.types {

   /**
    * TestUtil does not handle this, so we only use it for debugging individually
    */
   public class ArrayVariations: RTest {
      
      public ArrayVariations() : base() {
      }
      public Object[][] o1;
      
      public override void Set(int ver) {
         o1 = new Object[2][];
      }
   }
}