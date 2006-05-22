/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test.types {

   public abstract class RTest: RTestable {
      
      public RTest() : base() {
      }
      
      public Object NewInstance() {
         try {
            {
               return j4o.lang.Class.GetClassForObject(this).NewInstance();
            }
         }  catch (Exception e) {
            {
               return null;
            }
         }
      }
      
      public Object Set(Object obj, int ver) {
         ((RTest)obj).Set(ver);
         return obj;
      }
      
      public abstract void Set(int ver);
      
      public void Compare(ObjectContainer con, Object obj, int ver) {
         TestUtil.Compare(con, Set(NewInstance(), ver), obj, "", null);
      }
      
      public virtual bool Jdk2() {
         return false;
      }
      
      public bool Ver3() {
         return false;
      }
   }
}