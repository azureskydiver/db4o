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

   /**
    * note the special configuration for this class in Regression.openContainer()
    */
   public class DeepUpdate: RTestable {
      public ObjectSimplePublic d1;
      public DeepHelper d2;
      public DeepHelper[] d3;
      
      public DeepUpdate() : base() {
      }
      
      public void compare(ObjectContainer con, Object obj, int ver) {
         Compare.compare(con, set(newInstance(), ver), obj, "", null);
      }
      
      public override bool Equals(Object obj) {
         if (obj == null) {
            return false;
         }
         if (!(obj is DeepUpdate)) {
            return false;
         }
         DeepUpdate with = (DeepUpdate)obj;
         if (with.d1 != null && d1 != null) {
            if (d1.Equals(with.d1)) {
               if (with.d2 != null && d2 != null) {
                  if (d2.d1.Equals(with.d2.d1)) {
                     if (with.d3 != null && d3 != null) {
                        if (with.d3.Length == d3.Length) {
                           if (with.d3[0].Equals(d3[0])) {
                              if (with.d3[1].Equals(d3[1])) {
                                 return true;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
         return false;
      }
      
      public Object newInstance() {
         return new DeepUpdate();
      }
      
      public Object set(Object obj, int ver) {
         ((DeepUpdate)obj).set(ver);
         return obj;
      }
      
      public void set(int ver) {
         d1 = new ObjectSimplePublic();
         d2 = new DeepHelper();
         d3 = new DeepHelper[2];
         d3[0] = new DeepHelper();
         d3[1] = new DeepHelper();
         if (ver == 1) {
            d1.name = "OneONEOneONEOneONEOneONEOneONEOneONE";
         } else {
            d1.name = "TwoTWOTwoTWOTwoTWOTwoTWOTwoTWO";
         }
         d2.set(ver);
         d3[0].set(ver);
         d3[1].set(ver);
      }
      
      public bool jdk2() {
         return false;
      }
      
      public bool ver3() {
         return false;
      }
   }
}