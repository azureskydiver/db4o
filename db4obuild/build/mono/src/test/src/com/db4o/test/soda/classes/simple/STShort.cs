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

   public class STShort : STClass1 {
      static internal String DESCENDANT = "i_short";
      [Transient] public static SodaTest st;
      public short i_short;
      
      public STShort() : base() {
      }
      
      internal STShort(short a_short) : base() {
         i_short = a_short;
      }
      
      public Object[] store() {
         return new Object[]{
            new STShort((short)0),
new STShort((short)1),
new STShort((short)99),
new STShort((short)909)         };
      }
      
      public void testEquals() {
         Query q1 = st.query();
         q1.constrain(new STShort((short)0));
         q1.descend(DESCENDANT).constrain(System.Convert.ToInt16((short)0));
         st.expectOne(q1, store()[0]);
      }
      
      public void testNotEquals() {
         Query q1 = st.query();
         Object[] r1 = store();
         Constraint c1 = q1.constrain(r1[0]);
         q1.descend(DESCENDANT).constrain(System.Convert.ToInt16((short)0)).not();
         st.expect(q1, new Object[]{
            r1[1],
r1[2],
r1[3]         });
      }
      
      public void testGreater() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STShort((short)9));
         q1.descend(DESCENDANT).constraints().greater();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
      
      public void testSmaller() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STShort((short)1));
         q1.descend(DESCENDANT).constraints().smaller();
         st.expectOne(q1, store()[0]);
      }
      
      public void testContains() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STShort((short)9));
         q1.descend(DESCENDANT).constraints().contains();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
      
      public void testNotContains() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STShort((short)0));
         q1.descend(DESCENDANT).constrain(System.Convert.ToInt16((short)0)).contains().not();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[1],
r1[2]         });
      }
      
      public void testLike() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STShort((short)90));
         q1.descend(DESCENDANT).constraints().like();
         st.expectOne(q1, store()[3]);
         q1 = st.query();
         c1 = q1.constrain(new STShort((short)10));
         q1.descend(DESCENDANT).constraints().like();
         st.expectNone(q1);
      }
      
      public void testNotLike() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STShort((short)1));
         q1.descend(DESCENDANT).constraints().like().not();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[2],
r1[3]         });
      }
      
      public void testIdentity() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STShort((short)1));
         ObjectSet set1 = q1.execute();
         STShort identityConstraint1 = (STShort)set1.next();
         identityConstraint1.i_short = 9999;
         q1 = st.query();
         q1.constrain(identityConstraint1).identity();
         identityConstraint1.i_short = 1;
         st.expectOne(q1, store()[1]);
      }
      
      public void testNotIdentity() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STShort((short)1));
         ObjectSet set1 = q1.execute();
         STShort identityConstraint1 = (STShort)set1.next();
         identityConstraint1.i_short = 9080;
         q1 = st.query();
         q1.constrain(identityConstraint1).identity().not();
         identityConstraint1.i_short = 1;
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[2],
r1[3]         });
      }
      
      public void testConstraints() {
         Query q1 = st.query();
         q1.constrain(new STShort((short)1));
         q1.constrain(new STShort((short)0));
         Constraints cs1 = q1.constraints();
         Constraint[] csa1 = cs1.toArray();
         if (csa1.Length != 2) {
            st.error("Constraints not returned");
         }
      }
      
  }
}