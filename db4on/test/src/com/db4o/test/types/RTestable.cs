/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
namespace com.db4o.test.types {

   public interface RTestable {
      
      Object newInstance();
      
      Object set(Object obj, int ver);
      
      void compare(ObjectContainer con, Object obj, int ver);
      
      bool jdk2();
      
      bool ver3();
   }
}