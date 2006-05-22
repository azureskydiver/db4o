/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test.types {

   public class RenTwo: InterfaceHelper, RTestable {
      
      public RenTwo() : base() {
      }
      public String s1;
      public String s2;
      
      public void Compare(ObjectContainer con, Object obj, int ver) {
         TestUtil.Compare(con, Set(NewInstance(), ver), obj, "", null);
      }
      
      public override bool Equals(Object obj) {
         return obj != null && obj is RenTwo && s1 != null && s2 != null && s1.Equals(((RenTwo)obj).s1) && s2.Equals(((RenTwo)obj).s2);
      }
      
      public Object NewInstance() {
         return new RenTwo();
      }
      
      public Object Set(Object obj, int ver) {
         ((RenTwo)obj).Set(ver);
         return obj;
      }
      
      public void Set(int ver) {
         if (ver == 1) {
            s1 = "One";
            s2 = "One";
         } else {
            s1 = "Two";
            s2 = "Two";
         }
      }
      
      public bool Jdk2() {
         return false;
      }
      
      public bool Ver3() {
         return false;
      }
   }
}