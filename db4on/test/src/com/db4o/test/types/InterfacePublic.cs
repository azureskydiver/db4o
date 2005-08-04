/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test.types {

   public class InterfacePublic: RTestable {
      
      public InterfacePublic() : base() {
      }
      public InterfaceHelper oo;
      
      public void compare(ObjectContainer con, Object obj, int ver) {
         TestUtil.compare(con, set(newInstance(), ver), obj, "", null);
      }
      
      public Object newInstance() {
         return new InterfacePublic();
      }
      
      public Object set(Object obj, int ver) {
         ((InterfacePublic)obj).set(ver);
         return obj;
      }
      
      public void set(int ver) {
         oo = new ObjectSimplePublic();
         ((ObjectSimplePublic)oo).set(ver);
      }
      
      public bool jdk2() {
         return false;
      }
      
      public bool ver3() {
         return false;
      }
   }
}