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

   internal class TransactionObjectCarrier : Transaction {
      
      internal TransactionObjectCarrier(YapStream yapstream, Transaction transaction) : base(yapstream, transaction) {
      }
      
      internal override void commit() {
      }
      
      internal override void freeOnRollback(int i, int i_0_, int i_1_) {
      }
      
      internal override void setPointer(int i, int i_2_, int i_3_) {
         this.writePointer(i, i_2_, i_3_);
      }
   }
}