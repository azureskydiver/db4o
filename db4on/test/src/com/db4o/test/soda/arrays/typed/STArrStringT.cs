﻿/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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
      
      public Object[] Store() {
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
      
      public void TestDefaultContainsOne() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(new STArrStringT(new String[]{
            "bar"         }));
         st.Expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void TestDefaultContainsTwo() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(new STArrStringT(new String[]{
            "foo",
"bar"         }));
         st.Expect(q1, new Object[]{
            r1[3]         });
      }
      
      public void TestDescendOne() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(Class.GetClassForType(typeof(STArrStringT)));
         q1.Descend("strArr").Constrain("bar");
         st.Expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void TestDescendTwo() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(Class.GetClassForType(typeof(STArrStringT)));
         Query qElements1 = q1.Descend("strArr");
         qElements1.Constrain("foo");
         qElements1.Constrain("bar");
         st.Expect(q1, new Object[]{
            r1[3]         });
      }
      
      public void TestDescendOneNot() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(Class.GetClassForType(typeof(STArrStringT)));
         q1.Descend("strArr").Constrain("bar").Not();
         st.Expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2]         });
      }
      
      public void TestDescendTwoNot() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(Class.GetClassForType(typeof(STArrStringT)));
         Query qElements1 = q1.Descend("strArr");
         qElements1.Constrain("foo").Not();
         qElements1.Constrain("bar").Not();
         st.Expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2]         });
      }
   }
}