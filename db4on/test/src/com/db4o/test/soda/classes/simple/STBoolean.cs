/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.simple {

    public class STBoolean : STClass1 {
        [Transient] public static SodaTest st;
        public bool i_boolean;
      
        public STBoolean() : base() {
        }
      
        internal STBoolean(bool a_boolean) : base() {
            i_boolean = a_boolean;
        }
      
        public Object[] Store() {
            return new Object[]{
                                   new STBoolean(false),
                                   new STBoolean(true),
                                   new STBoolean(false),
                                   new STBoolean(false)         };
        }
      
        public void TestEqualsTrue() {
            Query q1 = st.Query();
            q1.Constrain(new STBoolean(true));
            Object[] r1 = Store();
            st.ExpectOne(q1, new STBoolean(true));
        }
      
        public void TestEqualsFalse() {
            Query q1 = st.Query();
            q1.Constrain(new STBoolean(false));
            q1.Descend("i_boolean").Constrain(System.Convert.ToBoolean(false));
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[2],
                                          r1[3]         });
        }
    }
}