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
namespace com.db4o.test.types {

   public class ObjectSimplePrivate: RTest, InterfaceHelper {
      private String name;
      
      public ObjectSimplePrivate() : base() {
      }
      
      public ObjectSimplePrivate(String a_name) : base() {
         name = a_name;
      }
      
      public override bool Equals(Object obj) {
         if (obj != null) {
            if (obj is ObjectSimplePrivate) {
               if (name != null) {
                  return name.Equals(((ObjectSimplePrivate)obj).name);
               }
            }
         }
         return false;
      }
      
      public override void set(int ver) {
         if (ver == 1) {
            name = "OneONEOneONEOneONEOneONEOneONEOneONE";
         } else {
            name = "TwoTWOTwoTWOTwoTWOTwoTWOTwoTWO";
         }
      }
      
      public override bool jdk2() {
         return true;
      }
   }
}