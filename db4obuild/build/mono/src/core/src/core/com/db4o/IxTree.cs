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

   abstract internal class IxTree : Tree {
      internal IxFieldTransaction i_fieldTransaction;
      internal int i_version;
      
      internal IxTree(IxFieldTransaction ixfieldtransaction) : base() {
         i_fieldTransaction = ixfieldtransaction;
         i_version = ixfieldtransaction.i_version;
      }
      
      internal override Tree add(Tree tree, int i) {
         if (i < 0) {
            if (i_subsequent == null) i_subsequent = tree; else i_subsequent = i_subsequent.add(tree);
         } else if (i_preceding == null) i_preceding = tree; else i_preceding = i_preceding.add(tree);
         return this.balanceCheckNulls();
      }
      
      abstract internal Tree addToCandidatesTree(Tree tree, QCandidates qcandidates, int[] xis);
      
      internal override Tree deepClone(Object obj) {
         try {
            {
               IxTree ixtree_0_1 = (IxTree)j4o.lang.JavaSystem.clone(this);
               ixtree_0_1.i_fieldTransaction = (IxFieldTransaction)obj;
               return ixtree_0_1;
            }
         }  catch (CloneNotSupportedException clonenotsupportedexception) {
            {
               j4o.lang.JavaSystem.printStackTrace(clonenotsupportedexception);
               return null;
            }
         }
      }
      
      internal YapDataType handler() {
         return i_fieldTransaction.i_index.i_field.getHandler();
      }
      
      internal int slotLength() {
         return handler().linkLength() + 4;
      }
      
      internal Transaction trans() {
         return i_fieldTransaction.i_trans;
      }
      
      abstract internal void write(YapDataType yapdatatype, YapWriter yapwriter);
      
      internal YapFile stream() {
         return trans().i_file;
      }
      
      internal virtual void beginMerge() {
         i_preceding = null;
         i_subsequent = null;
         i_size = this.ownSize();
      }
   }
}