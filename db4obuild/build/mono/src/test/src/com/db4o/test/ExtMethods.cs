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
using com.db4o.ext;
namespace com.db4o.test {

   public class ExtMethods {
      
      public ExtMethods() : base() {
      }
      
      public void test() {
         ExtMethods em1 = new ExtMethods();
         Test.store(em1);
         ExtObjectContainer eoc1 = Test.objectContainer();
         Test.ensure(!eoc1.isClosed());
         Test.ensure(eoc1.isActive(em1));
         Test.ensure(eoc1.isStored(em1));
         eoc1.deactivate(em1, 1);
         Test.ensure(!eoc1.isActive(em1));
         eoc1.activate(em1, 1);
         Test.ensure(eoc1.isActive(em1));
         long id1 = eoc1.getID(em1);
         Test.ensure(eoc1.isCached(id1));
         eoc1.purge(em1);
         Test.ensure(!eoc1.isCached(id1));
         Test.ensure(!eoc1.isStored(em1));
         Test.ensure(!eoc1.isActive(em1));
         eoc1.bind(em1, id1);
         Test.ensure(eoc1.isCached(id1));
         Test.ensure(eoc1.isStored(em1));
         Test.ensure(eoc1.isActive(em1));
         ExtMethods em21 = (ExtMethods)eoc1.getByID(id1);
         Test.ensure(em1 == em21);
         eoc1.purge();
         Test.ensure(eoc1.isCached(id1));
         Test.ensure(eoc1.isStored(em1));
         Test.ensure(eoc1.isActive(em1));
         em21 = (ExtMethods)eoc1.getByID(id1);
         Test.ensure(em1 == em21);
         Test.delete(em21);
         Test.commit();
         Test.ensure(!eoc1.isCached(id1));
         Test.ensure(!eoc1.isStored(em21));
         Test.ensure(!eoc1.isActive(em21));
         Test.ensure(!eoc1.isStored(null));
         Test.ensure(!eoc1.isActive(null));
         Test.ensure(!eoc1.isCached(0));
      }
   }
}