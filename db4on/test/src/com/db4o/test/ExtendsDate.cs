/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.test.types;
namespace com.db4o.test {

   public class ExtendsDate : j4o.util.Date, RTestable {
      
      public ExtendsDate() : base() {
      }
      
      public void compare(ObjectContainer con, Object obj, int ver) {
         Compare.compare(con, set(newInstance(), ver), obj, "", null);
      }
      
      public override bool Equals(Object obj) {
         if (obj != null) {
            if (obj is ExtendsDate) {
               return getTime() == ((ExtendsDate)obj).getTime();
            }
         }
         return false;
      }
      
      public Object newInstance() {
         return new ExtendsDate();
      }
      
      public Object set(Object obj, int ver) {
         ((ExtendsDate)obj).set(ver);
         return obj;
      }
      
      public void set(int ver) {
         setTime(ver);
      }
      
      public bool jdk2() {
         return false;
      }
      
      public bool ver3() {
         return false;
      }
   }
}