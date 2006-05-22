﻿/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.types {

   public class BiParentTypedPrivate: RTest {
      
      public BiParentTypedPrivate() : base() {
      }
      private BiChildTypedPrivate child;
      
      public override void Set(int ver) {
         child = new BiChildTypedPrivate(this, "set" + ver);
      }
      
      public override bool Jdk2() {
         return true;
      }
   }
}