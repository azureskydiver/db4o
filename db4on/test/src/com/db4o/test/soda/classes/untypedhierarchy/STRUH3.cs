/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.soda.classes.untypedhierarchy {

   public class STRUH3 {
      internal Object grandParent;
      internal Object parent;
      internal String foo3;
      
      public STRUH3() : base() {
      }
      
      public STRUH3(String str) : base() {
         foo3 = str;
      }
   }
}