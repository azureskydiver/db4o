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

   internal class MReadMultipleObjects : MsgD {
      
      internal MReadMultipleObjects() : base() {
      }
      
      internal override bool processMessageAtServer(YapSocket yapsocket) {
         int i1 = this.readInt();
         MsgD[] msgds1 = new MsgD[i1];
         int i_0_1 = (1 + i1) * 4;
         YapStream yapstream1 = this.getStream();
         Object obj1 = null;
         lock (yapstream1.i_lock) {
            for (int i_1_1 = 0; i_1_1 < i1; i_1_1++) {
               int i_2_1 = payLoad.readInt();
               YapWriter yapwriter1;
               try {
                  {
                     yapwriter1 = yapstream1.readWriterByID(this.getTransaction(), i_2_1);
                  }
               }  catch (Exception exception) {
                  {
                     yapwriter1 = null;
                  }
               }
               if (yapwriter1 != null) {
                  try {
                     {
                        YapClassAny.appendEmbedded(yapwriter1);
                     }
                  }  catch (Exception exception) {
                     {
                     }
                  }
                  msgds1[i_1_1] = Msg.OBJECT_TO_CLIENT.getWriter(yapwriter1);
                  i_0_1 += msgds1[i_1_1].payLoad.getLength();
               }
            }
         }
         MsgD msgd1 = Msg.READ_MULTIPLE_OBJECTS.getWriterForLength(this.getTransaction(), i_0_1);
         msgd1.writeInt(i1);
         for (int i_3_1 = 0; i_3_1 < i1; i_3_1++) {
            if (msgds1[i_3_1] == null) msgd1.writeInt(0); else {
               msgd1.writeInt(msgds1[i_3_1].payLoad.getLength());
               msgd1.payLoad.append(msgds1[i_3_1].payLoad._buffer);
            }
         }
         msgd1.write(yapstream1, yapsocket);
         return true;
      }
   }
}