/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using com.db4o.ext;
namespace com.db4o.test {

   public class SetSemaphore {
      
      public SetSemaphore() : base() {
      }
      
      public void Test() {
         ExtObjectContainer client11 = Tester.ObjectContainer();
         Tester.Ensure(client11.SetSemaphore("hi", 0));
         Tester.Ensure(client11.SetSemaphore("hi", 0));
         if (Tester.clientServer) {
            ExtObjectContainer client21 = Tester.Open();
            ExtObjectContainer client31 = Tester.Open();
            ExtObjectContainer client41 = Tester.Open();
            ExtObjectContainer client51 = Tester.Open();
            Tester.Ensure(!client21.SetSemaphore("hi", 0));
            client11.ReleaseSemaphore("hi");
            Tester.Ensure(client21.SetSemaphore("hi", 30));
            Tester.Ensure(!client11.SetSemaphore("hi", 0));
            Tester.Ensure(!client31.SetSemaphore("hi", 0));
            new GetAndRelease(client31);
            new GetAndRelease(client21);
            new GetAndRelease(client11);
            new GetAndRelease(client41);
            new GetAndRelease(client51);
            try {
               {
                  Thread.Sleep(1000);
               }
            }  catch (Exception e) {
               {
                  j4o.lang.JavaSystem.PrintStackTrace(e);
               }
            }
            Tester.Ensure(client11.SetSemaphore("hi", 0));
            client11.Close();
            new GetAndRelease(client31);
            new GetAndRelease(client21);
            try {
               {
                  Thread.Sleep(1000);
               }
            }  catch (Exception e) {
               {
                  j4o.lang.JavaSystem.PrintStackTrace(e);
               }
            }
            client21.Close();
            client31.Close();
            client41.Close();
            client51.SetSemaphore("hi", 1000);
         }
      }
      
      internal class GetAndRelease : Runnable {
         internal ExtObjectContainer client;
         
         public GetAndRelease(ExtObjectContainer client) : base() {
            this.client = client;
            new Thread(this).Start();
         }
         
         public void Run() {
            long time1 = j4o.lang.JavaSystem.CurrentTimeMillis();
            if (!client.Ext().IsClosed())
            {
                try{
                    Tester.Ensure(client.SetSemaphore("hi", 50000));
                    time1 = j4o.lang.JavaSystem.CurrentTimeMillis() - time1;
                    Console.WriteLine("Time to get semaphore: " + time1);
                    Thread.Sleep(50);
                    Console.WriteLine("About to release semaphore.");
                    client.ReleaseSemaphore("hi");
                }
                catch (Exception e)
                {
                    if (!client.Ext().IsClosed())
                    {
                        j4o.lang.JavaSystem.PrintStackTrace(e);
                    }
                }
            }
         }
      }
   }
}