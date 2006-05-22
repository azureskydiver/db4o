/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda.arrays.typed;
using com.db4o.test.soda.classes.simple;
using com.db4o.test.soda.classes.typedhierarchy;
using com.db4o.test.soda.classes.wrapper.untyped;
namespace com.db4o.test.soda {

    public class STMagic : STClass1, STInterface {
        [Transient] public static SodaTest st;
        public String str;
      
        public STMagic() : base() {
        }
      
        internal STMagic(String str) : base() {
            this.str = str;
        }
      
        public override String ToString() {
            return "STMagic: " + str;
        }
      
        /**
         * needed for STInterface test 
         */
        public Object ReturnSomething() {
            return str;
        }
      
        public Object[] Store() {
            return new Object[]{
                                   new STMagic("aaa"),
                                   new STMagic("aaax")         };
        }
      
        /**
         * Magic:  Query for all objects with a known attribute,  independant of the class or even if you don't know the class.
         */
        public void TestUnconstrainedClass() {
            Query q1 = st.Query();
            q1.Descend("str").Constrain("aaa");
            st.Expect(q1, new Object[]{
                                          new STMagic("aaa"),
                                          new STString("aaa"),
                                          new STStringU("aaa")         });
        }
      
        /**
         * Magic: Query for multiple classes. Every class gets it's own slot in the query graph.
         */
        public void TestMultiClass() {
            Query q1 = st.Query();
            q1.Constrain(Class.GetClassForType(typeof(STDouble))).Or(q1.Constrain(Class.GetClassForType(typeof(STString))));
            Object[] stDoubles1 = new STDouble().Store();
            Object[] stStrings1 = new STString().Store();
            Object[] res1 = new Object[stDoubles1.Length + stStrings1.Length];
			System.Array.Copy(stDoubles1, 0, res1, 0, stDoubles1.Length);
			System.Array.Copy(stStrings1, 0, res1, stDoubles1.Length, stStrings1.Length);
            st.Expect(q1, res1);
        }
      
        /**
         * Magic: Execute any node in the query graph. The data for this example can be found in STTH1.java.
         */
        public void TestExecuteAnyNode() {
            Query q1 = st.Query();
            q1.Constrain(new STTH1().Store()[5]);
            q1 = q1.Descend("h2").Descend("h3");
            st.ExpectOne(q1, new STTH3("str3"));
        }
      
        /**
         * Magic: Querying for an implemented Interface. Using an Evaluation allows calls to the interface methods during the run of the query.s
         */
        public void TestInterface() {
            Query q1 = st.Query();
            q1.Constrain(Class.GetClassForType(typeof(STInterface)));
            q1.Constrain(new InterfaceEvaluation());
            st.Expect(q1, new Object[]{
                                         new STMagic("aaa"),
                                         new STString("aaa")         });
        }

        class InterfaceEvaluation : Evaluation{
            public void Evaluate(Candidate candidate) {
                STInterface sti1 = (STInterface)candidate.GetObject();
                candidate.Include(sti1.ReturnSomething().Equals("aaa"));
            }
        }
    }
}