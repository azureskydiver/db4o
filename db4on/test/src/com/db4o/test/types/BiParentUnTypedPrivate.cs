/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.types {

   public class BiParentUnTypedPrivate: RTest {
      
      public BiParentUnTypedPrivate() : base() {
      }
      private Object child;
      
      public override void set(int ver) {
         child = new BiChildUnTypedPrivate(this, "set" + ver);
      }
      
      public override bool jdk2() {
         return true;
      }
   }
}