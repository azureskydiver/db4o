/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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
      
        public Object[] Store() {
            return new Object[]{
                                   new STBooleanWT(false),
                                   new STBooleanWT(true),
                                   new STBooleanWT(false),
                                   new STBooleanWT(false),
                                   new STBooleanWT()         };
        }
      
        public void TestEqualsTrue() {
            Query q1 = st.Query();
            q1.Constrain(new STBooleanWT(true));
            Object[] r1 = Store();
            st.ExpectOne(q1, new STBooleanWT(true));
        }
      
        public void TestEqualsFalse() {
            Query q1 = st.Query();
            q1.Constrain(new STBooleanWT(false));
            q1.Descend(DESCENDANT).Constrain(System.Convert.ToBoolean(false));
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[2],
                                          r1[3],
                                          r1[4]         });
        }
      
        public void TestNull() {
            Query q1 = st.Query();
            q1.Constrain(new STBooleanWT());
            q1.Descend(DESCENDANT).Constrain(null);
            Object[] r1 = Store();
            st.ExpectNone(q1);
        }
      
        public void TestNullOrTrue() {
            Query q1 = st.Query();
            q1.Constrain(new STBooleanWT());
            Query qd1 = q1.Descend(DESCENDANT);
            qd1.Constrain(null).Or(qd1.Constrain(System.Convert.ToBoolean(true)));
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[1]
                                      });
        }
      
        public void TestNotNullAndFalse() {
            Query q1 = st.Query();
            q1.Constrain(new STBooleanWT());
            Query qd1 = q1.Descend(DESCENDANT);
            qd1.Constrain(null).Not().And(qd1.Constrain(System.Convert.ToBoolean(false)));
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[2],
                                          r1[3],
                                          r1[4]
            });
        }
    }
}