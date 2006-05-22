/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.util;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.wrapper.untyped {

    public class STDateU : STClass {
        [Transient] public static SodaTest st;
        internal Object i_date;
      
        public STDateU() : base() {
        }
      
        internal STDateU(DateTime a_date) : base() {
            i_date = a_date;
        }
      
        public Object[] Store() {
            return new Object[]{
                                   new STDateU(new DateTime(1000)),
                                   new STDateU(new DateTime(4000)),
                                   new STDateU(new DateTime(5000)),
                                   new STDateU(new DateTime(6000)),
                                   new STDateU(new DateTime(7000))         };
        }
      
        public void TestEquals() {
            Query q1 = st.Query();
            q1.Constrain(Store()[1]);
            st.ExpectOne(q1, Store()[1]);
        }
      
        public void TestGreater() {
            Query q1 = st.Query();
            q1.Constrain(Store()[2]);
            q1.Descend("i_date").Constraints().Greater();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[3],
                                          r1[4]         });
        }
      
        public void TestSmaller() {
            Query q1 = st.Query();
            q1.Constrain(Store()[4]);
            q1.Descend("i_date").Constraints().Smaller();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[1],
                                          r1[2],
                                          r1[3]         });
        }
      
        public void TestNotGreaterOrEqual() {
            Query q1 = st.Query();
            q1.Constrain(Store()[3]);
            q1.Descend("i_date").Constraints().Not().Greater().Equal();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[1],
                                          r1[2]         });
        }
      
        public void TestNull() {
            Query q1 = st.Query();
            q1.Constrain(new STDateU());
            q1.Descend("i_date").Constrain(null);
            st.ExpectNone(q1);
        }
   }
}