/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.wrapper.untyped {

    public class STIntegerWU : STClass {
        [Transient] public static SodaTest st;
        internal Object i_int;
      
        public STIntegerWU() : base() {
        }
      
        internal STIntegerWU(int a_int) : base() {
            i_int = System.Convert.ToInt32(a_int);
        }
      
        public Object[] store() {
            return new Object[]{
                                   new STIntegerWU(0),
                                   new STIntegerWU(1),
                                   new STIntegerWU(99),
                                   new STIntegerWU(909)         };
        }
      
        public void testEquals() {
            Query q1 = st.query();
            q1.constrain(new STIntegerWU(0));
            q1.descend("i_int").constrain(System.Convert.ToInt32(0));
            st.expectOne(q1, store()[0]);
        }
      
        public void testNotEquals() {
            Query q1 = st.query();
            Object[] r1 = store();
            Constraint c1 = q1.constrain(new STIntegerWU());
            q1.descend("i_int").constrain(System.Convert.ToInt32(0)).not();
            st.expect(q1, new Object[]{
                                          r1[1],
                                          r1[2],
                                          r1[3]         });
        }
      
        public void testGreater() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STIntegerWU(9));
            q1.descend("i_int").constraints().greater();
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[2],
                                          r1[3]         });
        }
      
        public void testSmaller() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STIntegerWU(1));
            q1.descend("i_int").constraints().smaller();
            st.expectOne(q1, store()[0]);
        }
      
        public void testContains() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STIntegerWU(9));
            q1.descend("i_int").constraints().contains();
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[2],
                                          r1[3]         });
        }
      
        public void testNotContains() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STIntegerWU());
            q1.descend("i_int").constrain(System.Convert.ToInt32(0)).contains().not();
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[1],
                                          r1[2]         });
        }
      
        public void testLike() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STIntegerWU(90));
            q1.descend("i_int").constraints().like();
            st.expectOne(q1, new STIntegerWU(909));
            q1 = st.query();
            c1 = q1.constrain(new STIntegerWU(10));
            q1.descend("i_int").constraints().like();
            st.expectNone(q1);
        }
      
        public void testNotLike() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STIntegerWU(1));
            q1.descend("i_int").constraints().like().not();
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[0],
                                          r1[2],
                                          r1[3]         });
        }
      
        public void testIdentity() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STIntegerWU(1));
            ObjectSet set1 = q1.execute();
            STIntegerWU identityConstraint1 = (STIntegerWU)set1.next();
            identityConstraint1.i_int = System.Convert.ToInt32(9999);
            q1 = st.query();
            q1.constrain(identityConstraint1).identity();
            identityConstraint1.i_int = System.Convert.ToInt32(1);
            st.expectOne(q1, store()[1]);
        }
      
        public void testNotIdentity() {
            Query q1 = st.query();
            Constraint c1 = q1.constrain(new STIntegerWU(1));
            ObjectSet set1 = q1.execute();
            STIntegerWU identityConstraint1 = (STIntegerWU)set1.next();
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
            q1.constrain(new STIntegerWU(1));
            q1.constrain(new STIntegerWU(0));
            Constraints cs1 = q1.constraints();
            Constraint[] csa1 = cs1.toArray();
            if (csa1.Length != 2) {
                st.error("Constraints not returned");
            }
        }
    }

}