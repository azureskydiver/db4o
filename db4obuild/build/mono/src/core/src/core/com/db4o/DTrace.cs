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

   public class DTrace {
      public static bool enabled = false;
      private bool _enabled;
      private bool _break;
      private bool _log;
      private String _tag;
      private static long[] rangeStart;
      private static long[] rangeEnd;
      private static int rangeCount;
      public static DTrace BIND;
      public static DTrace CLOSE;
      public static DTrace COMMIT;
      public static DTrace CONTINUESET;
      public static DTrace FREE;
      public static DTrace FREE_ON_COMMIT;
      public static DTrace FREE_ON_ROLLBACK;
      public static DTrace GET_SLOT;
      public static DTrace NEW_INSTANCE;
      public static DTrace READ_ID;
      public static DTrace READ_SLOT;
      public static DTrace REFERENCE_REMOVED;
      public static DTrace REGULAR_SEEK;
      public static DTrace REMOVE_FROM_CLASS_INDEX;
      public static DTrace TRANS_COMMIT;
      public static DTrace TRANS_DONT_DELETE;
      public static DTrace TRANS_DELETE;
      public static DTrace WRITE_BYTES;
      public static DTrace WRITE_UPDATE_DELETE_MEMBERS;
      private static Object forInit = init();
      private static DTrace[] all;
      private static int current;
      
      private static void breakPoint() {
         bool xbool1 = true;
      }
      
      private static Object init() {
         return null;
      }
      
      internal DTrace(bool xbool, bool bool_0_, String xstring, bool bool_1_) : base() {
      }
      
      public void log() {
      }
      
      public void log(long l) {
      }
      
      public void logInfo(String xstring) {
      }
      
      public void log(long l, String xstring) {
      }
      
      public void logLength(long l, long l_2_) {
      }
      
      public void logEnd(long l, long l_3_) {
      }
      
      public void logEnd(long l, long l_4_, String xstring) {
      }
      
      public static void addRange(long l) {
      }
      
      public static void addRangeWithLength(long l, long l_5_) {
      }
      
      public static void addRangeWithEnd(long l, long l_6_) {
      }
      
      private String formatInt(long l) {
         return null;
      }
      
      private static void turnAllOffExceptFor(DTrace[] dtraces) {
      }
   }
}