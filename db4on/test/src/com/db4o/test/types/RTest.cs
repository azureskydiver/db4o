/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test.types {

   public abstract class RTest: RTestable {
      
      public RTest() : base() {
      }
      
      public Object newInstance() {
         try {
            {
               return j4o.lang.Class.getClassForObject(this).newInstance();
            }
         }  catch (Exception e) {
            {
               return null;
            }
         }
      }
      
      public Object set(Object obj, int ver) {
         ((RTest)obj).set(ver);
         return obj;
      }
      
      public abstract void set(int ver);
      
      public void compare(ObjectContainer con, Object obj, int ver) {
         Compare.compare(con, set(newInstance(), ver), obj, "", null);
      }
      
      public virtual bool jdk2() {
         return false;
      }
      
      public bool ver3() {
         return false;
      }
   }
}