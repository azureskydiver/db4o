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

   abstract internal class Debug {
      
      internal Debug() : base() {
      }
      public static bool atHome = false;
      public static bool indexAllFields = false;
      public static bool configureAllClasses = false;
      public static bool configureAllFields = false;
      public static bool toStrings = false;
      public static bool weakReferences = true;
      public static bool arrayTypes = true;
      public static bool verbose = false;
      public static bool fakeServer = false;
      static internal bool messages = false;
      public static bool nio = true;
      static internal bool lockFile = true;
      static internal bool longTimeOuts = false;
      static internal YapFile serverStream;
      static internal YapClient clientStream;
      static internal Queue4 clientMessageQueue;
      static internal Lock4 clientMessageQueueLock;
      
      public static void ensureLock(Object obj) {
      }
      
      public static bool exceedsMaximumBlockSize(int i) {
         if (i > 70000000) return true;
         return false;
      }
      
      public static bool exceedsMaximumArrayEntries(int i, bool xbool) {
         if (i > (xbool ? 700000000 : 7000000)) return true;
         return false;
      }
   }
}