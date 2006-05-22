/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.util;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.collections {

    public class STHashtableED : STClass {
      
        public STHashtableED() : base() {
        }
        [Transient] public static SodaTest st;

      
        public class ExtendHashtable : System.Collections.Hashtable {
         
            public ExtendHashtable() : base() {
            }
        }
      
        protected ExtendHashtable Vec(Object[] objects) {
            ExtendHashtable h1 = new ExtendHashtable();
            for (int i1 = 0; i1 < objects.Length; i1++) {
                h1.Add(objects[i1], i1);
            }
            return h1;
        }
      
        public Object[] Store() {
            return new Object[]{
                                   Vec(new Object[]{
                                                       System.Convert.ToInt32(6778),
                                                       System.Convert.ToInt32(6779)            }),
                                   Vec(new Object[]{
                                                       System.Convert.ToInt32(6778),
                                                       System.Convert.ToInt32(6789)            }),
                                   Vec(new Object[]{
                                                       "foo677",
                                                       new STElement("bar677", "barbar677")            }),
                                   Vec(new Object[]{
                                                       "foo6772",
                                                       new STElement("bar677", "barbar2677")            })         };
        }
      
        public void TestDefaultContainsInteger() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(Vec(new Object[]{
                                             System.Convert.ToInt32(6778)         }));
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[1]         });
        }
      
        public void TestDefaultContainsString() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(Vec(new Object[]{
                                             "foo677"         }));
            st.Expect(q1, new Object[]{
                                          r1[2]         });
        }
      
        public void TestDefaultContainsTwo() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(Vec(new Object[]{
                                             System.Convert.ToInt32(6778),
                                             System.Convert.ToInt32(6789)         }));
            st.Expect(q1, new Object[]{
                                          r1[1]         });
        }
      
        public void TestDefaultContainsObject() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(Vec(new Object[]{
                                             new STElement("bar677", null)         }));
            st.Expect(q1, new Object[]{
                                          r1[2],
                                          r1[3]         });
        }
    }
}