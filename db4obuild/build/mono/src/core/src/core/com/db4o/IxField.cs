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

   internal class IxField {
      static internal int MAX_LEAVES = 3;
      private static int i_version;
      internal YapField i_field;
      internal MetaIndex i_metaIndex;
      internal IxFieldTransaction i_globalIndex;
      internal Collection4 i_transactionIndices;
      internal IxFileRangeReader i_fileRangeReader;
      
      internal IxField(Transaction transaction, YapField yapfield, MetaIndex metaindex) : base() {
         i_metaIndex = metaindex;
         i_field = yapfield;
         i_globalIndex = new IxFieldTransaction(transaction, this);
         createGlobalFileRange();
      }
      
      internal IxFieldTransaction dirtyFieldTransaction(Transaction transaction) {
         IxFieldTransaction ixfieldtransaction1 = new IxFieldTransaction(transaction, this);
         if (i_transactionIndices == null) i_transactionIndices = new Collection4(); else {
            IxFieldTransaction ixfieldtransaction_0_1 = (IxFieldTransaction)i_transactionIndices.get(ixfieldtransaction1);
            if (ixfieldtransaction_0_1 != null) return ixfieldtransaction_0_1;
         }
         transaction.addDirtyFieldIndex(ixfieldtransaction1);
         ixfieldtransaction1.setRoot(Tree.deepClone(i_globalIndex.getRoot(), ixfieldtransaction1));
         ixfieldtransaction1.i_version = ++i_version;
         i_transactionIndices.add(ixfieldtransaction1);
         return ixfieldtransaction1;
      }
      
      internal IxFieldTransaction getFieldTransaction(Transaction transaction) {
         if (i_transactionIndices != null) {
            IxFieldTransaction ixfieldtransaction1 = new IxFieldTransaction(transaction, this);
            ixfieldtransaction1 = (IxFieldTransaction)i_transactionIndices.get(ixfieldtransaction1);
            if (ixfieldtransaction1 != null) return ixfieldtransaction1;
         }
         return i_globalIndex;
      }
      
      internal void commit(IxFieldTransaction ixfieldtransaction) {
         i_transactionIndices.remove(ixfieldtransaction);
         i_globalIndex.merge(ixfieldtransaction);
         int i1 = i_globalIndex.countLeaves();
         bool xbool1 = true;
         if (xbool1) {
            Transaction transaction1 = i_globalIndex.i_trans;
            int[] xis1 = {
               i_metaIndex.indexAddress,
i_metaIndex.indexLength,
i_metaIndex.patchAddress,
i_metaIndex.patchLength            };
            Tree tree1 = i_globalIndex.getRoot();
            YapDataType yapdatatype1 = i_field.getHandler();
            int i_1_1 = yapdatatype1.linkLength() + 4;
            i_metaIndex.indexEntries = tree1 == null ? 0 : tree1.i_size;
            i_metaIndex.indexLength = i_metaIndex.indexEntries * i_1_1;
            i_metaIndex.indexAddress = ((YapFile)transaction1.i_stream).getSlot(i_metaIndex.indexLength);
            i_metaIndex.patchEntries = 0;
            i_metaIndex.patchAddress = 0;
            i_metaIndex.patchLength = 0;
            transaction1.i_stream.setInternal(transaction1, i_metaIndex, 1, false);
            YapWriter yapwriter1 = new YapWriter(transaction1, i_metaIndex.indexAddress, i_1_1);
            if (tree1 != null) tree1.traverse(new IxField__1(this, yapdatatype1, yapwriter1));
            IxFileRange ixfilerange1 = createGlobalFileRange();
            Iterator4 iterator41 = i_transactionIndices.iterator();
            while (iterator41.hasNext()) {
               IxFieldTransaction ixfieldtransaction_2_1 = (IxFieldTransaction)iterator41.next();
               Tree tree_3_1 = ixfilerange1;
               if (tree_3_1 != null) tree_3_1 = tree_3_1.deepClone(ixfieldtransaction_2_1);
               Tree[] trees1 = {
                  tree_3_1               };
               ixfieldtransaction_2_1.getRoot().traverseFromLeaves(new IxField__2(this, ixfieldtransaction_2_1, trees1));
               ixfieldtransaction_2_1.setRoot(trees1[0]);
            }
            if (xis1[0] > 0) transaction1.i_file.free(xis1[0], xis1[1]);
            if (xis1[2] > 0) transaction1.i_file.free(xis1[2], xis1[3]);
         } else {
            Iterator4 iterator41 = i_transactionIndices.iterator();
            while (iterator41.hasNext()) ((IxFieldTransaction)iterator41.next()).merge(ixfieldtransaction);
         }
      }
      
      private IxFileRange createGlobalFileRange() {
         IxFileRange ixfilerange1 = null;
         if (i_metaIndex.indexEntries > 0) ixfilerange1 = new IxFileRange(i_globalIndex, i_metaIndex.indexAddress, 0, i_metaIndex.indexEntries);
         i_globalIndex.setRoot(ixfilerange1);
         return ixfilerange1;
      }
      
      internal void rollback(IxFieldTransaction ixfieldtransaction) {
         i_transactionIndices.remove(ixfieldtransaction);
      }
      
      internal IxFileRangeReader fileRangeReader() {
         if (i_fileRangeReader == null) i_fileRangeReader = new IxFileRangeReader(i_field.getHandler());
         return i_fileRangeReader;
      }
      
      public override String ToString() {
         StringBuffer stringbuffer1 = new StringBuffer();
         stringbuffer1.append("IxField  " + j4o.lang.JavaSystem.identityHashCode(this));
         if (i_globalIndex != null) {
            stringbuffer1.append("\n  Global \n   ");
            stringbuffer1.append(i_globalIndex.ToString());
         } else stringbuffer1.append("\n  no global index \n   ");
         if (i_transactionIndices != null) {
            Iterator4 iterator41 = i_transactionIndices.iterator();
            while (iterator41.hasNext()) {
               stringbuffer1.append("\n");
               stringbuffer1.append(iterator41.next().ToString());
            }
         }
         return stringbuffer1.ToString();
      }
   }
}