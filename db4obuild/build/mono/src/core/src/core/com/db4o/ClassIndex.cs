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

   internal class ClassIndex : YapMeta, ReadWriteable, UseSystemTransaction {
      
      internal ClassIndex() : base() {
      }
      internal Tree i_root;
      
      internal virtual void add(int i) {
         i_root = Tree.add(i_root, new TreeInt(i));
      }
      
      public int byteCount() {
         return 4 * (Tree.size(i_root) + 1);
      }
      
      public void clear() {
         i_root = null;
      }
      
      internal Tree cloneForYapClass(Transaction transaction, int i) {
         Tree[] trees1 = {
            Tree.deepClone(i_root, null)         };
         transaction.traverseAddedClassIDs(i, new ClassIndex__1(this, trees1));
         transaction.traverseRemovedClassIDs(i, new ClassIndex__2(this, trees1));
         return trees1[0];
      }
      
      internal override byte getIdentifier() {
         return (byte)88;
      }
      
      internal virtual long[] getInternalIDs(Transaction transaction, int i) {
         Tree tree1 = cloneForYapClass(transaction, i);
         if (tree1 == null) return new long[0];
         long[] ls1 = new long[tree1.size()];
         int[] xis1 = {
            0         };
         tree1.traverse(new ClassIndex__3(this, ls1, xis1));
         return ls1;
      }
      
      internal override int ownLength() {
         return 0 + byteCount();
      }
      
      public Object read(YapReader yapreader) {
         throw YapConst.virtualException();
      }
      
      internal override void readThis(Transaction transaction, YapReader yapreader) {
         i_root = new TreeReader(yapreader, new TreeInt(0)).read();
      }
      
      internal void remove(int i) {
         i_root = Tree.removeLike(i_root, new TreeInt(i));
      }
      
      internal virtual void setDirty(YapStream yapstream) {
         yapstream.setDirty(this);
      }
      
      public void write(YapWriter yapwriter) {
         writeThis(yapwriter);
      }
      
      internal override void writeThis(YapWriter yapwriter) {
         Tree.write(yapwriter, i_root);
      }
   }
}