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
      
      public Object[] store() {
         return new Object[]{
            new STOString(null),
new STOString("bbb"),
new STOString("bbb"),
new STOString("dod"),
new STOString("aaa"),
new STOString("Bbb"),
new STOString("bbq")         };
      }
      
      public void testAscending() {
         Query q1 = st.query();
         q1.constrain(Class.getClassForType(typeof(STOString)));
         q1.descend("foo").orderAscending();
         Object[] r1 = store();
         st.expectOrdered(q1, new Object[]{
            r1[5],
r1[4],
r1[1],
r1[2],
r1[6],
r1[3],
r1[0]         });
      }
      
      public void testDescending() {
         Query q1 = st.query();
         q1.constrain(Class.getClassForType(typeof(STOString)));
         q1.descend("foo").orderDescending();
         Object[] r1 = store();
         st.expectOrdered(q1, new Object[]{
            r1[3],
r1[6],
r1[2],
r1[1],
r1[4],
r1[5],
r1[0]         });
      }
      
      public void testAscendingLike() {
         Query q1 = st.query();
         q1.constrain(Class.getClassForType(typeof(STOString)));
         Query qStr1 = q1.descend("foo");
         qStr1.constrain("b").like();
         qStr1.orderAscending();
         Object[] r1 = store();
         st.expectOrdered(q1, new Object[]{
            r1[5],
r1[1],
r1[2],
r1[6]         });
      }

      
      public void testDescendingContains() {
         Query q1 = st.query();
         q1.constrain(Class.getClassForType(typeof(STOString)));
         Query qStr1 = q1.descend("foo");
         qStr1.constrain("b").contains();
         qStr1.orderDescending();
         Object[] r1 = store();
         st.expectOrdered(q1, new Object[]{
            r1[6],
r1[2],
r1[1],
r1[5]         });
      }
   }
}