/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.soda.classes.typedhierarchy {

   public class STETH3 : STETH2 {
      internal String foo3;
      
      public STETH3() : base() {
      }
      
      public STETH3(String str1, String str2, String str3) : base(str1, str2) {
         foo3 = str3;
      }
   }
}