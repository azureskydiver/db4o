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

   abstract internal class IxPatch : IxTree {
      internal int i_parentID;
      internal Object i_value;
      internal Queue4 i_queue;
      
      internal IxPatch(IxFieldTransaction ixfieldtransaction, int i, Object obj) : base(ixfieldtransaction) {
         i_parentID = i;
         i_value = obj;
      }
      
      public override Tree add(Tree tree) {
         int i1 = compare(tree);
         if (i1 == 0) {
            IxPatch ixpatch_0_1 = (IxPatch)tree;
            i1 = i_parentID - ixpatch_0_1.i_parentID;
            if (i1 == 0) {
               Queue4 queue41 = i_queue;
               if (queue41 == null) {
                  queue41 = new Queue4();
                  queue41.add(this);
               }
               queue41.add(ixpatch_0_1);
               ixpatch_0_1.i_queue = queue41;
               ixpatch_0_1.i_subsequent = i_subsequent;
               ixpatch_0_1.i_preceding = i_preceding;
               ixpatch_0_1.calculateSize();
               return ixpatch_0_1;
            }
         }
         return this.add(tree, i1);
      }
      
      internal override int compare(Tree tree) {
         YapDataType yapdatatype1 = i_fieldTransaction.i_index.i_field.getHandler();
         return yapdatatype1.compareTo(yapdatatype1.indexObject(this.trans(), i_value));
      }
   }
}