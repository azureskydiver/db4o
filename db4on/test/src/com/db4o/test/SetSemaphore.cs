/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o.ext;
namespace com.db4o.test {

   public class SetSemaphore {
      
      public SetSemaphore() : base() {
      }
      
      public void test() {
         ExtObjectContainer client11 = Test.objectContainer();
         Test.ensure(client11.setSemaphore("hi", 0));
         Test.ensure(client11.setSemaphore("hi", 0));
         if (Test.clientServer) {
            ExtObjectContainer client21 = Test.open();
            ExtObjectContainer client31 = Test.open();
            ExtObjectContainer client41 = Test.open();
            ExtObjectContainer client51 = Test.open();
            Test.ensure(!client21.setSemaphore("hi", 0));
            client11.releaseSemaphore("hi");
            Test.ensure(client21.setSemaphore("hi", 30));
            Test.ensure(!client11.setSemaphore("hi", 0));
            Test.ensure(!client31.setSemaphore("hi", 0));
            new GetAndRelease(client31);
            new GetAndRelease(client21);
            new GetAndRelease(client11);
            new GetAndRelease(client41);
            new GetAndRelease(client51);
            try {
               {
                  Thread.sleep(1000);
               }
            }  catch (Exception e) {
               {
                  j4o.lang.JavaSystem.printStackTrace(e);
               }
            }
            Test.ensure(client11.setSemaphore("hi", 0));
            client11.close();
            new GetAndRelease(client31);
            new GetAndRelease(client21);
            try {
               {
                  Thread.sleep(1000);
               }
            }  catch (Exception e) {
               {
                  j4o.lang.JavaSystem.printStackTrace(e);
               }
            }
            client21.close();
            client31.close();
            client41.close();
            client51.setSemaphore("hi", 1000);
         }
      }
      
      internal class GetAndRelease : Runnable {
         internal ExtObjectContainer client;
         
         public GetAndRelease(ExtObjectContainer client) : base() {
            this.client = client;
            new Thread(this).start();
         }
         
         public void run() {
            long time1 = j4o.lang.JavaSystem.currentTimeMillis();
            Test.ensure(client.setSemaphore("hi", 50000));
            time1 = j4o.lang.JavaSystem.currentTimeMillis() - time1;
            Console.WriteLine("Time to get semaphore: " + time1);
            try {
               {
                  Thread.sleep(50);
               }
            }  catch (Exception e) {
               {
                  j4o.lang.JavaSystem.printStackTrace(e);
               }
            }
            Console.WriteLine("About to release semaphore.");
            client.releaseSemaphore("hi");
         }
      }
   }
}