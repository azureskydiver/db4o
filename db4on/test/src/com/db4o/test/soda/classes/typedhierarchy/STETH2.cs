/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.soda.classes.typedhierarchy {

   public class STETH2 : STETH1 {
      internal String foo2;
      
      public STETH2() : base() {
      }
      
      public STETH2(String str1, String str2) : base(str1) {
         foo2 = str2;
      }
   }
}