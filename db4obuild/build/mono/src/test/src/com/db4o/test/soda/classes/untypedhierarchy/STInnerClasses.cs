/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

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
      
      public Object[] store() {
         return new Object[]{
            new Parent(new FirstClass("Example")),
new Parent(new FirstClass("no Example"))         };
      }
      
      /**
       * Only 
       */
      public void testNothing() {
         Query q1 = st.query();
         Query q21 = q1.descend("child");
         Object[] r1 = store();
         st.expect(q1, r1);
      }
      
      /**
       * Start the test.
       */
      public static void Main(String[] args) {
          new SodaTest().run(new STClass[] { new STInnerClasses()}, new STEngine[] {new STDb4o()}, false);
      }
   }
}