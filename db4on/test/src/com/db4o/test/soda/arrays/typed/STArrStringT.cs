/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.arrays.typed {

   public class STArrStringT : STClass {
      [Transient] public static SodaTest st;
      internal String[] strArr;
      
      public STArrStringT() : base() {
      }
      
      public STArrStringT(String[] arr) : base() {
         strArr = arr;
      }
      
      public Object[] store() {
         return new Object[]{
            new STArrStringT(),
new STArrStringT(new String[]{
               null            }),
new STArrStringT(new String[]{
               null,
null            }),
new STArrStringT(new String[]{
               "foo",
"bar",
"fly"            }),
new STArrStringT(new String[]{
               null,
"bar",
"wohay",
"johy"            })         };
      }
      
      public void testDefaultContainsOne() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STArrStringT(new String[]{
            "bar"         }));
         st.expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void testDefaultContainsTwo() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STArrStringT(new String[]{
            "foo",
"bar"         }));
         st.expect(q1, new Object[]{
            r1[3]         });
      }
      
      public void testDescendOne() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrStringT)));
         q1.descend("strArr").constrain("bar");
         st.expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void testDescendTwo() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrStringT)));
         Query qElements1 = q1.descend("strArr");
         qElements1.constrain("foo");
         qElements1.constrain("bar");
         st.expect(q1, new Object[]{
            r1[3]         });
      }
      
      public void testDescendOneNot() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrStringT)));
         q1.descend("strArr").constrain("bar").not();
         st.expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2]         });
      }
      
      public void testDescendTwoNot() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrStringT)));
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