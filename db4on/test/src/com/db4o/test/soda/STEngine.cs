/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o.query;
namespace com.db4o.test.soda {

   public interface STEngine {
      
      void reset();
      
      Query query();
      
      void open();
      
      void close();
      
      void store(Object obj);
      
      void commit();
      
      void delete(Object obj);
   }
}