/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.types {

   public class BiChildTypedPublic {
      
      public BiChildTypedPublic() : base() {
      }
      public BiParentTypedPublic parent;
      public String name;
   }
}