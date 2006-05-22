/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o.ext;
namespace com.db4o.test {

   public class ExtMethods {
      
      public ExtMethods() : base() {
      }
      
      public void Test() {
         ExtMethods em1 = new ExtMethods();
         Tester.Store(em1);
         ExtObjectContainer eoc1 = Tester.ObjectContainer();
         Tester.Ensure(!eoc1.IsClosed());
         Tester.Ensure(eoc1.IsActive(em1));
         Tester.Ensure(eoc1.IsStored(em1));
         eoc1.Deactivate(em1, 1);
         Tester.Ensure(!eoc1.IsActive(em1));
         eoc1.Activate(em1, 1);
         Tester.Ensure(eoc1.IsActive(em1));
         long id1 = eoc1.GetID(em1);
         Tester.Ensure(eoc1.IsCached(id1));
         eoc1.Purge(em1);
         Tester.Ensure(!eoc1.IsCached(id1));
         Tester.Ensure(!eoc1.IsStored(em1));
         Tester.Ensure(!eoc1.IsActive(em1));
         eoc1.Bind(em1, id1);
         Tester.Ensure(eoc1.IsCached(id1));
         Tester.Ensure(eoc1.IsStored(em1));
         Tester.Ensure(eoc1.IsActive(em1));
         ExtMethods em21 = (ExtMethods)eoc1.GetByID(id1);
         Tester.Ensure(em1 == em21);
         eoc1.Purge();
         Tester.Ensure(eoc1.IsCached(id1));
         Tester.Ensure(eoc1.IsStored(em1));
         Tester.Ensure(eoc1.IsActive(em1));
         em21 = (ExtMethods)eoc1.GetByID(id1);
         Tester.Ensure(em1 == em21);
         Tester.Delete(em21);
         Tester.Commit();
         Tester.Ensure(!eoc1.IsCached(id1));
         Tester.Ensure(!eoc1.IsStored(em21));
         Tester.Ensure(!eoc1.IsActive(em21));
         Tester.Ensure(!eoc1.IsStored(null));
         Tester.Ensure(!eoc1.IsActive(null));
         Tester.Ensure(!eoc1.IsCached(0));
      }
   }
}