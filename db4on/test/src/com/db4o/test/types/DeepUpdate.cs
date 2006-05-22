/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test.types {

   /**
    * note the special configuration for this class in Regression.OpenContainer()
    */
   public class DeepUpdate: RTestable {
      public ObjectSimplePublic d1;
      public DeepHelper d2;
      public DeepHelper[] d3;
      
      public DeepUpdate() : base() {
      }
      
      public void Compare(ObjectContainer con, Object obj, int ver) {
         TestUtil.Compare(con, Set(NewInstance(), ver), obj, "", null);
      }
      
      public override bool Equals(Object obj) {
         if (obj == null) {
            return false;
         }
         if (!(obj is DeepUpdate)) {
            return false;
         }
         DeepUpdate with = (DeepUpdate)obj;
         if (with.d1 != null && d1 != null) {
            if (d1.Equals(with.d1)) {
               if (with.d2 != null && d2 != null) {
                  if (d2.d1.Equals(with.d2.d1)) {
                     if (with.d3 != null && d3 != null) {
                        if (with.d3.Length == d3.Length) {
                           if (with.d3[0].Equals(d3[0])) {
                              if (with.d3[1].Equals(d3[1])) {
                                 return true;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
         return false;
      }
      
      public Object NewInstance() {
         return new DeepUpdate();
      }
      
      public Object Set(Object obj, int ver) {
         ((DeepUpdate)obj).Set(ver);
         return obj;
      }
      
      public void Set(int ver) {
         d1 = new ObjectSimplePublic();
         d2 = new DeepHelper();
         d3 = new DeepHelper[2];
         d3[0] = new DeepHelper();
         d3[1] = new DeepHelper();
         if (ver == 1) {
            d1.name = "OneONEOneONEOneONEOneONEOneONEOneONE";
         } else {
            d1.name = "TwoTWOTwoTWOTwoTWOTwoTWOTwoTWO";
         }
         d2.Set(ver);
         d3[0].Set(ver);
         d3[1].Set(ver);
      }
      
      public bool Jdk2() {
         return false;
      }
      
      public bool Ver3() {
         return false;
      }
   }
}