/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.wrapper.typed {

   public class STIntegerWT : STClass {
      [Transient] public static SodaTest st;
      internal Int32 i_int;
      
      public STIntegerWT() : base() {
      }
      
      internal STIntegerWT(int a_int) : base() {
         i_int = System.Convert.ToInt32(a_int);
      }
      
      public Object[] store() {
         return new Object[]{
            new STIntegerWT(0),
new STIntegerWT(1),
new STIntegerWT(99),
new STIntegerWT(909)         };
      }
      
      public void testEquals() {
         Query q1 = st.query();
         q1.constrain(new STIntegerWT(0));
         q1.descend("i_int").constrain(System.Convert.ToInt32(0));
         st.expectOne(q1, store()[0]);
      }
      
      public void testNotEquals() {
         Query q1 = st.query();
         Object[] r1 = store();
         Constraint c1 = q1.constrain(new STIntegerWT());
         q1.descend("i_int").constrain(System.Convert.ToInt32(0)).not();
         st.expect(q1, new Object[]{
            r1[1],
r1[2],
r1[3]         });
      }
      
      public void testGreater() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STIntegerWT(9));
         q1.descend("i_int").constraints().greater();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
      
      public void testSmaller() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STIntegerWT(1));
         q1.descend("i_int").constraints().smaller();
         st.expectOne(q1, store()[0]);
      }
      
      public void testContains() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STIntegerWT(9));
         q1.descend("i_int").constraints().contains();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
      
      public void testNotContains() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STIntegerWT());
         q1.descend("i_int").constrain(System.Convert.ToInt32(0)).contains().not();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[1],
r1[2]         });
      }
      
      public void testLike() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STIntegerWT(90));
         q1.descend("i_int").constraints().like();
         st.expectOne(q1, new STIntegerWT(909));
         q1 = st.query();
         c1 = q1.constrain(new STIntegerWT(10));
         q1.descend("i_int").constraints().like();
         st.expectNone(q1);
      }
      
      public void testNotLike() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STIntegerWT(1));
         q1.descend("i_int").constraints().like().not();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[2],
r1[3]         });
      }
      
      public void testIdentity() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STIntegerWT(1));
         ObjectSet set1 = q1.execute();
         STIntegerWT identityConstraint1 = (STIntegerWT)set1.next();
         identityConstraint1.i_int = System.Convert.ToInt32(9999);
         q1 = st.query();
         q1.constrain(identityConstraint1).identity();
         identityConstraint1.i_int = System.Convert.ToInt32(1);
         st.expectOne(q1, store()[1]);
      }
      
      public void testNotIdentity() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STIntegerWT(1));
         ObjectSet set1 = q1.execute();
         STIntegerWT identityConstraint1 = (STIntegerWT)set1.next();
         identityConstraint1.i_int = System.Convert.ToInt32(9080);
         q1 = st.query();
         q1.constrain(identityConstraint1).identity().not();
         identityConstraint1.i_int = System.Convert.ToInt32(1);
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[2],
r1[3]         });
      }
      
      public void testConstraints() {
         Query q1 = st.query();
         q1.constrain(new STIntegerWT(1));
         q1.constrain(new STIntegerWT(0));
         Constraints cs1 = q1.constraints();
         Constraint[] csa1 = cs1.toArray();
         if (csa1.Length != 2) {
            st.error("Constraints not returned");
         }
      }
      
  }
}