/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o;

import com.db4o.foundation.*;
import com.db4o.inside.*;
import com.db4o.inside.classindex.ClassIndexStrategy;
import com.db4o.inside.diagnostic.DiagnosticProcessor;
import com.db4o.inside.fieldindex.*;
import com.db4o.inside.ix.QxProcessor;
import com.db4o.inside.marshall.MarshallerFamily;

/**
 * Holds the tree of {@link QCandidate} objects and the list of {@link QCon} during query evaluation.
 * The query work (adding and removing nodes) happens here.
 * Candidates during query evaluation. {@link QCandidate} objects are stored in i_root
 * 
 * @exclude
 */
public final class QCandidates implements Visitor4 {

    // Transaction necessary as reference to stream
    public final Transaction i_trans;

    // root of the QCandidate tree
    private Tree i_root;

    // collection of all constraints
    private List4 i_constraints;

    // possible class information
    YapClass i_yapClass;

    // possible field information
    private QField i_field;

    // current executing constraint, only set where needed
    QCon i_currentConstraint;

    // QOrder tree
    Tree i_ordered;

    // 
    private int i_orderID;
    
    private IDGenerator _idGenerator;

    QCandidates(Transaction a_trans, YapClass a_yapClass, QField a_field) {
    	i_trans = a_trans;
    	i_yapClass = a_yapClass;
    	i_field = a_field;
   
    	if (a_field == null
    			|| a_field.i_yapField == null
				|| !(a_field.i_yapField.getHandler() instanceof YapClass)
    	) {
    		return;
    	}

    	YapClass yc = (YapClass) a_field.i_yapField.getHandler();
    	if (i_yapClass == null) {
    		i_yapClass = yc;
    	} else {
    		yc = i_yapClass.getHigherOrCommonHierarchy(yc);
    		if (yc != null) {
    			i_yapClass = yc;
    		}
    	}
    }

    public QCandidate addByIdentity(QCandidate candidate) {
        i_root = Tree.add(i_root, candidate);
        if(candidate._size == 0){
        	
        	// This means that the candidate was already present
        	// and QCandidate does not allow duplicates.
        	
        	// In this case QCandidate#isDuplicateOf will have
        	// placed the existing QCandidate in the i_root
        	// variable of the new candidate. We return it here: 
        	
        	return candidate.getRoot();
        
        }
        return candidate;
    }

    void addConstraint(QCon a_constraint) {
        i_constraints = new List4(i_constraints, a_constraint);
    }

    void addOrder(QOrder a_order) {
        i_ordered = Tree.add(i_ordered, a_order);
    }

    void applyOrdering(Tree a_ordered, int a_orderID) {
    	if (a_ordered == null || i_root == null) {
    		return;
    	}
    	if (a_orderID > 0) {
    		a_orderID = -a_orderID;
    	}
    	final boolean major = (a_orderID - i_orderID) < 0;
    	if (major) {
    		i_orderID = a_orderID;
    	}
    	
    	final int[] placement = { 0 };
    	// Step 1: Clear possible old ordering criteria
    	//         and store old order.
    	i_root.traverse(new Visitor4() {
    		public void visit(Object a_object) {
    			((QCandidate) a_object).hintOrder(0, major);
    			((QCandidate) a_object).hintOrder(placement[0]++, !major);
    		}
    	});
    	
    	// Step 2: Set new ordering criteria
    	placement[0] = 1;
    	a_ordered.traverse(new Visitor4() {
    		public void visit(Object a_object) {
    			QOrder qo = (QOrder) a_object;
    			QCandidate candidate = qo._candidate.getRoot();
    			candidate.hintOrder(placement[0]++, major);
    		}
    	});
    	
    	// Step 3: We need to put them all into a collection,
    	//         so we can safely remove the old tree joins
    	final Collection4 col = new Collection4();
    	i_root.traverse(new Visitor4() {
    		public void visit(Object a_object) {
    			QCandidate candidate = (QCandidate) a_object;
    			col.add(candidate);
    			
    		}
    	});
    	
    	// Step 4: Add them to our tree again.
    	final Tree[] newTree = { null };
    	Iterator4 i = col.iterator();
    	while(i.moveNext()){
    		QCandidate candidate = (QCandidate) i.current();
    		candidate._preceding = null;
    		candidate._subsequent = null;
    		candidate._size = 1;
    		newTree[0] = Tree.add(newTree[0], candidate);
    	}
    	
    	i_root = newTree[0];
    }

    void collect(final QCandidates a_candidates) {
		Iterator4 i = iterateConstraints();
		while(i.moveNext()){
			QCon qCon = (QCon)i.current();
			setCurrentConstraint(qCon);
			qCon.collect(a_candidates);
		}
		setCurrentConstraint(null);
    }

    void execute() {
        if(DTrace.enabled){
            DTrace.QUERY_PROCESS.log();
        }
        boolean foundIndex = processFieldIndexes();
        if(!foundIndex){
            loadFromClassIndex();
        }
        evaluate();
    }

	public int classIndexEntryCount() {
		return i_yapClass.indexEntryCount(i_trans);
	}

	private boolean processFieldIndexes() {
		if(i_constraints == null){
			return false;
		}
		FieldIndexProcessor processor = new FieldIndexProcessor(this);
		final FieldIndexProcessorResult result = processor.run();
		if (result == FieldIndexProcessorResult.FOUND_INDEX_BUT_NO_MATCH) {
			return true;
		}
		if (result == FieldIndexProcessorResult.NO_INDEX_FOUND) {
			return false;
		}
		i_root = TreeInt.toQCandidate(result.found, this);
		return true;
	}

    void evaluate() {
    	
    	if (i_constraints == null) {
    		return;
    	}
    	
    	Iterator4 i = iterateConstraints();
    	while(i.moveNext()){
            QCon qCon = (QCon)i.current();
            qCon.setCandidates(this);
    		qCon.evaluateSelf();
    	}
    	
    	i = iterateConstraints();
    	while(i.moveNext()){
    		((QCon)i.current()).evaluateSimpleChildren();
    	}
    	
    	i = iterateConstraints();
    	while(i.moveNext()){
    		((QCon)i.current()).evaluateEvaluations();
    	}
    	
    	i = iterateConstraints();
    	while(i.moveNext()){
    		((QCon)i.current()).evaluateCreateChildrenCandidates();
    	}
    	
    	i = iterateConstraints();
    	while(i.moveNext()){
    		((QCon)i.current()).evaluateCollectChildren();
    	}
    	
    	i = iterateConstraints();
    	while(i.moveNext()){
    		((QCon)i.current()).evaluateChildren();
    	}
    }

    boolean isEmpty() {
        final boolean[] ret = new boolean[] { true };
        traverse(new Visitor4() {
            public void visit(Object obj) {
                if (((QCandidate) obj)._include) {
                    ret[0] = false;
                }
            }
        });
        return ret[0];
    }

    boolean filter(Visitor4 a_host) {
        if (i_root != null) {
            i_root.traverse(a_host);
            i_root = i_root.filter(new Predicate4() {
                public boolean match(Object a_candidate) {
                    return ((QCandidate) a_candidate)._include;
                }
            });
        }

        return i_root != null;
    }
    
    int generateCandidateId(){
        if(_idGenerator == null){
            _idGenerator = new IDGenerator();
        }
        return - _idGenerator.next();
    }
    
    public Iterator4 iterateConstraints(){
        if(i_constraints == null){
            return Iterator4Impl.EMPTY;
        }
        return new Iterator4Impl(i_constraints);
    }
    
    final static class TreeIntBuilder {
    	public TreeInt tree;
    	
    	public void add(TreeInt node) {
    		tree = (TreeInt)Tree.add(tree, node);
    	}
    }

    void loadFromClassIndex() {
    	if (!isEmpty()) {
    		return;
    	}
    	
    	final TreeIntBuilder result = new TreeIntBuilder();
    	final ClassIndexStrategy index = i_yapClass.index();
		index.traverseAll(i_trans, new Visitor4() {
    		public void visit(Object obj) {
    			result.add(new QCandidate(QCandidates.this, null, ((Integer)obj).intValue(), true));
    		}
    	});
    
		i_root = result.tree;
        
        DiagnosticProcessor dp = i_trans.stream().i_handlers._diagnosticProcessor;
        if (dp.enabled()){
            dp.loadedFromClassIndex(i_yapClass);
        }
        
    }

	void setCurrentConstraint(QCon a_constraint) {
        i_currentConstraint = a_constraint;
    }

    void traverse(Visitor4 a_visitor) {
        if(i_root != null){
            i_root.traverse(a_visitor);
        }
    }

    boolean tryAddConstraint(QCon a_constraint) {

        if (i_field != null) {
            QField qf = a_constraint.getField();
            if (qf != null) {
                if (i_field.i_name != qf.i_name) {
                    return false;
                }
            }
        }

        if (i_yapClass == null || a_constraint.isNullConstraint()) {
            addConstraint(a_constraint);
            return true;
        }
        YapClass yc = a_constraint.getYapClass();
        if (yc != null) {
            yc = i_yapClass.getHigherOrCommonHierarchy(yc);
            if (yc != null) {
                i_yapClass = yc;
                addConstraint(a_constraint);
                return true;
            }
        }
        return false;
    }

    public void visit(Object a_tree) {
    	final QCandidate parent = (QCandidate) a_tree;
    	if (parent.createChild(this)) {
    		return;
    	}
    	
    	// No object found.
    	// All children constraints are necessarily false.
    	// Check immediately.
		Iterator4 i = iterateConstraints();
		while(i.moveNext()){
			((QCon)i.current()).visitOnNull(parent.getRoot());
		}
    		
    }
}
