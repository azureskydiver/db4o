/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

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
      
      public Object[] store() {
         return new Object[]{
            new STUH1(),
new STUH1("str1"),
new STUH1(new STUH2()),
new STUH1(new STUH2("str2")),
new STUH1(new STUH2(new STUH3("str3"))),
new STUH1(new STUH2(new STUH3("str3"), "str2"))         };
      }
      
      public void testStrNull() {
         Query q1 = st.query();
         q1.constrain(new STUH1());
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
         q1.constrain(new STUH1());
         q1.descend("foo1").constrain(null);
         q1.descend("h2").constrain(null);
         st.expectOne(q1, store()[0]);
      }
      
      public void testDescendantNotNull() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STUH1());
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
         q1.constrain(new STUH1());
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
         q1.constrain(new STUH1(new STUH2(new STUH3())));
         st.expect(q1, new Object[]{
            r1[4],
r1[5]         });
      }
      
      public void testDescendantDescendantValue() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STUH1(new STUH2(new STUH3("str3"))));
         st.expect(q1, new Object[]{
            r1[4],
r1[5]         });
      }
      
      public void testDescendantDescendantStringPath() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STUH1());
         q1.descend("h2").descend("h3").descend("foo3").constrain("str3");
         st.expect(q1, new Object[]{
            r1[4],
r1[5]         });
      }
      
      public void testSequentialAddition() {
         Query q1 = st.query();
         q1.constrain(new STUH1());
         Object[] r1 = store();
         Query cur1 = q1.descend("h2");
         cur1.constrain(new STUH2());
         cur1.descend("foo2").constrain("str2");
         cur1 = cur1.descend("h3");
         cur1.constrain(new STUH3());
         cur1.descend("foo3").constrain("str3");
         st.expectOne(q1, store()[5]);
      }
      
      public void testTwoLevelOr() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STUH1("str1"));
         q1.descend("foo1").constraints().or(q1.descend("h2").descend("h3").descend("foo3").constrain("str3"));
         st.expect(q1, new Object[]{
            r1[1],
r1[4],
r1[5]         });
      }
      
      public void testThreeLevelOr() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STUH1("str1"));
         q1.descend("foo1").constraints().or(q1.descend("h2").descend("foo2").constrain("str2")).or(q1.descend("h2").descend("h3").descend("foo3").constrain("str3"));
         st.expect(q1, new Object[]{
            r1[1],
r1[3],
r1[4],
r1[5]         });
      }
      
      public void testNonExistentDescendant() {
         Query q1 = st.query();
         STUH1 constraint1 = new STUH1();
         constraint1.foo1 = new STETH2();
         q1.constrain(constraint1);
         st.expectNone(q1);
      }
   }
}