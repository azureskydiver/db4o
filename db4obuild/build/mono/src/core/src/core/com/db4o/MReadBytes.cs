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

   internal class MReadBytes : MsgD {
      
      internal MReadBytes() : base() {
      }
      
      internal override YapWriter getByteLoad() {
         int i1 = payLoad.readInt();
         int i_0_1 = payLoad.getLength() - 4;
         payLoad.removeFirstBytes(4);
         payLoad.useSlot(i1, i_0_1);
         return payLoad;
      }
      
      internal override MsgD getWriter(YapWriter yapwriter) {
         MsgD msgd1 = this.getWriterForLength(yapwriter.getTransaction(), yapwriter.getLength() + 4);
         msgd1.payLoad.writeInt(yapwriter.getAddress());
         msgd1.payLoad.append(yapwriter._buffer);
         return msgd1;
      }
      
      internal override bool processMessageAtServer(YapSocket yapsocket) {
         YapStream yapstream1 = this.getStream();
         int i1 = this.readInt();
         int i_1_1 = this.readInt();
         lock (yapstream1.i_lock) {
            YapWriter yapwriter1 = new YapWriter(this.getTransaction(), i1, i_1_1);
            try {
               {
                  yapstream1.readBytes(yapwriter1._buffer, i1, i_1_1);
                  getWriter(yapwriter1).write(yapstream1, yapsocket);
               }
            }  catch (Exception exception) {
               {
                  Msg.NULL.write(yapstream1, yapsocket);
               }
            }
         }
         return true;
      }
   }
}