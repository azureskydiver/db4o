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

   internal class YapClient__1 : Closure4 {
      private YapClient stathis0;
      
      internal YapClient__1(YapClient yapclient) : base() {
         stathis0 = yapclient;
      }
      
      public Object run() {
         Object obj1 = null;
         Msg msg1 = (Msg)stathis0.messageQueue.next();
         if (msg1 != null) return msg1;
         if (stathis0.readerThread.isClosed()) Db4o.throwRuntimeException(20, stathis0.name());
         stathis0.messageQueueLock.snooze((long)stathis0.i_config.i_timeoutClientSocket);
         if (stathis0.readerThread.isClosed()) Db4o.throwRuntimeException(20, stathis0.name());
         msg1 = (Msg)stathis0.messageQueue.next();
         return msg1;
      }
   }
}