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

   public class QCandidates : Visitor4 {
      internal Transaction i_trans;
      private Tree i_root;
      internal List4 i_constraints;
      internal YapClass i_yapClass;
      private QField i_field;
      internal QCon i_currentConstraint;
      internal Tree i_ordered;
      private int i_orderID;
      
      internal QCandidates(Transaction transaction, YapClass yapclass, QField qfield) : base() {
         i_trans = transaction;
         i_yapClass = yapclass;
         i_field = qfield;
         if (qfield != null && qfield.i_yapField != null && qfield.i_yapField.getHandler() is YapClass) {
            YapClass yapclass_0_1 = (YapClass)qfield.i_yapField.getHandler();
            if (i_yapClass == null) i_yapClass = yapclass_0_1; else {
               yapclass_0_1 = i_yapClass.getHigherOrCommonHierarchy(yapclass_0_1);
               if (yapclass_0_1 != null) i_yapClass = yapclass_0_1;
            }
         }
      }
      
      internal QCandidate addByIdentity(QCandidate qcandidate) {
         i_root = Tree.add(i_root, qcandidate);
         if (qcandidate.i_size == 0) return qcandidate.getRoot();
         return qcandidate;
      }
      
      internal void addConstraint(QCon qcon) {
         i_constraints = new List4(i_constraints, qcon);
      }
      
      internal void addOrder(QOrder qorder) {
         i_ordered = Tree.add(i_ordered, qorder);
      }
      
      internal void applyOrdering(Tree tree, int i) {
         if (tree != null && i_root != null) {
            if (i > 0) i = -i;
            bool xbool1 = i - i_orderID < 0;
            if (xbool1) i_orderID = i;
            int[] xis1 = {
               0            };
            i_root.traverse(new QCandidates__1(this, xbool1, xis1));
            xis1[0] = 1;
            tree.traverse(new QCandidates__2(this, xis1, xbool1));
            Collection4 collection41 = new Collection4();
            i_root.traverse(new QCandidates__3(this, collection41));
            Tree[] trees1 = {
               null            };
            Iterator4 iterator41 = collection41.iterator();
            while (iterator41.hasNext()) {
               QCandidate qcandidate1 = (QCandidate)iterator41.next();
               qcandidate1.i_preceding = null;
               qcandidate1.i_subsequent = null;
               qcandidate1.i_size = 1;
               trees1[0] = Tree.add(trees1[0], qcandidate1);
            }
            i_root = trees1[0];
         }
      }
      
      internal void collect(QCandidates qcandidates_1_) {
         if (i_constraints != null) {
            Iterator4 iterator41 = new Iterator4(i_constraints);
            while (iterator41.hasNext()) {
               QCon qcon1 = (QCon)iterator41.next();
               setCurrentConstraint(qcon1);
               qcon1.collect(qcandidates_1_);
            }
         }
         setCurrentConstraint(null);
      }
      
      internal void execute() {
         QCon qcon1 = null;
         if (i_constraints != null) {
            int i1 = 2147483647;
            Iterator4 iterator41 = new Iterator4(i_constraints);
            while (iterator41.hasNext()) {
               QCon qcon_2_1 = (QCon)iterator41.next();
               qcon_2_1.setCandidates(this);
               qcon_2_1.identityEvaluation();
               int i_3_1 = qcon_2_1.candidateCountByIndex();
               if (i_3_1 >= 0 && i_3_1 < i1) qcon1 = qcon_2_1;
            }
            if (qcon1 != null) i_root = qcon1.loadFromBestChildIndex(this);
         }
         if (qcon1 == null) loadFromClassIndex();
         evaluate();
      }
      
      internal void evaluate() {
         if (i_constraints != null) {
            Iterator4 iterator41 = new Iterator4(i_constraints);
            while (iterator41.hasNext()) ((QCon)iterator41.next()).evaluateSelf();
            iterator41 = new Iterator4(i_constraints);
            while (iterator41.hasNext()) ((QCon)iterator41.next()).evaluateSimpleChildren();
            iterator41 = new Iterator4(i_constraints);
            while (iterator41.hasNext()) ((QCon)iterator41.next()).evaluateEvaluations();
            iterator41 = new Iterator4(i_constraints);
            while (iterator41.hasNext()) ((QCon)iterator41.next()).evaluateCreateChildrenCandidates();
            iterator41 = new Iterator4(i_constraints);
            while (iterator41.hasNext()) ((QCon)iterator41.next()).evaluateCollectChildren();
            iterator41 = new Iterator4(i_constraints);
            while (iterator41.hasNext()) ((QCon)iterator41.next()).evaluateChildren();
         }
      }
      
      internal bool isEmpty() {
         bool[] bools1 = {
            true         };
         traverse(new QCandidates__4(this, bools1));
         return bools1[0];
      }
      
      internal bool filter(Visitor4 visitor4) {
         if (i_root != null) {
            i_root.traverse(visitor4);
            i_root = i_root.filter(new QCandidates__5(this));
         }
         return i_root != null;
      }
      
      internal void loadFromClassIndex() {
         if (isEmpty()) {
            QCandidates qcandidates_4_1 = this;
            if (i_yapClass.getIndex() != null) {
               Tree[] trees1 = {
                  TreeInt.toQCandidate(i_yapClass.getIndexRoot(), this)               };
               i_trans.traverseAddedClassIDs(i_yapClass.getID(), new QCandidates__6(this, trees1, qcandidates_4_1));
               i_trans.traverseRemovedClassIDs(i_yapClass.getID(), new QCandidates__7(this, trees1, qcandidates_4_1));
               i_root = trees1[0];
            }
         }
      }
      
      internal void setCurrentConstraint(QCon qcon) {
         i_currentConstraint = qcon;
      }
      
      internal void traverse(Visitor4 visitor4) {
         if (i_root != null) i_root.traverse(visitor4);
      }
      
      internal bool tryAddConstraint(QCon qcon) {
         if (i_field != null) {
            QField qfield1 = qcon.getField();
            if (qfield1 != null && i_field.i_name != qfield1.i_name) return false;
         }
         if (i_yapClass == null || qcon.isNullConstraint()) {
            addConstraint(qcon);
            return true;
         }
         YapClass yapclass1 = qcon.getYapClass();
         if (yapclass1 != null) {
            yapclass1 = i_yapClass.getHigherOrCommonHierarchy(yapclass1);
            if (yapclass1 != null) {
               i_yapClass = yapclass1;
               addConstraint(qcon);
               return true;
            }
         }
         return false;
      }
      
      public void visit(Object obj) {
         QCandidate qcandidate1 = (QCandidate)obj;
         if (!qcandidate1.createChild(this)) {
            if (i_constraints != null) {
               Iterator4 iterator41 = new Iterator4(i_constraints);
               while (iterator41.hasNext()) ((QCon)iterator41.next()).visitOnNull(qcandidate1.getRoot());
            }
         }
      }
   }
}