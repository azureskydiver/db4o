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
      
      public Object[] store() {
         return new Object[]{
            new STFloatWU(Single.MinValue),
new STFloatWU((float)1.23E-5),
new STFloatWU((float)1.345),
new STFloatWU(Single.MaxValue)         };
      }
      
      public void testEquals() {
         Query q1 = st.query();
         q1.constrain(store()[0]);
         st.expectOne(q1, store()[0]);
      }
      
      public void testGreater() {
         Query q1 = st.query();
         q1.constrain(new STFloatWU((float)0.1));
         q1.descend("i_float").constraints().greater();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
      
      public void testSmaller() {
         Query q1 = st.query();
         q1.constrain(new STFloatWU((float)1.5));
         q1.descend("i_float").constraints().smaller();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2]         });
      }
   }
}