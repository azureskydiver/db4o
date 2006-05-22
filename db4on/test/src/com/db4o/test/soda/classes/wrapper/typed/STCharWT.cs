/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.wrapper.typed {

   public class STCharWT : STClass {
      static internal String DESCENDANT = "i_char";
      [Transient] public static SodaTest st;
      internal Char i_char;
      
      public STCharWT() : base() {
      }
      
      internal STCharWT(char a_char) : base() {
         i_char = System.Convert.ToChar(a_char);
      }
      
      public Object[] Store() {
         return new Object[]{
            new STCharWT((char)0),
new STCharWT((char)1),
new STCharWT((char)99),
new STCharWT((char)909)         };
      }
      
      public void TestEquals() {
         Query q1 = st.Query();
         q1.Constrain(new STCharWT((char)0));
         q1.Descend(DESCENDANT).Constrain(System.Convert.ToChar((char)0));
         st.ExpectOne(q1, Store()[0]);
      }
      
      public void TestNotEquals() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         Constraint c1 = q1.Constrain(r1[1]);
         q1.Descend(DESCENDANT).Constraints().Not();
         st.Expect(q1, new Object[]{
            r1[0],
r1[2],
r1[3]         });
      }
      
      public void TestGreater() {
         Query q1 = st.Query();
         Constraint c1 = q1.Constrain(new STCharWT((char)9));
         q1.Descend(DESCENDANT).Constraints().Greater();
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
      
      public void TestSmaller() {
         Query q1 = st.Query();
         Constraint c1 = q1.Constrain(new STCharWT((char)1));
         q1.Descend(DESCENDANT).Constraints().Smaller();
         st.ExpectOne(q1, Store()[0]);
      }
      
      public void TestIdentity() {
         Query q1 = st.Query();
         Constraint c1 = q1.Constrain(new STCharWT((char)1));
         ObjectSet set1 = q1.Execute();
         STCharWT identityConstraint1 = (STCharWT)set1.Next();
         identityConstraint1.i_char = System.Convert.ToChar((char)9999);
         q1 = st.Query();
         q1.Constrain(identityConstraint1).Identity();
         identityConstraint1.i_char = System.Convert.ToChar((char)1);
         st.ExpectOne(q1, Store()[1]);
      }
      
      public void TestNotIdentity() {
         Query q1 = st.Query();
         Constraint c1 = q1.Constrain(new STCharWT((char)1));
         ObjectSet set1 = q1.Execute();
         STCharWT identityConstraint1 = (STCharWT)set1.Next();
         identityConstraint1.i_char = System.Convert.ToChar((char)9080);
         q1 = st.Query();
         q1.Constrain(identityConstraint1).Identity().Not();
         identityConstraint1.i_char = System.Convert.ToChar((char)1);
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[0],
r1[2],
r1[3]         });
      }
      
      public void TestConstraints() {
         Query q1 = st.Query();
         q1.Constrain(new STCharWT((char)1));
         q1.Constrain(new STCharWT((char)0));
         Constraints cs1 = q1.Constraints();
         Constraint[] csa1 = cs1.ToArray();
         if (csa1.Length != 2) {
            st.Error("Constraints not returned");
         }
      }
      
  }
}