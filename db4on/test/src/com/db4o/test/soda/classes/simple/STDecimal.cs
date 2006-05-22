/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.util;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.simple {

    public class STDecimal : STClass1 {
        [Transient] public static SodaTest st;
        
        public decimal i_decimal;
      
        public STDecimal() : base() {
        }
      
        internal STDecimal(Decimal a_decimal) : base() {
            i_decimal = a_decimal;
        }
      
        public Object[] Store() {
            return new Object[]{
                                   new STDecimal(1000),
                                   new STDecimal(4000),
                                   new STDecimal(5000),
                                   new STDecimal(6000),
                                   new STDecimal(7000) };
        }
      
        public void TestEquals() {
            Query q1 = st.Query();
            q1.Constrain(Store()[1]);
            st.ExpectOne(q1, Store()[1]);
        }
      
        public void TestGreater() {
            Query q1 = st.Query();
            q1.Constrain(Store()[2]);
            q1.Descend("i_decimal").Constraints().Greater();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[3],
                                          r1[4]         });
        }
      
        public void TestSmaller() {
            Query q1 = st.Query();
            q1.Constrain(Store()[4]);
            q1.Descend("i_decimal").Constraints().Smaller();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[1],
                                          r1[2],
                                          r1[3]         });
        }

        public void TestGreaterOrEqual(){
            Query q = st.Query();
            q.Constrain(Store()[2]);
            q.Descend("i_decimal").Constraints().Greater().Equal();
            Object[] r = Store();
            st.Expect(q, new Object[] {r[2], r[3], r[4]});
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
            q1.Constrain(new STDecimal());
            q1.Descend("i_decimal").Constrain(null);
            st.ExpectNone(q1);
        }
    }
      
 }