/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.soda.classes.untypedhierarchy {

   public class STUH3 {
      internal Object foo3;
      
      public STUH3() : base() {
      }
      
      public STUH3(String str) : base() {
         foo3 = str;
      }
   }
}