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

   internal class P1ListElement : P1Object {
      public P1ListElement i_next;
      public Object i_object;
      
      public P1ListElement() : base() {
      }
      
      public P1ListElement(Transaction transaction, P1ListElement p1listelement_0_, Object obj) : base(transaction) {
         i_next = p1listelement_0_;
         i_object = obj;
      }
      
      public override int adjustReadDepth(int i) {
         if (i >= 1) return 1;
         return 0;
      }
      
      internal Object activatedObject(int i) {
         this.checkActive();
         this.activate(i_object, i);
         return i_object;
      }
      
      public override Object createDefault(Transaction transaction) {
         P1ListElement p1listelement_1_1 = new P1ListElement();
         p1listelement_1_1.setTrans(transaction);
         return p1listelement_1_1;
      }
      
      internal virtual void delete(bool xbool) {
         if (xbool) this.delete(i_object);
         this.delete();
      }
   }
}