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

   public abstract class Tree : Cloneable, Readable {
      
      public Tree() : base() {
      }
      internal Tree i_preceding;
      internal int i_size = 1;
      internal Tree i_subsequent;
      
      static internal Tree add(Tree tree, Tree tree_0_) {
         if (tree == null) return tree_0_;
         return tree.add(tree_0_);
      }
      
      public virtual Tree add(Tree tree_1_) {
         return add(tree_1_, compare(tree_1_));
      }
      
      internal virtual Tree add(Tree tree_2_, int i) {
         if (i < 0) {
            if (i_subsequent == null) {
               i_subsequent = tree_2_;
               i_size++;
            } else {
               i_subsequent = i_subsequent.add(tree_2_);
               if (i_preceding == null) return rotateLeft();
               return balance();
            }
         } else if (i > 0 || tree_2_.duplicates()) {
            if (i_preceding == null) {
               i_preceding = tree_2_;
               i_size++;
            } else {
               i_preceding = i_preceding.add(tree_2_);
               if (i_subsequent == null) return rotateRight();
               return balance();
            }
         } else tree_2_.isDuplicateOf(this);
         return this;
      }
      
      internal Tree balance() {
         int i1 = i_subsequent.i_size - i_preceding.i_size;
         if (i1 < -2) return rotateRight();
         if (i1 > 2) return rotateLeft();
         i_size = i_preceding.i_size + i_subsequent.i_size + ownSize();
         return this;
      }
      
      internal Tree balanceCheckNulls() {
         if (i_subsequent == null) {
            if (i_preceding == null) {
               i_size = ownSize();
               return this;
            }
            return rotateRight();
         }
         if (i_preceding == null) return rotateLeft();
         return balance();
      }
      
      public static int byteCount(Tree tree) {
         if (tree == null) return 4;
         return tree.byteCount();
      }
      
      public int byteCount() {
         if (variableLength()) {
            int[] xis1 = {
               4            };
            traverse(new Tree__1(this, xis1));
            return xis1[0];
         }
         return 4 + size() * ownLength();
      }
      
      internal void calculateSize() {
         if (i_preceding == null) {
            if (i_subsequent == null) i_size = ownSize(); else i_size = i_subsequent.i_size + ownSize();
         } else if (i_subsequent == null) i_size = i_preceding.i_size + ownSize(); else i_size = i_preceding.i_size + i_subsequent.i_size + ownSize();
      }
      
      abstract internal int compare(Tree tree_3_);
      
      static internal Tree deepClone(Tree tree, Object obj) {
         if (tree == null) return null;
         Tree tree_4_1 = tree.deepClone(obj);
         tree_4_1.i_size = tree.i_size;
         tree_4_1.i_preceding = deepClone(tree.i_preceding, obj);
         tree_4_1.i_subsequent = deepClone(tree.i_subsequent, obj);
         return tree_4_1;
      }
      
      internal virtual Tree deepClone(Object obj) {
         try {
            {
               return (Tree)j4o.lang.JavaSystem.clone(this);
            }
         }  catch (CloneNotSupportedException clonenotsupportedexception) {
            {
               return null;
            }
         }
      }
      
      internal virtual bool duplicates() {
         return true;
      }
      
      internal Tree filter(VisitorBoolean visitorboolean) {
         if (i_preceding != null) i_preceding = i_preceding.filter(visitorboolean);
         if (i_subsequent != null) i_subsequent = i_subsequent.filter(visitorboolean);
         if (!visitorboolean.isVisit(this)) return remove();
         return this;
      }
      
      static internal Tree find(Tree tree, Tree tree_5_) {
         if (tree == null) return null;
         return tree.find(tree_5_);
      }
      
      public Tree find(Tree tree_6_) {
         int i1 = compare(tree_6_);
         if (i1 < 0) {
            if (i_subsequent != null) return i_subsequent.find(tree_6_);
         } else if (i1 > 0) {
            if (i_preceding != null) return i_preceding.find(tree_6_);
         } else return this;
         return null;
      }
      
      static internal Tree findGreaterOrEqual(Tree tree, Tree tree_7_) {
         if (tree == null) return null;
         int i1 = tree.compare(tree_7_);
         if (i1 == 0) return tree;
         if (i1 > 0) {
            Tree tree_8_1 = findGreaterOrEqual(tree.i_preceding, tree_7_);
            if (tree_8_1 != null) return tree_8_1;
            return tree;
         }
         return findGreaterOrEqual(tree.i_subsequent, tree_7_);
      }
      
      static internal Tree findSmaller(Tree tree, Tree tree_9_) {
         if (tree == null) return null;
         int i1 = tree.compare(tree_9_);
         if (i1 < 0) {
            Tree tree_10_1 = findSmaller(tree.i_subsequent, tree_9_);
            if (tree_10_1 != null) return tree_10_1;
            return tree;
         }
         return findSmaller(tree.i_preceding, tree_9_);
      }
      
      internal virtual void isDuplicateOf(Tree tree_11_) {
         i_size = 0;
      }
      
      internal virtual int ownLength() {
         throw YapConst.virtualException();
      }
      
      internal virtual int ownSize() {
         return 1;
      }
      
      static internal Tree read(Tree tree, YapReader yapreader) {
         throw YapConst.virtualException();
      }
      
      public virtual Object read(YapReader yapreader) {
         throw YapConst.virtualException();
      }
      
      internal Tree remove() {
         if (i_subsequent != null && i_preceding != null) {
            i_subsequent = i_subsequent.rotateSmallestUp();
            i_subsequent.i_preceding = i_preceding;
            i_subsequent.calculateSize();
            return i_subsequent;
         }
         if (i_subsequent != null) return i_subsequent;
         return i_preceding;
      }
      
      internal void removeChildren() {
         i_preceding = null;
         i_subsequent = null;
         i_size = ownSize();
      }
      
      static internal Tree removeLike(Tree tree, Tree tree_12_) {
         if (tree == null) return null;
         return tree.removeLike(tree_12_);
      }
      
      public Tree removeLike(Tree tree_13_) {
         int i1 = compare(tree_13_);
         if (i1 == 0) return remove();
         if (i1 > 0) {
            if (i_preceding != null) i_preceding = i_preceding.removeLike(tree_13_);
         } else if (i_subsequent != null) i_subsequent = i_subsequent.removeLike(tree_13_);
         calculateSize();
         return this;
      }
      
      internal Tree removeNode(Tree tree_14_) {
         if (this == tree_14_) return remove();
         int i1 = compare(tree_14_);
         if (i1 >= 0 && i_preceding != null) i_preceding = i_preceding.removeNode(tree_14_);
         if (i1 <= 0 && i_subsequent != null) i_subsequent = i_subsequent.removeNode(tree_14_);
         calculateSize();
         return this;
      }
      
      internal Tree rotateLeft() {
         Tree tree_15_1 = i_subsequent;
         i_subsequent = tree_15_1.i_preceding;
         calculateSize();
         tree_15_1.i_preceding = this;
         if (tree_15_1.i_subsequent == null) tree_15_1.i_size = i_size + tree_15_1.ownSize(); else tree_15_1.i_size = i_size + tree_15_1.i_subsequent.i_size + tree_15_1.ownSize();
         return tree_15_1;
      }
      
      internal Tree rotateRight() {
         Tree tree_16_1 = i_preceding;
         i_preceding = tree_16_1.i_subsequent;
         calculateSize();
         tree_16_1.i_subsequent = this;
         if (tree_16_1.i_preceding == null) tree_16_1.i_size = i_size + tree_16_1.ownSize(); else tree_16_1.i_size = i_size + tree_16_1.i_preceding.i_size + tree_16_1.ownSize();
         return tree_16_1;
      }
      
      private Tree rotateSmallestUp() {
         if (i_preceding != null) {
            i_preceding = i_preceding.rotateSmallestUp();
            return rotateRight();
         }
         return this;
      }
      
      static internal int size(Tree tree) {
         if (tree == null) return 0;
         return tree.size();
      }
      
      public int size() {
         return i_size;
      }
      
      public void traverse(Visitor4 visitor4) {
         if (i_preceding != null) i_preceding.traverse(visitor4);
         visitor4.visit(this);
         if (i_subsequent != null) i_subsequent.traverse(visitor4);
      }
      
      internal void traverseFromLeaves(Visitor4 visitor4) {
         if (i_preceding != null) i_preceding.traverseFromLeaves(visitor4);
         if (i_subsequent != null) i_subsequent.traverseFromLeaves(visitor4);
         visitor4.visit(this);
      }
      
      internal virtual bool variableLength() {
         throw YapConst.virtualException();
      }
      
      static internal void write(YapWriter yapwriter, Tree tree) {
         if (tree == null) yapwriter.writeInt(0); else {
            yapwriter.writeInt(tree.size());
            tree.traverse(new Tree__2(yapwriter));
         }
      }
      
      public virtual void write(YapWriter yapwriter) {
         throw YapConst.virtualException();
      }
   }
}