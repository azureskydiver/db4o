/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.simple {

   public class STLong : STClass1 {
      [Transient] public static SodaTest st;
      public long i_long;
      
      public STLong() : base() {
      }
      
      internal STLong(long a_long) : base() {
         i_long = a_long;
      }
      
      public Object[] Store() {
         return new Object[]{
            new STLong(Int64.MinValue),
new STLong(-1),
new STLong(0),
new STLong(Int64.MaxValue - 1)         };
      }
      
      public void TestEquals() {
         Query q1 = st.Query();
         q1.Constrain(new STLong(Int64.MinValue));
         st.Expect(q1, new Object[]{
            new STLong(Int64.MinValue)         });
      }
      
      public void TestGreater() {
         Query q1 = st.Query();
         q1.Constrain(new STLong(-1));
         q1.Descend("i_long").Constraints().Greater();
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
      
      public void TestSmaller() {
         Query q1 = st.Query();
         q1.Constrain(new STLong(1));
         q1.Descend("i_long").Constraints().Smaller();
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2]         });
      }
      
      public void TestBetween() {
         Query q1 = st.Query();
         q1.Constrain(new STLong());
         Query sub1 = q1.Descend("i_long");
         sub1.Constrain(System.Convert.ToInt64(-3)).Greater();
         sub1.Constrain(System.Convert.ToInt64(3)).Smaller();
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[1],
r1[2]         });
      }
      
      public void TestAnd() {
         Query q1 = st.Query();
         q1.Constrain(new STLong());
         Query sub1 = q1.Descend("i_long");
         sub1.Constrain(System.Convert.ToInt64(-3)).Greater().And(sub1.Constrain(System.Convert.ToInt64(3)).Smaller());
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[1],
r1[2]         });
      }
      
      public void TestOr() {
         Query q1 = st.Query();
         q1.Constrain(new STLong());
         Query sub1 = q1.Descend("i_long");
         sub1.Constrain(System.Convert.ToInt64(3)).Greater().Or(sub1.Constrain(System.Convert.ToInt64(-3)).Smaller());
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[0],
r1[3]         });
      }
   }
}