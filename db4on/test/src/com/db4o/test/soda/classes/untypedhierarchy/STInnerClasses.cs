/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o.test.soda;
using com.db4o.test.soda.engines.db4o;
using com.db4o;
using com.db4o.query;
namespace com.db4o.test.soda.classes.untypedhierarchy {

   /**
    * epaul: Shows a bug.  carlrosenberger: Fixed! The error was due to the the behaviour of STCompare.java. It compared the syntetic fields in inner classes also. I changed the behaviour to neglect all fields that contain a "__".  @author <a href="mailto:Paul-Ebermann@gmx.de">Paul Ebermann</a> @version 0.1
    */
   public class STInnerClasses : STClass {
      [Transient] public static SodaTest st;
      
      public class Parent {
         public Object child;
         
         public Parent(Object o) : base() {
            child = o;
         }
         
         public override String ToString() {
            return "Parent[" + child + "]";
         }
         
         public Parent() : base() {
         }
      }
      
      public class FirstClass {
         public Object childFirst;
         
         public FirstClass(Object o) : base() {
            childFirst = o;
         }
         
         public override String ToString() {
            return "First[" + childFirst + "]";
         }
         
         public FirstClass() : base() {
         }
      }
      
      public STInnerClasses() : base() {
      }
      
      public Object[] Store() {
         return new Object[]{
            new Parent(new FirstClass("Example")),
new Parent(new FirstClass("no Example"))         };
      }
      
      /**
       * Only 
       */
      public void TestNothing() {
         Query q1 = st.Query();
         Query q21 = q1.Descend("child");
         Object[] r1 = Store();
         st.Expect(q1, r1);
      }
      
      /**
       * Start the test.
       */
      public static void Main(String[] args) {
          new SodaTest().Run(new STClass[] { new STInnerClasses()}, new STEngine[] {new STDb4o()}, false);
      }
   }
}