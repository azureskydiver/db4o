/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.arrays.obj {

   public class STArrStringO : STClass {
      [Transient] public static SodaTest st;
      internal Object strArr;
      
      public STArrStringO() : base() {
      }
      
      public STArrStringO(Object[] arr) : base() {
         strArr = arr;
      }
      
      public Object[] Store() {
         return new Object[]{
            new STArrStringO(),
new STArrStringO(new Object[]{
               null            }),
new STArrStringO(new Object[]{
               null,
null            }),
new STArrStringO(new Object[]{
               "foo",
"bar",
"fly"            }),
new STArrStringO(new Object[]{
               null,
"bar",
"wohay",
"johy"            })         };
      }
      
      public void TestDefaultContainsOne() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(new STArrStringO(new Object[]{
            "bar"         }));
         st.Expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void TestDefaultContainsTwo() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(new STArrStringO(new Object[]{
            "foo",
"bar"         }));
         st.Expect(q1, new Object[]{
            r1[3]         });
      }
      
      public void TestDescendOne() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(Class.GetClassForType(typeof(STArrStringO)));
         q1.Descend("strArr").Constrain("bar");
         st.Expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void TestDescendTwo() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(Class.GetClassForType(typeof(STArrStringO)));
         Query qElements1 = q1.Descend("strArr");
         qElements1.Constrain("foo");
         qElements1.Constrain("bar");
         st.Expect(q1, new Object[]{
            r1[3]         });
      }
      
      public void TestDescendOneNot() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(Class.GetClassForType(typeof(STArrStringO)));
         q1.Descend("strArr").Constrain("bar").Not();
         st.Expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2]         });
      }
      
      public void TestDescendTwoNot() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(Class.GetClassForType(typeof(STArrStringO)));
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