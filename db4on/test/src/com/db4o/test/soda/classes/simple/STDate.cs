/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.util;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.simple {

    public class STDate : STClass1 {
        [Transient] public static SodaTest st;
        
        public DateTime i_date;
      
        public STDate() : base() {
        }
      
        internal STDate(DateTime a_date) : base() {
            i_date = a_date;
        }
      
        public Object[] store() {
            return new Object[]{
                                   new STDate(new DateTime(1000)),
                                   new STDate(new DateTime(4000)),
                                   new STDate(new DateTime(5000)),
                                   new STDate(new DateTime(6000)),
                                   new STDate(new DateTime(7000))         };
        }
      
        public void testEquals() {
            Query q1 = st.query();
            q1.constrain(store()[1]);
            st.expectOne(q1, store()[1]);
        }
      
        public void testGreater() {
            Query q1 = st.query();
            q1.constrain(store()[2]);
            q1.descend("i_date").constraints().greater();
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[3],
                                          r1[4]         });
        }
      
        public void testSmaller() {
            Query q1 = st.query();
            q1.constrain(store()[4]);
            q1.descend("i_date").constraints().smaller();
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[0],
                                          r1[1],
                                          r1[2],
                                          r1[3]         });
        }

        public void testGreaterOrEqual(){
            Query q = st.query();
            q.constrain(store()[2]);
            q.descend("i_date").constraints().greater().equal();
            Object[] r = store();
            st.expect(q, new Object[] {r[2], r[3], r[4]});
        }

        public void testNotGreaterOrEqual() {
            Query q1 = st.query();
            q1.constrain(store()[3]);
            q1.descend("i_date").constraints().not().greater().equal();
            Object[] r1 = store();
            st.expect(q1, new Object[]{
                                          r1[0],
                                          r1[1],
                                          r1[2]         });
        }
      
        public void testNull() {
            Query q1 = st.query();
            q1.constrain(new STDate());
            q1.descend("i_date").constrain(null);
            st.expectNone(q1);
        }
    }
      
 }