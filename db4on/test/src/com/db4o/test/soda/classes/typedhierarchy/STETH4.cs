/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.soda.classes.typedhierarchy {

   public class STETH4 : STETH2 {
      internal String foo4;
      
      public STETH4() : base() {
      }
      
      public STETH4(String str1, String str2, String str3) : base(str1, str2) {
         foo4 = str3;
      }
   }
}