/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.types {

   public class MasterMonster: RTest {
      
      public MasterMonster() : base() {
      }
      public Object[] ooo;
      
      public override void set(int ver) {
         Object[] classes = allClassesButThis();
         ooo = new Object[classes.Length];
         for (int i = 0; i < classes.Length; i++) {
            try {
               {
                  RTestable test = (RTestable)classes[i];
               }
            }  catch (Exception e) {
               {
                  throw new RuntimeException("MasterMonster instantiation failed.");
               }
            }
         }
      }
      
      internal Object[] allClassesButThis() {
         Object[] all = Regression.allClasses();
         Object[] classes = new Object[all.Length - 1];
         j4o.lang.JavaSystem.arraycopy(all, 0, classes, 0, all.Length - 1);
         return classes;
      }
   }
}