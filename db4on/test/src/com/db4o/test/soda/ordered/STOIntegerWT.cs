/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.ordered {

   public class STOIntegerWT : STClass {
      [Transient] public static SodaTest st;
      internal Int32 i_int;
      
      public STOIntegerWT() : base() {
      }
      
      internal STOIntegerWT(int a_int) : base() {
         i_int = System.Convert.ToInt32(a_int);
      }
      
      public Object[] Store() {
         return new Object[]{
            new STOIntegerWT(1001),
new STOIntegerWT(99),
new STOIntegerWT(1),
new STOIntegerWT(909),
new STOIntegerWT(1001),
new STOIntegerWT(0),
new STOIntegerWT(1010),
};
      }
      
      public void TestAscending() {
         Query q1 = st.Query();
         q1.Constrain(Class.GetClassForType(typeof(STOIntegerWT)));
         q1.Descend("i_int").OrderAscending();
         Object[] r1 = Store();
         st.ExpectOrdered(q1, new Object[]{
            r1[5],
r1[2],
r1[1],
r1[3],
r1[0],
r1[4],
r1[6]
         });
      }
      
      public void TestDescending() {
         Query q1 = st.Query();
         q1.Constrain(Class.GetClassForType(typeof(STOIntegerWT)));
         q1.Descend("i_int").OrderDescending();
         Object[] r1 = Store();
         st.ExpectOrdered(q1, new Object[]{
            r1[6],
r1[4],
r1[0],
r1[3],
r1[1],
r1[2],
r1[5]
       });
      }
      
      public void TestAscendingGreater() {
         Query q1 = st.Query();
         q1.Constrain(Class.GetClassForType(typeof(STOIntegerWT)));
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