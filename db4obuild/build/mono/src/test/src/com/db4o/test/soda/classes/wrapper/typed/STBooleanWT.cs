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
namespace com.db4o.test.soda.classes.wrapper.typed {

    public class STBooleanWT : STClass {
        static internal String DESCENDANT = "i_boolean";
        [Transient] public static SodaTest st;
        internal Boolean i_boolean;
      
        public STBooleanWT() : base() {
        }
      
        internal STBooleanWT(bool a_boolean) : base() {
            i_boolean = System.Convert.ToBoolean(a_boolean);
        }
      
        public Object[] store() {
            return new Object[]{
                                   new STBooleanWT(false),
                                   new STBooleanWT(true),
                                   new STBooleanWT(false),
                                   new STBooleanWT(false),
                                   new STBooleanWT()         };
        }
      
        public void testEqualsTrue() {
            Query q1 = st.query();
            q1.constrain(new STBooleanWT(true));
            Object[] r1 = store();
            st.expectOne(q1, new STBooleanWT(true));
        }
      
        public void testEqualsFalse() {
            Query q1 = st.query();
            q1.constrain(new STBooleanWT(false));
            q1.descend(DESCENDANT).constrain(System.Convert.ToBoolean(false));
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[0],
                                          r1[2],
                                          r1[3],
                                          r1[4]         });
        }
      
        public void testNull() {
            Query q1 = st.query();
            q1.constrain(new STBooleanWT());
            q1.descend(DESCENDANT).constrain(null);
            Object[] r1 = store();
            st.expectNone(q1);
        }
      
        public void testNullOrTrue() {
            Query q1 = st.query();
            q1.constrain(new STBooleanWT());
            Query qd1 = q1.descend(DESCENDANT);
            qd1.constrain(null).or(qd1.constrain(System.Convert.ToBoolean(true)));
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[1]
                                      });
        }
      
        public void testNotNullAndFalse() {
            Query q1 = st.query();
            q1.constrain(new STBooleanWT());
            Query qd1 = q1.descend(DESCENDANT);
            qd1.constrain(null).not().and(qd1.constrain(System.Convert.ToBoolean(false)));
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[0],
                                          r1[2],
                                          r1[3],
                                          r1[4]
            });
        }
    }
}