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
using com.db4o.query;
namespace com.db4o {

   public abstract class QCon : Constraint, Visitor4 {
      static internal IDGenerator idGenerator = new IDGenerator();
      [Transient] internal QCandidates i_candidates;
      internal Collection4 i_childrenCandidates;
      internal List4 i_subConstraints;
      internal QE i_evaluator = QE.DEFAULT;
      internal int i_id;
      internal Collection4 i_joins;
      internal int i_orderID = 0;
      internal QCon i_parent;
      private bool i_removed = false;
      [Transient] internal Transaction i_trans;
      
      public QCon() : base() {
      }
      
      internal QCon(Transaction transaction) : base() {
         i_id = idGenerator.next();
         i_trans = transaction;
      }
      
      internal QCon addConstraint(QCon qcon_0_) {
         i_subConstraints = new List4(i_subConstraints, qcon_0_);
         return qcon_0_;
      }
      
      internal void addJoin(QConJoin qconjoin) {
         if (i_joins == null) i_joins = new Collection4();
         i_joins.add(qconjoin);
      }
      
      internal QCon addSharedConstraint(QField qfield, Object obj) {
         QConObject qconobject1 = new QConObject(i_trans, this, qfield, obj);
         addConstraint(qconobject1);
         return qconobject1;
      }
      
      public Constraint and(Constraint constraint) {
         lock (streamLock()) {
            return join(constraint, true);
         }
      }
      
      internal void applyOrdering() {
         if (i_orderID != 0) {
            QCon qcon_1_1 = getRoot();
            qcon_1_1.i_candidates.applyOrdering(i_candidates.i_ordered, i_orderID);
         }
      }
      
      internal bool attach(QQuery qquery, String xstring) {
         QCon qcon_2_1 = this;
         YapClass yapclass1 = getYapClass();
         bool[] bools1 = {
            false         };
         forEachChildField(xstring, new QCon__1(this, bools1, qquery));
         if (bools1[0]) return true;
         QField qfield1 = null;
         if (yapclass1 == null || yapclass1.holdsAnyClass()) {
            int[] xis1 = {
               0            };
            YapField[] yapfields1 = {
               null            };
            i_trans.i_stream.i_classCollection.yapFields(xstring, new QCon__2(this, yapfields1, xis1));
            if (xis1[0] == 0) return false;
            if (xis1[0] == 1) qfield1 = yapfields1[0].qField(i_trans); else qfield1 = new QField(i_trans, xstring, null, 0, 0);
         } else {
            if (yapclass1 != null) {
               YapField yapfield1 = yapclass1.getYapField(xstring);
               if (yapfield1 != null) qfield1 = yapfield1.qField(i_trans);
            }
            if (qfield1 == null) qfield1 = new QField(i_trans, xstring, null, 0, 0);
         }
         QConPath qconpath1 = new QConPath(i_trans, qcon_2_1, qfield1);
         qquery.addConstraint(qconpath1);
         qcon_2_1.addConstraint(qconpath1);
         return true;
      }
      
      internal virtual int candidateCountByIndex() {
         return -1;
      }
      
      internal virtual int candidateCountByIndex(int i) {
         return -1;
      }
      
      internal void checkLastJoinRemoved() {
         if (i_joins.size() == 0) i_joins = null;
      }
      
      internal virtual void collect(QCandidates qcandidates) {
      }
      
      public virtual Constraint contains() {
         throw notSupported();
      }
      
      internal virtual void createCandidates(Collection4 collection4) {
         Iterator4 iterator41 = collection4.iterator();
         while (iterator41.hasNext()) {
            QCandidates qcandidates1 = (QCandidates)iterator41.next();
            if (qcandidates1.tryAddConstraint(this)) {
               i_candidates = qcandidates1;
               return;
            }
         }
         i_candidates = new QCandidates(i_trans, getYapClass(), getField());
         i_candidates.addConstraint(this);
         collection4.add(i_candidates);
      }
      
      internal virtual void doNotInclude(QCandidate qcandidate) {
         if (i_parent != null) i_parent.visit1(qcandidate, this, false); else qcandidate.doNotInclude();
      }
      
      public virtual Constraint equal() {
         throw notSupported();
      }
      
      internal virtual bool evaluate(QCandidate qcandidate) {
         throw YapConst.virtualException();
      }
      
      internal void evaluateChildren() {
         Iterator4 iterator41 = i_childrenCandidates.iterator();
         while (iterator41.hasNext()) ((QCandidates)iterator41.next()).evaluate();
      }
      
      internal void evaluateCollectChildren() {
         Iterator4 iterator41 = i_childrenCandidates.iterator();
         while (iterator41.hasNext()) ((QCandidates)iterator41.next()).collect(i_candidates);
      }
      
      internal void evaluateCreateChildrenCandidates() {
         i_childrenCandidates = new Collection4();
         if (i_subConstraints != null) {
            Iterator4 iterator41 = new Iterator4(i_subConstraints);
            while (iterator41.hasNext()) ((QCon)iterator41.next()).createCandidates(i_childrenCandidates);
         }
      }
      
      internal void evaluateEvaluations() {
         if (i_subConstraints != null) {
            Iterator4 iterator41 = new Iterator4(i_subConstraints);
            while (iterator41.hasNext()) ((QCon)iterator41.next()).evaluateEvaluationsExec(i_candidates, true);
         }
      }
      
      internal virtual void evaluateEvaluationsExec(QCandidates qcandidates, bool xbool) {
      }
      
      internal virtual void evaluateSelf() {
         i_candidates.filter(this);
      }
      
      internal void evaluateSimpleChildren() {
         if (i_subConstraints != null) {
            Iterator4 iterator41 = new Iterator4(i_subConstraints);
            while (iterator41.hasNext()) {
               QCon qcon_3_1 = (QCon)iterator41.next();
               i_candidates.setCurrentConstraint(qcon_3_1);
               qcon_3_1.setCandidates(i_candidates);
               qcon_3_1.evaluateSimpleExec(i_candidates);
               qcon_3_1.applyOrdering();
            }
            i_candidates.setCurrentConstraint(null);
         }
      }
      
      internal virtual void evaluateSimpleExec(QCandidates qcandidates) {
      }
      
      internal virtual void exchangeConstraint(QCon qcon_4_, QCon qcon_5_) {
         List4 list41 = null;
         for (List4 list4_6_1 = i_subConstraints; list4_6_1 != null; list4_6_1 = list4_6_1.i_next) {
            if (list4_6_1.i_object == qcon_4_) {
               if (list41 == null) i_subConstraints = list4_6_1.i_next; else list41.i_next = list4_6_1.i_next;
            }
            list41 = list4_6_1;
         }
         i_subConstraints = new List4(i_subConstraints, qcon_5_);
      }
      
      internal void forEachChildField(String xstring, Visitor4 visitor4) {
         if (i_subConstraints != null) {
            Iterator4 iterator41 = new Iterator4(i_subConstraints);
            while (iterator41.hasNext()) {
               Object obj1 = iterator41.next();
               if (obj1 is QConObject && ((QConObject)obj1).i_field.i_name.Equals(xstring)) visitor4.visit(obj1);
            }
         }
      }
      
      internal virtual QField getField() {
         return null;
      }
      
      public virtual Object getObject() {
         throw notSupported();
      }
      
      internal QCon getRoot() {
         if (i_parent != null) return i_parent.getRoot();
         return this;
      }
      
      internal QCon getTopLevelJoin() {
         if (i_joins == null) return this;
         Iterator4 iterator41 = i_joins.iterator();
         if (i_joins.size() == 1) return ((QCon)iterator41.next()).getTopLevelJoin();
         Collection4 collection41 = new Collection4();
         while (iterator41.hasNext()) collection41.ensure(((QCon)iterator41.next()).getTopLevelJoin());
         iterator41 = collection41.iterator();
         QCon qcon_7_1 = (QCon)iterator41.next();
         if (collection41.size() == 1) return qcon_7_1;
         while (iterator41.hasNext()) qcon_7_1 = (QCon)qcon_7_1.and((Constraint)iterator41.next());
         return qcon_7_1;
      }
      
      internal virtual YapClass getYapClass() {
         return null;
      }
      
      public virtual Constraint greater() {
         throw notSupported();
      }
      
      internal virtual bool hasObjectInParentPath(Object obj) {
         if (i_parent != null) return i_parent.hasObjectInParentPath(obj);
         return false;
      }
      
      public virtual Constraint identity() {
         throw notSupported();
      }
      
      public virtual void identityEvaluation() {
         throw YapConst.virtualException();
      }
      
      internal bool isNot() {
         return i_evaluator is QENot;
      }
      
      internal virtual bool isNullConstraint() {
         return false;
      }
      
      internal virtual Constraint join(Constraint constraint, bool xbool) {
         if (!(constraint is QCon)) return null;
         if (constraint == this) return this;
         return join1((QCon)constraint, xbool);
      }
      
      internal Constraint join1(QCon qcon_8_, bool xbool) {
         if (qcon_8_ is QConstraints) {
            bool bool_9_1 = false;
            Collection4 collection41 = new Collection4();
            Constraint[] constraints1 = ((QConstraints)qcon_8_).toArray();
            for (int i2 = 0; i2 < constraints1.Length; i2++) collection41.ensure(((QCon)constraints1[i2]).joinHook());
            Constraint[] constraints_10_1 = new Constraint[collection41.size()];
            int i1 = 0;
            Iterator4 iterator41 = collection41.iterator();
            while (iterator41.hasNext()) constraints_10_1[i1++] = join((Constraint)iterator41.next(), xbool);
            return new QConstraints(i_trans, constraints_10_1);
         }
         QCon qcon_11_1 = joinHook();
         QCon qcon_12_1 = qcon_8_.joinHook();
         if (qcon_11_1 == qcon_12_1) return qcon_11_1;
         QConJoin qconjoin1 = new QConJoin(i_trans, qcon_11_1, qcon_12_1, xbool);
         qcon_11_1.addJoin(qconjoin1);
         qcon_12_1.addJoin(qconjoin1);
         return qconjoin1;
      }
      
      internal QCon joinHook() {
         return getTopLevelJoin();
      }
      
      public virtual Constraint like() {
         throw notSupported();
      }
      
      internal virtual Tree loadFromBestChildIndex(QCandidates qcandidates) {
         return null;
      }
      
      internal virtual Tree loadFromIndex(QCandidates qcandidates) {
         return null;
      }
      
      internal virtual void log(String xstring) {
      }
      
      internal virtual String logObject() {
         return "";
      }
      
      internal virtual void marshall() {
         if (i_subConstraints != null) {
            Iterator4 iterator41 = new Iterator4(i_subConstraints);
            while (iterator41.hasNext()) ((QCon)iterator41.next()).marshall();
         }
      }
      
      public virtual Constraint not() {
         lock (streamLock()) {
            if (!(i_evaluator is QENot)) i_evaluator = new QENot(i_evaluator);
            return this;
         }
      }
      
      private RuntimeException notSupported() {
         return new RuntimeException("Not supported.");
      }
      
      public Constraint or(Constraint constraint) {
         lock (streamLock()) {
            return join(constraint, false);
         }
      }
      
      internal bool remove() {
         if (!i_removed) {
            i_removed = true;
            removeChildrenJoins();
            return true;
         }
         return false;
      }
      
      internal virtual void removeChildrenJoins() {
         if (i_joins != null) {
            Iterator4 iterator41 = i_joins.iterator();
            while (iterator41.hasNext()) {
               QConJoin qconjoin1 = (QConJoin)iterator41.next();
               if (qconjoin1.removeForParent(this)) i_joins.remove(qconjoin1);
            }
            checkLastJoinRemoved();
         }
      }
      
      internal void removeJoin(QConJoin qconjoin) {
         i_joins.remove(qconjoin);
         checkLastJoinRemoved();
      }
      
      internal void removeNot() {
         if (isNot()) i_evaluator = ((QENot)i_evaluator).i_evaluator;
      }
      
      internal void setCandidates(QCandidates qcandidates) {
         i_candidates = qcandidates;
      }
      
      internal void setOrdering(int i) {
         i_orderID = i;
      }
      
      internal void setParent(QCon qcon_13_) {
         i_parent = qcon_13_;
      }
      
      internal virtual QCon shareParent(Object obj, bool[] bools) {
         return null;
      }
      
      internal virtual QConClass shareParentForClass(Class var_class, bool[] bools) {
         return null;
      }
      
      public virtual Constraint smaller() {
         throw notSupported();
      }
      
      protected Object streamLock() {
         return i_trans.i_stream.i_lock;
      }
      
      internal bool supportsOrdering() {
         return false;
      }
      
      internal virtual void unmarshall(Transaction transaction) {
         if (i_trans == null) {
            i_trans = transaction;
            if (i_parent != null) i_parent.unmarshall(transaction);
            if (i_joins != null) {
               Iterator4 iterator41 = i_joins.iterator();
               while (iterator41.hasNext()) ((QCon)iterator41.next()).unmarshall(transaction);
            }
            if (i_subConstraints != null) {
               Iterator4 iterator41 = new Iterator4(i_subConstraints);
               while (iterator41.hasNext()) ((QCon)iterator41.next()).unmarshall(transaction);
            }
         }
      }
      
      public virtual void visit(Object obj) {
         QCandidate qcandidate1 = (QCandidate)obj;
         visit1(qcandidate1.getRoot(), this, evaluate(qcandidate1));
      }
      
      internal void visit(QCandidate qcandidate, bool xbool) {
         visit1(qcandidate, this, i_evaluator.not(xbool));
      }
      
      internal void visit1(QCandidate qcandidate, QCon qcon_14_, bool xbool) {
         if (i_joins != null) {
            Iterator4 iterator41 = i_joins.iterator();
            while (iterator41.hasNext()) qcandidate.evaluate(new QPending((QConJoin)iterator41.next(), this, xbool));
         } else if (!xbool) doNotInclude(qcandidate);
      }
      
      internal void visitOnNull(QCandidate qcandidate) {
         if (i_subConstraints != null) {
            Iterator4 iterator41 = new Iterator4(i_subConstraints);
            while (iterator41.hasNext()) ((QCon)iterator41.next()).visitOnNull(qcandidate);
         }
         if (visitSelfOnNull()) visit(qcandidate, isNullConstraint());
      }
      
      internal virtual bool visitSelfOnNull() {
         return true;
      }
   }
}