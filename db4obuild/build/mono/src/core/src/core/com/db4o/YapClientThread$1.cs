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

   internal class YapClientThread__1 : Closure4 {
      private Msg val__message;
      private YapClientThread stathis0;
      
      internal YapClientThread__1(YapClientThread yapclientthread, Msg msg) : base() {
         stathis0 = yapclientthread;
         val__message = msg;
      }
      
      public Object run() {
         stathis0.messageQueue.add(val__message);
         stathis0.messageQueueLock.awake();
         return null;
      }
   }
}