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
using com.db4o.config;
namespace com.db4o {

   public class Unobfuscated {
      
      public Unobfuscated() : base() {
      }
      static internal Object random;
      
      static internal bool createDb4oList(Object obj) {
         ((YapStream)obj).checkClosed();
         return !((YapStream)obj).isInstantiating();
      }
      
      public static byte[] generateSignature() {
         YapWriter yapwriter1 = new YapWriter(null, 300);
         YLong.writeLong(j4o.lang.JavaSystem.currentTimeMillis(), yapwriter1);
         YLong.writeLong(randomLong(), yapwriter1);
         YLong.writeLong(randomLong() + 1L, yapwriter1);
         return yapwriter1.getWrittenBytes();
      }
      
      static internal void logErr(Configuration configuration, int i, String xstring, Exception throwable) {
         Db4o.logErr(configuration, i, xstring, throwable);
      }
      
      static internal void purgeUnsychronized(Object obj, Object obj_0_) {
         ((YapStream)obj).purge1(obj_0_);
      }
      
      public static long randomLong() {
         return j4o.lang.JavaSystem.currentTimeMillis();
      }
      
      static internal void shutDownHookCallback(Object obj) {
         ((YapStream)obj).failedToShutDown();
      }
   }
}