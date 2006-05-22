/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.ext;
namespace com.db4o.test {

   public class Semaphores : AllTestsConfAll {
      
      public Semaphores() : base() {
      }
      
      public void Test() {
         ExtObjectContainer eoc1 = Tester.ObjectContainer();
         eoc1.SetSemaphore("SEM", 0);
         Tester.Ensure(eoc1.SetSemaphore("SEM", 0) == true);
         if (Tester.clientServer) {
            ExtObjectContainer client21 = null;
            try {
               {
                  client21 = Db4o.OpenClient(SERVER_HOSTNAME, SERVER_PORT, DB4O_USER, DB4O_PASSWORD).Ext();
                  Tester.Ensure(client21.SetSemaphore("SEM", 0) == false);
                  eoc1.ReleaseSemaphore("SEM");
                  Tester.Ensure(client21.SetSemaphore("SEM", 0) == true);
               }
            }  catch (Exception e) {
               {
                  j4o.lang.JavaSystem.PrintStackTrace(e);
                  return;
               }
            }
         } else {
            eoc1.ReleaseSemaphore("SEM");
         }
      }
   }
}