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

   internal class IxFileRange : IxTree {
      internal int _address;
      internal int _addressOffset;
      internal int _entries;
      
      public IxFileRange(IxFieldTransaction ixfieldtransaction, int i, int i_0_, int i_1_) : base(ixfieldtransaction) {
         _address = i;
         _addressOffset = i_0_;
         _entries = i_1_;
         i_size = i_1_;
      }
      
      public override Tree add(Tree tree) {
         return i_fieldTransaction.i_index.fileRangeReader().add(this, tree);
      }
      
      internal override int compare(Tree tree) {
         return i_fieldTransaction.i_index.fileRangeReader().compare(this, tree);
      }
      
      internal override int ownSize() {
         return _entries;
      }
      
      internal override void write(YapDataType yapdatatype, YapWriter yapwriter) {
         YapFile yapfile1 = (YapFile)yapwriter.getStream();
         int i1 = _entries * this.slotLength();
         yapfile1.copy(_address, _addressOffset, yapwriter.getAddress(), yapwriter.addressOffset(), i1);
         yapwriter.moveForward(i1);
      }
      
      internal override Tree addToCandidatesTree(Tree tree, QCandidates qcandidates, int[] xis) {
         return i_fieldTransaction.i_index.fileRangeReader().addToCandidatesTree(qcandidates, tree, this, xis);
      }
      
      public override String ToString() {
         YapFile yapfile1 = this.stream();
         Transaction transaction1 = this.trans();
         YapReader yapreader1 = new YapReader(this.slotLength());
         StringBuffer stringbuffer1 = new StringBuffer();
         stringbuffer1.append("IxFileRange");
         for (int i1 = 0; i1 < _entries; i1++) {
            int i_2_1 = _address + i1 * this.slotLength();
            yapreader1.read(yapfile1, i_2_1, _addressOffset);
            yapreader1._offset = 0;
            stringbuffer1.append("\n  ");
            Object obj1 = this.handler().indexObject(transaction1, this.handler().readIndexEntry(yapreader1));
            int i_3_1 = yapreader1.readInt();
            stringbuffer1.append("Parent: " + i_3_1);
            stringbuffer1.append("\n ");
            stringbuffer1.append(obj1);
         }
         return stringbuffer1.ToString();
      }
      
      public void incrementAddress(int i) {
         _addressOffset += i;
      }
   }
}