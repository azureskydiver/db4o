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

   internal class Transaction__1 : Visitor4 {
      private bool[] val__foundOne;
      private Transaction val__finalThis;
      private Transaction stathis0;
      
      internal Transaction__1(Transaction transaction, bool[] bools, Transaction transaction_0_) : base() {
         stathis0 = transaction;
         val__foundOne = bools;
         val__finalThis = transaction_0_;
      }
      
      public void visit(Object obj) {
         TreeIntObject treeintobject1 = (TreeIntObject)obj;
         if (treeintobject1.i_object != null) {
            Object[] objs1 = (Object[])treeintobject1.i_object;
            val__foundOne[0] = true;
            YapObject yapobject1 = (YapObject)objs1[0];
            int i1 = System.Convert.ToInt32((Int32)objs1[1]);
            Object obj_1_1 = yapobject1.getObject();
            if (obj_1_1 == null) {
               objs1 = val__finalThis.i_stream.getObjectAndYapObjectByID(val__finalThis, yapobject1.getID());
               obj_1_1 = objs1[0];
               yapobject1 = (YapObject)objs1[1];
            }
            stathis0.i_stream.delete4(val__finalThis, yapobject1, obj_1_1, i1);
         }
         stathis0.i_delete = Tree.add(stathis0.i_delete, new TreeIntObject(treeintobject1.i_key, null));
      }
   }
}