/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using j4o.lang;
using j4o.util;
using com.db4o.query;
using com.db4o.test.soda;
using com.db4o.test.soda.collections;
namespace com.db4o.test.soda.experiments {

   internal class Department {
      internal String name;
      internal ICollection emps;
      
      internal Department() : base() {
      }
      
      internal Department(String name) : base() {
      }
   }
}