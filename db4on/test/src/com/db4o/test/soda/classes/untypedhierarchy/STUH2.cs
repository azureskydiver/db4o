/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.soda.classes.untypedhierarchy {

   public class STUH2 {
      internal Object h3;
      internal Object foo2;
      
      public STUH2() : base() {
      }
      
      public STUH2(STUH3 a3) : base() {
         h3 = a3;
      }
      
      public STUH2(String str) : base() {
         foo2 = str;
      }
      
      public STUH2(STUH3 a3, String str) : base() {
         h3 = a3;
         foo2 = str;
      }
   }
}