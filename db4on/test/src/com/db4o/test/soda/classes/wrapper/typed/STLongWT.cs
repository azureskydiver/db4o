/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.wrapper.typed {

   public class STLongWT : STClass {
      [Transient] public static SodaTest st;
      internal Int64 i_long;
      
      public STLongWT() : base() {
      }
      
      internal STLongWT(long a_long) : base() {
         i_long = System.Convert.ToInt64(a_long);
      }
      
      public Object[] store() {
         return new Object[]{
            new STLongWT(Int64.MinValue),
new STLongWT(-1),
new STLongWT(0),
new STLongWT(Int64.MaxValue - 1)         };
      }
      
      public void testEquals() {
         Query q1 = st.query();
         q1.constrain(new STLongWT(Int64.MinValue));
         st.expect(q1, new Object[]{
            new STLongWT(Int64.MinValue)         });
      }
      
      public void testGreater() {
         Query q1 = st.query();
         q1.constrain(new STLongWT(-1));
         q1.descend("i_long").constraints().greater();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
      
      public void testSmaller() {
         Query q1 = st.query();
         q1.constrain(new STLongWT(1));
         q1.descend("i_long").constraints().smaller();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2]         });
      }
      
      public void testBetween() {
         Query q1 = st.query();
         q1.constrain(new STLongWT());
         Query sub1 = q1.descend("i_long");
         sub1.constrain(System.Convert.ToInt64(-3)).greater();
         sub1.constrain(System.Convert.ToInt64(3)).smaller();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[1],
r1[2]         });
      }
      
      public void testAnd() {
         Query q1 = st.query();
         q1.constrain(new STLongWT());
         Query sub1 = q1.descend("i_long");
         sub1.constrain(System.Convert.ToInt64(-3)).greater().and(sub1.constrain(System.Convert.ToInt64(3)).smaller());
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[1],
r1[2]         });
      }
      
      public void testOr() {
         Query q1 = st.query();
         q1.constrain(new STLongWT());
         Query sub1 = q1.descend("i_long");
         sub1.constrain(System.Convert.ToInt64(3)).greater().or(sub1.constrain(System.Convert.ToInt64(-3)).smaller());
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[3]         });
      }
   }
}