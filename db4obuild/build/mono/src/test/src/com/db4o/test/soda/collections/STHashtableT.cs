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
using j4o.util;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.collections {

    public class STHashtableT : STClass {
        internal System.Collections.Hashtable col;
        [Transient] public static SodaTest st;
      
        public STHashtableT() : base() {
        }
      
        public STHashtableT(Object[] arr) : base() {
            col = new System.Collections.Hashtable();
            for (int i1 = 0; i1 < arr.Length; i1++) {
                col.Add(arr[i1], System.Convert.ToInt32(i1));
            }
        }
      
        public Object[] store() {
            return new Object[]{
                                   new STHashtableT(),
                                   new STHashtableT(new Object[0]),
                                   new STHashtableT(new Object[]{
               
                                                                    System.Convert.ToInt32(0)            }),
                                   new STHashtableT(new Object[]{
                                                                    System.Convert.ToInt32(1),
                                                                    System.Convert.ToInt32(17),
                                                                    System.Convert.ToInt32(Int32.MaxValue - 1)            }),
                                   new STHashtableT(new Object[]{
                                                                    System.Convert.ToInt32(3),
                                                                    System.Convert.ToInt32(17),
                                                                    System.Convert.ToInt32(25),
                                                                    System.Convert.ToInt32(Int32.MaxValue - 2)            }),
                                   new STHashtableT(new Object[]{
                                                                    "foo",
                                                                    new STElement("bar", "barbar")            }),
                                   new STHashtableT(new Object[]{
                                                                    "foo2",
                                                                    new STElement("bar", "barbar2")            })         };
        }
      
        public void testDefaultContainsInteger() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(new STHashtableT(new Object[]{
                                                          System.Convert.ToInt32(17)         }));
            st.expect(q1, new Object[]{
                                          r1[3],
                                          r1[4]         });
        }
      
        public void testDefaultContainsString() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(new STHashtableT(new Object[]{
                                                          "foo"         }));
            st.expect(q1, new Object[]{
                                          r1[5]         });
        }
      
        public void testDefaultContainsTwo() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(new STHashtableT(new Object[]{
                                                          System.Convert.ToInt32(17),
                                                          System.Convert.ToInt32(25)         }));
            st.expect(q1, new Object[]{
                                          r1[4]         });
        }
      
        public void testDescendOne() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(Class.getClassForType(typeof(STHashtableT)));
            q1.descend("col").constrain(System.Convert.ToInt32(17));
            st.expect(q1, new Object[]{
                                          r1[3],
                                          r1[4]         });
        }
      
        public void testDescendTwo() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(Class.getClassForType(typeof(STHashtableT)));
            Query qElements1 = q1.descend("col");
            qElements1.constrain(System.Convert.ToInt32(17));
            qElements1.constrain(System.Convert.ToInt32(25));
            st.expect(q1, new Object[]{
                                          r1[4]         });
        }
      
        public void testDescendSmaller() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(Class.getClassForType(typeof(STHashtableT)));
            Query qElements1 = q1.descend("col");
            qElements1.constrain(System.Convert.ToInt32(3)).smaller();
            st.expect(q1, new Object[]{
                                          r1[2],
                                          r1[3]         });
        }
      
        public void testDefaultContainsObject() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(new STHashtableT(new Object[]{
                                                          new STElement("bar", null)         }));
            st.expect(q1, new Object[]{
                                          r1[5],
                                          r1[6]         });
        }
      
        public void testDescendToObject() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(new STHashtableT());
            q1.descend("col").descend("foo1").constrain("bar");
            st.expect(q1, new Object[]{
                                          r1[5],
                                          r1[6]         });
        }
    }
}