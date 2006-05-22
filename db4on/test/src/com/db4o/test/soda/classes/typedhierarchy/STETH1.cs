/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.typedhierarchy {

   /**
    * ETH: Extends Typed Hierarchy 
    */
   public class STETH1 : STClass {
      [Transient] public static SodaTest st;
      internal String foo1;
      
      public STETH1() : base() {
      }
      
      public STETH1(String str) : base() {
         foo1 = str;
      }
      
      public Object[] Store() {
         return new Object[]{
            new STETH1(),
new STETH1("str1"),
new STETH2(),
new STETH2("str1", "str2"),
new STETH3(),
new STETH3("str1a", "str2", "str3"),
new STETH3("str1a", "str2a", null),
new STETH4(),
new STETH4("str1a", "str2", "str4"),
new STETH4("str1b", "str2a", "str4")         };
      }
      
      public void TestStrNull() {
         Query q1 = st.Query();
         q1.Constrain(new STETH1());
         q1.Descend("foo1").Constrain(null);
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[0],
r1[2],
r1[4],
r1[7]         });
      }
      
      public void TestTwoNull() {
         Query q1 = st.Query();
         q1.Constrain(new STETH1());
         q1.Descend("foo1").Constrain(null);
         q1.Descend("foo3").Constrain(null);
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[0],
r1[2],
r1[4],
r1[7]         });
      }
      
      public void TestClass() {
         Query q1 = st.Query();
         q1.Constrain(Class.GetClassForType(typeof(STETH2)));
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[2],
r1[3],
r1[4],
r1[5],
r1[6],
r1[7],
r1[8],
r1[9]         });
      }
      
      public void TestOrClass() {
         Query q1 = st.Query();
         q1.Constrain(Class.GetClassForType(typeof(STETH3))).Or(q1.Constrain(Class.GetClassForType(typeof(STETH4))));
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[4],
r1[5],
r1[6],
r1[7],
r1[8],
r1[9]         });
      }
      
      public void TestAndClass() {
         Query q1 = st.Query();
         q1.Constrain(Class.GetClassForType(typeof(STETH1)));
         q1.Constrain(Class.GetClassForType(typeof(STETH4)));
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[7],
r1[8],
r1[9]         });
      }
      
      public void TestParalellDescendantPaths() {
         Query q1 = st.Query();
         q1.Constrain(Class.GetClassForType(typeof(STETH3))).Or(q1.Constrain(Class.GetClassForType(typeof(STETH4))));
         q1.Descend("foo3").Constrain("str3").Or(q1.Descend("foo4").Constrain("str4"));
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[5],
r1[8],
r1[9]         });
      }
      
      public void TestOrObjects() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(r1[3]).Or(q1.Constrain(r1[5]));
         st.Expect(q1, new Object[]{
            r1[3],
r1[5]         });
      }
   }
}