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

   internal class MQueryExecute : MsgObject {
      
      internal MQueryExecute() : base() {
      }
      
      internal override bool processMessageAtServer(YapSocket yapsocket) {
         Transaction transaction1 = this.getTransaction();
         YapStream yapstream1 = this.getStream();
         QResult qresult1 = new QResult(transaction1);
         this.unmarshall();
         QQuery qquery1 = (QQuery)yapstream1.unmarshall(payLoad);
         qquery1.unmarshall(this.getTransaction());
         lock (yapstream1.i_lock) {
            try {
               {
                  qquery1.execute2(qresult1);
               }
            }  catch (Exception exception) {
               {
                  qresult1 = new QResult(this.getTransaction());
               }
            }
         }
         this.writeQueryResult(this.getTransaction(), qresult1, yapsocket);
         return true;
      }
   }
}