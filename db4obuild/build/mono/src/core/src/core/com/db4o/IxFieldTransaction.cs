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

   internal class IxFieldTransaction : Visitor4 {
      internal IxField i_index;
      internal Transaction i_trans;
      internal int i_version;
      private Tree i_root;
      
      internal IxFieldTransaction(Transaction transaction, IxField ixfield) : base() {
         i_trans = transaction;
         i_index = ixfield;
      }
      
      public override bool Equals(Object obj) {
         return i_trans == ((IxFieldTransaction)obj).i_trans;
      }
      
      internal void add(IxPatch ixpatch) {
         i_root = Tree.add(i_root, ixpatch);
      }
      
      internal Tree getRoot() {
         return i_root;
      }
      
      internal void commit() {
         i_index.commit(this);
      }
      
      internal void rollback() {
         i_index.rollback(this);
      }
      
      internal void merge(IxFieldTransaction ixfieldtransaction_0_) {
         Tree tree1 = ixfieldtransaction_0_.getRoot();
         if (tree1 != null) tree1.traverseFromLeaves(this);
      }
      
      public void visit(Object obj) {
         if (obj is IxPatch) {
            IxPatch ixpatch1 = (IxPatch)obj;
            if (ixpatch1.i_queue != null) {
               Queue4 queue41 = ixpatch1.i_queue;
               ixpatch1.i_queue = null;
               while ((ixpatch1 = (IxPatch)queue41.next()) != null) {
                  ixpatch1.i_queue = null;
                  addPatchToRoot(ixpatch1);
               }
            } else addPatchToRoot(ixpatch1);
         }
      }
      
      private void addPatchToRoot(IxPatch ixpatch) {
         if (ixpatch.i_version != i_version) {
            ixpatch.beginMerge();
            ixpatch.handler().prepareComparison(ixpatch.handler().indexObject(i_trans, ixpatch.i_value));
            if (i_root == null) i_root = ixpatch; else i_root = i_root.add(ixpatch);
         }
      }
      
      internal int countLeaves() {
         if (i_root == null) return 0;
         int[] xis1 = {
            0         };
         i_root.traverse(new IxFieldTransaction__1(this, xis1));
         return xis1[0];
      }
      
      public void setRoot(Tree tree) {
         i_root = tree;
      }
      
      public override String ToString() {
         StringBuffer stringbuffer1 = new StringBuffer();
         stringbuffer1.append("IxFieldTransaction ");
         stringbuffer1.append(j4o.lang.JavaSystem.identityHashCode(this));
         if (i_root == null) stringbuffer1.append("\n    Empty"); else i_root.traverse(new IxFieldTransaction__2(this, stringbuffer1));
         return stringbuffer1.ToString();
      }
   }
}