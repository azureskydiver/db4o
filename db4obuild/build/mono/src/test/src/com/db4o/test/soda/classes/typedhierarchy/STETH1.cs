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
      
      public Object[] store() {
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
      
      public void testStrNull() {
         Query q1 = st.query();
         q1.constrain(new STETH1());
         q1.descend("foo1").constrain(null);
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[2],
r1[4],
r1[7]         });
      }
      
      public void testTwoNull() {
         Query q1 = st.query();
         q1.constrain(new STETH1());
         q1.descend("foo1").constrain(null);
         q1.descend("foo3").constrain(null);
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[2],
r1[4],
r1[7]         });
      }
      
      public void testClass() {
         Query q1 = st.query();
         q1.constrain(Class.getClassForType(typeof(STETH2)));
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[2],
r1[3],
r1[4],
r1[5],
r1[6],
r1[7],
r1[8],
r1[9]         });
      }
      
      public void testOrClass() {
         Query q1 = st.query();
         q1.constrain(Class.getClassForType(typeof(STETH3))).or(q1.constrain(Class.getClassForType(typeof(STETH4))));
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[4],
r1[5],
r1[6],
r1[7],
r1[8],
r1[9]         });
      }
      
      public void testAndClass() {
         Query q1 = st.query();
         q1.constrain(Class.getClassForType(typeof(STETH1)));
         q1.constrain(Class.getClassForType(typeof(STETH4)));
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[7],
r1[8],
r1[9]         });
      }
      
      public void testParalellDescendantPaths() {
         Query q1 = st.query();
         q1.constrain(Class.getClassForType(typeof(STETH3))).or(q1.constrain(Class.getClassForType(typeof(STETH4))));
         q1.descend("foo3").constrain("str3").or(q1.descend("foo4").constrain("str4"));
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[5],
r1[8],
r1[9]         });
      }
      
      public void testOrObjects() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(r1[3]).or(q1.constrain(r1[5]));
         st.expect(q1, new Object[]{
            r1[3],
r1[5]         });
      }
   }
}