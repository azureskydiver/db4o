/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.config.*;
import com.db4o.query.*;

class QCandidate extends TreeInt implements Candidate, Orderable {

    // db4o ID is stored in i_key;
    
    // db4o byte stream storing the object
    YapReader i_bytes;

    final QCandidates i_candidates;

    // Dependant candidates
    private List4 i_dependants;

    // whether to include in the result set
    // may use id for optimisation ???
    boolean i_include = true;

    private Object i_member;

    // Comparable
    Orderable i_order;

    // Possible pending joins on children
    Tree i_pendingJoins;

    // The evaluation root to compare all ORs
    private QCandidate i_root;

    // the YapClass of this object
    YapClass i_yapClass;

    // temporary yapField and member for one field during evaluation
    YapField i_yapField; // null denotes null object

    private QCandidate() {
        super(0);
        i_candidates = null;
        // dummy constructor to get "this" out of declaration for C#
    }

    QCandidate(QCandidates candidates, int id, boolean include) {
        super(id);
        i_candidates = candidates;
        i_order = this;
        i_include = include;
    }

    QCandidate(QCandidates candidates, Object obj, int id) {
        super(id);
        i_candidates = candidates;
        i_order = this;
        i_member = obj;
    }

    void addDependant(QCandidate a_candidate) {
        i_dependants = new List4(i_dependants, a_candidate);
    }

    private void checkInstanceOfCompare() {
        if (i_member instanceof Compare) {
            i_member = ((Compare)i_member).compare();
            YapFile stream = getStream();
            i_yapClass = stream.getYapClass(i_member.getClass(), false);
            i_key = (int)stream.getID(i_member);
            i_bytes = stream.readReaderByID(getTransaction(), i_key);
        }
    }

    /**
	 * **
	 * <Tree Code>***
	 */

    int compare(Tree a_to) {
        return i_order.compareTo(((QCandidate)a_to).i_order);
    }

    public int compareTo(Object a_object) {
        return i_key - ((TreeInt)a_object).i_key;
    }

    boolean createChild(final QCandidates a_candidates) {
        if (i_include) {

            QCandidate candidate = null;

            if (i_yapField != null) {
                YapDataType handler = i_yapField.getHandler();
                if (handler != null) {

                    final YapReader[] arrayBytes = { i_bytes };
                    final YapDataType arrayWrapper =
                        handler.readArrayWrapper(getTransaction(), arrayBytes);

                    if (arrayWrapper != null) {

                        final int offset = arrayBytes[0]._offset;
                        boolean outerRes = true;

                        // The following construct is worse than not ideal.
                        // For each constraint it completely reads the
						// underlying structure again. The structure could b
                        // kept fairly easy. TODO: Optimize!

                        if (a_candidates.i_constraints != null) {
                            Iterator4 i = new Iterator4(a_candidates.i_constraints);
                            while (i.hasNext()) {

                                QCon qcon = (QCon)i.next();
                                QField qf = qcon.getField();
                                if (qf == null || qf.i_name.equals(i_yapField.getName())) {

                                    QCon tempParent = qcon.i_parent;
                                    qcon.setParent(null);

                                    final QCandidates candidates =
                                        new QCandidates(a_candidates.i_trans, null, qf);
                                    candidates.addConstraint(qcon);

                                    qcon.setCandidates(candidates);
                                    arrayWrapper.readCandidates(arrayBytes[0], candidates);
                                    arrayBytes[0]._offset = offset;

                                    final boolean isNot = qcon.isNot();
                                    if (isNot) {
                                        qcon.removeNot();
                                    }

                                    candidates.evaluate();

                                    final Tree[] pending = new Tree[1];
                                    final boolean[] innerRes = { isNot };
                                    candidates.traverse(new Visitor4() {
                                        public void visit(Object obj) {

                                            QCandidate cand = (QCandidate)obj;

                                            if (cand.include()) {
                                                innerRes[0] = !isNot;
                                            }

                                            // Collect all pending subresults.
                                            
                                            if(cand.i_pendingJoins != null){
                                                cand.i_pendingJoins.traverse(new Visitor4() {
                                                    public void visit(Object a_object) {
                                                        QPending newPending = (QPending)a_object;

                                                        // We need to change
														// the
                                                        // constraint here, so
														// our
                                                        // pending collector
														// uses
                                                        // the right
														// comparator.
                                                        newPending.changeConstraint();
                                                        QPending oldPending =
                                                            (QPending)Tree.find(
                                                                pending[0],
                                                                newPending);
                                                        if (oldPending != null) {

                                                            // We only keep one
                                                            // pending result
															// for
                                                            // all array
															// elements.
                                                            // and memorize,
                                                            // whether we had a
                                                            // true or a false
                                                            // result.
                                                            // or both.

                                                            if (oldPending.i_result
                                                                != newPending.i_result) {
                                                                oldPending.i_result = QPending.BOTH;
                                                            }

                                                        } else {
                                                            pending[0] =
                                                                Tree.add(pending[0], newPending);
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });

                                    if (isNot) {
                                        qcon.not();
                                    }

                                    // In case we had pending subresults, we
									// need to communicate
                                    // them up to our root.
                                    if (pending[0] != null) {
                                        pending[0].traverse(new Visitor4() {
                                            public void visit(Object a_object) {
                                                getRoot().evaluate((QPending)a_object);
                                            }
                                        });
                                    }

                                    if (!innerRes[0]) {

                                        if (Deploy.debugQueries) {
                                            System.out.println(
                                                "  Array evaluation false. Constraint:"
                                                    + qcon.i_id);
                                        }

                                        // Again this could be double triggering.
										// 
                                        // We want to clean up the "No route"
										// at some stage.
                                        
                                        qcon.visit(getRoot(), qcon.i_evaluator.not(false));

                                        outerRes = false;
                                    }

                                    qcon.setParent(tempParent);

                                }
                            }
                        }

                        return outerRes;
                    }

                    // We may get simple types here too, if the YapField was null
                    // in the higher level simple evaluation. Evaluate these
					// immediately.

                    if (handler.getType() == YapConst.TYPE_SIMPLE) {
                        a_candidates.i_currentConstraint.visit(this);
                        return true;
                    }
                }
            }

            if (candidate == null) {
                candidate = readSubCandidate(a_candidates);
            }
            if (candidate != null) {

                // fast early check for YapClass
                if (a_candidates.i_yapClass != null && a_candidates.i_yapClass.isStrongTyped()) {
                    if (i_yapField != null) {
                        YapDataType handler = i_yapField.getHandler();
                        if (handler != null && (handler.getType() == YapConst.TYPE_CLASS)) {
                            YapClass yc = (YapClass)handler;
                            if (yc instanceof YapClassAny) {
                                yc = candidate.readYapClass();
                            }
                            if (!yc.canHold(a_candidates.i_yapClass.getJavaClass())) {
                                return false;
                            }
                        }
                    }
                }

                addDependant(a_candidates.addByIdentity(candidate));
                return true;
            }
        }
        return false;
    }

    void doNotInclude() {
        i_include = false;
        if (i_dependants != null) {
            Iterator4 i = new Iterator4(i_dependants);
            i_dependants = null;
            while (i.hasNext()) {
                ((QCandidate)i.next()).doNotInclude();
            }
        }
    }

    boolean duplicates() {
        return i_order.hasDuplicates();
    }

    boolean evaluate(final QConObject a_constraint, final QE a_evaluator) {
        if (i_member == null) {
            i_member = value();
        }
        return a_evaluator.evaluate(a_constraint, this, a_constraint.translate(i_member));
    }

    boolean evaluate(QPending a_pending) {

        if (Deploy.debugQueries) {
            System.out.println(
                "Pending arrived Join: "
                    + a_pending.i_join.i_id
                    + " Constraint:"
                    + a_pending.i_constraint.i_id
                    + " res:"
                    + a_pending.i_result);
        }

        QPending oldPending = (QPending)Tree.find(i_pendingJoins, a_pending);

        if (oldPending == null) {
            a_pending.changeConstraint();
            i_pendingJoins = Tree.add(i_pendingJoins, a_pending);
            return true;
        } else {
            i_pendingJoins = i_pendingJoins.removeNode(oldPending);
            oldPending.i_join.evaluatePending(this, oldPending, a_pending, a_pending.i_result);
            return false;
        }

    }

    Class getJavaClass() {
        readYapClass();
        if (i_yapClass == null) {
            return null;
        }
        return i_yapClass.getJavaClass();
    }

    /** **<Candidate interface code>*** */
    
    public ObjectContainer objectContainer(){
        return getStream();
    }

    public Object getObject() {
        Object obj = value(true);
        if(obj instanceof YapReader) {
            /* CHANGED (pr) */
            YapReader reader=(YapReader)obj;
            int offset=reader._offset;
            obj = reader.toString(getTransaction());
            reader._offset=offset;
        }
        return obj;
    }

    QCandidate getRoot() {
        return i_root == null ? this : i_root;
    }

    private YapFile getStream() {
        return getTransaction().i_file;
    }

    private Transaction getTransaction() {
        return i_candidates.i_trans;
    }

    public boolean hasDuplicates() {

        // Subcandidates are evaluated along with their constraints
        // in one big QCandidates object. The tree can have duplicates
        // so evaluation can be cascaded up to different roots.

        return i_root != null;
    }

    public void hintOrder(int a_order, boolean a_major) {
        i_order = new Order();
        i_order.hintOrder(a_order, a_major);
    }

    public boolean include() {
        return i_include;
    }

    /**
	 * For external interface use only. Call doNotInclude() internally so
	 * dependancies can be checked.
	 */
    public void include(boolean flag) {
        // TODO:
        // Internal and external flag may need to be handled seperately.
        i_include = flag;
    }

    void isDuplicateOf(Tree a_tree) {
        i_size = 0;
        i_root = (QCandidate)a_tree;
    }

    YapComparable prepareComparison(YapStream a_stream, Object a_constraint) {
        if (i_yapField != null) {
            return i_yapField.prepareComparison(a_constraint);
        }
        if (i_yapClass == null) {
            YapClass yc = null;
            if (i_bytes != null) {
                yc = a_stream.getYapClass(a_constraint.getClass(), true);
            } else {
                if (i_member != null) {
                    yc = a_stream.getYapClass(i_member.getClass(), false);
                }
            }
            if (yc != null) {
                if (i_member != null && i_member.getClass().isArray()) {
                    YapDataType ydt = (YapDataType)yc.prepareComparison(a_constraint);
                    if (Array4.isNDimensional(i_member.getClass())) {
                        YapArrayN yan = new YapArrayN(ydt, false);
                        return yan;
                    } else {
                        YapArray ya = new YapArray(ydt, false);
                        return ya;
                    }
                } else {
                    return yc.prepareComparison(a_constraint);
                }
            }
            return null;
        } else {
            return i_yapClass.prepareComparison(a_constraint);
        }
    }

    private void read() {
        if (i_include) {
            if (i_bytes == null) {
                if (i_key > 0) {
                    i_bytes = getStream().readReaderByID(getTransaction(), i_key);
                    if (i_bytes == null) {
                        i_include = false;
                    }
                } else {
                    i_include = false;
                }
            }
        }
    }

    private QCandidate readSubCandidate(QCandidates candidateCollection) {
        int id = 0;
        read();
        if (i_bytes != null) {
            final int offset = i_bytes._offset;

            try {
                id = i_bytes.readInt();
            } catch (Exception e) {
                return null;
            }
            i_bytes._offset = offset;

            if (id != 0) {
                QCandidate candidate = new QCandidate(candidateCollection, id, true);
                candidate.i_root = getRoot();
                return candidate;
            }
        }
        return null;
    }

    private void readThis(boolean a_activate) {
        read();

        Transaction trans = getTransaction();
        if (trans != null) {

            i_member = trans.i_stream.getByID1(trans, i_key);

            if (i_member != null && (a_activate || i_member instanceof Compare)) {
                trans.i_stream.activate1(trans, i_member);
                checkInstanceOfCompare();
            }
        }
    }

    YapClass readYapClass() {
        if (i_yapClass == null) {
            read();
            if (i_bytes != null) {

                i_bytes._offset = 0;
                if (Deploy.debug) {
                    i_bytes.readBegin(0, YapConst.YAPOBJECT);
                }
                i_yapClass = getStream().getYapClass(i_bytes.readInt());
                if(i_yapClass != null){
	                if (YapConst.CLASS_COMPARE.isAssignableFrom(i_yapClass.getJavaClass())) {
	                    readThis(false);
	                }
                }
            }
        }
        return i_yapClass;
    }

    public String toString() {
        if (Deploy.debugQueries) {
            String str = "QCandidate ";
            if (i_yapClass != null) {
                str += "\n   YapClass " + i_yapClass.getName();
            }
            if (i_yapField != null) {
                str += "\n   YapField " + i_yapField.getName();
            }
            if (i_member != null) {
                str += "\n   Member " + i_member.toString();
            }
            if (i_root != null) {
                str += "\n  rooted by:\n";
                str += i_root.toString();
            } else {
                str += "\n  ROOT";
            }
            return str;
        }
        return super.toString();
    }

    void useField(QField a_field) {
        read();
        if (i_bytes == null) {
            i_yapField = null;
        } else {
            readYapClass();
            i_member = null;
            if (a_field == null) {
                i_yapField = null;
            } else {
                if(i_yapClass == null){
                    i_yapField = null;
                }else{
	                i_yapField = a_field.getYapField(i_yapClass);
	                if (i_yapField == null | ! i_yapClass.findOffset(i_bytes, i_yapField)) {
	                    if (i_yapClass.holdsAnyClass()) {
	                        i_yapField = null;
	                    } else {
	                        i_yapField = new YapFieldNull();
	                    }
	                }
                }
            }
        }
    }

    Object value() {
        return value(false);
    }

    // TODO: This is only used for Evaluations. Handling may need
    // to be different for collections also.
    Object value(boolean a_activate) {
        if (i_member == null) {
            if (i_yapField == null) {
                readThis(a_activate);
            } else {
                int offset = i_bytes._offset;
                try {
                    i_member = i_yapField.readQuery(getTransaction(), i_bytes);
                } catch (CorruptionException ce) {
                    i_member = null;
                }
                i_bytes._offset = offset;
                checkInstanceOfCompare();
            }
        }
        return i_member;
    }
}
