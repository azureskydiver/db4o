/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.arrays.untyped {

   public class STArrIntegerU : STClass {
      [Transient] public static SodaTest st;
      internal Object[] intArr;
      
      public STArrIntegerU() : base() {
      }
      
      public STArrIntegerU(Object[] arr) : base() {
         intArr = arr;
      }
      
      public Object[] store() {
         return new Object[]{
            new STArrIntegerU(),
new STArrIntegerU(new Object[0]),
new STArrIntegerU(new Object[]{
               System.Convert.ToInt32(0),
System.Convert.ToInt32(0)            }),
new STArrIntegerU(new Object[]{
               System.Convert.ToInt32(1),
System.Convert.ToInt32(17),
System.Convert.ToInt32(Int32.MaxValue - 1)            }),
new STArrIntegerU(new Object[]{
               System.Convert.ToInt32(3),
System.Convert.ToInt32(17),
System.Convert.ToInt32(25),
System.Convert.ToInt32(Int32.MaxValue - 2)            })         };
      }
      
      public void testDefaultContainsOne() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STArrIntegerU(new Object[]{
            System.Convert.ToInt32(17)         }));
         st.expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void testDefaultContainsTwo() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(new STArrIntegerU(new Object[]{
            System.Convert.ToInt32(17),
System.Convert.ToInt32(25)         }));
         st.expect(q1, new Object[]{
            r1[4]         });
      }
      
      public void testDescendOne() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrIntegerU)));
         q1.descend("intArr").constrain(System.Convert.ToInt32(17));
         st.expect(q1, new Object[]{
            r1[3],
r1[4]         });
      }
      
      public void testDescendTwo() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrIntegerU)));
         Query qElements1 = q1.descend("intArr");
         qElements1.constrain(System.Convert.ToInt32(17));
         qElements1.constrain(System.Convert.ToInt32(25));
         st.expect(q1, new Object[]{
            r1[4]         });
      }
      
      public void testDescendSmaller() {
         Query q1 = st.query();
         Object[] r1 = store();
         q1.constrain(Class.getClassForType(typeof(STArrIntegerU)));
         Query qElements1 = q1.descend("intArr");
         qElements1.constrain(System.Convert.ToInt32(3)).smaller();
         st.expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
   }
}