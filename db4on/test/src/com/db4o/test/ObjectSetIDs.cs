/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.ext;
using com.db4o.query;
namespace com.db4o.test {

   public class ObjectSetIDs {
      
      public ObjectSetIDs() : base() {
      }
      static internal int COUNT = 11;
      
      public void Store() {
         Tester.DeleteAllInstances(this);
         for (int i1 = 0; i1 < COUNT; i1++) {
            Tester.Store(new ObjectSetIDs());
         }
      }
      
      public void Test() {
         ExtObjectContainer con1 = Tester.ObjectContainer();
         Query q1 = Tester.Query();
         q1.Constrain(j4o.lang.Class.GetClassForObject(this));
         ObjectSet res1 = q1.Execute();
         long[] ids11 = new long[res1.Size()];
         int i1 = 0;
         while (res1.HasNext()) {
            ids11[i1++] = con1.GetID(res1.Next());
         }
         res1.Reset();
         long[] ids21 = res1.Ext().GetIDs();
         Tester.Ensure(ids11.Length == COUNT);
         Tester.Ensure(ids21.Length == COUNT);
         for (int j1 = 0; j1 < ids11.Length; j1++) {
            bool found1 = false;
            for (int k1 = 0; k1 < ids21.Length; k1++) {
               if (ids11[j1] == ids21[k1]) {
                  found1 = true;
                  break;
               }
            }
            Tester.Ensure(found1);
         }
      }
   }
}