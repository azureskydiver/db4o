/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.wrapper.typed {

   public class STIntegerWT : STClass {
      [Transient] public static SodaTest st;
      internal Int32 i_int;
      
      public STIntegerWT() : base() {
      }
      
      internal STIntegerWT(int a_int) : base() {
         i_int = System.Convert.ToInt32(a_int);
      }
      
      public Object[] Store() {
         return new Object[]{
            new STIntegerWT(0),
new STIntegerWT(1),
new STIntegerWT(99),
new STIntegerWT(909)         };
      }
      
      public void TestEquals() {
         Query q1 = st.Query();
         q1.Constrain(new STIntegerWT(0));
         q1.Descend("i_int").Constrain(System.Convert.ToInt32(0));
         st.ExpectOne(q1, Store()[0]);
      }
      
      public void TestNotEquals() {
         Query q1 = st.Query();
         Object[] r1 = Store();
         Constraint c1 = q1.Constrain(new STIntegerWT());
         q1.Descend("i_int").Constrain(System.Convert.ToInt32(0)).Not();
         st.Expect(q1, new Object[]{
            r1[1],
r1[2],
r1[3]         });
      }
      
      public void TestGreater() {
         Query q1 = st.Query();
         Constraint c1 = q1.Constrain(new STIntegerWT(9));
         q1.Descend("i_int").Constraints().Greater();
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
      
      public void TestSmaller() {
         Query q1 = st.Query();
         Constraint c1 = q1.Constrain(new STIntegerWT(1));
         q1.Descend("i_int").Constraints().Smaller();
         st.ExpectOne(q1, Store()[0]);
      }
      
      public void TestContains() {
         Query q1 = st.Query();
         Constraint c1 = q1.Constrain(new STIntegerWT(9));
         q1.Descend("i_int").Constraints().Contains();
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
      
      public void TestNotContains() {
         Query q1 = st.Query();
         Constraint c1 = q1.Constrain(new STIntegerWT());
         q1.Descend("i_int").Constrain(System.Convert.ToInt32(0)).Contains().Not();
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[1],
r1[2]         });
      }
      
      public void TestLike() {
         Query q1 = st.Query();
         Constraint c1 = q1.Constrain(new STIntegerWT(90));
         q1.Descend("i_int").Constraints().Like();
         st.ExpectOne(q1, new STIntegerWT(909));
         q1 = st.Query();
         c1 = q1.Constrain(new STIntegerWT(10));
         q1.Descend("i_int").Constraints().Like();
         st.ExpectNone(q1);
      }
      
      public void TestNotLike() {
         Query q1 = st.Query();
         Constraint c1 = q1.Constrain(new STIntegerWT(1));
         q1.Descend("i_int").Constraints().Like().Not();
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[0],
r1[2],
r1[3]         });
      }
      
      public void TestIdentity() {
         Query q1 = st.Query();
         Constraint c1 = q1.Constrain(new STIntegerWT(1));
         ObjectSet set1 = q1.Execute();
         STIntegerWT identityConstraint1 = (STIntegerWT)set1.Next();
         identityConstraint1.i_int = System.Convert.ToInt32(9999);
         q1 = st.Query();
         q1.Constrain(identityConstraint1).Identity();
         identityConstraint1.i_int = System.Convert.ToInt32(1);
         st.ExpectOne(q1, Store()[1]);
      }
      
      public void TestNotIdentity() {
         Query q1 = st.Query();
         Constraint c1 = q1.Constrain(new STIntegerWT(1));
         ObjectSet set1 = q1.Execute();
         STIntegerWT identityConstraint1 = (STIntegerWT)set1.Next();
         identityConstraint1.i_int = System.Convert.ToInt32(9080);
         q1 = st.Query();
         q1.Constrain(identityConstraint1).Identity().Not();
         identityConstraint1.i_int = System.Convert.ToInt32(1);
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[0],
r1[2],
r1[3]         });
      }
      
      public void TestConstraints() {
         Query q1 = st.Query();
         q1.Constrain(new STIntegerWT(1));
         q1.Constrain(new STIntegerWT(0));
         Constraints cs1 = q1.Constraints();
         Constraint[] csa1 = cs1.ToArray();
         if (csa1.Length != 2) {
            st.Error("Constraints not returned");
         }
      }
      
  }
}