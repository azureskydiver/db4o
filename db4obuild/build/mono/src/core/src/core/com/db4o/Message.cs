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
namespace com.db4o {

   internal class Message {
      internal PrintStream stream;
      
      internal Message(YapStream yapstream, String xstring) : base() {
         stream = yapstream.i_config.outStream();
         print(xstring, true);
      }
      
      internal Message(String xstring, int i, PrintStream printstream, bool xbool) : base() {
         stream = printstream;
         print(Messages.get(i, xstring), xbool);
      }
      
      internal Message(String xstring, int i, PrintStream printstream) : this(xstring, i, printstream, true) {
      }
      
      private void print(String xstring, bool xbool) {
         if (stream != null) {
            if (xbool) stream.println("[" + Db4o.version() + "   " + YDate.now() + "] ");
            stream.println(" " + xstring);
         }
      }
   }
}