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
namespace com.db4o.test {

   public class SetDeactivated {
      internal String foo;
      
      public SetDeactivated() : base() {
      }
      
      public SetDeactivated(String foo) : base() {
         this.foo = foo;
      }
      
      public void store() {
         Test.deleteAllInstances(this);
         Test.store(new SetDeactivated("hi"));
         Test.commit();
      }
      
      public void test() {
         SetDeactivated sd1 = (SetDeactivated)Test.getOne(this);
         Test.objectContainer().deactivate(sd1, 1);
         Test.store(sd1);
         Test.objectContainer().purge(sd1);
         sd1 = (SetDeactivated)Test.getOne(this);
         Test.objectContainer().activate(sd1, 1);
         Test.ensure(sd1.foo.Equals("hi"));
      }
   }
}