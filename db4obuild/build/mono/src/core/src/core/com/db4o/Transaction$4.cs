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

   internal class Transaction__4 : Visitor4 {
      private bool val__a_add;
      private Collection4 val__a_indices;
      private Transaction stathis0;
      
      internal Transaction__4(Transaction transaction, bool xbool, Collection4 collection4) : base() {
         stathis0 = transaction;
         val__a_add = xbool;
         val__a_indices = collection4;
      }
      
      public void visit(Object obj) {
         TreeIntObject treeintobject1 = (TreeIntObject)obj;
         YapClass yapclass1 = stathis0.i_stream.getYapClass(treeintobject1.i_key);
         ClassIndex classindex1 = yapclass1.getIndex();
         if (treeintobject1.i_object != null) {
            Object obj_0_1 = null;
            Visitor4 visitor41;
            if (val__a_add) visitor41 = new Transaction__5(this, classindex1); else visitor41 = new Transaction__6(this, classindex1);
            ((Tree)treeintobject1.i_object).traverse(visitor41);
            if (!val__a_indices.containsByIdentity(classindex1)) val__a_indices.add(classindex1);
         }
      }
      
      static internal Transaction access__000(Transaction__4 var_4) {
         return var_4.stathis0;
      }
   }
}