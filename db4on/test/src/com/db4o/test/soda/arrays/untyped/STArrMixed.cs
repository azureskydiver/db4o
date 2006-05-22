/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.arrays.untyped {

    public class STArrMixed : STClass {
        [Transient] public static SodaTest st;
        internal Object[] arr;
      
        public STArrMixed() : base() {
        }
      
        public STArrMixed(Object[] arr) : base() {
            this.arr = arr;
        }
      
        public Object[] Store() {
            return new Object[]{
                                   new STArrMixed(),
                                   new STArrMixed(new Object[0]),
                                   new STArrMixed(new Object[]{
                                                                  System.Convert.ToInt32(0),
                                                                  System.Convert.ToInt32(0),
                                                                  "foo",
                                                                  System.Convert.ToBoolean(false)            }),
                                   new STArrMixed(new Object[]{
                                                                  System.Convert.ToInt32(1),
                                                                  System.Convert.ToInt32(17),
                                                                  System.Convert.ToInt32(Int32.MaxValue - 1),
                                                                  "foo",
                                                                  "bar"            }),
                                   new STArrMixed(new Object[]{
                                                                  System.Convert.ToInt32(3),
                                                                  System.Convert.ToInt32(17),
                                                                  System.Convert.ToInt32(25),
                                                                  System.Convert.ToInt32(Int32.MaxValue - 2)            })         };
        }
      
        public void TestDefaultContainsInteger() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(new STArrMixed(new Object[]{
                                                        System.Convert.ToInt32(17)         }));
            st.Expect(q1, new Object[]{
                                          r1[3],
                                          r1[4]         });
        }
      
        public void TestDefaultContainsString() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(new STArrMixed(new Object[]{
                                                        "foo"         }));
            st.Expect(q1, new Object[]{
                                          r1[2],
                                          r1[3]         });
        }
      
        public void TestDefaultContainsBoolean() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(new STArrMixed(new Object[]{
                                                        System.Convert.ToBoolean(false)         }));
            st.Expect(q1, new Object[]{
                                          r1[2]         });
        }
      
        public void TestDefaultContainsTwo() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(new STArrMixed(new Object[]{
                                                        System.Convert.ToInt32(17),
                                                        "bar"         }));
            st.Expect(q1, new Object[]{
                                          r1[3]         });
        }
      
        public void TestDescendOne() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(typeof(STArrMixed));
            q1.Descend("arr").Constrain(System.Convert.ToInt32(17));
            st.Expect(q1, new Object[]{
                                          r1[3],
                                          r1[4]         });
        }
      
        public void TestDescendTwo() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(typeof(STArrMixed));
            Query qElements1 = q1.Descend("arr");
            qElements1.Constrain(System.Convert.ToInt32(17));
            qElements1.Constrain("bar");
            st.Expect(q1, new Object[]{
                                          r1[3]         });
        }
      
        public void TestDescendSmaller() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(typeof(STArrMixed));
            Query qElements1 = q1.Descend("arr");
            qElements1.Constrain(System.Convert.ToInt32(3)).Smaller();
            st.Expect(q1, new Object[]{
                                          r1[2],
                                          r1[3]         });
        }
    }
}