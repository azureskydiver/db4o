/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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