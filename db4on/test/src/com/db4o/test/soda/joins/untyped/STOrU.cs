/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.joins.untyped {

    public class STOrU : STClass {
        [Transient] public static SodaTest st;
        internal Object orInt;
        internal Object orString;
      
        public STOrU() : base() {
        }
      
        internal STOrU(int a_int, String a_string) : base() {
            if (a_int != 0) {
                orInt = a_int;
            }
            orString = a_string;
        }
      
        public override String ToString() {
            return "STOr: int:" + orInt + " str:" + orString;
        }
      
        public Object[] Store() {
            return new Object[]{
                                   new STOrU(0, "hi"),
                                   new STOrU(5, null),
                                   new STOrU(1000, "joho"),
                                   new STOrU(30000, "osoo"),
                                   new STOrU(Int32.MaxValue - 1, null)         };
        }
      
        public void TestSmallerGreater() {
            Query q1 = st.Query();
            q1.Constrain(new STOrU());
            Query sub1 = q1.Descend("orInt");
            sub1.Constrain(30000).Greater().Or(sub1.Constrain(6).Smaller());
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[1],
                                          r1[4]         });
        }
      
        public void TestGreaterGreater() {
            Query q1 = st.Query();
            q1.Constrain(new STOrU());
            Query sub1 = q1.Descend("orInt");
            sub1.Constrain(System.Convert.ToInt32(30000)).Greater().Or(sub1.Constrain(System.Convert.ToInt32(5)).Greater());
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[2],
                                          r1[3],
                                          r1[4]         });
        }
      
        public void TestGreaterEquals() {
            Query q1 = st.Query();
            q1.Constrain(new STOrU());
            Query sub1 = q1.Descend("orInt");
            sub1.Constrain(1000).Greater().Or(sub1.Constrain(0));
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[3],
                                          r1[4]         });
        }
      
        public void TestEqualsNull() {
            Query q1 = st.Query();
            q1.Constrain(new STOrU(1000, null));
            q1.Descend("orInt").Constraints().Or(q1.Descend("orString").Constrain(null));
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[1],
                                          r1[2],
                                          r1[4]         });
        }
      
        public void TestAndOrAnd() {
            Query q1 = st.Query();
            q1.Constrain(new STOrU(0, null));
            q1.Descend("orInt").Constrain(System.Convert.ToInt32(5)).And(q1.Descend("orString").Constrain(null)).Or(q1.Descend("orInt").Constrain(System.Convert.ToInt32(1000)).And(q1.Descend("orString").Constrain("joho")));
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[1],
                                          r1[2]         });
        }
      
        public void TestOrAndOr() {
            Query q1 = st.Query();
            q1.Constrain(new STOrU(0, null));
            q1.Descend("orInt").Constrain(System.Convert.ToInt32(5)).Or(q1.Descend("orString").Constrain(null)).And(q1.Descend("orInt").Constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).Or(q1.Descend("orString").Constrain("joho")));
            st.ExpectOne(q1, Store()[4]);
        }
      
        public void TestOrOrAnd() {
            Query q1 = st.Query();
            q1.Constrain(new STOrU(0, null));
            q1.Descend("orInt").Constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).Or(q1.Descend("orString").Constrain("joho")).Or(q1.Descend("orInt").Constrain(System.Convert.ToInt32(5)).And(q1.Descend("orString").Constrain(null)));
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[1],
                                          r1[2],
                                          r1[4]         });
        }
      
        public void TestMultiOrAnd() {
            Query q1 = st.Query();
            q1.Constrain(new STOrU(0, null));
            q1.Descend("orInt").Constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).Or(q1.Descend("orString").Constrain("joho")).Or(q1.Descend("orInt").Constrain(System.Convert.ToInt32(5)).And(q1.Descend("orString").Constrain("joho"))).Or(q1.Descend("orInt").Constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).Or(q1.Descend("orString").Constrain(null)).And(q1.Descend("orInt").Constrain(System.Convert.ToInt32(5)).Or(q1.Descend("orString").Constrain(null))));
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[1],
                                          r1[2],
                                          r1[4]         });
        }
      
        public void TestNotSmallerGreater() {
            Query q1 = st.Query();
            q1.Constrain(new STOrU());
            Query sub1 = q1.Descend("orInt");
            sub1.Constrain(30000).Greater().Or(sub1.Constrain(1000).Smaller()).Not();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[2],
                                          r1[3]
                                                  });
        }
      
        public void TestNotGreaterGreater() {
            Query q1 = st.Query();
            q1.Constrain(new STOrU());
            Query sub1 = q1.Descend("orInt");
            sub1.Constrain(30000).Greater().Or(sub1.Constrain(5).Greater()).Not();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[1]
            });
        }
      
        public void TestNotGreaterEquals() {
            Query q1 = st.Query();
            q1.Constrain(new STOrU());
            Query sub1 = q1.Descend("orInt");
            sub1.Constrain(1000).Greater().Or(sub1.Constrain(0)).Not();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[1],
                                          r1[2]         });
        }
      
        public void TestNotEqualsNull() {
            Query q1 = st.Query();
            q1.Constrain(new STOrU(1000, null));
            q1.Descend("orInt").Constraints().Or(q1.Descend("orString").Constrain(null)).Not();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[3]         });
        }
      
        public void TestNotAndOrAnd() {
            Query q1 = st.Query();
            q1.Constrain(new STOrU(0, null));
            q1.Descend("orInt").Constrain(System.Convert.ToInt32(5)).And(q1.Descend("orString").Constrain(null)).Or(q1.Descend("orInt").Constrain(System.Convert.ToInt32(1000)).And(q1.Descend("orString").Constrain("joho"))).Not();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[3],
                                          r1[4]         });
        }
      
        public void TestNotOrAndOr() {
            Query q1 = st.Query();
            q1.Constrain(new STOrU(0, null));
            q1.Descend("orInt").Constrain(System.Convert.ToInt32(5)).Or(q1.Descend("orString").Constrain(null)).And(q1.Descend("orInt").Constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).Or(q1.Descend("orString").Constrain("joho"))).Not();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[1],
                                          r1[2],
                                          r1[3]         });
        }
      
        public void TestNotOrOrAnd() {
            Query q1 = st.Query();
            q1.Constrain(new STOrU(0, null));
            q1.Descend("orInt").Constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).Or(q1.Descend("orString").Constrain("joho")).Or(q1.Descend("orInt").Constrain(System.Convert.ToInt32(5)).And(q1.Descend("orString").Constrain(null))).Not();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[3]         });
        }
      
        public void TestNotMultiOrAnd() {
            Query q1 = st.Query();
            q1.Constrain(new STOrU(0, null));
            q1.Descend("orInt").Constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).Or(q1.Descend("orString").Constrain("joho")).Or(q1.Descend("orInt").Constrain(System.Convert.ToInt32(5)).And(q1.Descend("orString").Constrain("joho"))).Or(q1.Descend("orInt").Constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).Or(q1.Descend("orString").Constrain(null)).And(q1.Descend("orInt").Constrain(System.Convert.ToInt32(5)).Or(q1.Descend("orString").Constrain(null)))).Not();
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[0],
                                          r1[3]         });
        }
      
        public void TestOrNotAndOr() {
            Query q1 = st.Query();
            q1.Constrain(new STOrU(0, null));
            q1.Descend("orInt").Constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).Or(q1.Descend("orString").Constrain("joho")).Not().And(q1.Descend("orInt").Constrain(System.Convert.ToInt32(5)).Or(q1.Descend("orString").Constrain(null)));
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[1]         });
        }
      
        public void TestAndNotAndAnd() {
            Query q1 = st.Query();
            q1.Constrain(new STOrU(0, null));
            q1.Descend("orInt").Constrain(System.Convert.ToInt32(Int32.MaxValue - 1)).And(q1.Descend("orString").Constrain(null)).Not().And(q1.Descend("orInt").Constrain(System.Convert.ToInt32(5)).Or(q1.Descend("orString").Constrain("osoo")));
            Object[] r1 = Store();
            st.Expect(q1, new Object[]{
                                          r1[1],
                                          r1[3]         });
        }
    }
}