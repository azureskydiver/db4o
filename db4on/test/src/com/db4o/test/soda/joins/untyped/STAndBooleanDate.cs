/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using com.db4o;
using j4o.lang;
using j4o.util;
using com.db4o.test.soda;
namespace com.db4o.test.soda.joins.untyped {

    public class STAndBooleanDate {
        [Transient] public static SodaTest st;
        internal bool shipped;
        internal j4o.util.Date dateOrdered;
      
        public STAndBooleanDate() : base() {
        }
      
        public STAndBooleanDate(bool shipped, int year, int month, int day) : base() {
            this.shipped = shipped;
            this.dateOrdered = new j4o.util.Date( new DateTime(year, month, day));
        }
      
        public Object[] Store() {
            return new Object[]{
                                   new STAndBooleanDate(false, 2002, 11, 1),
                                   new STAndBooleanDate(false, 2002, 12, 3),
                                   new STAndBooleanDate(false, 2002, 12, 5),
                                   new STAndBooleanDate(true, 2002, 11, 3),
                                   new STAndBooleanDate(true, 2002, 12, 4),
                                   new STAndBooleanDate(true, 2002, 12, 6)         };
        }
    }
}