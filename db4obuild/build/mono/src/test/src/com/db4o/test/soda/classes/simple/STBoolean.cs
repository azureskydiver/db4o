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
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.simple {

    public class STBoolean : STClass1 {
        [Transient] public static SodaTest st;
        public bool i_boolean;
      
        public STBoolean() : base() {
        }
      
        internal STBoolean(bool a_boolean) : base() {
            i_boolean = a_boolean;
        }
      
        public Object[] store() {
            return new Object[]{
                                   new STBoolean(false),
                                   new STBoolean(true),
                                   new STBoolean(false),
                                   new STBoolean(false)         };
        }
      
        public void testEqualsTrue() {
            Query q1 = st.query();
            q1.constrain(new STBoolean(true));
            Object[] r1 = store();
            st.expectOne(q1, new STBoolean(true));
        }
      
        public void testEqualsFalse() {
            Query q1 = st.query();
            q1.constrain(new STBoolean(false));
            q1.descend("i_boolean").constrain(System.Convert.ToBoolean(false));
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[0],
                                          r1[2],
                                          r1[3]         });
        }
    }
}