/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.ordered {

   public class STOInteger : STClass {
      [Transient] public static SodaTest st;
      internal int i_int;
      
      public STOInteger() : base() {
      }
      
      internal STOInteger(int a_int) : base() {
         i_int = a_int;
      }
      
      public override String ToString() {
         return "STInteger: " + i_int;
      }
      
      public Object[] store() {
         return new Object[]{
            new STOInteger(1001),
new STOInteger(99),
new STOInteger(1),
new STOInteger(909),
new STOInteger(1001),
new STOInteger(0),
new STOInteger(1010)         };
      }
      
      public void testAscending() {
         Query q1 = st.query();
         q1.constrain(Class.getClassForType(typeof(STOInteger)));
         q1.descend("i_int").orderAscending();
         Object[] r1 = store();
         st.expectOrdered(q1, new Object[]{
            r1[5],
r1[2],
r1[1],
r1[3],
r1[0],
r1[4],
r1[6]         });
      }
      
      public void testDescending() {
         Query q1 = st.query();
         q1.constrain(Class.getClassForType(typeof(STOInteger)));
         q1.descend("i_int").orderDescending();
         Object[] r1 = store();
         st.expectOrdered(q1, new Object[]{
            r1[6],
r1[4],
r1[0],
r1[3],
r1[1],
r1[2],
r1[5]         });
      }
      
      public void testAscendingGreater() {
         Query q1 = st.query();
         q1.constrain(Class.getClassForType(typeof(STOInteger)));
         Query qInt1 = q1.descend("i_int");
         qInt1.constrain(System.Convert.ToInt32(100)).greater();
         qInt1.orderAscending();
         Object[] r1 = store();
         st.expectOrdered(q1, new Object[]{
            r1[3],
r1[0],
r1[4],
r1[6]         });
      }
   }
}