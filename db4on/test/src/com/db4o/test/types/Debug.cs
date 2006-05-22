/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.util;
namespace com.db4o.test.types {

   public class Debug: RTest {

      
      public Debug() : base() {
      }
       public Object oFloat;
       public String indi;
      
       public override void Set(int ver) {
           if (ver == 1) {
               oFloat = new Single[]{Single.MaxValue - 2};
               indi = "a";
           } else {
               indi = "b";
           }
       }
       
      public override bool Jdk2() {
         return false;
      }
   }
}