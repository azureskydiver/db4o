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

   internal class ClassIndexClient : ClassIndex {
      private YapClass i_yapClass;
      
      internal ClassIndexClient(YapClass yapclass) : base() {
         i_yapClass = yapclass;
      }
      
      internal override void add(int i) {
         throw YapConst.virtualException();
      }
      
      internal override long[] getInternalIDs(Transaction transaction, int i) {
         YapClient yapclient1 = (YapClient)i_yapClass.getStream();
         yapclient1.writeMsg(Msg.GET_INTERNAL_IDS.getWriterForInt(transaction, i));
         YapWriter yapwriter1 = yapclient1.expectedByteResponse(Msg.ID_LIST);
         int i_0_1 = yapwriter1.readInt();
         long[] ls1 = new long[i_0_1];
         for (int i_1_1 = 0; i_1_1 < i_0_1; i_1_1++) ls1[i_1_1] = (long)yapwriter1.readInt();
         return ls1;
      }
      
      internal override void read(Transaction transaction) {
      }
      
      internal override void setDirty(YapStream yapstream) {
      }
      
      internal override void setID(YapStream yapstream, int i) {
      }
      
      internal void write(YapStream yapstream) {
      }
      
      internal override void writeOwnID(YapWriter yapwriter) {
         yapwriter.writeInt(0);
      }
   }
}