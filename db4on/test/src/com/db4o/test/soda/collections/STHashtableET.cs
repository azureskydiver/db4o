/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using System.Collections;
using j4o.lang;
using j4o.util;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;


namespace com.db4o.test.soda.collections {

    public class STHashtableET : STClass {
      
        public class ExtendHashtable : System.Collections.Hashtable {
         
            public ExtendHashtable() : base() {
            }
        }
        [Transient] public static SodaTest st;
        internal ExtendHashtable col;
      
        public STHashtableET() : base() {
        }
      
        public STHashtableET(Object[] arr) : base() {
            col = new ExtendHashtable();
            for (int i1 = 0; i1 < arr.Length; i1++) {
                col.Add(arr[i1], i1);
            }
        }
      
        public Object[] Store() {
            return new Object[]{
                                   new STHashtableET(),
                                   new STHashtableET(new Object[0]),
                                   new STHashtableET(new Object[]{
                                                                     0
                                                                 }),
                                   new STHashtableET(new Object[]{
                                                                     1,
                                                                     17,
                                                                     Int32.MaxValue - 1  }),
                                   new STHashtableET(new Object[]{
                                                                     3,
                                                                     17,
                                                                     25,
                                                                     Int32.MaxValue - 2  }),
                                   new STHashtableET(new Object[]{
                                                                     "foo",
                                                                     new STElement("bar", "barbar")            }),
                                   new STHashtableET(new Object[]{
                                                                     "foo2",
                                                                     new STElement("bar", "barbar2")            })         };
        }
      
        public void TestDefaultContainsInteger() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(new STHashtableET(new Object[]{17}));
            st.Expect(q1, new Object[]{
                                          r1[3],
                                          r1[4]         });
        }
      
//        public void TestDefaultContainsString() {
//            Query q1 = st.Query();
//            Object[] r1 = Store();
//            q1.Constrain(new STHashtableET(new Object[]{
//                                                           "foo"         }));
//            st.Expect(q1, new Object[]{
//                                          r1[5]         });
//        }
//      
//        public void TestDefaultContainsTwo() {
//            Query q1 = st.Query();
//            Object[] r1 = Store();
//            q1.Constrain(new STHashtableET(new Object[]{
//                                                           System.Convert.ToInt32(17),
//                                                           System.Convert.ToInt32(25)         }));
//            st.Expect(q1, new Object[]{
//                                          r1[4]         });
//        }
//      
//        public void TestDescendOne() {
//            Query q1 = st.Query();
//            Object[] r1 = Store();
//            q1.Constrain(Class.GetClassForType(typeof(STHashtableET)));
//            q1.Descend("col").Constrain(System.Convert.ToInt32(17));
//            st.Expect(q1, new Object[]{
//                                          r1[3],
//                                          r1[4]         });
//        }
//      
//        public void TestDescendTwo() {
//            Query q1 = st.Query();
//            Object[] r1 = Store();
//            q1.Constrain(Class.GetClassForType(typeof(STHashtableET)));
//            Query qElements1 = q1.Descend("col");
//            qElements1.Constrain(System.Convert.ToInt32(17));
//            qElements1.Constrain(System.Convert.ToInt32(25));
//            st.Expect(q1, new Object[]{
//                                          r1[4]         });
//        }
//      
//        public void TestDescendSmaller() {
//            Query q1 = st.Query();
//            Object[] r1 = Store();
//            q1.Constrain(Class.GetClassForType(typeof(STHashtableET)));
//            Query qElements1 = q1.Descend("col");
//            qElements1.Constrain(System.Convert.ToInt32(3)).Smaller();
//            st.Expect(q1, new Object[]{
//                                          r1[2],
//                                          r1[3]         });
//        }
//      
//        public void TestDefaultContainsObject() {
//            Query q1 = st.Query();
//            Object[] r1 = Store();
//            q1.Constrain(new STHashtableET(new Object[]{
//                                                           new STElement("bar", null)         }));
//            st.Expect(q1, new Object[]{
//                                          r1[5],
//                                          r1[6]         });
//        }
//      
//        public void TestDescendToObject() {
//            Query q1 = st.Query();
//            Object[] r1 = Store();
//            q1.Constrain(new STHashtableET());
//            q1.Descend("col").Descend("foo1").Constrain("bar");
//            st.Expect(q1, new Object[]{
//                                          r1[5],
//                                          r1[6]         });
//        }
    }
}