/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.util;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.simple {

    public class STDecimalU : STClass1 {
        [Transient] public static SodaTest st;
        
        public object i_decimal;
      
        public STDecimalU() : base() {
        }
      
        internal STDecimalU(object a_decimal) : base() {
            i_decimal = a_decimal;
        }
      
        public Object[] Store() {
            return new Object[]{
                                   new STDecimalU(null),
                                   new STDecimalU(1000),
                                   new STDecimalU(4000),
                                   new STDecimalU(5000),
                                   new STDecimalU(6000),
                                   new STDecimalU(7000) };
        }
      
        public void TestEquals() {
            Query q1 = st.Query();
            q1.Constrain(Store()[1]);
            st.ExpectOne(q1, Store()[1]);
        }
      
        public void TestGreater() {
            Query q1 = st.Query();
            q1.Constrain(Store()[3]);
            q1.Descend("i_decimal").Constraints().Greater();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[4],
                                          r1[5]         });
        }
      
        public void TestSmaller() {
            Query q1 = st.Query();
            q1.Constrain(Store()[4]);
            q1.Descend("i_decimal").Constraints().Smaller();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[1],
                                          r1[2],
                                          r1[3]         });
        }

        public void TestGreaterOrEqual(){
            Query q = st.Query();
            q.Constrain(Store()[3]);
            q.Descend("i_decimal").Constraints().Greater().Equal();
            Object[] r = Store();
            st.Expect(q, new Object[] {r[3], r[4], r[5]});
        }

        public void TestNotGreaterOrEqual() {
            Query q1 = st.Query();
            q1.Constrain(Store()[3]);
            q1.Descend("i_decimal").Constraints().Not().Greater().Equal();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[1],
                                          r1[2]         });
        }
      
        public void TestNull() {
            Query q1 = st.Query();
            q1.Constrain(new STDecimalU());
            q1.Descend("i_decimal").Constrain(null);
            st.ExpectOne(q1, Store()[0]);
        }
    }
      
 }