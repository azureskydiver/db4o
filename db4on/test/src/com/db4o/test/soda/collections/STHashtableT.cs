/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.util;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.collections {

    public class STHashtableT : STClass {
        internal System.Collections.Hashtable col;
        [Transient] public static SodaTest st;
      
        public STHashtableT() : base() {
        }
      
        public STHashtableT(Object[] arr) : base() {
            col = new System.Collections.Hashtable();
            for (int i1 = 0; i1 < arr.Length; i1++) {
                col.Add(arr[i1], System.Convert.ToInt32(i1));
            }
        }
      
        public Object[] Store() {
            return new Object[]{
                                   new STHashtableT(),
                                   new STHashtableT(new Object[0]),
                                   new STHashtableT(new Object[]{
               
                                                                    System.Convert.ToInt32(0)            }),
                                   new STHashtableT(new Object[]{
                                                                    System.Convert.ToInt32(1),
                                                                    System.Convert.ToInt32(17),
                                                                    System.Convert.ToInt32(Int32.MaxValue - 1)            }),
                                   new STHashtableT(new Object[]{
                                                                    System.Convert.ToInt32(3),
                                                                    System.Convert.ToInt32(17),
                                                                    System.Convert.ToInt32(25),
                                                                    System.Convert.ToInt32(Int32.MaxValue - 2)            }),
                                   new STHashtableT(new Object[]{
                                                                    "foo",
                                                                    new STElement("bar", "barbar")            }),
                                   new STHashtableT(new Object[]{
                                                                    "foo2",
                                                                    new STElement("bar", "barbar2")            })         };
        }
      
        public void TestDefaultContainsInteger() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(new STHashtableT(new Object[]{
                                                          System.Convert.ToInt32(17)         }));
            st.Expect(q1, new Object[]{
                                          r1[3],
                                          r1[4]         });
        }
      
        public void TestDefaultContainsString() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(new STHashtableT(new Object[]{
                                                          "foo"         }));
            st.Expect(q1, new Object[]{
                                          r1[5]         });
        }
      
        public void TestDefaultContainsTwo() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(new STHashtableT(new Object[]{
                                                          System.Convert.ToInt32(17),
                                                          System.Convert.ToInt32(25)         }));
            st.Expect(q1, new Object[]{
                                          r1[4]         });
        }
      
        public void TestDescendOne() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(Class.GetClassForType(typeof(STHashtableT)));
            q1.Descend("col").Constrain(System.Convert.ToInt32(17));
            st.Expect(q1, new Object[]{
                                          r1[3],
                                          r1[4]         });
        }
      
        public void TestDescendTwo() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(Class.GetClassForType(typeof(STHashtableT)));
            Query qElements1 = q1.Descend("col");
            qElements1.Constrain(System.Convert.ToInt32(17));
            qElements1.Constrain(System.Convert.ToInt32(25));
            st.Expect(q1, new Object[]{
                                          r1[4]         });
        }
      
        public void TestDescendSmaller() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(Class.GetClassForType(typeof(STHashtableT)));
            Query qElements1 = q1.Descend("col");
            qElements1.Constrain(System.Convert.ToInt32(3)).Smaller();
            st.Expect(q1, new Object[]{
                                          r1[2],
                                          r1[3]         });
        }
      
        public void TestDefaultContainsObject() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(new STHashtableT(new Object[]{
                                                          new STElement("bar", null)         }));
            st.Expect(q1, new Object[]{
                                          r1[5],
                                          r1[6]         });
        }
      
        public void TestDescendToObject() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(new STHashtableT());
            q1.Descend("col").Descend("foo1").Constrain("bar");
            st.Expect(q1, new Object[]{
                                          r1[5],
                                          r1[6]         });
        }
    }
}