/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.soda.classes.typedhierarchy {

   public class STSDFT3 : STSDFT1 {
      internal String foo;
      
      public STSDFT3() : base() {
      }
      
      public STSDFT3(String str) : base() {
         foo = str;
      }
   }
}