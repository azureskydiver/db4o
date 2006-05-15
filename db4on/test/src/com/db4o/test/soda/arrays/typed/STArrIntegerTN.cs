/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.arrays.typed {

   public class STArrIntegerTN : STClass {
      [Transient] public static SodaTest st;
      internal int[,,] intArr;
      
      public STArrIntegerTN() : base() {
      }
      
      public STArrIntegerTN(int[,,] arr) : base() {
         intArr = arr;
      }
      
      public Object[] store() {
         STArrIntegerTN[] arr1 = new STArrIntegerTN[5];
         arr1[0] = new STArrIntegerTN();
         int[,,] content1 = new int[0,0,0];
         arr1[1] = new STArrIntegerTN(content1);
         content1 = new int[1,2,3];
         content1[0,0,1] = 0;
         content1[0,1,0] = 0;
         arr1[2] = new STArrIntegerTN(content1);
         content1 = new int[1,2,3];
         content1[0,0,0] = 1;
         content1[0,1,0] = 17;
         content1[0,1,1] = Int32.MaxValue - 1;
         arr1[3] = new STArrIntegerTN(content1);
         content1 = new int[1,2,2];
         content1[0,0,0] = 3;
         content1[0,0,1] = 17;
         content1[0,1,0] = 25;
         content1[0,1,1] = Int32.MaxValue - 2;
         arr1[4] = new STArrIntegerTN(content1);
         Object[] ret1 = new Object[arr1.Length];
		 System.Array.Copy(arr1, 0, ret1, 0, arr1.Length);
         return ret1;
      }
      
      public void testDefaultContainsOne() {
         Query q1 = st.query();
         Object[] r1 = store();
         int[,,] content1 = new int[1,1,1];
         content1[0,0,0] = 17;
         q1.constrain(new STArrIntegerTN(content1));
         st.expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void testDefaultContainsTwo() {
         Query q1 = st.query();
         Object[] r1 = store();
         int[,,] content1 = new int[2,1,1];
         content1[0,0,0] = 17;
         content1[1,0,0] = 25;
         q1.constrain(new STArrIntegerTN(content1));
         st.expect(q1, new Object[]{
            r1[4]         });
      }
      
      public void testDescendOne() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrIntegerTN)));
         q1.descend("intArr").constrain(System.Convert.ToInt32(17));
         st.expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void testDescendTwo() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrIntegerTN)));
         Query qElements1 = q1.descend("intArr");
         qElements1.constrain(System.Convert.ToInt32(17));
         qElements1.constrain(System.Convert.ToInt32(25));
         st.expect(q1, new Object[]{
            r1[4]         });
      }
      
      public void testDescendSmaller() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrIntegerTN)));
         Query qElements1 = q1.descend("intArr");
         qElements1.constrain(System.Convert.ToInt32(3)).smaller();
         st.expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
      
      public void testDescendNotSmaller() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrIntegerTN)));
         Query qElements1 = q1.descend("intArr");
         qElements1.constrain(System.Convert.ToInt32(3)).smaller();
         st.expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
   }
}