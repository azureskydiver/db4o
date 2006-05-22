/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.experiments {

    public class STIdentityEvaluation : STClass1 {
        [Transient] public static SodaTest st;
      
        public Object[] Store() {
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
      
        public void Test() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(new Helper("aaa"));
            ObjectSet os = q1.Execute();
            Helper helperA1 = (Helper)os.Next();
            q1 = st.Query();
            q1.Constrain(Class.GetClassForType(typeof(STIdentityEvaluation)));
            q1.Descend("helper").Constrain(helperA1).Identity();
            q1.Constrain(new EvaluateIdentity());
            st.Expect(q1, new Object[]{
                                         r1[1],
                                         r1[2],
                                         r1[3]         });
        }

        class EvaluateIdentity : Evaluation{
            public void Evaluate(Candidate candidate) {
                candidate.Include(true);
            }
        }

      
        public void TestMemberClassConstraint() {
            Query q1 = st.Query();
            Object[] r1 = Store();
            q1.Constrain(Class.GetClassForType(typeof(STIdentityEvaluation)));
            q1.Descend("helper").Constrain(typeof(HelperDerivate));
            st.Expect(q1, new Object[]{
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