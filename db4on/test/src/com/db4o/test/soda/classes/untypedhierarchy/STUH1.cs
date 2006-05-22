/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
using com.db4o.test.soda.classes.typedhierarchy;
namespace com.db4o.test.soda.classes.untypedhierarchy {

   /**
    * UH: Untyped Hierarchy 
    */
   public class STUH1 : STClass {
      [Transient] public static SodaTest st;
      internal Object h2;
      internal Object foo1;
      
      public STUH1() : base() {
      }
      
      public STUH1(STUH2 a2) : base() {
         h2 = a2;
      }
      
      public STUH1(String str) : base() {
         foo1 = str;
      }
      
      public STUH1(STUH2 a2, String str) : base() {
         h2 = a2;
         foo1 = str;
      }
      
      public Object[] Store() {
         return new Object[]{
            new STUH1(),
new STUH1("str1"),
new STUH1(new STUH2()),
new STUH1(new STUH2("str2")),
new STUH1(new STUH2(new STUH3("str3"))),
new STUH1(new STUH2(new STUH3("str3"), "str2"))         };
      }
      
      public void TestStrNull() {
         Query q1 = st.Query();
         q1.Constrain(new STUH1());
         q1.Descend("foo1").Constrain(null);
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[0],
r1[2],
r1[3],
r1[4],
r1[5]         });
      }
      
      public void TestBothNull() {
         Query q1 = st.Query();
         q1.Constrain(new STUH1());
         q1.Descend("foo1").Constrain(null);
         q1.Descend("h2").Constrain(null);
         st.ExpectOne(q1, Store()[0]);
      }
      
      public void TestDescendantNotNull() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(new STUH1());
         q1.Descend("h2").Constrain(null).Not();
         st.Expect(q1, new Object[]{
            r1[2],
r1[3],
r1[4],
r1[5]         });
      }
      
      public void TestDescendantDescendantNotNull() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(new STUH1());
         q1.Descend("h2").Descend("h3").Constrain(null).Not();
         st.Expect(q1, new Object[]{
            r1[4],
r1[5]         });
      }
      
      public void TestDescendantExists() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(r1[2]);
         st.Expect(q1, new Object[]{
            r1[2],
r1[3],
r1[4],
r1[5]         });
      }
      
      public void TestDescendantValue() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(r1[3]);
         st.Expect(q1, new Object[]{
            r1[3],
r1[5]         });
      }
      
      public void TestDescendantDescendantExists() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(new STUH1(new STUH2(new STUH3())));
         st.Expect(q1, new Object[]{
            r1[4],
r1[5]         });
      }
      
      public void TestDescendantDescendantValue() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(new STUH1(new STUH2(new STUH3("str3"))));
         st.Expect(q1, new Object[]{
            r1[4],
r1[5]         });
      }
      
      public void TestDescendantDescendantStringPath() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(new STUH1());
         q1.Descend("h2").Descend("h3").Descend("foo3").Constrain("str3");
         st.Expect(q1, new Object[]{
            r1[4],
r1[5]         });
      }
      
      public void TestSequentialAddition() {
         Query q1 = st.Query();
         q1.Constrain(new STUH1());
         Object[] r1 = Store();
         Query cur1 = q1.Descend("h2");
         cur1.Constrain(new STUH2());
         cur1.Descend("foo2").Constrain("str2");
         cur1 = cur1.Descend("h3");
         cur1.Constrain(new STUH3());
         cur1.Descend("foo3").Constrain("str3");
         st.ExpectOne(q1, Store()[5]);
      }
      
      public void TestTwoLevelOr() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(new STUH1("str1"));
         q1.Descend("foo1").Constraints().Or(q1.Descend("h2").Descend("h3").Descend("foo3").Constrain("str3"));
         st.Expect(q1, new Object[]{
            r1[1],
r1[4],
r1[5]         });
      }
      
      public void TestThreeLevelOr() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(new STUH1("str1"));
         q1.Descend("foo1").Constraints().Or(q1.Descend("h2").Descend("foo2").Constrain("str2")).Or(q1.Descend("h2").Descend("h3").Descend("foo3").Constrain("str3"));
         st.Expect(q1, new Object[]{
            r1[1],
r1[3],
r1[4],
r1[5]         });
      }
      
      public void TestNonExistentDescendant() {
         Query q1 = st.Query();
         STUH1 constraint1 = new STUH1();
         constraint1.foo1 = new STETH2();
         q1.Constrain(constraint1);
         st.ExpectNone(q1);
      }
   }
}