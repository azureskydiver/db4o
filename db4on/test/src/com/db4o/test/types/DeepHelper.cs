/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.types {

   public class DeepHelper {
      public ObjectSimplePublic d1;
      
      public DeepHelper() : base() {
         d1 = new ObjectSimplePublic();
      }
      
      public void Set(int ver) {
         if (ver == 1) {
            d1.name = "OneONEOneONEOneONEOneONEOneONEOneONE";
         } else {
            d1.name = "TwoTWOTwoTWOTwoTWOTwoTWOTwoTWO";
         }
      }
   }
}