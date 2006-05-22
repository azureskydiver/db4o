/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
using com.db4o.test.soda.arrays.typed;
namespace com.db4o.test.soda.classes.simple {

    public class STString : STClass1, STInterface {
        [Transient] public static SodaTest st;
        public String str;
      
        public STString() : base() {
        }
      
        public STString(String str) : base() {
            this.str = str;
        }
      
        /**
         * needed for STInterface test 
         */
        public Object ReturnSomething() {
            return str;
        }
      
        public Object[] Store() {
            return new Object[]{
                                   new STString(null),
                                   new STString("aaa"),
                                   new STString("bbb"),
                                   new STString("dod")         };
        }
      
        public void TestEquals() {
            Query q1 = st.Query();
            q1.Constrain(Store()[2]);
            st.ExpectOne(q1, Store()[2]);
        }
      
        public void TestNotEquals() {
            Query q1 = st.Query();
            Constraint c1 = q1.Constrain(Store()[2]);
            q1.Descend("str").Constraints().Not();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[1],
                                          r1[3]         });
        }
      
        public void TestDescendantEquals() {
            Query q1 = st.Query();
            q1.Constrain(new STString());
            q1.Descend("str").Constrain("bbb");
            st.ExpectOne(q1, new STString("bbb"));
        }
      
        public void TestContains() {
            Query q1 = st.Query();
            Constraint c1 = q1.Constrain(new STString("od"));
            q1.Descend("str").Constraints().Contains();
            st.ExpectOne(q1, new STString("dod"));
        }
      
        public void TestNotContains() {
            Query q1 = st.Query();
            Constraint c1 = q1.Constrain(new STString("od"));
            q1.Descend("str").Constraints().Contains().Not();
            st.Expect(q1, new Object[]{
                                          new STString(null),
                                          new STString("aaa"),
                                          new STString("bbb")         });
        }
      
        public void TestLike() {
            Query q1 = st.Query();
            Constraint c1 = q1.Constrain(new STString("do"));
            q1.Descend("str").Constraints().Like();
            st.ExpectOne(q1, new STString("dod"));
            q1 = st.Query();
            c1 = q1.Constrain(new STString("od"));
            q1.Descend("str").Constraints().Like();
            st.ExpectOne(q1, new STString("dod"));
        }
      
        public void TestNotLike() {
            Query q1 = st.Query();
            Constraint c1 = q1.Constrain(new STString("aaa"));
            q1.Descend("str").Constraints().Like().Not();
            st.Expect(q1, new Object[]{
                                          new STString(null),
                                          new STString("bbb"),
                                          new STString("dod")         });
            q1 = st.Query();
            c1 = q1.Constrain(new STString("xxx"));
            q1.Descend("str").Constraints().Like();
            st.ExpectNone(q1);
        }
      
        public void TestIdentity() {
            Query q1 = st.Query();
            Constraint c1 = q1.Constrain(new STString("aaa"));
            ObjectSet set1 = q1.Execute();
            STString identityConstraint1 = (STString)set1.Next();
            identityConstraint1.str = "hihs";
            q1 = st.Query();
            q1.Constrain(identityConstraint1).Identity();
            identityConstraint1.str = "aaa";
            st.ExpectOne(q1, new STString("aaa"));
        }
      
        public void TestNotIdentity() {
            Query q1 = st.Query();
            Constraint c1 = q1.Constrain(new STString("aaa"));
            ObjectSet set1 = q1.Execute();
            STString identityConstraint1 = (STString)set1.Next();
            identityConstraint1.str = null;
            q1 = st.Query();
            q1.Constrain(identityConstraint1).Identity().Not();
            identityConstraint1.str = "aaa";
            st.Expect(q1, new Object[]{
                                          new STString(null),
                                          new STString("bbb"),
                                          new STString("dod")         });
        }
      
        public void TestNull() {
            Query q1 = st.Query();
            Constraint c1 = q1.Constrain(new STString(null));
            q1.Descend("str").Constrain(null);
            st.ExpectOne(q1, new STString(null));
        }
      
        public void TestNotNull() {
            Query q1 = st.Query();
            Constraint c1 = q1.Constrain(new STString(null));
            q1.Descend("str").Constrain(null).Not();
            st.Expect(q1, new Object[]{
                                          new STString("aaa"),
                                          new STString("bbb"),
                                          new STString("dod")         });
        }
      
        public void TestConstraints() {
            Query q1 = st.Query();
            q1.Constrain(new STString("aaa"));
            q1.Constrain(new STString("bbb"));
            Constraints cs1 = q1.Constraints();
            Constraint[] csa1 = cs1.ToArray();
            if (csa1.Length != 2) {
                st.Error("Constraints not returned");
            }
        }
      
      
    }
}