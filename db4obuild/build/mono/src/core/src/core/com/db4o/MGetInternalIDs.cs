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

   internal class MGetInternalIDs : MsgD {
      
      internal MGetInternalIDs() : base() {
      }
      
      internal override bool processMessageAtServer(YapSocket yapsocket) {
         YapWriter yapwriter1 = this.getByteLoad();
         YapStream yapstream1 = this.getStream();
         long[] ls1;
         lock (yapstream1.i_lock) {
            try {
               {
                  ls1 = yapstream1.getYapClass(yapwriter1.readInt()).getIDs(this.getTransaction());
               }
            }  catch (Exception exception) {
               {
                  ls1 = new long[0];
               }
            }
         }
         int i1 = ls1.Length;
         MsgD msgd1 = Msg.ID_LIST.getWriterForLength(this.getTransaction(), 4 * (i1 + 1));
         YapWriter yapwriter_0_1 = msgd1.getPayLoad();
         yapwriter_0_1.writeInt(i1);
         for (int i_1_1 = 0; i_1_1 < i1; i_1_1++) yapwriter_0_1.writeInt((int)ls1[i_1_1]);
         msgd1.write(yapstream1, yapsocket);
         return true;
      }
   }
}