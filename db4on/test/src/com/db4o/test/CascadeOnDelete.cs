/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

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
         ObjectContainer con = Tester.objectContainer();
         Tester.deleteAllInstances(this);
         Tester.deleteAllInstances(new ObjectSimplePublic());
         ObjectClass oc1 = Db4o.configure().objectClass(this);
         oc1.cascadeOnDelete(cascadeOnDelete);
         oc1.cascadeOnUpdate(cascadeOnUpdate);
         con = Tester.reOpen();
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
         Tester.ensureOccurrences(cod1.osp[0], 1);
         con.commit();
         Tester.ensureOccurrences(cod1.osp[0], 1);
      }
   }
}