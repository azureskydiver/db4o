/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.wrapper.untyped {

    public class STBooleanWU : STClass1 {
        static internal String DESCENDANT = "i_boolean";
        [Transient] public static SodaTest st;
        public Object i_boolean;
      
        public STBooleanWU() : base() {
        }
      
        internal STBooleanWU(bool a_boolean) : base() {
            i_boolean = System.Convert.ToBoolean(a_boolean);
        }
      
        public Object[] Store() {
            return new Object[]{
                                   new STBooleanWU(false),
                                   new STBooleanWU(true),
                                   new STBooleanWU(false),
                                   new STBooleanWU(false),
                                   new STBooleanWU()         };
        }
      
        public void TestEqualsTrue() {
            Query q1 = st.Query();
            q1.Constrain(new STBooleanWU(true));
            Object[] r1 = Store();
            st.ExpectOne(q1, new STBooleanWU(true));
        }
      
        public void TestEqualsFalse() {
            Query q1 = st.Query();
            q1.Constrain(new STBooleanWU(false));
            q1.Descend(DESCENDANT).Constrain(System.Convert.ToBoolean(false));
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[2],
                                          r1[3]
                                      });
        }
      
        public void TestNull() {
            Query q1 = st.Query();
            q1.Constrain(new STBooleanWU());
            q1.Descend(DESCENDANT).Constrain(null);
            Object[] r1 = Store();
            st.ExpectOne(q1, new STBooleanWU());
        }
      
        public void TestNullOrTrue() {
            Query q1 = st.Query();
            q1.Constrain(new STBooleanWU());
            Query qd1 = q1.Descend(DESCENDANT);
            qd1.Constrain(null).Or(qd1.Constrain(System.Convert.ToBoolean(true)));
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[1],
                                          r1[4]         });
        }
      
        public void TestNotNullAndFalse() {
            Query q1 = st.Query();
            q1.Constrain(new STBooleanWU());
            Query qd1 = q1.Descend(DESCENDANT);
            qd1.Constrain(null).Not().And(qd1.Constrain(System.Convert.ToBoolean(false)));
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[2],
                                          r1[3]         });
        }
    }
}