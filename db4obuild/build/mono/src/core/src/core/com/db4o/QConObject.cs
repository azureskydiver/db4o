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
using com.db4o.config;
using com.db4o.query;
namespace com.db4o {

   public class QConObject : QCon {
      internal Object i_object;
      internal int i_objectID;
      [Transient] internal YapClass i_yapClass;
      internal int i_yapClassID;
      internal QField i_field;
      [Transient] internal YapComparable i_comparator;
      internal ObjectAttribute i_attributeProvider;
      [Transient] private bool i_selfComparison = false;
      [Transient] private IxTraverser i_indexTraverser;
      [Transient] private QCon i_indexConstraint;
      [Transient] private bool i_loadedFromIndex;
      
      public QConObject() : base() {
      }
      
      internal QConObject(Transaction transaction, QCon qcon, QField qfield, Object obj) : base(transaction) {
         i_parent = qcon;
         if (obj is Compare) obj = ((Compare)obj).compare();
         i_object = obj;
         i_field = qfield;
         associateYapClass(transaction, obj);
      }
      
      private void associateYapClass(Transaction transaction, Object obj) {
         if (obj == null) {
            i_object = null;
            i_comparator = Null.INSTANCE;
            i_yapClass = null;
         } else {
            i_yapClass = transaction.i_stream.getYapClass(j4o.lang.Class.getClassForObject(obj), true);
            if (i_yapClass != null) {
               i_object = i_yapClass.getComparableObject(obj);
               if (obj != i_object) {
                  i_attributeProvider = i_yapClass.i_config.i_queryAttributeProvider;
                  i_yapClass = transaction.i_stream.getYapClass(j4o.lang.Class.getClassForObject(i_object), true);
               }
               if (i_yapClass != null) i_yapClass.collectConstraints(transaction, this, i_object, new QConObject__1(this)); else associateYapClass(transaction, null);
            } else associateYapClass(transaction, null);
         }
      }
      
      internal override int candidateCountByIndex() {
         int i1 = -1;
         if (i_joins == null && i_subConstraints != null) {
            Iterator4 iterator41 = new Iterator4(i_subConstraints);
            while (iterator41.hasNext()) {
               QCon qcon1 = (QCon)iterator41.next();
               int i_0_1 = qcon1.candidateCountByIndex(1);
               if (i_0_1 >= 0 && (i1 == -1 || i_0_1 < i1)) {
                  i_indexConstraint = qcon1;
                  i1 = i_0_1;
               }
            }
         }
         return i1;
      }
      
      internal override int candidateCountByIndex(int i) {
         int i_1_1 = -1;
         if (i == 1 && i_joins == null && i_field != null && i_field.i_yapField != null && i_field.i_yapField.hasIndex() && i_field.i_yapField.canLoadByIndex(this, i_evaluator)) {
            i_indexTraverser = new IxTraverser();
            i_1_1 = i_indexTraverser.findBoundsQuery(this, (IxTree)i_field.i_yapField.getIndexRoot(i_trans));
         }
         return i_1_1;
      }
      
      internal override void createCandidates(Collection4 collection4) {
         if (!i_loadedFromIndex || i_subConstraints != null) base.createCandidates(collection4);
      }
      
      internal override bool evaluate(QCandidate qcandidate) {
         try {
            {
               return qcandidate.evaluate(this, i_evaluator);
            }
         }  catch (Exception exception) {
            {
               return false;
            }
         }
      }
      
      internal override void evaluateEvaluationsExec(QCandidates qcandidates, bool xbool) {
         if (i_field.isSimple()) {
            bool bool_2_1 = false;
            if (i_subConstraints != null) {
               Iterator4 iterator41 = new Iterator4(i_subConstraints);
               while (iterator41.hasNext()) {
                  if (iterator41.next() is QEvaluation) {
                     bool_2_1 = true;
                     break;
                  }
               }
            }
            if (bool_2_1) {
               qcandidates.traverse(i_field);
               Iterator4 iterator41 = new Iterator4(i_subConstraints);
               while (iterator41.hasNext()) ((QCon)iterator41.next()).evaluateEvaluationsExec(qcandidates, false);
            }
         }
      }
      
      internal override void evaluateSelf() {
         if (i_yapClass != null && !(i_yapClass is YapClassPrimitive)) {
            if (!i_evaluator.identity()) {
               if (i_yapClass == i_candidates.i_yapClass && i_evaluator == QE.DEFAULT && i_joins == null) return;
               i_selfComparison = true;
            }
            i_comparator = i_yapClass.prepareComparison(i_object);
         }
         base.evaluateSelf();
         i_selfComparison = false;
      }
      
      internal override void collect(QCandidates qcandidates) {
         if (i_field.isClass()) {
            qcandidates.traverse(i_field);
            qcandidates.filter(i_candidates);
         }
      }
      
      internal override void evaluateSimpleExec(QCandidates qcandidates) {
         if ((i_orderID != 0 || !i_loadedFromIndex) && (i_field.isSimple() || isNullConstraint())) {
            qcandidates.traverse(i_field);
            prepareComparison(i_field);
            qcandidates.filter(this);
         }
      }
      
      internal YapComparable getComparator(QCandidate qcandidate) {
         if (i_comparator == null) return qcandidate.prepareComparison(i_trans.i_stream, i_object);
         return i_comparator;
      }
      
      internal override YapClass getYapClass() {
         return i_yapClass;
      }
      
      internal override QField getField() {
         return i_field;
      }
      
      internal int getObjectID() {
         if (i_objectID == 0) {
            i_objectID = i_trans.i_stream.getID1(i_trans, i_object);
            if (i_objectID == 0) i_objectID = -1;
         }
         return i_objectID;
      }
      
      internal override bool hasObjectInParentPath(Object obj) {
         if (obj == i_object) return true;
         return base.hasObjectInParentPath(obj);
      }
      
      public override void identityEvaluation() {
         if (i_evaluator.identity()) {
            int i1 = getObjectID();
            if (i1 != 0) i_candidates.addByIdentity(new QCandidate(i_candidates, i1, !(i_evaluator is QENot)));
         }
      }
      
      internal override bool isNullConstraint() {
         return i_object == null;
      }
      
      internal override Tree loadFromBestChildIndex(QCandidates qcandidates) {
         return i_indexConstraint.loadFromIndex(qcandidates);
      }
      
      internal override Tree loadFromIndex(QCandidates qcandidates) {
         i_loadedFromIndex = true;
         return i_indexTraverser.getMatches(qcandidates);
      }
      
      internal override void log(String xstring) {
      }
      
      internal override String logObject() {
         return "";
      }
      
      internal override void marshall() {
         base.marshall();
         getObjectID();
         if (i_yapClass != null) i_yapClassID = i_yapClass.getID();
      }
      
      internal void prepareComparison(QField qfield) {
         if (isNullConstraint() & !qfield.isArray()) i_comparator = Null.INSTANCE; else i_comparator = qfield.prepareComparison(i_object);
      }
      
      internal override void removeChildrenJoins() {
         base.removeChildrenJoins();
         i_subConstraints = null;
      }
      
      internal override QCon shareParent(Object obj, bool[] bools) {
         if (i_parent != null && i_field.canHold(obj)) return i_parent.addSharedConstraint(i_field, obj);
         return null;
      }
      
      internal override QConClass shareParentForClass(Class var_class, bool[] bools) {
         if (i_parent != null && i_field.canHold(var_class)) {
            QConClass qconclass1 = new QConClass(i_trans, i_parent, i_field, var_class);
            i_parent.addConstraint(qconclass1);
            return qconclass1;
         }
         return null;
      }
      
      internal Object translate(Object obj) {
         if (i_attributeProvider != null) {
            i_candidates.i_trans.i_stream.activate1(i_candidates.i_trans, obj);
            return i_attributeProvider.attribute(obj);
         }
         return obj;
      }
      
      internal override void unmarshall(Transaction transaction) {
         if (i_trans == null) {
            base.unmarshall(transaction);
            if (i_object == null) i_comparator = Null.INSTANCE;
            if (i_yapClassID != 0) i_yapClass = transaction.i_stream.getYapClass(i_yapClassID);
            if (i_field != null) i_field.unmarshall(transaction);
            if (i_objectID != 0) {
               Object obj1 = transaction.i_stream.getByID((long)i_objectID);
               if (obj1 != null) i_object = obj1;
            }
         }
      }
      
      public override void visit(Object obj) {
         QCandidate qcandidate1 = (QCandidate)obj;
         bool xbool1 = true;
         bool bool_3_1 = false;
         if (i_selfComparison) {
            YapClass yapclass1 = qcandidate1.readYapClass();
            if (yapclass1 != null) {
               xbool1 = i_evaluator.not(i_yapClass.getHigherHierarchy(yapclass1) == i_yapClass);
               bool_3_1 = true;
            }
         }
         if (!bool_3_1) xbool1 = evaluate(qcandidate1);
         if (i_orderID != 0 && xbool1) {
            Object obj_4_1 = qcandidate1.value();
            if (obj_4_1 != null && i_field != null) {
               YapComparable yapcomparable1 = i_comparator;
               i_comparator = i_field.prepareComparison(qcandidate1.value());
               i_candidates.addOrder(new QOrder(this, qcandidate1));
               i_comparator = yapcomparable1.prepareComparison(i_object);
            }
         }
         this.visit1(qcandidate1.getRoot(), this, xbool1);
      }
      
      public override Constraint contains() {
         lock (this.streamLock()) {
            i_evaluator = i_evaluator.add(new QEContains());
            return this;
         }
      }
      
      public override Constraint equal() {
         lock (this.streamLock()) {
            i_evaluator = i_evaluator.add(new QEEqual());
            return this;
         }
      }
      
      public override Object getObject() {
         lock (this.streamLock()) {
            return i_object;
         }
      }
      
      public override Constraint greater() {
         lock (this.streamLock()) {
            i_evaluator = i_evaluator.add(new QEGreater());
            return this;
         }
      }
      
      public override Constraint identity() {
         lock (this.streamLock()) {
            removeChildrenJoins();
            i_evaluator = i_evaluator.add(new QEIdentity());
            return this;
         }
      }
      
      public override Constraint like() {
         lock (this.streamLock()) {
            i_evaluator = i_evaluator.add(new QELike());
            return this;
         }
      }
      
      public override Constraint smaller() {
         lock (this.streamLock()) {
            i_evaluator = i_evaluator.add(new QESmaller());
            return this;
         }
      }
      
      public override String ToString() {
         return base.ToString();
      }
   }
}