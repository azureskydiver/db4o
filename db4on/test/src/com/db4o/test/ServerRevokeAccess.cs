/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

using System;
using j4o.lang;
using j4o.io;
using com.db4o;
namespace com.db4o.test {

   public class ServerRevokeAccess {
      
      public ServerRevokeAccess() : base() {
      }
      static internal String FILE = "ServerRevokeAccessTest.yap";
      
      public void Test() {
         if (!Tester.IsClientServer() && Tester.currentRunner.CLIENT_SERVER) {
            try {
               {
                  new File(FILE).Delete();
                  ObjectServer server1 = Db4o.OpenServer(FILE, AllTests.SERVER_PORT);
                  String user1 = "hohohi";
                  String password1 = "hohoho";
                  server1.GrantAccess(user1, password1);
                  ObjectContainer con1 = Db4o.OpenClient(AllTests.SERVER_HOSTNAME, AllTests.SERVER_PORT, user1, password1);
                  Tester.Ensure(con1 != null);
                  con1.Close();
                  server1.Ext().RevokeAccess(user1);
                  bool exceptionThrown1 = false;
                  try {
                     {
                        con1 = Db4o.OpenClient(AllTests.SERVER_HOSTNAME, AllTests.SERVER_PORT, user1, password1);
                     }
                  }  catch (Exception e) {
                     {
                        exceptionThrown1 = true;
                     }
                  }
                  Tester.Ensure(exceptionThrown1);
                  server1.Close();
               }
            }  catch (Exception e) {
               {
                  j4o.lang.JavaSystem.PrintStackTrace(e);
               }
            }
         }
      }
   }
}