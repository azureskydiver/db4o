/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.wrapper.untyped {

    public class STShortWU : STClass {
        static internal String DESCENDANT = "i_short";
        [Transient] public static SodaTest st;
        internal Object i_short;
      
        public STShortWU() : base() {
        }
      
        internal STShortWU(short a_short) : base() {
            i_short = System.Convert.ToInt16(a_short);
        }
      
        public Object[] Store() {
            return new Object[]{
                                   new STShortWU((short)0),
                                   new STShortWU((short)1),
                                   new STShortWU((short)99),
                                   new STShortWU((short)909)         };
        }
      
        public void TestEquals() {
            Query q1 = st.Query();
            q1.Constrain(new STShortWU((short)0));
            q1.Descend(DESCENDANT).Constrain(System.Convert.ToInt16((short)0));
            st.ExpectOne(q1, Store()[0]);
        }
      
        public void TestNotEquals() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            Constraint c1 = q1.Constrain(r1[0]);
            q1.Descend(DESCENDANT).Constraints().Not();
            st.Expect(q1, new Object[]{
                                          r1[1],
                                          r1[2],
                                          r1[3]         });
        }
      
        public void TestGreater() {
            Query q1 = st.Query();
            Constraint c1 = q1.Constrain(new STShortWU((short)9));
            q1.Descend(DESCENDANT).Constraints().Greater();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[2],
                                          r1[3]         });
        }
      
        public void TestSmaller() {
            Query q1 = st.Query();
            Constraint c1 = q1.Constrain(new STShortWU((short)1));
            q1.Descend(DESCENDANT).Constraints().Smaller();
            st.ExpectOne(q1, Store()[0]);
        }
      
        public void TestContains() {
            Query q1 = st.Query();
            Constraint c1 = q1.Constrain(new STShortWU((short)9));
            q1.Descend(DESCENDANT).Constraints().Contains();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[2],
                                          r1[3]         });
        }
      
        public void TestNotContains() {
            Query q1 = st.Query();
            Constraint c1 = q1.Constrain(new STShortWU((short)0));
            q1.Descend(DESCENDANT).Constraints().Contains().Not();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[1],
                                          r1[2]         });
        }
      
        public void TestLike() {
            Query q1 = st.Query();
            Constraint c1 = q1.Constrain(new STShortWU((short)90));
            q1.Descend(DESCENDANT).Constraints().Like();
            st.ExpectOne(q1, Store()[3]);
            q1 = st.Query();
            c1 = q1.Constrain(new STShortWU((short)10));
            q1.Descend(DESCENDANT).Constraints().Like();
            st.ExpectNone(q1);
        }
      
        public void TestNotLike() {
            Query q1 = st.Query();
            Constraint c1 = q1.Constrain(new STShortWU((short)1));
            q1.Descend(DESCENDANT).Constraints().Like().Not();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[2],
                                          r1[3]         });
        }
      
        public void TestIdentity() {
            Query q1 = st.Query();
            Constraint c1 = q1.Constrain(new STShortWU((short)1));
            ObjectSet set1 = q1.Execute();
            STShortWU identityConstraint1 = (STShortWU)set1.Next();
            identityConstraint1.i_short = System.Convert.ToInt16((short)9999);
            q1 = st.Query();
            q1.Constrain(identityConstraint1).Identity();
            identityConstraint1.i_short = System.Convert.ToInt16((short)1);
            st.ExpectOne(q1, Store()[1]);
        }
      
        public void TestNotIdentity() {
            Query q1 = st.Query();
            Constraint c1 = q1.Constrain(new STShortWU((short)1));
            ObjectSet set1 = q1.Execute();
            STShortWU identityConstraint1 = (STShortWU)set1.Next();
            identityConstraint1.i_short = System.Convert.ToInt16((short)9080);
            q1 = st.Query();
            q1.Constrain(identityConstraint1).Identity().Not();
            identityConstraint1.i_short = System.Convert.ToInt16((short)1);
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[2],
                                          r1[3]         });
        }
      
        public void TestConstraints() {
            Query q1 = st.Query();
            q1.Constrain(new STShortWU((short)1));
            q1.Constrain(new STShortWU((short)0));
            Constraints cs1 = q1.Constraints();
            Constraint[] csa1 = cs1.ToArray();
            if (csa1.Length != 2) {
                st.Error("Constraints not returned");
            }
        }
      
    }
}