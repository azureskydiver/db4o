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
using com.db4o;
using com.db4o.test.types;
namespace com.db4o.test {

   public class RenTwo : InterfaceHelper, RTestable {
      
      public RenTwo() : base() {
      }
      public String s1;
      public String s2;
      
      public void compare(ObjectContainer con, Object obj, int ver) {
         Compare.compare(con, set(newInstance(), ver), obj, "", null);
      }
      
      public override bool Equals(Object obj) {
         return obj != null && obj is RenTwo && s1 != null && s2 != null && s1.Equals(((RenTwo)obj).s1) && s2.Equals(((RenTwo)obj).s2);
      }
      
      public Object newInstance() {
         return new RenTwo();
      }
      
      public Object set(Object obj, int ver) {
         ((RenTwo)obj).set(ver);
         return obj;
      }
      
      public void set(int ver) {
         if (ver == 1) {
            s1 = "One";
            s2 = "One";
         } else {
            s1 = "Two";
            s2 = "Two";
         }
      }
      
      public bool jdk2() {
         return false;
      }
      
      public bool ver3() {
         return false;
      }
   }
}