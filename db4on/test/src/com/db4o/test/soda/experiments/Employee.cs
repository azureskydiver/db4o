/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.util;
using com.db4o.query;
using com.db4o.test.soda;
using com.db4o.test.soda.collections;
namespace com.db4o.test.soda.experiments {

   internal class Employee {
      
      internal Employee() : base() {
      }
      internal String name;
      internal Single salary;
      internal Department dept;
      internal Employee boss;
   }
}