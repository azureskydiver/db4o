/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.types {

   public class BiParentTypedPublic: RTest {
      
      public BiParentTypedPublic() : base() {
      }
      public BiChildTypedPublic child;
      
      public override void set(int ver) {
         child = new BiChildTypedPublic();
         child.parent = this;
         if (ver == 1) {
            child.name = "set1";
         } else {
            child.name = "set2";
         }
      }
   }
}