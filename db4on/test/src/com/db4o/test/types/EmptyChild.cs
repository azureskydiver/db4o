/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.types {

   public class EmptyChild: Empty {
      
      public EmptyChild() : base() {
      }
      
      public override void Set(int ver) {
      }
   }
}