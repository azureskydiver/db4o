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
      
      public Object[] Store() {
         return new Object[]{
            new STDoubleWU(0),
new STDoubleWU(0),
new STDoubleWU(1.01),
new STDoubleWU(99.99),
new STDoubleWU(909.0)         };
      }
      
      public void TestEquals() {
         Query q1 = st.Query();
         q1.Constrain(new STDoubleWU(0));
         q1.Descend("i_double").Constrain(System.Convert.ToDouble(0));
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[0],
r1[1]         });
      }
      
      public void TestGreater() {
         Query q1 = st.Query();
         q1.Constrain(new STDoubleWU(1));
         q1.Descend("i_double").Constraints().Greater();
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[2],
r1[3],
r1[4]         });
      }
      
      public void TestSmaller() {
         Query q1 = st.Query();
         q1.Constrain(new STDoubleWU(1));
         q1.Descend("i_double").Constraints().Smaller();
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[0],
r1[1]         });
      }
      
      public void TestGreaterOrEqual() {
         Query q1 = st.Query();
         q1.Constrain(Store()[2]);
         q1.Descend("i_double").Constraints().Greater().Equal();
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[2],
r1[3],
r1[4]         });
      }
      
      public void TestGreaterAndNot() {
         Query q1 = st.Query();
         q1.Constrain(new STDoubleWU());
         Query val1 = q1.Descend("i_double");
         val1.Constrain(System.Convert.ToDouble(0)).Greater();
         val1.Constrain(System.Convert.ToDouble(99.99)).Not();
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[2],
r1[4]         });
      }
   }
}