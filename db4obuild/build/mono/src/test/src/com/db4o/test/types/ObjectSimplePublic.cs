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
namespace com.db4o.test.types {

   public class ObjectSimplePublic: InterfaceHelper, RTestable {
      public String name;
      
      public ObjectSimplePublic() : base() {
      }
      
      public ObjectSimplePublic(String a_name) : base() {
         name = a_name;
      }
      
      public void compare(ObjectContainer con, Object obj, int ver) {
         Compare.compare(con, set(newInstance(), ver), obj, "", null);
      }
      
      public override bool Equals(Object obj) {
         if (obj != null) {
            if (obj is ObjectSimplePublic) {
               if (name != null) {
                  return name.Equals(((ObjectSimplePublic)obj).name);
               }
            }
         }
         return false;
      }
      
      public Object newInstance() {
         return new ObjectSimplePublic();
      }
      
      public Object set(Object obj, int ver) {
         ((ObjectSimplePublic)obj).set(ver);
         return obj;
      }
      
      public void set(int ver) {
         if (ver == 1) {
            name = "OneONEOneONEOneONEOneONEOneONEOneONE";
         } else {
            name = "TwoTWOTwoTWOTwoTWOTwoTWOTwoTWO";
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