/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.simple {

   public class STFloat : STClass1 {
      [Transient] public static SodaTest st;
      public float i_float;
      
      public STFloat() : base() {
      }
      
      internal STFloat(float a_float) : base() {
         i_float = a_float;
      }
      
      public Object[] Store() {
         return new Object[]{
            new STFloat(Single.MinValue),
new STFloat((float)1.23E-5),
new STFloat((float)1.345),
new STFloat(Single.MaxValue)         };
      }
      
      public void TestEquals() {
         Query q1 = st.Query();
         q1.Constrain(Store()[0]);
         st.ExpectOne(q1, Store()[0]);
      }
      
      public void TestGreater() {
         Query q1 = st.Query();
         q1.Constrain(new STFloat((float)0.1));
         q1.Descend("i_float").Constraints().Greater();
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
      
      public void TestSmaller() {
         Query q1 = st.Query();
         q1.Constrain(new STFloat((float)1.5));
         q1.Descend("i_float").Constraints().Smaller();
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2]         });
      }
   }
}