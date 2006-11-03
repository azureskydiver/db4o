/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.arrays.typed
{

    public class STArrIntegerT : STClass1
    {
        [Transient]
        public static SodaTest st;
        public int[] intArr;

        public STArrIntegerT()
            : base()
        {
        }

        public STArrIntegerT(int[] arr)
            : base()
        {
            intArr = arr;
        }

        public Object[] Store()
        {
            return new Object[]{
            new STArrIntegerT(),
new STArrIntegerT(new int[0]),
new STArrIntegerT(new int[]{
               0,
0            }),
new STArrIntegerT(new int[]{
               1,
17,
Int32.MaxValue - 1            }),
new STArrIntegerT(new int[]{
               3,
17,
25,
Int32.MaxValue - 2            })         };
        }

        public void TestDefaultContainsOne()
        {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(new STArrIntegerT(new int[]{
            17         }));
            st.Expect(q1, new Object[]{
            r1[3],
r1[4]         });
        }

        public void TestDefaultContainsTwo()
        {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(new STArrIntegerT(new int[]{
            17,
25         }));
            st.Expect(q1, new Object[]{
            r1[4]         });
        }

        public void TestDescendOne()
        {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(Class.GetClassForType(typeof(STArrIntegerT)));
            q1.Descend("intArr").Constrain(System.Convert.ToInt32(17));
            st.Expect(q1, new Object[]{
            r1[3],
r1[4]         });
        }

        public void TestDescendTwo()
        {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(Class.GetClassForType(typeof(STArrIntegerT)));
            Query qElements1 = q1.Descend("intArr");
            qElements1.Constrain(System.Convert.ToInt32(17));
            qElements1.Constrain(System.Convert.ToInt32(25));
            st.Expect(q1, new Object[]{
            r1[4]         });
        }

        public void TestDescendSmaller()
        {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(Class.GetClassForType(typeof(STArrIntegerT)));
            Query qElements1 = q1.Descend("intArr");
            qElements1.Constrain(System.Convert.ToInt32(3)).Smaller();
            st.Expect(q1, new Object[]{
            r1[2],
r1[3]         });
        }

        public void TestDescendNotSmaller()
        {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(Class.GetClassForType(typeof(STArrIntegerT)));
            Query qElements1 = q1.Descend("intArr");
            qElements1.Constrain(System.Convert.ToInt32(3)).Smaller();
            st.Expect(q1, new Object[]{
            r1[2],
r1[3]         });
        }
    }
}