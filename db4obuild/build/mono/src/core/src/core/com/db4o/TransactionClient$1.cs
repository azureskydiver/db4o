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

   internal class TransactionClient__1 : Visitor4 {
      private TransactionClient stathis0;
      
      internal TransactionClient__1(TransactionClient transactionclient) : base() {
         stathis0 = transactionclient;
      }
      
      public void visit(Object obj) {
         TreeIntObject treeintobject1 = (TreeIntObject)obj;
         if (treeintobject1.i_object != null) {
            Object[] objs1 = (Object[])treeintobject1.i_object;
            YapObject yapobject1 = (YapObject)objs1[0];
            TransactionClient.access__002(stathis0, Tree.add(TransactionClient.access__000(stathis0), new TreeIntObject(yapobject1.getID(), yapobject1)));
         }
      }
   }
}