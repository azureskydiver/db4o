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
namespace com.db4o.test.soda.joins.typed {

   public class STOrT : STClass {
      [Transient] public static SodaTest st;
      internal int orInt;
      internal String orString;
      
      public STOrT() : base() {
      }
      
      internal STOrT(int a_int, String a_string) : base() {
         orInt = a_int;
         orString = a_string;
      }
      
      public override String ToString() {
         return "STOr: int:" + orInt + " str:" + orString;
      }
      
      public Object[] store() {
         return new Object[]{
            new STOrT(0, "hi"),
new STOrT(5, null),
new STOrT(1000, "joho"),
new STOrT(30000, "osoo"),
new STOrT(Int32.MaxValue - 1, null)         };
      }
      
      public void testSmallerGreater() {
         Query q1 = st.query();
         q1.constrain(new STOrT());
         Query sub1 = q1.descend("orInt");
         sub1.constrain(System.Convert.ToInt32(30000)).greater().or(sub1.constrain(System.Convert.ToInt32(5)).smaller());
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[4]         });
      }
      
      public void testGreaterGreater() {
         Query q1 = st.query();
         q1.constrain(new STOrT());
         Query sub1 = q1.descend("orInt");
         sub1.constrain(System.Convert.ToInt32(30000)).greater().or(sub1.constrain(System.Convert.ToInt32(5)).greater());
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[2],
r1[3],
r1[4]         });
      }
      
      public void testGreaterEquals() {
         Query q1 = st.query();
         q1.constrain(new STOrT());
         Query sub1 = q1.descend("orInt");
         sub1.constrain(System.Convert.ToInt32(1000)).greater().or(sub1.constrain(System.Convert.ToInt32(0)));
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[3],
r1[4]         });
      }
      
      public void testEqualsNull() {
         Query q1 = st.query();
         q1.constrain(new STOrT(1000, null));
         q1.descend("orInt").constraints().or(q1.descend("orString").constrain(null));
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[1],
r1[2],
r1[4]         });
      }
      
      public void testAndOrAnd() {
         Query q1 = st.query();
         q1.constrain(new STOrT(0, null));
         q1.descend("orInt").constrain(System.Convert.ToInt32(5)).and(q1.descend("orString").constrain(null)).or(q1.descend("orInt").constrain(System.Convert.ToInt32(1000)).and(q1.descend("orString").constrain("joho")));
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[1],
r1[2]         });
      }
      
      public void testOrAndOr() {
         Query q1 = st.query();
         q1.constrain(new STOrT(0, null));
         q1.descend("orInt").constrain(System.Convert.ToInt32(5)).or(q1.descend("orString").constrain(null)).and(q1.descend("orInt").constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).or(q1.descend("orString").constrain("joho")));
         st.expectOne(q1, store()[4]);
      }
      
      public void testOrOrAnd() {
         Query q1 = st.query();
         q1.constrain(new STOrT(0, null));
         q1.descend("orInt").constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).or(q1.descend("orString").constrain("joho")).or(q1.descend("orInt").constrain(System.Convert.ToInt32(5)).and(q1.descend("orString").constrain(null)));
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[1],
r1[2],
r1[4]         });
      }
      
      public void testMultiOrAnd() {
         Query q1 = st.query();
         q1.constrain(new STOrT(0, null));
         q1.descend("orInt").constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).or(q1.descend("orString").constrain("joho")).or(q1.descend("orInt").constrain(System.Convert.ToInt32(5)).and(q1.descend("orString").constrain("joho"))).or(q1.descend("orInt").constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).or(q1.descend("orString").constrain(null)).and(q1.descend("orInt").constrain(System.Convert.ToInt32(5)).or(q1.descend("orString").constrain(null))));
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[1],
r1[2],
r1[4]         });
      }
      
      public void testNotSmallerGreater() {
         Query q1 = st.query();
         q1.constrain(new STOrT());
         Query sub1 = q1.descend("orInt");
         sub1.constrain(System.Convert.ToInt32(30000)).greater().or(sub1.constrain(System.Convert.ToInt32(5)).smaller()).not();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[1],
r1[2],
r1[3]         });
      }
      
      public void testNotGreaterGreater() {
         Query q1 = st.query();
         q1.constrain(new STOrT());
         Query sub1 = q1.descend("orInt");
         sub1.constrain(System.Convert.ToInt32(30000)).greater().or(sub1.constrain(System.Convert.ToInt32(5)).greater()).not();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[1]         });
      }
      
      public void testNotGreaterEquals() {
         Query q1 = st.query();
         q1.constrain(new STOrT());
         Query sub1 = q1.descend("orInt");
         sub1.constrain(System.Convert.ToInt32(1000)).greater().or(sub1.constrain(System.Convert.ToInt32(0))).not();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[1],
r1[2]         });
      }
      
      public void testNotEqualsNull() {
         Query q1 = st.query();
         q1.constrain(new STOrT(1000, null));
         q1.descend("orInt").constraints().or(q1.descend("orString").constrain(null)).not();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[3]         });
      }
      
      public void testNotAndOrAnd() {
         Query q1 = st.query();
         q1.constrain(new STOrT(0, null));
         q1.descend("orInt").constrain(System.Convert.ToInt32(5)).and(q1.descend("orString").constrain(null)).or(q1.descend("orInt").constrain(System.Convert.ToInt32(1000)).and(q1.descend("orString").constrain("joho"))).not();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[3],
r1[4]         });
      }
      
      public void testNotOrAndOr() {
         Query q1 = st.query();
         q1.constrain(new STOrT(0, null));
         q1.descend("orInt").constrain(System.Convert.ToInt32(5)).or(q1.descend("orString").constrain(null)).and(q1.descend("orInt").constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).or(q1.descend("orString").constrain("joho"))).not();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2],
r1[3]         });
      }
      
      public void testNotOrOrAnd() {
         Query q1 = st.query();
         q1.constrain(new STOrT(0, null));
         q1.descend("orInt").constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).or(q1.descend("orString").constrain("joho")).or(q1.descend("orInt").constrain(System.Convert.ToInt32(5)).and(q1.descend("orString").constrain(null))).not();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[3]         });
      }
      
      public void testNotMultiOrAnd() {
         Query q1 = st.query();
         q1.constrain(new STOrT(0, null));
         q1.descend("orInt").constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).or(q1.descend("orString").constrain("joho")).or(q1.descend("orInt").constrain(System.Convert.ToInt32(5)).and(q1.descend("orString").constrain("joho"))).or(q1.descend("orInt").constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).or(q1.descend("orString").constrain(null)).and(q1.descend("orInt").constrain(System.Convert.ToInt32(5)).or(q1.descend("orString").constrain(null)))).not();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[3]         });
      }
      
      public void testOrNotAndOr() {
         Query q1 = st.query();
         q1.constrain(new STOrT(0, null));
         q1.descend("orInt").constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).or(q1.descend("orString").constrain("joho")).not().and(q1.descend("orInt").constrain(System.Convert.ToInt32(5)).or(q1.descend("orString").constrain(null)));
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[1]         });
      }
      
      public void testAndNotAndAnd() {
         Query q1 = st.query();
         q1.constrain(new STOrT(0, null));
         q1.descend("orInt").constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).and(q1.descend("orString").constrain(null)).not().and(q1.descend("orInt").constrain(System.Convert.ToInt32(5)).or(q1.descend("orString").constrain("osoo")));
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[1],
r1[3]         });
      }
   }
}