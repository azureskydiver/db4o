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

   public class Callbacks: RTest {
      
      public Callbacks() : base() {
      }
      public String name;
      
      public void objectOnActivate(ObjectContainer container) {
         Console.WriteLine("onActivate");
      }
      
      public void objectOnDeactivate(ObjectContainer container) {
         Console.WriteLine("onDeactivate");
      }
      
      public void objectOnDelete(ObjectContainer container) {
         Console.WriteLine("onDelete");
      }
      
      public void objectOnNew(ObjectContainer container) {
         Console.WriteLine("onNew");
      }
      
      public void objectOnUpdate(ObjectContainer container) {
         Console.WriteLine("onUpdate");
      }
      
      public override void set(int ver) {
         if (ver == 1) {
            name = "OneONEOneONEOneONEOneONEOneONEOneONE";
         } else {
            name = "TwoTWOTwoTWOTwoTWOTwoTWOTwoTWO";
         }
      }
   }
}