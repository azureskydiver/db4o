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
      
      public void store() {
         Tester.deleteAllInstances(this);
         for (int i1 = 0; i1 < COUNT; i1++) {
            Tester.store(new ObjectSetIDs());
         }
      }
      
      public void test() {
         ExtObjectContainer con1 = Tester.objectContainer();
         Query q1 = Tester.query();
         q1.constrain(j4o.lang.Class.getClassForObject(this));
         ObjectSet res1 = q1.execute();
         long[] ids11 = new long[res1.size()];
         int i1 = 0;
         while (res1.hasNext()) {
            ids11[i1++] = con1.getID(res1.next());
         }
         res1.reset();
         long[] ids21 = res1.ext().getIDs();
         Tester.ensure(ids11.Length == COUNT);
         Tester.ensure(ids21.Length == COUNT);
         for (int j1 = 0; j1 < ids11.Length; j1++) {
            bool found1 = false;
            for (int k1 = 0; k1 < ids21.Length; k1++) {
               if (ids11[j1] == ids21[k1]) {
                  found1 = true;
                  break;
               }
            }
            Tester.ensure(found1);
         }
      }
   }
}