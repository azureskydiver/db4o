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

   internal class Transaction__2 : Visitor4 {
      private Transaction val__finalThis;
      private byte[] val__a_signature;
      private Object[] val__ret;
      private Transaction stathis0;
      
      internal Transaction__2(Transaction transaction, Transaction transaction_0_, byte[] xis, Object[] objs) : base() {
         stathis0 = transaction;
         val__finalThis = transaction_0_;
         val__a_signature = xis;
         val__ret = objs;
      }
      
      public void visit(Object obj) {
         QCandidate qcandidate1 = (QCandidate)obj;
         Object[] objs1 = val__finalThis.i_stream.getObjectAndYapObjectByID(val__finalThis, qcandidate1.i_key);
         if (objs1[1] != null) {
            YapObject yapobject1 = (YapObject)objs1[1];
            VirtualAttributes virtualattributes1 = yapobject1.virtualAttributes(val__finalThis);
            byte[] xis1 = virtualattributes1.i_database.i_signature;
            bool xbool1 = true;
            if (val__a_signature.Length == xis1.Length) {
               for (int i1 = 0; i1 < val__a_signature.Length; i1++) {
                  if (val__a_signature[i1] != xis1[i1]) {
                     xbool1 = false;
                     break;
                  }
               }
            } else xbool1 = false;
            if (xbool1) {
               val__ret[0] = objs1[0];
               val__ret[1] = objs1[1];
            }
         }
      }
   }
}