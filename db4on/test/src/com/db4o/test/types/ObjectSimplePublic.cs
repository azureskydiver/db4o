/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test.types {

   public class ObjectSimplePublic: InterfaceHelper, RTestable {
      public String name;
      
      public ObjectSimplePublic() : base() {
      }
      
      public ObjectSimplePublic(String a_name) : base() {
         name = a_name;
      }
      
      public void compare(ObjectContainer con, Object obj, int ver) {
         TestUtil.compare(con, set(newInstance(), ver), obj, "", null);
      }
      
      public override bool Equals(Object obj) {
         if (obj != null) {
            if (obj is ObjectSimplePublic) {
               if (name != null) {
                  return name.Equals(((ObjectSimplePublic)obj).name);
               }
            }
         }
         return false;
      }
      
      public Object newInstance() {
         return new ObjectSimplePublic();
      }
      
      public Object set(Object obj, int ver) {
         ((ObjectSimplePublic)obj).set(ver);
         return obj;
      }
      
      public void set(int ver) {
         if (ver == 1) {
            name = "OneONEOneONEOneONEOneONEOneONEOneONE";
         } else {
            name = "TwoTWOTwoTWOTwoTWOTwoTWOTwoTWO";
         }
      }
      
      public bool jdk2() {
         return false;
      }
      
      public bool ver3() {
         return false;
      }
   }
}