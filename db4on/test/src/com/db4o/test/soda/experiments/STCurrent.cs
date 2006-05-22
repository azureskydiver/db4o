/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.util;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
using com.db4o.test.soda.collections;
namespace com.db4o.test.soda.experiments {

    public class STCurrent : STClass {
        [Transient] public static SodaTest st;
        internal SodaTest pm;
        internal String mystr;
      
        public STCurrent() : base() {
        }
      
        public STCurrent(String str) : base() {
            this.mystr = str;
        }
      
        public override String ToString() {
            return "STCurrent: " + mystr;
        }
      
        public Object[] Store() {
            return new Object[]{};
        }
      
        public void TestDescendOne() {

        }
    }
}