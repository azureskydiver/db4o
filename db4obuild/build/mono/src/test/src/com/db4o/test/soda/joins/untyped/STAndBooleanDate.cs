/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

using System;
using com.db4o;
using j4o.lang;
using j4o.util;
using com.db4o.test.soda;
namespace com.db4o.test.soda.joins.untyped {

    public class STAndBooleanDate {
        [Transient] public static SodaTest st;
        internal bool shipped;
        internal Date dateOrdered;
      
        public STAndBooleanDate() : base() {
        }
      
        public STAndBooleanDate(bool shipped, int year, int month, int day) : base() {
            this.shipped = shipped;
            this.dateOrdered = new Date( new DateTime(year, month, day));
        }
      
        public Object[] store() {
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