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

   internal class IxField__2 : Visitor4 {
      private IxFieldTransaction val__ft;
      private Tree[] val__tree;
      private IxField stathis0;
      
      internal IxField__2(IxField ixfield, IxFieldTransaction ixfieldtransaction, Tree[] trees) : base() {
         stathis0 = ixfield;
         val__ft = ixfieldtransaction;
         val__tree = trees;
      }
      
      public void visit(Object obj) {
         IxTree ixtree1 = (IxTree)obj;
         if (ixtree1.i_version == val__ft.i_version && !(ixtree1 is IxFileRange)) {
            ixtree1.beginMerge();
            val__tree[0] = val__tree[0].add(ixtree1);
         }
      }
   }
}