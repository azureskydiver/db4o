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
      
        public Object[] store() {
            return new Object[]{
                                   new STBoolean(false),
                                   new STBoolean(true),
                                   new STBoolean(false),
                                   new STBoolean(false)         };
        }
      
        public void testEqualsTrue() {
            Query q1 = st.query();
            q1.constrain(new STBoolean(true));
            Object[] r1 = store();
            st.expectOne(q1, new STBoolean(true));
        }
      
        public void testEqualsFalse() {
            Query q1 = st.query();
            q1.constrain(new STBoolean(false));
            q1.descend("i_boolean").constrain(System.Convert.ToBoolean(false));
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[0],
                                          r1[2],
                                          r1[3]         });
        }
    }
}