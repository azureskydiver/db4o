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
        public Object returnSomething() {
            return str;
        }
      
        public Object[] store() {
            return new Object[]{
                                   new STMagic("aaa"),
                                   new STMagic("aaax")         };
        }
      
        /**
         * Magic:  Query for all objects with a known attribute,  independant of the class or even if you don't know the class.
         */
        public void testUnconstrainedClass() {
            Query q1 = st.query();
            q1.descend("str").constrain("aaa");
            st.expect(q1, new Object[]{
                                          new STMagic("aaa"),
                                          new STString("aaa"),
                                          new STStringU("aaa")         });
        }
      
        /**
         * Magic: Query for multiple classes. Every class gets it's own slot in the query graph.
         */
        public void testMultiClass() {
            Query q1 = st.query();
            q1.constrain(Class.getClassForType(typeof(STDouble))).or(q1.constrain(Class.getClassForType(typeof(STString))));
            Object[] stDoubles1 = new STDouble().store();
            Object[] stStrings1 = new STString().store();
            Object[] res1 = new Object[stDoubles1.Length + stStrings1.Length];
            j4o.lang.JavaSystem.arraycopy(stDoubles1, 0, res1, 0, stDoubles1.Length);
            j4o.lang.JavaSystem.arraycopy(stStrings1, 0, res1, stDoubles1.Length, stStrings1.Length);
            st.expect(q1, res1);
        }
      
        /**
         * Magic: Execute any node in the query graph. The data for this example can be found in STTH1.java.
         */
        public void testExecuteAnyNode() {
            Query q1 = st.query();
            q1.constrain(new STTH1().store()[5]);
            q1 = q1.descend("h2").descend("h3");
            st.expectOne(q1, new STTH3("str3"));
        }
      
        /**
         * Magic: Querying for an implemented Interface. Using an Evaluation allows calls to the interface methods during the run of the query.s
         */
        public void testInterface() {
            Query q1 = st.query();
            q1.constrain(Class.getClassForType(typeof(STInterface)));
            q1.constrain(new InterfaceEvaluation());
            st.expect(q1, new Object[]{
                                         new STMagic("aaa"),
                                         new STString("aaa")         });
        }

        class InterfaceEvaluation : Evaluation{
            public void evaluate(Candidate candidate) {
                STInterface sti1 = (STInterface)candidate.getObject();
                candidate.include(sti1.returnSomething().Equals("aaa"));
            }
        }
    }
}