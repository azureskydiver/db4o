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

   internal class IxPath : Cloneable, Visitor4 {
      private QCandidates i_candidates;
      private Tree i_candidatesTree;
      internal int i_comparisonResult;
      internal int[] i_lowerAndUpperMatch;
      internal int i_upperNull = -1;
      internal IxPath i_next;
      internal IxTraverser i_traverser;
      internal IxTree i_tree;
      
      internal IxPath(IxTraverser ixtraverser, IxPath ixpath_0_, IxTree ixtree, int i) : base() {
         i_traverser = ixtraverser;
         i_next = ixpath_0_;
         i_tree = ixtree;
         i_comparisonResult = i;
         if (ixtree is IxFileRange) i_lowerAndUpperMatch = ixtree.i_fieldTransaction.i_index.fileRangeReader().lowerAndUpperMatches();
      }
      
      internal Tree addPrecedingToCandidatesTree(Tree tree, QCandidates qcandidates) {
         i_candidatesTree = tree;
         i_candidates = qcandidates;
         if (i_tree.i_preceding != null && (i_next == null || i_next.i_tree != i_tree.i_preceding)) i_tree.i_preceding.traverse(this);
         if (i_lowerAndUpperMatch != null) {
            int[] xis1 = {
               i_upperNull,
i_lowerAndUpperMatch[0] - 1            };
            i_candidatesTree = i_tree.addToCandidatesTree(i_candidatesTree, i_candidates, xis1);
         } else if (i_comparisonResult < 0) visit(i_tree);
         return i_candidatesTree;
      }
      
      internal Tree addSubsequentToCandidatesTree(Tree tree, QCandidates qcandidates) {
         i_candidatesTree = tree;
         i_candidates = qcandidates;
         if (i_tree.i_subsequent != null && (i_next == null || i_next.i_tree != i_tree.i_subsequent)) i_tree.i_subsequent.traverse(this);
         if (i_lowerAndUpperMatch != null) {
            int[] xis1 = {
               i_lowerAndUpperMatch[1] + 1,
((IxFileRange)i_tree)._entries - 1            };
            i_candidatesTree = i_tree.addToCandidatesTree(i_candidatesTree, i_candidates, xis1);
         } else if (i_comparisonResult > 0) visit(i_tree);
         return i_candidatesTree;
      }
      
      internal Tree addToCandidatesTree(Tree tree, QCandidates qcandidates) {
         if (i_comparisonResult == 0 && i_traverser.i_take[1]) tree = i_tree.addToCandidatesTree(tree, qcandidates, i_lowerAndUpperMatch);
         return tree;
      }
      
      internal IxPath append(IxPath ixpath_1_, IxPath ixpath_2_) {
         if (ixpath_1_ == null) return this;
         i_next = ixpath_1_;
         return ixpath_2_;
      }
      
      internal IxPath append(IxTree ixtree, int i) {
         i_next = new IxPath(i_traverser, null, ixtree, i);
         i_next.i_tree = ixtree;
         return i_next;
      }
      
      internal bool carriesTheSame(IxPath ixpath_3_) {
         return i_tree == ixpath_3_.i_tree;
      }
      
      private void checkUpperNull() {
         if (i_upperNull == -1) {
            i_traverser.i_handler.prepareComparison(null);
            i_tree.compare(null);
            int[] xis1 = i_tree.i_fieldTransaction.i_index.fileRangeReader().lowerAndUpperMatches();
            if (xis1[0] == 0) i_upperNull = xis1[1] + 1; else i_upperNull = 0;
         }
      }
      
      internal int countMatching() {
         if (i_comparisonResult == 0) {
            if (i_lowerAndUpperMatch == null) {
               if (i_tree is IxRemove) return 0;
               return 1;
            }
            return i_lowerAndUpperMatch[1] - i_lowerAndUpperMatch[0] + 1;
         }
         return 0;
      }
      
      internal int countPreceding(bool xbool) {
         int i1 = 0;
         if (i_tree.i_preceding != null && (i_next == null || i_next.i_tree != i_tree.i_preceding)) i1 += i_tree.i_preceding.i_size;
         if (i_lowerAndUpperMatch != null) {
            if (xbool) i_upperNull = 0; else checkUpperNull();
            i1 += i_lowerAndUpperMatch[0] - i_upperNull;
         } else if (i_comparisonResult < 0 && !(i_tree is IxRemove)) i1++;
         return i1;
      }
      
      internal int countSubsequent() {
         int i1 = 0;
         if (i_tree.i_subsequent != null && (i_next == null || i_next.i_tree != i_tree.i_subsequent)) i1 += i_tree.i_subsequent.i_size;
         if (i_lowerAndUpperMatch != null) i1 += ((IxFileRange)i_tree)._entries - i_lowerAndUpperMatch[1] - 1; else if (i_comparisonResult > 0 && !(i_tree is IxRemove)) i1++;
         return i1;
      }
      
      internal IxPath shallowClone() {
         try {
            {
               return (IxPath)j4o.lang.JavaSystem.clone(this);
            }
         }  catch (CloneNotSupportedException clonenotsupportedexception) {
            {
               return null;
            }
         }
      }
      
      public override String ToString() {
         return i_tree.ToString();
      }
      
      public void visit(Object obj) {
         i_candidatesTree = ((IxTree)obj).addToCandidatesTree(i_candidatesTree, i_candidates, null);
      }
   }
}