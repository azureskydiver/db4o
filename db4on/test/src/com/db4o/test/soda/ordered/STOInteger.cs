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
      
      public Object[] Store() {
         return new Object[]{
            new STOInteger(1001),
new STOInteger(99),
new STOInteger(1),
new STOInteger(909),
new STOInteger(1001),
new STOInteger(0),
new STOInteger(1010)         };
      }
      
      public void TestAscending() {
         Query q1 = st.Query();
         q1.Constrain(Class.GetClassForType(typeof(STOInteger)));
         q1.Descend("i_int").OrderAscending();
         Object[] r1 = Store();
         st.ExpectOrdered(q1, new Object[]{
            r1[5],
r1[2],
r1[1],
r1[3],
r1[0],
r1[4],
r1[6]         });
      }
      
      public void TestDescending() {
         Query q1 = st.Query();
         q1.Constrain(Class.GetClassForType(typeof(STOInteger)));
         q1.Descend("i_int").OrderDescending();
         Object[] r1 = Store();
         st.ExpectOrdered(q1, new Object[]{
            r1[6],
r1[4],
r1[0],
r1[3],
r1[1],
r1[2],
r1[5]         });
      }
      
      public void TestAscendingGreater() {
         Query q1 = st.Query();
         q1.Constrain(Class.GetClassForType(typeof(STOInteger)));
         Query qInt1 = q1.Descend("i_int");
         qInt1.Constrain(System.Convert.ToInt32(100)).Greater();
         qInt1.OrderAscending();
         Object[] r1 = Store();
         st.ExpectOrdered(q1, new Object[]{
            r1[3],
r1[0],
r1[4],
r1[6]         });
      }
   }
}