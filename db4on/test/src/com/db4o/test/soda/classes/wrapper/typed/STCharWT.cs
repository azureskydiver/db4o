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
      
      public Object[] store() {
         return new Object[]{
            new STCharWT((char)0),
new STCharWT((char)1),
new STCharWT((char)99),
new STCharWT((char)909)         };
      }
      
      public void testEquals() {
         Query q1 = st.query();
         q1.constrain(new STCharWT((char)0));
         q1.descend(DESCENDANT).constrain(System.Convert.ToChar((char)0));
         st.expectOne(q1, store()[0]);
      }
      
      public void testNotEquals() {
         Query q1 = st.query();
         Object[] r1 = store();
         Constraint c1 = q1.constrain(r1[1]);
         q1.descend(DESCENDANT).constraints().not();
         st.expect(q1, new Object[]{
            r1[0],
r1[2],
r1[3]         });
      }
      
      public void testGreater() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STCharWT((char)9));
         q1.descend(DESCENDANT).constraints().greater();
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[2],
r1[3]         });
      }
      
      public void testSmaller() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STCharWT((char)1));
         q1.descend(DESCENDANT).constraints().smaller();
         st.expectOne(q1, store()[0]);
      }
      
      public void testIdentity() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STCharWT((char)1));
         ObjectSet set1 = q1.execute();
         STCharWT identityConstraint1 = (STCharWT)set1.next();
         identityConstraint1.i_char = System.Convert.ToChar((char)9999);
         q1 = st.query();
         q1.constrain(identityConstraint1).identity();
         identityConstraint1.i_char = System.Convert.ToChar((char)1);
         st.expectOne(q1, store()[1]);
      }
      
      public void testNotIdentity() {
         Query q1 = st.query();
         Constraint c1 = q1.constrain(new STCharWT((char)1));
         ObjectSet set1 = q1.execute();
         STCharWT identityConstraint1 = (STCharWT)set1.next();
         identityConstraint1.i_char = System.Convert.ToChar((char)9080);
         q1 = st.query();
         q1.constrain(identityConstraint1).identity().not();
         identityConstraint1.i_char = System.Convert.ToChar((char)1);
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[2],
r1[3]         });
      }
      
      public void testConstraints() {
         Query q1 = st.query();
         q1.constrain(new STCharWT((char)1));
         q1.constrain(new STCharWT((char)0));
         Constraints cs1 = q1.constraints();
         Constraint[] csa1 = cs1.toArray();
         if (csa1.Length != 2) {
            st.error("Constraints not returned");
         }
      }
      
  }
}