/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.types {

   public class BiChildTypedPrivate {
      private BiParentTypedPrivate parent;
      private String name;
      
      public BiChildTypedPrivate() : base() {
      }
      
      public BiChildTypedPrivate(BiParentTypedPrivate a_parent, String a_name) : base() {
         parent = a_parent;
         name = a_name;
      }
   }
}