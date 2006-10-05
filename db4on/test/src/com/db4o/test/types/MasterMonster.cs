/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
namespace com.db4o.test.types {

   public class MasterMonster: RTest {
      
      public MasterMonster() : base() {
      }
      public Object[] ooo;
      
      public override void Set(int ver) {
         Object[] classes = AllClassesButThis();
         ooo = new Object[classes.Length];
         for (int i = 0; i < classes.Length; i++) {
            try {
               {
                  RTestable test = (RTestable)classes[i];
               }
            }  catch (Exception e) {
               {
                  throw new Exception("MasterMonster instantiation failed.");
               }
            }
         }
      }
      
      internal Object[] AllClassesButThis() {
         Object[] all = Regression.AllClasses();
         Object[] classes = new Object[all.Length - 1];
		 System.Array.Copy(all, 0, classes, 0, all.Length - 1);
         return classes;
      }
   }
}