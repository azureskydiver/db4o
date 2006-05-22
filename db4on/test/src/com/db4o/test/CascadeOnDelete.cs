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
      
      public void Test() {
         NoAccidentalDeletes();
      }
      
      private void NoAccidentalDeletes() {
         NoAccidentalDeletes1(true, true);
         NoAccidentalDeletes1(true, false);
         NoAccidentalDeletes1(false, true);
         NoAccidentalDeletes1(false, false);
      }
      
      private void NoAccidentalDeletes1(bool cascadeOnUpdate, bool cascadeOnDelete) {
         ObjectContainer con = Tester.ObjectContainer();
         Tester.DeleteAllInstances(this);
         Tester.DeleteAllInstances(new ObjectSimplePublic());
         ObjectClass oc1 = Db4o.Configure().ObjectClass(this);
         oc1.CascadeOnDelete(cascadeOnDelete);
         oc1.CascadeOnUpdate(cascadeOnUpdate);
         con = Tester.ReOpen();
         ObjectSimplePublic myOsp1 = new ObjectSimplePublic();
         myOsp1.Set(1);
         CascadeOnDelete cod1 = new CascadeOnDelete();
         cod1.osp = new ObjectSimplePublic[]{
            myOsp1         };
         con.Set(cod1);
         con.Commit();
         cod1.osp[0].name = "abrakadabra";
         con.Set(cod1);
         if (!cascadeOnDelete && !cascadeOnUpdate) {
            con.Set(cod1.osp[0]);
         }
         Tester.EnsureOccurrences(cod1.osp[0], 1);
         con.Commit();
         Tester.EnsureOccurrences(cod1.osp[0], 1);
      }
   }
}