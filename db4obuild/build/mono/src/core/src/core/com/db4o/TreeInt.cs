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

   public class TreeInt : Tree, ReadWriteable {
      internal int i_key;
      
      public TreeInt(int i) : base() {
         i_key = i;
      }
      
      internal override int compare(Tree tree) {
         return i_key - ((TreeInt)tree).i_key;
      }
      
      internal Tree deepClone() {
         return new TreeInt(i_key);
      }
      
      internal override bool duplicates() {
         return false;
      }
      
      static internal TreeInt find(Tree tree, int i) {
         if (tree == null) return null;
         return ((TreeInt)tree).find(i);
      }
      
      internal TreeInt find(int i) {
         int i_0_1 = i_key - i;
         if (i_0_1 < 0) {
            if (i_subsequent != null) return ((TreeInt)i_subsequent).find(i);
         } else if (i_0_1 > 0) {
            if (i_preceding != null) return ((TreeInt)i_preceding).find(i);
         } else return this;
         return null;
      }
      
      public override Object read(YapReader yapreader) {
         return new TreeInt(yapreader.readInt());
      }
      
      public override void write(YapWriter yapwriter) {
         yapwriter.writeInt(i_key);
      }
      
      internal override int ownLength() {
         return 4;
      }
      
      internal override bool variableLength() {
         return false;
      }
      
      internal QCandidate toQCandidate(QCandidates qcandidates) {
         QCandidate qcandidate1 = new QCandidate(qcandidates, i_key, true);
         qcandidate1.i_preceding = toQCandidate((TreeInt)i_preceding, qcandidates);
         qcandidate1.i_subsequent = toQCandidate((TreeInt)i_subsequent, qcandidates);
         qcandidate1.i_size = i_size;
         return qcandidate1;
      }
      
      static internal QCandidate toQCandidate(TreeInt treeint, QCandidates qcandidates) {
         if (treeint == null) return null;
         return treeint.toQCandidate(qcandidates);
      }
   }
}