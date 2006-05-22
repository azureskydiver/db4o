/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.wrapper.untyped {

   public class STFloatWU : STClass {
      [Transient] public static SodaTest st;
      internal Object i_float;
      
      public STFloatWU() : base() {
      }
      
      internal STFloatWU(float a_float) : base() {
         i_float = System.Convert.ToSingle(a_float);
      }
      
      public Object[] Store() {
         return new Object[]{
            new STFloatWU(Single.MinValue),
new STFloatWU((float)1.23E-5),
new STFloatWU((float)1.345),
new STFloatWU(Single.MaxValue)         };
      }
      
      public void TestEquals() {
         Query q1 = st.Query();
         q1.Constrain(Store()[0]);
         st.ExpectOne(q1, Store()[0]);
      }
      
      public void TestGreater() {
         Query q1 = st.Query();
         q1.Constrain(new STFloatWU((float)0.1));
         q1.Descend("i_float").Constraints().Greater();
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
      
      public void TestSmaller() {
         Query q1 = st.Query();
         q1.Constrain(new STFloatWU((float)1.5));
         q1.Descend("i_float").Constraints().Smaller();
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2]         });
      }
   }
}