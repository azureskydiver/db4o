/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.soda.classes.typedhierarchy {

   public class STRTH2 {
      internal STRTH1 parent;
      internal STRTH3 h3;
      internal String foo2;
      
      public STRTH2() : base() {
      }
      
      public STRTH2(STRTH3 a3) : base() {
         h3 = a3;
         a3.parent = this;
      }
      
      public STRTH2(String str) : base() {
         foo2 = str;
      }
      
      public STRTH2(STRTH3 a3, String str) : base() {
         h3 = a3;
         a3.parent = this;
         foo2 = str;
      }
   }
}