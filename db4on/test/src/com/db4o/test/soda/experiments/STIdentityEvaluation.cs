/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.experiments {

    public class STIdentityEvaluation : STClass1 {
        [Transient] public static SodaTest st;
      
        public Object[] store() {
            Helper helperA1 = new Helper("aaa");
            return new Object[]{
                                   new STIdentityEvaluation(null),
                                   new STIdentityEvaluation(helperA1),
                                   new STIdentityEvaluation(helperA1),
                                   new STIdentityEvaluation(helperA1),
                                   new STIdentityEvaluation(new HelperDerivate("bbb")),
                                   new STIdentityEvaluation(new Helper("dod"))         };
        }
        public Helper helper;
      
        public STIdentityEvaluation() : base() {
        }
      
        public STIdentityEvaluation(Helper h) : base() {
            this.helper = h;
        }
      
        public void test() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(new Helper("aaa"));
            ObjectSet os = q1.execute();
            Helper helperA1 = (Helper)os.next();
            q1 = st.query();
            q1.constrain(Class.getClassForType(typeof(STIdentityEvaluation)));
            q1.descend("helper").constrain(helperA1).identity();
            q1.constrain(new EvaluateIdentity());
            st.expect(q1, new Object[]{
                                         r1[1],
                                         r1[2],
                                         r1[3]         });
        }

        class EvaluateIdentity : Evaluation{
            public void evaluate(Candidate candidate) {
                candidate.include(true);
            }
        }

      
        public void testMemberClassConstraint() {
            Query q1 = st.query();
            Object[] r1 = store();
            q1.constrain(Class.getClassForType(typeof(STIdentityEvaluation)));
            q1.descend("helper").constrain(typeof(HelperDerivate));
            st.expect(q1, new Object[]{
                                          r1[4]         });
        }
      
        public class Helper {
            public String hString;
         
            public Helper() : base() {
            }
         
            public Helper(String str) : base() {
                hString = str;
            }
        }
      
        public class HelperDerivate : Helper {
         
            public HelperDerivate() : base() {
            }
         
            public HelperDerivate(String str) : base(str) {
            }
        }
    }
}