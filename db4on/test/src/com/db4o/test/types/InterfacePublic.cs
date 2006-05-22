/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test.types {

   public class InterfacePublic: RTestable {
      
      public InterfacePublic() : base() {
      }
      public InterfaceHelper oo;
      
      public void Compare(ObjectContainer con, Object obj, int ver) {
         TestUtil.Compare(con, Set(NewInstance(), ver), obj, "", null);
      }
      
      public Object NewInstance() {
         return new InterfacePublic();
      }
      
      public Object Set(Object obj, int ver) {
         ((InterfacePublic)obj).Set(ver);
         return obj;
      }
      
      public void Set(int ver) {
         oo = new ObjectSimplePublic();
         ((ObjectSimplePublic)oo).Set(ver);
      }
      
      public bool Jdk2() {
         return false;
      }
      
      public bool Ver3() {
         return false;
      }
   }
}