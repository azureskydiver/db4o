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
using com.db4o.reflect;
namespace com.db4o {

   public class Db4o {
      
      public Db4o() : base() {
      }
      static internal Config4Impl i_config = new Config4Impl();
      private static Sessions i_sessions = new Sessions();
      static internal Object Lock = initialize();
      static internal String licTo = "";
      private static bool expirationMessagePrinted;
      
      private static Object initialize() {
         Platform.getDefaultConfiguration(i_config);
         return new Object();
      }
      
      public static void Main(String[] strings) {
         j4o.lang.JavaSystem._out.println(version());
      }
      
      public static Class classForName(String xstring) {
         return classForName(null, xstring);
      }
      
      static internal Class classForName(YapStream yapstream, String xstring) {
         try {
            {
               Config4Impl config4impl1 = yapstream == null ? i_config : yapstream.i_config;
               if (config4impl1.i_classLoader != null) return config4impl1.i_classLoader.loadClass(xstring);
               return Class.forName(xstring);
            }
         }  catch (Exception throwable) {
            {
               return null;
            }
         }
      }
      
      public static Configuration configure() {
         return i_config;
      }
      
      public static void licensedTo(String xstring) {
      }
      
      static internal void logErr(Configuration configuration, int i, String xstring, Exception throwable) {
         if (configuration == null) configuration = i_config;
         PrintStream printstream1 = ((Config4Impl)configuration).errStream();
         new Message(xstring, i, printstream1);
         if (throwable != null) {
            new Message(null, 25, printstream1);
            j4o.lang.JavaSystem.printStackTrace(throwable, printstream1);
            new Message(null, 26, printstream1, false);
         }
      }
      
      static internal void logMsg(Configuration configuration, int i, String xstring) {
         Config4Impl config4impl1 = (Config4Impl)configuration;
         if (config4impl1 == null) config4impl1 = i_config;
         if (config4impl1.i_messageLevel > 0) new Message(xstring, i, config4impl1.outStream());
      }
      
      static internal void notAvailable() {
         throwRuntimeException(29);
      }
      
      public static ObjectContainer openClient(String xstring, int i, String string_0_, String string_1_) {
         lock (Lock) {
            return new YapClient(new YapSocket(xstring, i), string_0_, string_1_, true);
         }
      }
      
      public static ObjectContainer openFile(String xstring) {
         lock (Lock) {
            return i_sessions.open(xstring);
         }
      }
      
      protected static ObjectContainer openMemoryFile1(MemoryFile memoryfile) {
         lock (Lock) {
            if (memoryfile == null) memoryfile = new MemoryFile();
            Object obj1 = null;
            YapMemoryFile yapmemoryfile1;
            try {
               {
                  yapmemoryfile1 = new YapMemoryFile(memoryfile);
               }
            }  catch (Exception throwable) {
               {
                  logErr(i_config, 4, "Memory File", throwable);
                  return null;
               }
            }
            if (yapmemoryfile1 != null) {
               Platform.postOpen(yapmemoryfile1);
               logMsg(i_config, 5, "Memory File");
            }
            return yapmemoryfile1;
         }
      }
      
      public static ObjectServer openServer(String xstring, int i) {
         lock (Lock) {
            ObjectContainer objectcontainer1 = openFile(xstring);
            if (objectcontainer1 != null) return new YapServer((YapFile)objectcontainer1, i);
            return null;
         }
      }
      
      static internal IReflect reflector() {
         return i_config.reflector();
      }
      
      static internal void forEachSession(Visitor4 visitor4) {
         i_sessions.forEach(visitor4);
      }
      
      static internal void sessionStopped(Session session) {
         i_sessions.remove(session);
      }
      
      static internal void throwRuntimeException(int i) {
         throwRuntimeException(i, null, null);
      }
      
      static internal void throwRuntimeException(int i, Exception throwable) {
         throwRuntimeException(i, null, throwable);
      }
      
      static internal void throwRuntimeException(int i, String xstring) {
         throwRuntimeException(i, xstring, null);
      }
      
      static internal void throwRuntimeException(int i, String xstring, Exception throwable) {
         logErr(i_config, i, xstring, throwable);
         throw new RuntimeException(Messages.get(i, xstring));
      }
      
      public static String version() {
         return "db4o " + Db4oVersion.name;
      }
   }
}