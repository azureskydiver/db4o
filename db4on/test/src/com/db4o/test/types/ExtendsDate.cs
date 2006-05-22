/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test.types {

   public class ExtendsDate : j4o.util.Date, RTestable {
      
      public ExtendsDate() : base() {
      }
      
      public void Compare(ObjectContainer con, Object obj, int ver) {
         TestUtil.Compare(con, Set(NewInstance(), ver), obj, "", null);
      }
      
      public override bool Equals(Object obj) {
         if (obj != null) {
            if (obj is ExtendsDate) {
               return GetTime() == ((ExtendsDate)obj).GetTime();
            }
         }
         return false;
      }
      
      public Object NewInstance() {
         return new ExtendsDate();
      }
      
      public Object Set(Object obj, int ver) {
         ((ExtendsDate)obj).Set(ver);
         return obj;
      }
      
      public void Set(int ver) {
         SetTime(ver);
      }
      
      public bool Jdk2() {
         return false;
      }
      
      public bool Ver3() {
         return false;
      }
   }
}