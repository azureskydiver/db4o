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
         Tester.store(em1);
         ExtObjectContainer eoc1 = Tester.objectContainer();
         Tester.ensure(!eoc1.isClosed());
         Tester.ensure(eoc1.isActive(em1));
         Tester.ensure(eoc1.isStored(em1));
         eoc1.deactivate(em1, 1);
         Tester.ensure(!eoc1.isActive(em1));
         eoc1.activate(em1, 1);
         Tester.ensure(eoc1.isActive(em1));
         long id1 = eoc1.getID(em1);
         Tester.ensure(eoc1.isCached(id1));
         eoc1.purge(em1);
         Tester.ensure(!eoc1.isCached(id1));
         Tester.ensure(!eoc1.isStored(em1));
         Tester.ensure(!eoc1.isActive(em1));
         eoc1.bind(em1, id1);
         Tester.ensure(eoc1.isCached(id1));
         Tester.ensure(eoc1.isStored(em1));
         Tester.ensure(eoc1.isActive(em1));
         ExtMethods em21 = (ExtMethods)eoc1.getByID(id1);
         Tester.ensure(em1 == em21);
         eoc1.purge();
         Tester.ensure(eoc1.isCached(id1));
         Tester.ensure(eoc1.isStored(em1));
         Tester.ensure(eoc1.isActive(em1));
         em21 = (ExtMethods)eoc1.getByID(id1);
         Tester.ensure(em1 == em21);
         Tester.delete(em21);
         Tester.commit();
         Tester.ensure(!eoc1.isCached(id1));
         Tester.ensure(!eoc1.isStored(em21));
         Tester.ensure(!eoc1.isActive(em21));
         Tester.ensure(!eoc1.isStored(null));
         Tester.ensure(!eoc1.isActive(null));
         Tester.ensure(!eoc1.isCached(0));
      }
   }
}