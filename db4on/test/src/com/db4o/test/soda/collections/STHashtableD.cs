/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.util;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.collections {

    public class STHashtableD : STClass {
      
        public STHashtableD() : base() {
        }
        [Transient] public static SodaTest st;
      
        protected System.Collections.Hashtable Vec(Object[] objects) {
            System.Collections.Hashtable h1 = new System.Collections.Hashtable();
            for (int i1 = 0; i1 < objects.Length; i1++) {
                h1.Add(objects[i1], i1);
            }
            return h1;
        }
      
        public Object[] Store() {
            return new Object[]{
                                   Vec(new Object[]{
                                                       5778,
                                                       5779            }),
                                   Vec(new Object[]{
                                                       5778,
                                                       5789            }),
                                   Vec(new Object[]{
                                                       "foo577",
                                                       new STElement("bar577", "barbar577")            }),
                                   Vec(new Object[]{
                                                       "foo5772",
                                                       new STElement("bar577", "barbar2577")            })         };
        }
      
        public void TestDefaultContainsInteger() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(Vec(new Object[]{5778}));
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[1]         });
        }
      
        public void TestDefaultContainsString() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(Vec(new Object[]{
                                             "foo577"         }));
            st.Expect(q1, new Object[]{
                                          r1[2]         });
        }
      
        public void TestDefaultContainsTwo() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(Vec(new Object[]{
                                             System.Convert.ToInt32(5778),
                                             System.Convert.ToInt32(5789)         }));
            st.Expect(q1, new Object[]{
                                          r1[1]         });
        }
      
        public void TestDefaultContainsObject() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(Vec(new Object[]{
                                             new STElement("bar577", null)         }));
            st.Expect(q1, new Object[]{
                                          r1[2],
                                          r1[3]         });
        }
    }
}