/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o;
using com.db4o.ext;
namespace com.db4o.test {

   public class Semaphores : AllTestsConfAll {
      
      public Semaphores() : base() {
      }
      
      public void test() {
         ExtObjectContainer eoc1 = Test.objectContainer();
         eoc1.setSemaphore("SEM", 0);
         Test.ensure(eoc1.setSemaphore("SEM", 0) == true);
         if (Test.clientServer) {
            ExtObjectContainer client21 = null;
            try {
               {
                  client21 = Db4o.openClient(SERVER_HOSTNAME, SERVER_PORT, DB4O_USER, DB4O_PASSWORD).ext();
                  Test.ensure(client21.setSemaphore("SEM", 0) == false);
                  eoc1.releaseSemaphore("SEM");
                  Test.ensure(client21.setSemaphore("SEM", 0) == true);
               }
            }  catch (Exception e) {
               {
                  j4o.lang.JavaSystem.printStackTrace(e);
                  return;
               }
            }
         } else {
            eoc1.releaseSemaphore("SEM");
         }
      }
   }
}