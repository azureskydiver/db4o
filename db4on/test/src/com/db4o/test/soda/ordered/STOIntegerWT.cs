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
      
      public Object[] store() {
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
      
      public void testAscending() {
         Query q1 = st.query();
         q1.constrain(Class.getClassForType(typeof(STOIntegerWT)));
         q1.descend("i_int").orderAscending();
         Object[] r1 = store();
         st.expectOrdered(q1, new Object[]{
            r1[5],
r1[2],
r1[1],
r1[3],
r1[0],
r1[4],
r1[6]
         });
      }
      
      public void testDescending() {
         Query q1 = st.query();
         q1.constrain(Class.getClassForType(typeof(STOIntegerWT)));
         q1.descend("i_int").orderDescending();
         Object[] r1 = store();
         st.expectOrdered(q1, new Object[]{
            r1[6],
r1[4],
r1[0],
r1[3],
r1[1],
r1[2],
r1[5]
       });
      }
      
      public void testAscendingGreater() {
         Query q1 = st.query();
         q1.constrain(Class.getClassForType(typeof(STOIntegerWT)));
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