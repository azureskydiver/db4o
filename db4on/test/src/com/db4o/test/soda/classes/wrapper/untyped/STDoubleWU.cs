/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.wrapper.untyped {

   public class STDoubleWU : STClass {
      [Transient] public static SodaTest st;
      internal Object i_double;
      
      public STDoubleWU() : base() {
      }
      
      internal STDoubleWU(double a_double) : base() {
         i_double = System.Convert.ToDouble(a_double);
      }
      
      public Object[] store() {
         return new Object[]{
            new STDoubleWU(0),
new STDoubleWU(0),
new STDoubleWU(1.01),
new STDoubleWU(99.99),
new STDoubleWU(909.0)         };
      }
      
      public void testEquals() {
         Query q1 = st.query();
         q1.constrain(new STDoubleWU(0));
         q1.descend("i_double").constrain(System.Convert.ToDouble(0));
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[1]         });
      }
      
      public void testGreater() {
         Query q1 = st.query();
         q1.constrain(new STDoubleWU(1));
         q1.descend("i_double").constraints().greater();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[2],
r1[3],
r1[4]         });
      }
      
      public void testSmaller() {
         Query q1 = st.query();
         q1.constrain(new STDoubleWU(1));
         q1.descend("i_double").constraints().smaller();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[1]         });
      }
      
      public void testGreaterOrEqual() {
         Query q1 = st.query();
         q1.constrain(store()[2]);
         q1.descend("i_double").constraints().greater().equal();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[2],
r1[3],
r1[4]         });
      }
      
      public void testGreaterAndNot() {
         Query q1 = st.query();
         q1.constrain(new STDoubleWU());
         Query val1 = q1.descend("i_double");
         val1.constrain(System.Convert.ToDouble(0)).greater();
         val1.constrain(System.Convert.ToDouble(99.99)).not();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[2],
r1[4]         });
      }
   }
}