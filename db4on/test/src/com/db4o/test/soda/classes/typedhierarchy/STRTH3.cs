/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.soda.classes.typedhierarchy {

   public class STRTH3 {
      internal STRTH1 grandParent;
      internal STRTH2 parent;
      internal String foo3;
      
      public STRTH3() : base() {
      }
      
      public STRTH3(String str) : base() {
         foo3 = str;
      }
   }
}