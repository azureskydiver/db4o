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
using com.db4o.config;
using com.db4o.ext;
using com.db4o.foundation;
namespace com.db4o {

   internal class YapServer : ObjectServer, ExtObjectServer, Runnable {
      private String i_name;
      private YapServerSocket i_serverSocket;
      private int i_threadIDGen = 1;
      private Collection4 i_threads = new Collection4();
      private YapFile i_yapFile;
      
      internal YapServer(YapFile yapfile, int i) : base() {
         yapfile.setServer(true);
         i_name = "db4o ServerSocket  FILE: " + yapfile.ToString() + "  PORT:" + i;
         i_yapFile = yapfile;
         Config4Impl config4impl1 = (Config4Impl)i_yapFile.configure();
         config4impl1.callbacks(false);
         config4impl1.i_isServer = true;
         yapfile.getYapClass(YapConst.CLASS_STATICCLASS, true);
         config4impl1.i_exceptionalClasses.forEachValue(new YapServer__1(this, yapfile));
         if (config4impl1.i_messageLevel == 0) config4impl1.i_messageLevel = 1;
         if (i > 0) {
            try {
               {
                  i_serverSocket = new YapServerSocket(i);
                  i_serverSocket.setSoTimeout(config4impl1.i_timeoutServerSocket);
               }
            }  catch (IOException ioexception) {
               {
                  Db4o.throwRuntimeException(30, "" + i);
               }
            }
            new Thread(this).start();
            lock (this) {
               try {
                  {
                     j4o.lang.JavaSystem.wait(this, 1000L);
                  }
               }  catch (Exception exception) {
                  {
                  }
               }
            }
         }
      }
      
      public void backup(String xstring) {
         i_yapFile.backup(xstring);
      }
      
      internal void checkClosed() {
         if (i_yapFile == null) Db4o.throwRuntimeException(20, i_name);
         i_yapFile.checkClosed();
      }
      
      public bool close() {
         lock (Db4o.Lock) {
            Cool.sleepIgnoringInterruption(100L);
            try {
               {
                  if (i_serverSocket != null) i_serverSocket.close();
               }
            }  catch (Exception exception) {
               {
               }
            }
            i_serverSocket = null;
            bool xbool1 = i_yapFile == null ? true : i_yapFile.close();
            lock (i_threads) {
               Iterator4 iterator41 = i_threads.iterator();
               while (iterator41.hasNext()) ((YapServerThread)iterator41.next()).close();
            }
            i_yapFile = null;
            return xbool1;
         }
      }
      
      public Configuration configure() {
         return i_yapFile.configure();
      }
      
      public ExtObjectServer ext() {
         return this;
      }
      
      internal YapServerThread findThread(int i) {
         lock (i_threads) {
            Iterator4 iterator41 = i_threads.iterator();
            while (iterator41.hasNext()) {
               YapServerThread yapserverthread1 = (YapServerThread)iterator41.next();
               if (yapserverthread1.i_threadID == i) return yapserverthread1;
            }
         }
         return null;
      }
      
      public void grantAccess(String xstring, String string_0_) {
         lock (i_yapFile.i_lock) {
            checkClosed();
            User user1 = new User();
            user1.name = xstring;
            i_yapFile.showInternalClasses(true);
            User user_1_1 = (User)i_yapFile.get(user1).next();
            if (user_1_1 != null) {
               user_1_1.password = string_0_;
               i_yapFile.set(user_1_1);
            } else {
               user1.password = string_0_;
               i_yapFile.set(user1);
            }
            i_yapFile.commit();
            i_yapFile.showInternalClasses(false);
         }
      }
      
      public ObjectContainer objectContainer() {
         return i_yapFile;
      }
      
      public ObjectContainer openClient() {
         try {
            {
               return new YapClient(openFakeClientSocket(), "embedded client" + (i_threadIDGen - 1), "", false);
            }
         }  catch (IOException ioexception) {
            {
               j4o.lang.JavaSystem.printStackTrace(ioexception);
               return null;
            }
         }
      }
      
      internal YapSocketFake openFakeClientSocket() {
         YapSocketFake yapsocketfake1 = new YapSocketFake(this);
         YapSocketFake yapsocketfake_2_1 = new YapSocketFake(this, yapsocketfake1);
         try {
            {
               YapServerThread yapserverthread1 = new YapServerThread(this, i_yapFile, yapsocketfake_2_1, i_threadIDGen++, true);
               lock (i_threads) {
                  i_threads.add(yapserverthread1);
               }
               yapserverthread1.start();
               return yapsocketfake1;
            }
         }  catch (Exception exception) {
            {
               j4o.lang.JavaSystem.printStackTrace(exception);
               return null;
            }
         }
      }
      
      internal void removeThread(YapServerThread yapserverthread) {
         lock (i_threads) {
            i_threads.remove(yapserverthread);
         }
      }
      
      public void revokeAccess(String xstring) {
         lock (i_yapFile.i_lock) {
            i_yapFile.showInternalClasses(true);
            checkClosed();
            User user1 = new User();
            user1.name = xstring;
            ObjectSet objectset1 = i_yapFile.get(user1);
            while (objectset1.hasNext()) i_yapFile.delete(objectset1.next());
            i_yapFile.commit();
            i_yapFile.showInternalClasses(false);
         }
      }
      
      public void run() {
         Thread.currentThread().setName(i_name);
         i_yapFile.logMsg(31, "" + i_serverSocket.getLocalPort());
         lock (this) {
            j4o.lang.JavaSystem.notify(this);
         }
         while (i_serverSocket != null) {
            try {
               {
                  YapServerThread yapserverthread1 = new YapServerThread(this, i_yapFile, i_serverSocket.accept(), i_threadIDGen++, false);
                  lock (i_threads) {
                     i_threads.add(yapserverthread1);
                  }
                  yapserverthread1.start();
               }
            }  catch (Exception exception) {
               {
               }
            }
         }
      }
   }
}