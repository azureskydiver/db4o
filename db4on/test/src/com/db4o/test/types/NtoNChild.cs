/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.types {

   public class NtoNChild {
      
      public NtoNChild() : base() {
      }
      public NtoNParent[] parents;
      public String name;
   }
}