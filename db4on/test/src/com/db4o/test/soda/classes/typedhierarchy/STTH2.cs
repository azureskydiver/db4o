/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.soda.classes.typedhierarchy {

   public class STTH2 {
      public STTH3 h3;
      public String foo2;
      
      public STTH2() : base() {
      }
      
      public STTH2(STTH3 a3) : base() {
         h3 = a3;
      }
      
      public STTH2(String str) : base() {
         foo2 = str;
      }
      
      public STTH2(STTH3 a3, String str) : base() {
         h3 = a3;
         foo2 = str;
      }
   }
}