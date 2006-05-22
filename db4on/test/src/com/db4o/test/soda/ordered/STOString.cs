/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.ordered {

   public class STOString : STClass {
      [Transient] public static SodaTest st;
      internal String foo;
      
      public STOString() : base() {
      }
      
      public STOString(String str) : base() {
         this.foo = str;
      }
      
      public Object[] Store() {
         return new Object[]{
            new STOString(null),
new STOString("bbb"),
new STOString("bbb"),
new STOString("dod"),
new STOString("aaa"),
new STOString("Bbb"),
new STOString("bbq")         };
      }
      
      public void TestAscending() {
         Query q1 = st.Query();
         q1.Constrain(Class.GetClassForType(typeof(STOString)));
         q1.Descend("foo").OrderAscending();
         Object[] r1 = Store();
         st.ExpectOrdered(q1, new Object[]{
            r1[5],
r1[4],
r1[1],
r1[2],
r1[6],
r1[3],
r1[0]         });
      }
      
      public void TestDescending() {
         Query q1 = st.Query();
         q1.Constrain(Class.GetClassForType(typeof(STOString)));
         q1.Descend("foo").OrderDescending();
         Object[] r1 = Store();
         st.ExpectOrdered(q1, new Object[]{
            r1[3],
r1[6],
r1[2],
r1[1],
r1[4],
r1[5],
r1[0]         });
      }
      
      public void TestAscendingLike() {
         Query q1 = st.Query();
         q1.Constrain(Class.GetClassForType(typeof(STOString)));
         Query qStr1 = q1.Descend("foo");
         qStr1.Constrain("b").Like();
         qStr1.OrderAscending();
         Object[] r1 = Store();
         st.ExpectOrdered(q1, new Object[]{
            r1[5],
r1[1],
r1[2],
r1[6]         });
      }

      
      public void TestDescendingContains() {
         Query q1 = st.Query();
         q1.Constrain(Class.GetClassForType(typeof(STOString)));
         Query qStr1 = q1.Descend("foo");
         qStr1.Constrain("b").Contains();
         qStr1.OrderDescending();
         Object[] r1 = Store();
         st.ExpectOrdered(q1, new Object[]{
            r1[6],
r1[2],
r1[1],
r1[5]         });
      }
   }
}