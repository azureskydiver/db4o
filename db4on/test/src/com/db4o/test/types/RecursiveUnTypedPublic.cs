/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.types {

   public class RecursiveUnTypedPublic: RTest {
      
      public RecursiveUnTypedPublic() : base() {
      }
      public Object recurse;
      public String depth;
      
      public override void Set(int ver) {
         Set(ver, 10);
      }
      
      private void Set(int ver, int a_depth) {
         depth = "s" + ver + ":" + a_depth;
         if (a_depth > 0) {
            recurse = new RecursiveUnTypedPublic();
            ((RecursiveUnTypedPublic)recurse).Set(ver, a_depth - 1);
         }
      }
   }
}