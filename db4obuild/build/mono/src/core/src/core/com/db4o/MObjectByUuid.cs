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

   internal class MObjectByUuid : MsgD {
      
      internal MObjectByUuid() : base() {
      }
      
      internal override bool processMessageAtServer(YapSocket yapsocket) {
         long l1 = this.readLong();
         byte[] xis1 = this.readBytes();
         int i1 = 0;
         YapStream yapstream1 = this.getStream();
         Transaction transaction1 = this.getTransaction();
         lock (yapstream1.i_lock) {
            try {
               {
                  Object[] objs1 = transaction1.objectAndYapObjectBySignature(l1, xis1);
                  if (objs1[1] != null) {
                     YapObject yapobject1 = (YapObject)objs1[1];
                     i1 = yapobject1.getID();
                  }
               }
            }  catch (Exception exception) {
               {
               }
            }
         }
         Msg.OBJECT_BY_UUID.getWriterForInt(transaction1, i1).write(yapstream1, yapsocket);
         return true;
      }
   }
}