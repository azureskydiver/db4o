/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.soda.classes.untypedhierarchy {

   public class STRUH2 {
      internal Object parent;
      internal Object h3;
      internal String foo2;
      
      public STRUH2() : base() {
      }
      
      public STRUH2(STRUH3 a3) : base() {
         h3 = a3;
         a3.parent = this;
      }
      
      public STRUH2(String str) : base() {
         foo2 = str;
      }
      
      public STRUH2(STRUH3 a3, String str) : base() {
         h3 = a3;
         a3.parent = this;
         foo2 = str;
      }
   }
}