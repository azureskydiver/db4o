/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.arrays.typed {

   public class STArrIntegerWT : STClass {
      [Transient] public static SodaTest st;
      internal Int32[] intArr;
      
      public STArrIntegerWT() : base() {
      }
      
      public STArrIntegerWT(Int32[] arr) : base() {
         intArr = arr;
      }
      
      public Object[] Store() {
         return new Object[]{
            new STArrIntegerWT(),
new STArrIntegerWT(new Int32[0]),
new STArrIntegerWT(new Int32[]{
               System.Convert.ToInt32(0),
System.Convert.ToInt32(0)            }),
new STArrIntegerWT(new Int32[]{
               System.Convert.ToInt32(1),
System.Convert.ToInt32(17),
System.Convert.ToInt32(Int32.MaxValue - 1)            }),
new STArrIntegerWT(new Int32[]{
               System.Convert.ToInt32(3),
System.Convert.ToInt32(17),
System.Convert.ToInt32(25),
System.Convert.ToInt32(Int32.MaxValue - 2)            })         };
      }
      
      public void TestDefaultContainsOne() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(new STArrIntegerWT(new Int32[]{
            System.Convert.ToInt32(17)         }));
         st.Expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void TestDefaultContainsTwo() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         q1.Constrain(new STArrIntegerWT(new Int32[]{
            System.Convert.ToInt32(17),
System.Convert.ToInt32(25)         }));
         st.Expect(q1, new Object[]{
            r1[4]         });
      }
   }
}