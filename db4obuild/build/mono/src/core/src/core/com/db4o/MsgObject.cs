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

   internal class MsgObject : MsgD {
      
      internal MsgObject() : base() {
      }
      private static int LENGTH_FOR_ALL = 16;
      private static int LENGTH_FOR_FIRST = 16;
      internal int i_id;
      internal int i_address;
      
      internal MsgD getWriter(YapWriter yapwriter, int[] xis) {
         int i1 = yapwriter.getLength() + 16;
         if (xis != null) i1 += xis.Length * 4;
         int i_0_1 = yapwriter.embeddedCount();
         if (i_0_1 > 0) i1 += 16 * i_0_1 + yapwriter.embeddedLength();
         MsgD msgd1 = this.getWriterForLength(yapwriter.getTransaction(), i1);
         if (xis != null) {
            for (int i_1_1 = 0; i_1_1 < xis.Length; i_1_1++) msgd1.payLoad.writeInt(xis[i_1_1]);
         }
         msgd1.payLoad.writeInt(i_0_1);
         yapwriter.appendTo(msgd1.payLoad, -1);
         return msgd1;
      }
      
      internal override MsgD getWriter(YapWriter yapwriter) {
         return getWriter(yapwriter, null);
      }
      
      internal MsgD getWriter(YapClass yapclass, YapWriter yapwriter) {
         return getWriter(yapwriter, new int[]{
            yapclass.getID()         });
      }
      
      internal MsgD getWriter(YapClass yapclass, int i, YapWriter yapwriter) {
         return getWriter(yapwriter, new int[]{
            yapclass.getID(),
i         });
      }
      
      public YapWriter unmarshall() {
         return unmarshall(0);
      }
      
      public YapWriter unmarshall(int i) {
         payLoad.setTransaction(this.getTransaction());
         int i_2_1 = payLoad.readInt();
         int i_3_1 = payLoad.readInt();
         if (i_3_1 == 0) return null;
         i_id = payLoad.readInt();
         i_address = payLoad.readInt();
         if (i_2_1 == 0) payLoad.removeFirstBytes(16 + i); else {
            payLoad._offset += i_3_1;
            YapWriter[] yapwriters1 = new YapWriter[i_2_1 + 1];
            yapwriters1[0] = payLoad;
            new YapWriter(payLoad, yapwriters1, 1);
            payLoad.trim4(16 + i, i_3_1);
         }
         payLoad.useSlot(i_id, i_address, i_3_1);
         return payLoad;
      }
   }
}