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

   internal class IxFileRangeReader : Readable {
      private int _baseAddress;
      private int _baseAddressOffset;
      private int _addressOffset;
      internal IxFileRange _fileRange;
      private YapDataType _handler;
      private QCandidates _candidates;
      private int _lower;
      private int _upper;
      private int _cursor;
      private YapReader _reader;
      private int _slotLength;
      private int _linkLegth;
      
      internal IxFileRangeReader(YapDataType yapdatatype) : base() {
         _handler = yapdatatype;
         _linkLegth = yapdatatype.linkLength();
         _slotLength = _linkLegth + 4;
         _reader = new YapReader(_slotLength);
      }
      
      internal Tree add(IxFileRange ixfilerange, Tree tree) {
         setFileRange(ixfilerange);
         YapFile yapfile1 = ixfilerange.stream();
         Transaction transaction1 = ixfilerange.trans();
         int i1;
         do {
            _reader.read(yapfile1, _baseAddress, _baseAddressOffset + _addressOffset);
            _reader._offset = 0;
            i1 = _handler.compareTo(_handler.indexObject(transaction1, _handler.readIndexEntry(_reader)));
            if (i1 == 0) {
               int i_0_1 = _reader.readInt();
               i1 = i_0_1 - ((IxPatch)tree).i_parentID;
            }
            if (i1 > 0) {
               _upper = _cursor - 1;
               if (_upper < _lower) _upper = _lower;
            } else if (i1 < 0) {
               _lower = _cursor + 1;
               if (_lower > _upper) _lower = _upper;
            } else {
               if (tree is IxRemove) {
                  IxRemove ixremove1 = (IxRemove)tree;
                  if (_cursor == 0) {
                     tree.i_preceding = ixfilerange.i_preceding;
                     if (ixfilerange._entries == 1) {
                        tree.i_subsequent = ixfilerange.i_subsequent;
                        return tree.balanceCheckNulls();
                     }
                     ixfilerange._entries--;
                     ixfilerange.incrementAddress(_slotLength);
                     ixfilerange.i_preceding = null;
                     tree.i_subsequent = ixfilerange;
                  } else if (_cursor + 1 == ixfilerange._entries) {
                     tree.i_preceding = ixfilerange;
                     tree.i_subsequent = ixfilerange.i_subsequent;
                     ixfilerange.i_subsequent = null;
                     ixfilerange._entries--;
                  } else return insert(tree, _cursor, 0);
                  ixfilerange.calculateSize();
                  return tree.balanceCheckNulls();
               }
               if (_cursor == 0) {
                  tree.i_subsequent = ixfilerange;
                  return tree.rotateLeft();
               }
               if (_cursor == ixfilerange._entries) {
                  tree.i_preceding = ixfilerange;
                  return tree.rotateRight();
               }
               return insert(tree, _cursor, i1);
            }
         }          while (adjustCursor());
         if (_cursor == 0 && i1 > 0) return ixfilerange.add(tree, 1);
         if (_cursor == ixfilerange._entries - 1 && i1 < 0) return ixfilerange.add(tree, -1);
         return insert(tree, _cursor, i1);
      }
      
      public Tree addToCandidatesTree(QCandidates qcandidates, Tree tree, IxFileRange ixfilerange, int[] xis) {
         _candidates = qcandidates;
         if (xis == null) xis = new int[]{
            0,
ixfilerange._entries - 1         };
         YapFile yapfile1 = _fileRange.stream();
         int i1 = ixfilerange._address;
         int i_1_1 = ixfilerange._addressOffset;
         int i_2_1 = xis[1] - xis[0] + 1;
         if (i_2_1 > 0) {
            YapReader yapreader1 = new YapReader(i_2_1 * _slotLength);
            yapreader1.read(yapfile1, i1, i_1_1 + xis[0] * _slotLength);
            Tree tree_3_1 = new TreeReader(yapreader1, this, false).read(i_2_1);
            if (tree_3_1 != null) tree = Tree.add(tree, tree_3_1);
         }
         return tree;
      }
      
      private bool adjustCursor() {
         if (_upper < _lower) return false;
         int i1 = _cursor;
         _cursor = _lower + (_upper - _lower) / 2;
         if (_cursor == i1 && _cursor == _lower && _lower < _upper) _cursor++;
         _addressOffset = _cursor * _slotLength;
         return _cursor != i1;
      }
      
      internal int compare(IxFileRange ixfilerange, Tree tree) {
         setFileRange(ixfilerange);
         YapFile yapfile1 = ixfilerange.stream();
         Transaction transaction1 = ixfilerange.trans();
         do {
            _reader.read(yapfile1, _baseAddress, _baseAddressOffset + _addressOffset);
            _reader._offset = 0;
            int i1 = _handler.compareTo(_handler.indexObject(transaction1, _handler.readIndexEntry(_reader)));
            if (i1 > 0) _upper = _cursor - 1; else if (i1 < 0) _lower = _cursor + 1; else return 0;
         }          while (adjustCursor());
         return _cursor == 0 ? 1 : -1;
      }
      
      private Tree insert(Tree tree, int i, int i_4_) {
         int i_5_1 = i_4_ <= 0 ? 1 : 0;
         int i_6_1 = (i + i_5_1) * _slotLength;
         int i_7_1 = _fileRange._entries - i - i_5_1;
         _fileRange._entries = i_4_ < 0 ? i + 1 : i;
         IxFileRange ixfilerange1 = new IxFileRange(_fileRange.i_fieldTransaction, _baseAddress, _baseAddressOffset + i_6_1, i_7_1);
         ixfilerange1.i_subsequent = _fileRange.i_subsequent;
         _fileRange.i_subsequent = null;
         tree.i_preceding = _fileRange.balanceCheckNulls();
         tree.i_subsequent = ixfilerange1.balanceCheckNulls();
         return tree.balance();
      }
      
      internal int[] lowerAndUpperMatches() {
         int[] xis1 = {
            _lower,
_upper         };
         if (_lower > _upper) return xis1;
         YapFile yapfile1 = _fileRange.stream();
         Transaction transaction1 = _fileRange.trans();
         int i1 = _cursor;
         _upper = _cursor;
         adjustCursor();
         while_0_: do {
            do {
               _reader.read(yapfile1, _baseAddress, _baseAddressOffset + _addressOffset);
               _reader._offset = 0;
               int i_8_1 = _handler.compareTo(_handler.indexObject(transaction1, _handler.readIndexEntry(_reader)));
               if (i_8_1 == 0) _upper = _cursor; else {
                  _lower = _cursor + 1;
                  if (_lower > _upper) {
                     xis1[0] = _upper;
                     goto while_0_;
                  }
               }
            }             while (adjustCursor());
            xis1[0] = _upper;
         }          while (false);
         _upper = xis1[1];
         _lower = i1;
         if (_lower > _upper) _lower = _upper;
         adjustCursor();
         while_1_: do {
            do {
               _reader.read(yapfile1, _baseAddress, _baseAddressOffset + _addressOffset);
               _reader._offset = 0;
               int i_9_1 = _handler.compareTo(_handler.indexObject(transaction1, _handler.readIndexEntry(_reader)));
               if (i_9_1 == 0) _lower = _cursor; else {
                  _upper = _cursor - 1;
                  if (_upper < _lower) {
                     xis1[1] = _lower;
                     goto while_1_;
                  }
               }
            }             while (adjustCursor());
            xis1[1] = _lower;
         }          while (false);
         return xis1;
      }
      
      private void setFileRange(IxFileRange ixfilerange) {
         _fileRange = ixfilerange;
         _lower = 0;
         _upper = ixfilerange._entries - 1;
         _baseAddress = ixfilerange._address;
         _baseAddressOffset = ixfilerange._addressOffset;
         adjustCursor();
      }
      
      public Object read(YapReader yapreader) {
         yapreader.incrementOffset(_linkLegth);
         return new QCandidate(_candidates, yapreader.readInt(), true);
      }
      
      public int byteCount() {
         return _slotLength;
      }
   }
}