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
using com.db4o.config;
using com.db4o.test.types;
namespace com.db4o.test {

   public class CascadeOnDelete {
      
      public CascadeOnDelete() : base() {
      }
      public ObjectSimplePublic[] osp;
      
      public void test() {
         noAccidentalDeletes();
      }
      
      private void noAccidentalDeletes() {
         noAccidentalDeletes1(true, true);
         noAccidentalDeletes1(true, false);
         noAccidentalDeletes1(false, true);
         noAccidentalDeletes1(false, false);
      }
      
      private void noAccidentalDeletes1(bool cascadeOnUpdate, bool cascadeOnDelete) {
         ObjectContainer con = Test.objectContainer();
         Test.deleteAllInstances(this);
         Test.deleteAllInstances(new ObjectSimplePublic());
         ObjectClass oc1 = Db4o.configure().objectClass(this);
         oc1.cascadeOnDelete(cascadeOnDelete);
         oc1.cascadeOnUpdate(cascadeOnUpdate);
         con = Test.reOpen();
         ObjectSimplePublic myOsp1 = new ObjectSimplePublic();
         myOsp1.set(1);
         CascadeOnDelete cod1 = new CascadeOnDelete();
         cod1.osp = new ObjectSimplePublic[]{
            myOsp1         };
         con.set(cod1);
         con.commit();
         cod1.osp[0].name = "abrakadabra";
         con.set(cod1);
         if (!cascadeOnDelete && !cascadeOnUpdate) {
            con.set(cod1.osp[0]);
         }
         Test.ensureOccurrences(cod1.osp[0], 1);
         con.commit();
         Test.ensureOccurrences(cod1.osp[0], 1);
      }
   }
}