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
      
        protected ExtendHashtable vec(Object[] objects) {
            ExtendHashtable h1 = new ExtendHashtable();
            for (int i1 = 0; i1 < objects.Length; i1++) {
                h1.Add(objects[i1], i1);
            }
            return h1;
        }
      
        public Object[] store() {
            return new Object[]{
                                   vec(new Object[]{
                                                       System.Convert.ToInt32(6778),
                                                       System.Convert.ToInt32(6779)            }),
                                   vec(new Object[]{
                                                       System.Convert.ToInt32(6778),
                                                       System.Convert.ToInt32(6789)            }),
                                   vec(new Object[]{
                                                       "foo677",
                                                       new STElement("bar677", "barbar677")            }),
                                   vec(new Object[]{
                                                       "foo6772",
                                                       new STElement("bar677", "barbar2677")            })         };
        }
      
        public void testDefaultContainsInteger() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(vec(new Object[]{
                                             System.Convert.ToInt32(6778)         }));
            st.expect(q1, new Object[]{
                                          r1[0],
                                          r1[1]         });
        }
      
        public void testDefaultContainsString() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(vec(new Object[]{
                                             "foo677"         }));
            st.expect(q1, new Object[]{
                                          r1[2]         });
        }
      
        public void testDefaultContainsTwo() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(vec(new Object[]{
                                             System.Convert.ToInt32(6778),
                                             System.Convert.ToInt32(6789)         }));
            st.expect(q1, new Object[]{
                                          r1[1]         });
        }
      
        public void testDefaultContainsObject() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(vec(new Object[]{
                                             new STElement("bar677", null)         }));
            st.expect(q1, new Object[]{
                                          r1[2],
                                          r1[3]         });
        }
    }
}