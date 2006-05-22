/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.arrays.obj {

   public class STArrStringON : STClass {
      [Transient] public static SodaTest st;
      internal Object strArr;
      
      public STArrStringON() : base() {
      }
      
      public STArrStringON(Object[,,] arr) : base() {
         strArr = arr;
      }
      
      public Object[] Store() {
         STArrStringON[] arr1 = new STArrStringON[5];
         arr1[0] = new STArrStringON();
         String[,,] content1 = new String[1,1,2];
         arr1[1] = new STArrStringON(content1);
         content1 = new String[1,2,3];
         arr1[2] = new STArrStringON(content1);
         content1 = new String[1,2,3];
         content1[0,0,1] = "foo";
         content1[0,1,0] = "bar";
         content1[0,1,2] = "fly";
         arr1[3] = new STArrStringON(content1);
         content1 = new String[1,2,3];
         content1[0,0,0] = "bar";
         content1[0,1,0] = "wohay";
         content1[0,1,1] = "johy";
         arr1[4] = new STArrStringON(content1);
         Object[] ret1 = new Object[arr1.Length];
		 System.Array.Copy(arr1, 0, ret1, 0, arr1.Length);
         return ret1;
      }
      
      public void TestDefaultContainsOne() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         String[,,] content1 = new String[1,1,1];
         content1[0,0,0] = "bar";
         q1.Constrain(new STArrStringON(content1));
         st.Expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void TestDefaultContainsTwo() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         String[,,] content1 = new String[2,1,1];
         content1[0,0,0] = "bar";
         content1[1,0,0] = "foo";
         q1.Constrain(new STArrStringON(content1));
         st.Expect(q1, new Object[]{
            r1[3]         });
      }
      
      public void TestDescendOne() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(Class.GetClassForType(typeof(STArrStringON)));
         q1.Descend("strArr").Constrain("bar");
         st.Expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void TestDescendTwo() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(Class.GetClassForType(typeof(STArrStringON)));
         Query qElements1 = q1.Descend("strArr");
         qElements1.Constrain("foo");
         qElements1.Constrain("bar");
         st.Expect(q1, new Object[]{
            r1[3]         });
      }
      
      public void TestDescendOneNot() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(Class.GetClassForType(typeof(STArrStringON)));
         q1.Descend("strArr").Constrain("bar").Not();
         st.Expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2]         });
      }
      
      public void TestDescendTwoNot() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(Class.GetClassForType(typeof(STArrStringON)));
         Query qElements1 = q1.Descend("strArr");
         qElements1.Constrain("foo").Not();
         qElements1.Constrain("bar").Not();
         st.Expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2]         });
      }
   }
}