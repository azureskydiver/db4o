/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.types {

   public class ObjectSimplePrivate: RTest, InterfaceHelper {
      private String name;
      
      public ObjectSimplePrivate() : base() {
      }
      
      public ObjectSimplePrivate(String a_name) : base() {
         name = a_name;
      }
      
      public override bool Equals(Object obj) {
         if (obj != null) {
            if (obj is ObjectSimplePrivate) {
               if (name != null) {
                  return name.Equals(((ObjectSimplePrivate)obj).name);
               }
            }
         }
         return false;
      }
      
      public override void set(int ver) {
         if (ver == 1) {
            name = "OneONEOneONEOneONEOneONEOneONEOneONE";
         } else {
            name = "TwoTWOTwoTWOTwoTWOTwoTWOTwoTWO";
         }
      }
      
      public override bool jdk2() {
         return true;
      }
   }
}