/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.arrays.untyped {

   public class STArrMixedN : STClass {
      [Transient] public static SodaTest st;
      internal Object[,,] arr;
      
      public STArrMixedN() : base() {
      }
      
      public STArrMixedN(Object[,,] arr) : base() {
         this.arr = arr;
      }
      
      public Object[] store() {
         STArrMixedN[] arr1 = new STArrMixedN[5];
         arr1[0] = new STArrMixedN();
         object[,,] content1 = new object[1,1,2];
         arr1[1] = new STArrMixedN(content1);
         content1 = new object[2,2,3];
         arr1[2] = new STArrMixedN(content1);
         content1 = new object[2,2,3];
         content1[0,0,1] = "foo";
         content1[0,1,0] = "bar";
         content1[0,1,2] = "fly";
         content1[1,0,0] = System.Convert.ToBoolean(false);
         arr1[3] = new STArrMixedN(content1);
         content1 = new object[2,2,3];
         content1[0,0,0] = "bar";
         content1[0,1,0] = "wohay";
         content1[0,1,1] = "johy";
         content1[1,0,0] = System.Convert.ToInt32(12);
         arr1[4] = new STArrMixedN(content1);
         Object[] ret1 = new Object[arr1.Length];
		 System.Array.Copy(arr1, 0, ret1, 0, arr1.Length);
         return ret1;
      }
      
      public void testDefaultContainsString() {
         Query q1 = st.query();
         Object[] r1 = store();
         Object[,,] content1 = new Object[1,1,1];
         content1[0,0,0] = "bar";
         q1.constrain(new STArrMixedN(content1));
         st.expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void testDefaultContainsInteger() {
         Query q1 = st.query();
         Object[] r1 = store();
         object[,,] content1 = new object[1,1,1];
         content1[0,0,0] = System.Convert.ToInt32(12);
         q1.constrain(new STArrMixedN(content1));
         st.expect(q1, new Object[]{
            r1[4]         });
      }
      
      public void testDefaultContainsBoolean() {
         Query q1 = st.query();
         Object[] r1 = store();
         Object[,,] content1 = new Object[1,1,1];
         content1[0,0,0] = System.Convert.ToBoolean(false);
         q1.constrain(new STArrMixedN(content1));
         st.expect(q1, new Object[]{
            r1[3]         });
      }
      
      public void testDefaultContainsTwo() {
         Query q1 = st.query();
         Object[] r1 = store();
         object[,,] content1 = new object[2,1,1];
         content1[0,0,0] = "bar";
         content1[1,0,0] = System.Convert.ToInt32(12);
         q1.constrain(new STArrMixedN(content1));
         st.expect(q1, new Object[]{
            r1[4]         });
      }
      
      public void testDescendOne() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrMixedN)));
         q1.descend("arr").constrain("bar");
         st.expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void testDescendTwo() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrMixedN)));
         Query qElements1 = q1.descend("arr");
         qElements1.constrain("foo");
         qElements1.constrain("bar");
         st.expect(q1, new Object[]{
            r1[3]         });
      }
      
      public void testDescendOneNot() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrMixedN)));
         q1.descend("arr").constrain("bar").not();
         st.expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2]         });
      }
      
      public void testDescendTwoNot() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrMixedN)));
         Query qElements1 = q1.descend("arr");
         qElements1.constrain("foo").not();
         qElements1.constrain("bar").not();
         st.expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2]         });
      }
   }
}