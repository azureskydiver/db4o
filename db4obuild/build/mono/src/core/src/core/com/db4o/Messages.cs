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
namespace com.db4o {

   public class Messages {
      
      public Messages() : base() {
      }
      private static String[] i_messages;
      
      public static String get(int i) {
         return get(i, null);
      }
      
      public static String get(int i, String xstring) {
         if (i < 0) return xstring;
         load();
         if (i_messages == null || i > i_messages.Length - 1) return "msg[" + i + "]";
         String string_0_1 = i_messages[i];
         if (xstring != null) {
            int i_1_1 = string_0_1.IndexOf("%", 0);
            if (i_1_1 > -1) string_0_1 = string_0_1.Substring(0, i_1_1) + "\'" + xstring + "\'" + string_0_1.Substring(i_1_1 + 1);
         }
         return string_0_1;
      }
      
      private static void load() {
         if (i_messages == null) i_messages = new String[]{
            "",
"blocksize should be between 1 and 127",
"% close request",
"% closed",
"Exception opening %",
"% opened O.K.",
"Class %: Instantiation failed. \n Check custom ObjectConstructor code.",
"Class %: Instantiation failed.\n Add a constructor for use with db4o, ideally with zero arguments.",
"renaming %",
"rename not possible. % already exists",
"rename failed",
"File close failed.",
"File % not available for readwrite access.",
"File read access failed.",
"File not found: % Creating new file",
"Creation of file failed: %",
"File write failed.",
"File format incompatible.",
"Uncaught Exception. Engine closed.",
"writing log for %",
"% is closed. close() was called or open() failed.",
"Filename not specified.",
"The database file is locked by another process.",
"Class not available: %. Check CLASSPATH settings.",
"finalized while performing a task.\n DO NOT USE CTRL + C OR System.exit() TO STOP THE ENGINE.",
"Please mail the following to info@db4o.com:\n <db4o stacktrace>",
"</db4o stacktrace>",
"Creation of lock file failed: %",
"Previous session was not shut down correctly",
"This feature is not available in this version.",
"Could not open port: %",
"Server listening on port: %",
"Client % connected.",
"Client % timed out and closed.",
"Connection closed by client %.",
"Connection closed by server. %.",
"% connected to server.",
"The directory % can neither be found nor created.",
"This blob was never stored.",
"Blob file % not available.",
"Failure finding blob filename.",
"File does not exist %.",
"Failed to connect to server.",
"No blob data stored.",
"Uncaught Exception. db4o engine closed.",
"Add a public zero-parameter constructor to class %.",
"This method can only be called before opening the database file.",
"AccessibleObject#setAccessible() is not available. Private fields can not be stored.",
"ObjectTranslator could not be installed: %.",
"",
"% closed by ShutdownHook.",
"This database file has already been used with a different trial version.\n Please use an unrestricted version for productive use.\n Unrestricted versions are available for members of the db4o developer network.\n\n",
"",
"Call Db4o.licensedTo(key) to initialize this licensed db4o database engine.",
"This is a db4o trial version. Licensed versions can be obtained from www.db4o.com.",
"Thread interrupted.",
"Password can not be null.",
"Classes does not match.",
"rename() needs to be executed on the server.",
"Primitive types like % can not be stored directly. Store and retrieve them in wrapper objects.",
"Backups can not be run from clients and memory files.",
"Backup in progress.",
"Only use persisted first class objects as keys for IdentityHashMap."         };
      }
   }
}