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
      
        public Object[] store() {
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
      
        public void testDefaultContainsInteger() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(new STHashtableET(new Object[]{17}));
            st.expect(q1, new Object[]{
                                          r1[3],
                                          r1[4]         });
        }
      
//        public void testDefaultContainsString() {
//            Query q1 = st.query();
//            Object[] r1 = store();
//            q1.constrain(new STHashtableET(new Object[]{
//                                                           "foo"         }));
//            st.expect(q1, new Object[]{
//                                          r1[5]         });
//        }
//      
//        public void testDefaultContainsTwo() {
//            Query q1 = st.query();
//            Object[] r1 = store();
//            q1.constrain(new STHashtableET(new Object[]{
//                                                           System.Convert.ToInt32(17),
//                                                           System.Convert.ToInt32(25)         }));
//            st.expect(q1, new Object[]{
//                                          r1[4]         });
//        }
//      
//        public void testDescendOne() {
//            Query q1 = st.query();
//            Object[] r1 = store();
//            q1.constrain(Class.getClassForType(typeof(STHashtableET)));
//            q1.descend("col").constrain(System.Convert.ToInt32(17));
//            st.expect(q1, new Object[]{
//                                          r1[3],
//                                          r1[4]         });
//        }
//      
//        public void testDescendTwo() {
//            Query q1 = st.query();
//            Object[] r1 = store();
//            q1.constrain(Class.getClassForType(typeof(STHashtableET)));
//            Query qElements1 = q1.descend("col");
//            qElements1.constrain(System.Convert.ToInt32(17));
//            qElements1.constrain(System.Convert.ToInt32(25));
//            st.expect(q1, new Object[]{
//                                          r1[4]         });
//        }
//      
//        public void testDescendSmaller() {
//            Query q1 = st.query();
//            Object[] r1 = store();
//            q1.constrain(Class.getClassForType(typeof(STHashtableET)));
//            Query qElements1 = q1.descend("col");
//            qElements1.constrain(System.Convert.ToInt32(3)).smaller();
//            st.expect(q1, new Object[]{
//                                          r1[2],
//                                          r1[3]         });
//        }
//      
//        public void testDefaultContainsObject() {
//            Query q1 = st.query();
//            Object[] r1 = store();
//            q1.constrain(new STHashtableET(new Object[]{
//                                                           new STElement("bar", null)         }));
//            st.expect(q1, new Object[]{
//                                          r1[5],
//                                          r1[6]         });
//        }
//      
//        public void testDescendToObject() {
//            Query q1 = st.query();
//            Object[] r1 = store();
//            q1.constrain(new STHashtableET());
//            q1.descend("col").descend("foo1").constrain("bar");
//            st.expect(q1, new Object[]{
//                                          r1[5],
//                                          r1[6]         });
//        }
    }
}