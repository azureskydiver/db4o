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

   internal class IxTraverser {
      
      internal IxTraverser() : base() {
      }
      private IxPath i_appendHead;
      private IxPath i_appendTail;
      internal QCandidates i_candidates;
      private IxPath i_greatHead;
      private IxPath i_greatTail;
      internal YapDataType i_handler;
      private IxPath i_smallHead;
      private IxPath i_smallTail;
      internal Tree i_tree;
      internal bool[] i_take;
      
      private void add(IxPath ixpath, IxPath ixpath_0_, IxPath ixpath_1_) {
         addPathTree(ixpath);
         if (ixpath_0_ != null && ixpath_1_ != null && ixpath_0_.carriesTheSame(ixpath_1_)) add(ixpath_0_, ixpath_0_.i_next, ixpath_1_.i_next); else {
            addGreater(ixpath_1_);
            addSmaller(ixpath_0_);
         }
      }
      
      private void addGreater(IxPath ixpath) {
         if (ixpath != null) {
            if (ixpath.i_next == null) addSubsequent(ixpath); else {
               if (ixpath.i_next.i_tree == ixpath.i_tree.i_preceding) addSubsequent(ixpath); else addPathTree(ixpath);
               addGreater(ixpath.i_next);
            }
         }
      }
      
      private void addPathTree(IxPath ixpath) {
         if (ixpath != null) i_tree = ixpath.addToCandidatesTree(i_tree, i_candidates);
      }
      
      private void addAll(Tree tree) {
         if (tree != null) {
            i_tree = ((IxTree)tree).addToCandidatesTree(i_tree, i_candidates, null);
            addAll(tree.i_preceding);
            addAll(tree.i_subsequent);
         }
      }
      
      private void addPreceding(IxPath ixpath) {
         addPathTree(ixpath);
         addAll(ixpath.i_tree.i_preceding);
      }
      
      private void addSmaller(IxPath ixpath) {
         if (ixpath != null) {
            if (ixpath.i_next == null) addPreceding(ixpath); else {
               if (ixpath.i_next.i_tree == ixpath.i_tree.i_subsequent) addPreceding(ixpath); else addPathTree(ixpath);
               addSmaller(ixpath.i_next);
            }
         }
      }
      
      private void addSubsequent(IxPath ixpath) {
         addPathTree(ixpath);
         addAll(ixpath.i_tree.i_subsequent);
      }
      
      private int countGreater(IxPath ixpath, int i) {
         if (ixpath.i_next == null) return i + countSubsequent(ixpath);
         if (ixpath.i_next.i_tree == ixpath.i_tree.i_preceding) i += countSubsequent(ixpath); else i += ixpath.countMatching();
         return countGreater(ixpath.i_next, i);
      }
      
      private int countPreceding(IxPath ixpath) {
         return Tree.size(ixpath.i_tree.i_preceding) + ixpath.countMatching();
      }
      
      private int countSmaller(IxPath ixpath, int i) {
         if (ixpath.i_next == null) return i + countPreceding(ixpath);
         if (ixpath.i_next.i_tree == ixpath.i_tree.i_subsequent) i += countPreceding(ixpath); else i += ixpath.countMatching();
         return countSmaller(ixpath.i_next, i);
      }
      
      private int countSpan(IxPath ixpath, IxPath ixpath_2_, IxPath ixpath_3_) {
         if (ixpath_2_ == null) {
            if (ixpath_3_ == null) return ixpath.countMatching();
            return countGreater(ixpath_3_, ixpath.countMatching());
         }
         if (ixpath_3_ == null) return countSmaller(ixpath_2_, ixpath.countMatching());
         if (ixpath_2_.carriesTheSame(ixpath_3_)) return countSpan(ixpath_2_, ixpath_2_.i_next, ixpath_3_.i_next);
         return ixpath.countMatching() + countGreater(ixpath_3_, 0) + countSmaller(ixpath_2_, 0);
      }
      
      private int countSubsequent(IxPath ixpath) {
         return Tree.size(ixpath.i_tree.i_subsequent) + ixpath.countMatching();
      }
      
      private void delayedAppend(IxTree ixtree, int i) {
         if (i_appendHead == null) {
            i_appendHead = new IxPath(this, null, ixtree, i);
            i_appendTail = i_appendHead;
         } else i_appendTail = i_appendTail.append(ixtree, i);
      }
      
      private void findBoth() {
         if (i_greatTail.i_comparisonResult == 0) {
            findSmallestEqualFromEqual((IxTree)i_greatTail.i_tree.i_preceding);
            resetDelayedAppend();
            findGreatestEqualFromEqual((IxTree)i_greatTail.i_tree.i_subsequent);
         } else if (i_greatTail.i_comparisonResult < 0) findBoth1((IxTree)i_greatTail.i_tree.i_subsequent); else findBoth1((IxTree)i_greatTail.i_tree.i_preceding);
      }
      
      private void findBoth1(IxTree ixtree) {
         if (ixtree != null) {
            int i1 = ixtree.compare(null);
            i_greatTail = i_greatTail.append(ixtree, i1);
            i_smallTail = i_smallTail.append(ixtree, i1);
            findBoth();
         }
      }
      
      internal int findBoundsQuery(QConObject qconobject, IxTree ixtree) {
         if (!qconobject.i_evaluator.supportsIndex()) return -1;
         i_take = new bool[]{
            false,
false,
false,
false         };
         qconobject.i_evaluator.indexBitMap(i_take);
         return findBounds1(qconobject.i_object, ixtree);
      }
      
      internal int findBoundsExactMatch(Object obj, IxTree ixtree) {
         i_take = new bool[]{
            false,
true,
false,
false         };
         return findBounds1(obj, ixtree);
      }
      
      private int findBounds1(Object obj, IxTree ixtree) {
         if (ixtree != null) {
            i_handler = ixtree.handler();
            i_handler.prepareComparison(obj);
            int i1 = ixtree.compare(null);
            i_greatHead = new IxPath(this, null, ixtree, i1);
            i_greatTail = i_greatHead;
            i_smallHead = i_greatHead.shallowClone();
            i_smallTail = i_smallHead;
            findBoth();
            int i_4_1 = 0;
            if (i_take[1]) i_4_1 += countSpan(i_greatHead, i_greatHead.i_next, i_smallHead.i_next);
            if (i_take[0]) {
               for (IxPath ixpath1 = i_smallHead; ixpath1 != null; ixpath1 = ixpath1.i_next) i_4_1 += ixpath1.countPreceding(i_take[3]);
            }
            if (i_take[2]) {
               for (IxPath ixpath1 = i_greatHead; ixpath1 != null; ixpath1 = ixpath1.i_next) i_4_1 += ixpath1.countSubsequent();
            }
            return i_4_1;
         }
         return 0;
      }
      
      private void findGreatestEqual(IxTree ixtree) {
         int i1 = ixtree.compare(null);
         i_greatTail = i_greatTail.append(ixtree, i1);
         if (i1 == 0) findGreatestEqualFromEqual(ixtree); else if (i1 < 0) {
            if (ixtree.i_subsequent != null) findGreatestEqual((IxTree)ixtree.i_subsequent);
         } else if (ixtree.i_preceding != null) findGreatestEqual((IxTree)ixtree.i_preceding);
      }
      
      private void findGreatestEqualFromEqual(IxTree ixtree) {
         if (ixtree != null) {
            int i1 = ixtree.compare(null);
            delayedAppend(ixtree, i1);
            if (i1 == 0) {
               i_greatTail = i_greatTail.append(i_appendHead, i_appendTail);
               resetDelayedAppend();
            }
            if (i1 > 0) findGreatestEqualFromEqual((IxTree)ixtree.i_preceding); else findGreatestEqualFromEqual((IxTree)ixtree.i_subsequent);
         }
      }
      
      private void findSmallestEqual(IxTree ixtree) {
         int i1 = ixtree.compare(null);
         i_smallTail = i_smallTail.append(ixtree, i1);
         if (i1 == 0) findSmallestEqualFromEqual(ixtree); else if (i1 < 0) {
            if (ixtree.i_subsequent != null) findSmallestEqual((IxTree)ixtree.i_subsequent);
         } else if (ixtree.i_preceding != null) findSmallestEqual((IxTree)ixtree.i_preceding);
      }
      
      private void findSmallestEqualFromEqual(IxTree ixtree) {
         if (ixtree != null) {
            int i1 = ixtree.compare(null);
            delayedAppend(ixtree, i1);
            if (i1 == 0) {
               i_smallTail = i_smallTail.append(i_appendHead, i_appendTail);
               resetDelayedAppend();
            }
            if (i1 < 0) findSmallestEqualFromEqual((IxTree)ixtree.i_subsequent); else findSmallestEqualFromEqual((IxTree)ixtree.i_preceding);
         }
      }
      
      internal Tree getMatches(QCandidates qcandidates) {
         i_candidates = qcandidates;
         i_tree = null;
         if (i_take[1] && i_greatHead != null) add(i_greatHead, i_greatHead.i_next, i_smallHead.i_next);
         if (i_take[0]) {
            for (IxPath ixpath1 = i_smallHead; ixpath1 != null; ixpath1 = ixpath1.i_next) i_tree = ixpath1.addPrecedingToCandidatesTree(i_tree, qcandidates);
         }
         if (i_take[2]) {
            for (IxPath ixpath1 = i_greatHead; ixpath1 != null; ixpath1 = ixpath1.i_next) i_tree = ixpath1.addSubsequentToCandidatesTree(i_tree, qcandidates);
         }
         return i_tree;
      }
      
      private void resetDelayedAppend() {
         i_appendHead = null;
         i_appendTail = null;
      }
   }
}