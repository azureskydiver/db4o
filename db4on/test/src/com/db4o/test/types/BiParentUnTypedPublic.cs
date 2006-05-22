/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.types {

   public class BiParentUnTypedPublic: RTest {
      
      public BiParentUnTypedPublic() : base() {
      }
      public Object child;
      
      public override void Set(int ver) {
         child = new BiChildUnTypedPublic();
         ((BiChildUnTypedPublic)child).parent = this;
         ((BiChildUnTypedPublic)child).name = "set" + ver;
      }
   }
}