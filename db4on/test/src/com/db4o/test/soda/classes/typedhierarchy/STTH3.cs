/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.soda.classes.typedhierarchy {

   public class STTH3 {
      public String foo3;
      
      public STTH3() : base() {
      }
      
      public STTH3(String str) : base() {
         foo3 = str;
      }
   }
}