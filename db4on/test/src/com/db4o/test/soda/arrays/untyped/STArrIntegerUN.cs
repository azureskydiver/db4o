/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.arrays.untyped {

   public class STArrIntegerUN : STClass {
      [Transient] public static SodaTest st;
      internal Object[,,] intArr;
      
      public STArrIntegerUN() : base() {
      }
      
      public STArrIntegerUN(Object[,,] arr) : base() {
         intArr = arr;
      }
      
      public Object[] Store() {
         STArrIntegerUN[] arr1 = new STArrIntegerUN[5];
         arr1[0] = new STArrIntegerUN();
         Object[,,] content1 = new Object[0,0,0];
         arr1[1] = new STArrIntegerUN(content1);
         content1 = new Object[1,2,3];
         content1[0,0,1] = System.Convert.ToInt32(0);
         content1[0,1,0] = System.Convert.ToInt32(0);
         arr1[2] = new STArrIntegerUN(content1);
         content1 = new Object[1,2,3];
         content1[0,0,0] = System.Convert.ToInt32(1);
         content1[0,1,0] = System.Convert.ToInt32(17);
         content1[0,1,1] = System.Convert.ToInt32(Int32.MaxValue - 1);
         arr1[3] = new STArrIntegerUN(content1);
         content1 = new Object[1,2,2];
         content1[0,0,0] = System.Convert.ToInt32(3);
         content1[0,0,1] = System.Convert.ToInt32(17);
         content1[0,1,0] = System.Convert.ToInt32(25);
         content1[0,1,1] = System.Convert.ToInt32(Int32.MaxValue - 2);
         arr1[4] = new STArrIntegerUN(content1);
         Object[] ret1 = new Object[arr1.Length];
		 System.Array.Copy(arr1, 0, ret1, 0, arr1.Length);
         return ret1;
      }
      
      public void TestDefaultContainsOne() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         Object[,,] content1 = new Object[1,1,1];
         content1[0,0,0] = System.Convert.ToInt32(17);
         q1.Constrain(new STArrIntegerUN(content1));
         st.Expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void TestDefaultContainsTwo() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         Object[,,] content1 = new Object[2,1,1];
         content1[0,0,0] = System.Convert.ToInt32(17);
         content1[1,0,0] = System.Convert.ToInt32(25);
         q1.Constrain(new STArrIntegerUN(content1));
         st.Expect(q1, new Object[]{
            r1[4]         });
      }
      
      public void TestDescendOne() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(Class.GetClassForType(typeof(STArrIntegerUN)));
         q1.Descend("intArr").Constrain(System.Convert.ToInt32(17));
         st.Expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void TestDescendTwo() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(Class.GetClassForType(typeof(STArrIntegerUN)));
         Query qElements1 = q1.Descend("intArr");
         qElements1.Constrain(System.Convert.ToInt32(17));
         qElements1.Constrain(System.Convert.ToInt32(25));
         st.Expect(q1, new Object[]{
            r1[4]         });
      }
      
      public void TestDescendSmaller() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(Class.GetClassForType(typeof(STArrIntegerUN)));
         Query qElements1 = q1.Descend("intArr");
         qElements1.Constrain(System.Convert.ToInt32(3)).Smaller();
         st.Expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
      
      public void TestDescendNotSmaller() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(Class.GetClassForType(typeof(STArrIntegerUN)));
         Query qElements1 = q1.Descend("intArr");
         qElements1.Constrain(System.Convert.ToInt32(3)).Smaller();
         st.Expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
   }
}