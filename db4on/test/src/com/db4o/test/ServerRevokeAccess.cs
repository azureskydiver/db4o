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
      
      public void test() {
         if (!Test.isClientServer() && Test.currentRunner.CLIENT_SERVER) {
            try {
               {
                  new File(FILE).delete();
                  ObjectServer server1 = Db4o.openServer(FILE, AllTests.SERVER_PORT);
                  String user1 = "hohohi";
                  String password1 = "hohoho";
                  server1.grantAccess(user1, password1);
                  ObjectContainer con1 = Db4o.openClient(AllTests.SERVER_HOSTNAME, AllTests.SERVER_PORT, user1, password1);
                  Test.ensure(con1 != null);
                  con1.close();
                  server1.ext().revokeAccess(user1);
                  bool exceptionThrown1 = false;
                  try {
                     {
                        con1 = Db4o.openClient(AllTests.SERVER_HOSTNAME, AllTests.SERVER_PORT, user1, password1);
                     }
                  }  catch (Exception e) {
                     {
                        exceptionThrown1 = true;
                     }
                  }
                  Test.ensure(exceptionThrown1);
                  server1.close();
               }
            }  catch (Exception e) {
               {
                  j4o.lang.JavaSystem.printStackTrace(e);
               }
            }
         }
      }
   }
}