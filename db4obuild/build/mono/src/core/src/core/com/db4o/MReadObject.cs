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

   internal class MReadObject : MsgD {
      
      internal MReadObject() : base() {
      }
      
      internal override bool processMessageAtServer(YapSocket yapsocket) {
         Object obj1 = null;
         YapStream yapstream1 = this.getStream();
         YapWriter yapwriter1;
         lock (yapstream1.i_lock) {
            try {
               {
                  yapwriter1 = yapstream1.readWriterByID(this.getTransaction(), payLoad.readInt());
               }
            }  catch (Exception exception) {
               {
                  yapwriter1 = null;
               }
            }
         }
         if (yapwriter1 == null) yapwriter1 = new YapWriter(this.getTransaction(), 0, 0);
         Msg.OBJECT_TO_CLIENT.getWriter(yapwriter1).write(yapstream1, yapsocket);
         return true;
      }
   }
}