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

   internal class P1HashElement : P1ListElement {
      public Object i_key;
      public int i_hashCode;
      public int i_position;
      
      public P1HashElement() : base() {
      }
      
      public P1HashElement(Transaction transaction, P1ListElement p1listelement, Object obj, int i, Object obj_0_) : base(transaction, p1listelement, obj_0_) {
         i_hashCode = i;
         i_key = obj;
      }
      
      public override int adjustReadDepth(int i) {
         return 1;
      }
      
      internal Object activatedKey(int i) {
         this.checkActive();
         this.activate(i_key, i);
         return i_key;
      }
      
      internal override void delete(bool xbool) {
         if (xbool) this.delete(i_key);
         base.delete(xbool);
      }
   }
}