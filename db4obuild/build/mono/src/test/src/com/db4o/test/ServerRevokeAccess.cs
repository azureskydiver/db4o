/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public
License along with this program; if not, write to the Free
Software Foundation, Inc., 59 Temple Place - Suite 330, Boston,
MA  02111-1307, USA. */

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