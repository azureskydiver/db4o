/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.types {

   public class RecursiveTypedPublic: RTest {
      
      public RecursiveTypedPublic() : base() {
      }
      public RecursiveTypedPublic recurse;
      public String depth;
      
      public override void set(int ver) {
         set(ver, 10);
      }
      
      private void set(int ver, int a_depth) {
         depth = "s" + ver + ":" + a_depth;
         if (a_depth > 0) {
            recurse = new RecursiveTypedPublic();
            recurse.set(ver, a_depth - 1);
         }
      }
   }
}