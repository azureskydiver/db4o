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

    public class STByte : STClass1 {
        static internal String DESCENDANT = "i_byte";
        [Transient] public static SodaTest st;
        public byte i_byte;
      
        public STByte() : base() {
        }
      
        internal STByte(byte a_byte) : base() {
            i_byte = a_byte;
        }
      
        public Object[] store() {
            return new Object[]{
                                   new STByte((byte)0),
                                   new STByte((byte)1),
                                   new STByte((byte)99),
                                   new STByte((byte)113)         };
        }
      
        public void testEquals() {
            Query q1 = st.query();
            q1.constrain(new STByte((byte)0));
            q1.descend(DESCENDANT).constrain(System.Convert.ToByte((byte)0));
            st.expectOne(q1, store()[0]);
        }
      
        public void testNotEquals() {
            Query q1 = st.query();
            Object[] r1 = store();
            Constraint c1 = q1.constrain(r1[0]);
            q1.descend(DESCENDANT).constrain(System.Convert.ToByte((byte)0)).not();
            st.expect(q1, new Object[]{
                                          r1[1],
                                          r1[2],
                                          r1[3]         });
        }
      
        public void testGreater() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STByte((byte)9));
            q1.descend(DESCENDANT).constraints().greater();
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[2],
                                          r1[3]         });
        }
      
        public void testSmaller() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STByte((byte)1));
            q1.descend(DESCENDANT).constraints().smaller();
            st.expectOne(q1, store()[0]);
        }
      
        public void testContains() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STByte((byte)9));
            q1.descend(DESCENDANT).constraints().contains();
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[2]         });
        }
      
        public void testNotContains() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STByte((byte)0));
            q1.descend(DESCENDANT).constrain(System.Convert.ToByte((byte)0)).contains().not();
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[1],
                                          r1[2],
                                          r1[3]         });
        }
      
        public void testLike() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STByte((byte)11));
            q1.descend(DESCENDANT).constraints().like();
            st.expectOne(q1, new STByte((byte)113));
            q1 = st.query();
            c1 = q1.constrain(new STByte((byte)10));
            q1.descend(DESCENDANT).constraints().like();
            st.expectNone(q1);
        }
      
        public void testNotLike() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STByte((byte)1));
            q1.descend(DESCENDANT).constraints().like().not();
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[0],
                                          r1[2]         });
        }
      
        public void testIdentity() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STByte((byte)1));
            ObjectSet set1 = q1.execute();
            STByte identityConstraint1 = (STByte)set1.next();
            identityConstraint1.i_byte = 102;
            q1 = st.query();
            q1.constrain(identityConstraint1).identity();
            identityConstraint1.i_byte = 1;
            st.expectOne(q1, store()[1]);
        }
      
        public void testNotIdentity() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STByte((byte)1));
            ObjectSet set1 = q1.execute();
            STByte identityConstraint1 = (STByte)set1.next();
            identityConstraint1.i_byte = 102;
            q1 = st.query();
            q1.constrain(identityConstraint1).identity().not();
            identityConstraint1.i_byte = 1;
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[0],
                                          r1[2],
                                          r1[3]         });
        }
      
        public void testConstraints() {
            Query q1 = st.query();
            q1.constrain(new STByte((byte)1));
            q1.constrain(new STByte((byte)0));
            Constraints cs1 = q1.constraints();
            Constraint[] csa1 = cs1.toArray();
            if (csa1.Length != 2) {
                st.error("Constraints not returned");
            }
        }
      
    }
}