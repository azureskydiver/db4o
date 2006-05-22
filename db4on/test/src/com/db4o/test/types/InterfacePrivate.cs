/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.types {

   public class InterfacePrivate: RTest {
      
      public InterfacePrivate() : base() {
      }
      private InterfaceHelper oo;
      
      public override void Set(int ver) {
         oo = new ObjectSimplePrivate();
         ((ObjectSimplePrivate)oo).Set(ver);
      }
      
      public override bool Jdk2() {
         return true;
      }
   }
}