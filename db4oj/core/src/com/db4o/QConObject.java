/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.config.*;
import com.db4o.query.*;
import com.db4o.reflect.*;

/**
 * Object constraint
 *
 * @exclude
 */
public class QConObject extends QCon {

    // the constraining object
    Object                        i_object;

    // cache for the db4o object ID
    int                           i_objectID;

    // the YapClass
    transient YapClass            i_yapClass;

    // needed for marshalling the request
    int                           i_yapClassID;

    QField                        i_field;

    transient YapComparable       i_comparator;

    ObjectAttribute               i_attributeProvider;

    private transient boolean     i_selfComparison = false;

    private transient IxTraverser i_indexTraverser;

    private transient QCon        i_indexConstraint;

    private transient boolean     i_loadedFromIndex;

    public QConObject() {
        // C/S only
    }

    QConObject(Transaction a_trans, QCon a_parent, QField a_field,
        Object a_object) {
        super(a_trans);
        i_parent = a_parent;
        if (a_object instanceof Compare) {
            a_object = ((Compare) a_object).compare();
        }
        i_object = a_object;
        i_field = a_field;
        associateYapClass(a_trans, a_object);
    }

    private void associateYapClass(Transaction a_trans, Object a_object) {
        if (a_object == null) {
            i_object = null;
            i_comparator = Null.INSTANCE;
            i_yapClass = null;
        } else {
            i_yapClass = a_trans.i_stream
                .getYapClass(a_trans.reflector().forObject(a_object), true);
            if (i_yapClass != null) {
                i_object = i_yapClass.getComparableObject(a_object);
                if (a_object != i_object) {
                    i_attributeProvider = i_yapClass.i_config.i_queryAttributeProvider;
                    i_yapClass = a_trans.i_stream.getYapClass(a_trans.reflector().forObject(i_object)
                        , true);
                }
                if (i_yapClass != null) {
                    i_yapClass.collectConstraints(a_trans, this, i_object,
                        new Visitor4() {

                            public void visit(Object obj) {
                                addConstraint((QCon) obj);
                            }
                        });
                } else {
                    associateYapClass(a_trans, null);
                }
            } else {
                associateYapClass(a_trans, null);
            }
        }
    }

    int candidateCountByIndex() {
        int count = -1; // indicates no index
        if (i_joins == null) {
            if (i_subConstraints != null) {
                Iterator4 i = new Iterator4(i_subConstraints);
                while (i.hasNext()) {
                    QCon qCon = (QCon) i.next();
                    int newCount = qCon.candidateCountByIndex(1);
                    if (newCount >= 0) {
                        if (count == -1 || newCount < count) {
                            i_indexConstraint = qCon;
                            count = newCount;
                        }
                    }
                }
            }
        }
        return count;
    }

    int candidateCountByIndex(int depth) {
        int count = -1; // indicates no index
        if (depth == 1) {

            // TODO: try to resolve joins also

            if (i_joins == null && i_field != null
                && i_field.i_yapField != null && i_field.i_yapField.hasIndex()
                && i_field.i_yapField.canLoadByIndex(this, i_evaluator)) {

                i_indexTraverser = new IxTraverser();
                count = i_indexTraverser.findBoundsQuery(this,
                    (IxTree) i_field.i_yapField.getIndexRoot(i_trans));
            }

            // TODO: may load from deeper indexes also

        }
        return count;
    }

    void createCandidates(Collection4 a_candidateCollection) {
        if (i_loadedFromIndex && i_subConstraints == null) {
            return;
        }
        super.createCandidates(a_candidateCollection);
    }

    boolean evaluate(QCandidate a_candidate) {
        try {
            return a_candidate.evaluate(this, i_evaluator);
        } catch (Exception e) {
            return false;
        }
    }

    void evaluateEvaluationsExec(final QCandidates a_candidates,
        boolean rereadObject) {
        if (i_field.isSimple()) {
            boolean hasEvaluation = false;
            if (i_subConstraints != null) {
                Iterator4 i = new Iterator4(i_subConstraints);
                while (i.hasNext()) {
                    if (i.next() instanceof QEvaluation) {
                        hasEvaluation = true;
                        break;
                    }
                }
            }
            if (hasEvaluation) {
                a_candidates.traverse(i_field);
                Iterator4 i = new Iterator4(i_subConstraints);
                while (i.hasNext()) {
                    ((QCon) i.next()).evaluateEvaluationsExec(a_candidates,
                        false);
                }
            }
        }
    }

    void evaluateSelf() {
        if (i_yapClass != null) {
            if (!(i_yapClass instanceof YapClassPrimitive)) {
                if (!i_evaluator.identity()) {
                    if (i_yapClass == i_candidates.i_yapClass) {
                        if (i_evaluator == QE.DEFAULT && (i_joins == null)) {
                            return;
                        }
                    }
                    i_selfComparison = true;
                }
                i_comparator = i_yapClass.prepareComparison(i_object);
            }
        }
        super.evaluateSelf();
        i_selfComparison = false;
    }

    void collect(QCandidates a_candidates) {
        if (i_field.isClass()) {
            a_candidates.traverse(i_field);
            a_candidates.filter(i_candidates);
        }
    }

    void evaluateSimpleExec(QCandidates a_candidates) {
        if (i_orderID != 0 || !i_loadedFromIndex) {
            if (i_field.isSimple() || isNullConstraint()) {
                a_candidates.traverse(i_field);
                prepareComparison(i_field);
                a_candidates.filter(this);
            }
        }
    }

    YapComparable getComparator(QCandidate a_candidate) {
        if (i_comparator == null) {
            return a_candidate.prepareComparison(i_trans.i_stream, i_object);
        }
        return i_comparator;
    }

    YapClass getYapClass() {
        return i_yapClass;
    }

    QField getField() {
        return i_field;
    }

    int getObjectID() {
        if (i_objectID == 0) {
            i_objectID = i_trans.i_stream.getID1(i_trans, i_object);
            if (i_objectID == 0) {
                i_objectID = -1;
            }
        }
        return i_objectID;
    }

    boolean hasObjectInParentPath(Object obj) {
        if (obj == i_object) {
            return true;
        }
        return super.hasObjectInParentPath(obj);
    }

    public void identityEvaluation() {
        if (i_evaluator.identity()) {
            int id = getObjectID();
            if (id != 0) {
                i_candidates.addByIdentity(new QCandidate(i_candidates, id,
                    !(i_evaluator instanceof QENot)));
            }
        }
    }

    boolean isNullConstraint() {
        return i_object == null;
    }

    Tree loadFromBestChildIndex(QCandidates a_candidates) {
        return i_indexConstraint.loadFromIndex(a_candidates);
    }

    Tree loadFromIndex(QCandidates a_candidates) {
        i_loadedFromIndex = true;
        return i_indexTraverser.getMatches(a_candidates);
    }

    void log(String indent) {
        if (Deploy.debugQueries) {
            super.log(indent);
        }
    }

    String logObject() {
        if (Deploy.debugQueries) {
            if (i_object != null) {
                return i_object.toString();
            }
            return "[NULL]";
        } else {
            return "";
        }
    }

    void marshall() {
        super.marshall();
        getObjectID();
        if (i_yapClass != null) {
            i_yapClassID = i_yapClass.getID();
        }
    }

    void prepareComparison(QField a_field) {
        if (isNullConstraint() & !a_field.isArray()) {
            i_comparator = Null.INSTANCE;
        } else {
            i_comparator = a_field.prepareComparison(i_object);
        }
    }

    void removeChildrenJoins() {
        super.removeChildrenJoins();
        i_subConstraints = null;
    }

    QCon shareParent(Object a_object, boolean[] removeExisting) {
        if (i_parent != null) {
            if (i_field.canHold(a_object)) {
                return i_parent.addSharedConstraint(i_field, a_object);
            }
        }
        return null;
    }

    QConClass shareParentForClass(IClass a_class, boolean[] removeExisting) {
        if (i_parent != null) {
            if (i_field.canHold(a_class)) {
                QConClass newConstraint = new QConClass(i_trans, i_parent,
                    i_field, a_class);
                i_parent.addConstraint(newConstraint);
                return newConstraint;
            }
        }
        return null;
    }

    final Object translate(Object candidate) {
        if (i_attributeProvider != null) {
            i_candidates.i_trans.i_stream.activate1(i_candidates.i_trans,
                candidate);
            return i_attributeProvider.attribute(candidate);
        }
        return candidate;
    }

    void unmarshall(Transaction a_trans) {
        if (i_trans == null) {
            super.unmarshall(a_trans);

            if (i_object == null) {
                i_comparator = Null.INSTANCE;
            }
            if (i_yapClassID != 0) {
                i_yapClass = a_trans.i_stream.getYapClass(i_yapClassID);
            }
            if (i_field != null) {
                i_field.unmarshall(a_trans);
            }
            
            if(i_objectID != 0){
                Object obj = a_trans.i_stream.getByID(i_objectID);
                if(obj != null){
                    i_object = obj;
                }
            }
        }
    }

    public void visit(Object obj) {
        QCandidate qc = (QCandidate) obj;
        boolean res = true;
        boolean processed = false;
        if (i_selfComparison) {
            YapClass yc = qc.readYapClass();
            if (yc != null) {
                res = i_evaluator
                    .not(i_yapClass.getHigherHierarchy(yc) == i_yapClass);
                processed = true;
            }
        }
        if (!processed) {
            res = evaluate(qc);
        }
        if (i_orderID != 0 && res) {
            Object cmp = qc.value();
            if (cmp != null && i_field != null) {
                YapComparable comparatorBackup = i_comparator;
                i_comparator = i_field.prepareComparison(qc.value());
                i_candidates.addOrder(new QOrder(this, qc));
                i_comparator = comparatorBackup.prepareComparison(i_object);
            }
        }
        visit1(qc.getRoot(), this, res);
    }

    public Constraint contains() {
        synchronized (streamLock()) {
            i_evaluator = i_evaluator.add(new QEContains());
            return this;
        }
    }

    public Constraint equal() {
        synchronized (streamLock()) {
            i_evaluator = i_evaluator.add(new QEEqual());
            return this;
        }
    }

    public Object getObject() {
        synchronized (streamLock()) {
            return i_object;
        }
    }

    public Constraint greater() {
        synchronized (streamLock()) {
            i_evaluator = i_evaluator.add(new QEGreater());
            return this;
        }
    }

    public Constraint identity() {
        synchronized (streamLock()) {

            // TODO: this is may not be correct for NOT
            // It may be necessary to add an if(i_evaluator.identity())
            removeChildrenJoins();
            i_evaluator = i_evaluator.add(new QEIdentity());
            return this;
        }
    }

    public Constraint like() {
        synchronized (streamLock()) {
            i_evaluator = i_evaluator.add(new QELike());
            return this;
        }
    }

    public Constraint smaller() {
        synchronized (streamLock()) {
            i_evaluator = i_evaluator.add(new QESmaller());
            return this;
        }
    }

    public String toString() {
        if (Deploy.debugQueries) {
            String str = "QConObject ";
            if (i_object != null) {
                str += i_object.toString();
            }
            return str;
        }
        return super.toString();
    }

}