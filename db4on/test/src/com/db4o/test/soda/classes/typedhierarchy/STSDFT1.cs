/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.query;
using com.db4o.test.soda;
namespace com.db4o.test.soda.classes.typedhierarchy {

   /**
    * SDFT: Same descendant field typed
    */
   public class STSDFT1 : STClass {
      [Transient] public static SodaTest st;
      
      public STSDFT1() : base() {
      }
      
      public Object[] Store() {
         return new Object[]{
            new STSDFT1(),
new STSDFT2(),
new STSDFT2("str1"),
new STSDFT2("str2"),
new STSDFT3(),
new STSDFT3("str1"),
new STSDFT3("str3")         };
      }
      
      public void TestStrNull() {
         Query q1 = st.Query();
         q1.Constrain(new STSDFT1());
         q1.Descend("foo").Constrain(null);
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[0],
r1[1],
r1[4]         });
      }
      
      public void TestStrVal() {
         Query q1 = st.Query();
         q1.Constrain(Class.GetClassForType(typeof(STSDFT1)));
         q1.Descend("foo").Constrain("str1");
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[2],
r1[5]         });
      }
      
      public void TestOrValue() {
         Query q1 = st.Query();
         q1.Constrain(Class.GetClassForType(typeof(STSDFT1)));
         Query foo1 = q1.Descend("foo");
         foo1.Constrain("str1").Or(foo1.Constrain("str2"));
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[2],
r1[3],
r1[5]         });
      }
      
      public void TestOrNull() {
         Query q1 = st.Query();
         q1.Constrain(Class.GetClassForType(typeof(STSDFT1)));
         Query foo1 = q1.Descend("foo");
         foo1.Constrain("str1").Or(foo1.Constrain(null));
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2],
r1[4],
r1[5]         });
      }
      
      public void TestTripleOrNull() {
         Query q1 = st.Query();
         q1.Constrain(Class.GetClassForType(typeof(STSDFT1)));
         Query foo1 = q1.Descend("foo");
         foo1.Constrain("str1").Or(foo1.Constrain(null)).Or(foo1.Constrain("str2"));
         Object[] r1 = Store();
         st.Expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2],
r1[3],
r1[4],
r1[5]         });
      }
   }
}