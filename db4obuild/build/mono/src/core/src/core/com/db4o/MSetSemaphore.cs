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

   internal class MSetSemaphore : MsgD {
      
      internal MSetSemaphore() : base() {
      }
      
      internal override bool processMessageAtServer(YapSocket yapsocket) {
         int i1 = this.readInt();
         String xstring1 = this.readString();
         YapFile yapfile1 = (YapFile)this.getStream();
         bool xbool1 = yapfile1.setSemaphore(this.getTransaction(), xstring1, i1);
         if (xbool1) Msg.SUCCESS.write(yapfile1, yapsocket); else Msg.FAILED.write(yapfile1, yapsocket);
         return true;
      }
   }
}