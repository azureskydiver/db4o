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
namespace com.db4o.test.soda.arrays.untyped {

    public class STArrMixed : STClass {
        [Transient] public static SodaTest st;
        internal Object[] arr;
      
        public STArrMixed() : base() {
        }
      
        public STArrMixed(Object[] arr) : base() {
            this.arr = arr;
        }
      
        public Object[] store() {
            return new Object[]{
                                   new STArrMixed(),
                                   new STArrMixed(new Object[0]),
                                   new STArrMixed(new Object[]{
                                                                  System.Convert.ToInt32(0),
                                                                  System.Convert.ToInt32(0),
                                                                  "foo",
                                                                  System.Convert.ToBoolean(false)            }),
                                   new STArrMixed(new Object[]{
                                                                  System.Convert.ToInt32(1),
                                                                  System.Convert.ToInt32(17),
                                                                  System.Convert.ToInt32(Int32.MaxValue - 1),
                                                                  "foo",
                                                                  "bar"            }),
                                   new STArrMixed(new Object[]{
                                                                  System.Convert.ToInt32(3),
                                                                  System.Convert.ToInt32(17),
                                                                  System.Convert.ToInt32(25),
                                                                  System.Convert.ToInt32(Int32.MaxValue - 2)            })         };
        }
      
        public void testDefaultContainsInteger() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(new STArrMixed(new Object[]{
                                                        System.Convert.ToInt32(17)         }));
            st.expect(q1, new Object[]{
                                          r1[3],
                                          r1[4]         });
        }
      
        public void testDefaultContainsString() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(new STArrMixed(new Object[]{
                                                        "foo"         }));
            st.expect(q1, new Object[]{
                                          r1[2],
                                          r1[3]         });
        }
      
        public void testDefaultContainsBoolean() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(new STArrMixed(new Object[]{
                                                        System.Convert.ToBoolean(false)         }));
            st.expect(q1, new Object[]{
                                          r1[2]         });
        }
      
        public void testDefaultContainsTwo() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(new STArrMixed(new Object[]{
                                                        System.Convert.ToInt32(17),
                                                        "bar"         }));
            st.expect(q1, new Object[]{
                                          r1[3]         });
        }
      
        public void testDescendOne() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(typeof(STArrMixed));
            q1.descend("arr").constrain(System.Convert.ToInt32(17));
            st.expect(q1, new Object[]{
                                          r1[3],
                                          r1[4]         });
        }
      
        public void testDescendTwo() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(typeof(STArrMixed));
            Query qElements1 = q1.descend("arr");
            qElements1.constrain(System.Convert.ToInt32(17));
            qElements1.constrain("bar");
            st.expect(q1, new Object[]{
                                          r1[3]         });
        }
      
        public void testDescendSmaller() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(typeof(STArrMixed));
            Query qElements1 = q1.descend("arr");
            qElements1.constrain(System.Convert.ToInt32(3)).smaller();
            st.expect(q1, new Object[]{
                                          r1[2],
                                          r1[3]         });
        }
    }
}