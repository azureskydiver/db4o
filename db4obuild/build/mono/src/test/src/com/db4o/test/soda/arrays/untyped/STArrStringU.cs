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
namespace com.db4o.test.soda.arrays.untyped {

   public class STArrStringU : STClass {
      [Transient] public static SodaTest st;
      internal Object[] strArr;
      
      public STArrStringU() : base() {
      }
      
      public STArrStringU(Object[] arr) : base() {
         strArr = arr;
      }
      
      public Object[] store() {
         return new Object[]{
            new STArrStringU(),
new STArrStringU(new Object[]{
               null            }),
new STArrStringU(new Object[]{
               null,
null            }),
new STArrStringU(new Object[]{
               "foo",
"bar",
"fly"            }),
new STArrStringU(new Object[]{
               null,
"bar",
"wohay",
"johy"            })         };
      }
      
      public void testDefaultContainsOne() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STArrStringU(new Object[]{
            "bar"         }));
         st.expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void testDefaultContainsTwo() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STArrStringU(new Object[]{
            "foo",
"bar"         }));
         st.expect(q1, new Object[]{
            r1[3]         });
      }
      
      public void testDescendOne() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrStringU)));
         q1.descend("strArr").constrain("bar");
         st.expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void testDescendTwo() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrStringU)));
         Query qElements1 = q1.descend("strArr");
         qElements1.constrain("foo");
         qElements1.constrain("bar");
         st.expect(q1, new Object[]{
            r1[3]         });
      }
      
      public void testDescendOneNot() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrStringU)));
         q1.descend("strArr").constrain("bar").not();
         st.expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2]         });
      }
      
      public void testDescendTwoNot() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrStringU)));
         Query qElements1 = q1.descend("strArr");
         qElements1.constrain("foo").not();
         qElements1.constrain("bar").not();
         st.expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2]         });
      }
   }
}