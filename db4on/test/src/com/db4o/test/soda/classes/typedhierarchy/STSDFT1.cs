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
      
      public Object[] store() {
         return new Object[]{
            new STSDFT1(),
new STSDFT2(),
new STSDFT2("str1"),
new STSDFT2("str2"),
new STSDFT3(),
new STSDFT3("str1"),
new STSDFT3("str3")         };
      }
      
      public void testStrNull() {
         Query q1 = st.query();
         q1.constrain(new STSDFT1());
         q1.descend("foo").constrain(null);
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[1],
r1[4]         });
      }
      
      public void testStrVal() {
         Query q1 = st.query();
         q1.constrain(Class.getClassForType(typeof(STSDFT1)));
         q1.descend("foo").constrain("str1");
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[2],
r1[5]         });
      }
      
      public void testOrValue() {
         Query q1 = st.query();
         q1.constrain(Class.getClassForType(typeof(STSDFT1)));
         Query foo1 = q1.descend("foo");
         foo1.constrain("str1").or(foo1.constrain("str2"));
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[2],
r1[3],
r1[5]         });
      }
      
      public void testOrNull() {
         Query q1 = st.query();
         q1.constrain(Class.getClassForType(typeof(STSDFT1)));
         Query foo1 = q1.descend("foo");
         foo1.constrain("str1").or(foo1.constrain(null));
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2],
r1[4],
r1[5]         });
      }
      
      public void testTripleOrNull() {
         Query q1 = st.query();
         q1.constrain(Class.getClassForType(typeof(STSDFT1)));
         Query foo1 = q1.descend("foo");
         foo1.constrain("str1").or(foo1.constrain(null)).or(foo1.constrain("str2"));
         Object[] r1 = store();
         st.expect(q1, new Object[]{
            r1[0],
r1[1],
r1[2],
r1[3],
r1[4],
r1[5]         });
      }
   }
}