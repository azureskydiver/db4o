/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.typedhierarchy {

   /**
    * TH: Typed Hierarchy 
    */
   public class STTH1 : STClass1 {
      [Transient] public static SodaTest st;
      public STTH2 h2;
      public String foo1;
      
      public STTH1() : base() {
      }
      
      public STTH1(STTH2 a2) : base() {
         h2 = a2;
      }
      
      public STTH1(String str) : base() {
         foo1 = str;
      }
      
      public STTH1(STTH2 a2, String str) : base() {
         h2 = a2;
         foo1 = str;
      }
      
      public Object[] store() {
         return new Object[]{
            new STTH1(),
new STTH1("str1"),
new STTH1(new STTH2()),
new STTH1(new STTH2("str2")),
new STTH1(new STTH2(new STTH3("str3"))),
new STTH1(new STTH2(new STTH3("str3"), "str2"))         };
      }
      
      public void testStrNull() {
         Query q1 = st.query();
         q1.constrain(new STTH1());
         q1.descend("foo1").constrain(null);
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[2],
r1[3],
r1[4],
r1[5]         });
      }
      
      public void testBothNull() {
         Query q1 = st.query();
         q1.constrain(new STTH1());
         q1.descend("foo1").constrain(null);
         q1.descend("h2").constrain(null);
         st.expectOne(q1, store()[0]);
      }
      
      public void testDescendantNotNull() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STTH1());
         q1.descend("h2").constrain(null).not();
         st.expect(q1, new Object[]{
            r1[2],
r1[3],
r1[4],
r1[5]         });
      }
      
      public void testDescendantDescendantNotNull() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STTH1());
         q1.descend("h2").descend("h3").constrain(null).not();
         st.expect(q1, new Object[]{
            r1[4],
r1[5]         });
      }
      
      public void testDescendantExists() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(r1[2]);
         st.expect(q1, new Object[]{
            r1[2],
r1[3],
r1[4],
r1[5]         });
      }
      
      public void testDescendantValue() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(r1[3]);
         st.expect(q1, new Object[]{
            r1[3],
r1[5]         });
      }
      
      public void testDescendantDescendantExists() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STTH1(new STTH2(new STTH3())));
         st.expect(q1, new Object[]{
            r1[4],
r1[5]         });
      }
      
      public void testDescendantDescendantValue() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STTH1(new STTH2(new STTH3("str3"))));
         st.expect(q1, new Object[]{
            r1[4],
r1[5]         });
      }
      
      public void testDescendantDescendantStringPath() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STTH1());
         q1.descend("h2").descend("h3").descend("foo3").constrain("str3");
         st.expect(q1, new Object[]{
            r1[4],
r1[5]         });
      }
      
      public void testSequentialAddition() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STTH1());
         Query cur1 = q1.descend("h2");
         cur1.constrain(new STTH2());
         cur1.descend("foo2").constrain("str2");
         cur1 = cur1.descend("h3");
         cur1.constrain(new STTH3());
         cur1.descend("foo3").constrain("str3");
         st.expectOne(q1, store()[5]);
      }
      
      public void testTwoLevelOr() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STTH1("str1"));
         q1.descend("foo1").constraints().or(q1.descend("h2").descend("h3").descend("foo3").constrain("str3"));
         st.expect(q1, new Object[]{
            r1[1],
r1[4],
r1[5]         });
      }
      
      public void testThreeLevelOr() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STTH1("str1"));
         q1.descend("foo1").constraints().or(q1.descend("h2").descend("foo2").constrain("str2")).or(q1.descend("h2").descend("h3").descend("foo3").constrain("str3"));
         st.expect(q1, new Object[]{
            r1[1],
r1[3],
r1[4],
r1[5]         });
      }
   }
}