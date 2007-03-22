/* Copyright (C) 2004   db4objects Inc.   http://www.db4o.com */

package com.db4o.internal.query.processor;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;
import com.db4o.internal.classindex.*;
import com.db4o.internal.diagnostic.*;
import com.db4o.internal.fieldindex.*;
import com.db4o.internal.marshall.*;


/**
 * Holds the tree of {@link QCandidate} objects and the list of {@link QCon} during query evaluation.
 * The query work (adding and removing nodes) happens here.
 * Candidates during query evaluation. {@link QCandidate} objects are stored in i_root
 * 
 * @exclude
 */
public final class QCandidates implements Visitor4 {

    // Transaction necessary as reference to stream
    public final LocalTransaction i_trans;

    // root of the QCandidate tree
    public Tree i_root;

    // collection of all constraints
    private List4 i_constraints;

    // possible class information
    ClassMetadata i_yapClass;

    // possible field information
    private QField i_field;

    // current executing constraint, only set where needed
    QCon i_currentConstraint;

    // QOrder tree
    Tree i_ordered;

    // 
    private int _majorOrderingID;
    
    private IDGenerator _idGenerator;

    QCandidates(LocalTransaction a_trans, ClassMetadata a_yapClass, QField a_field) {
    	i_trans = a_trans;
    	i_yapClass = a_yapClass;
    	i_field = a_field;
   
    	if (a_field == null
    			|| a_field.i_yapField == null
				|| !(a_field.i_yapField.getHandler() instanceof ClassMetadata)
    	) {
    		return;
    	}

    	ClassMetadata yc = (ClassMetadata) a_field.i_yapField.getHandler();
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
    
    void applyOrdering(Tree orderedCandidates, int orderingID) {
    	
    	if (orderedCandidates == null || i_root == null) {
    		return;
    	}
    	
    	int absoluteOrderingID = Math.abs(orderingID);
    	
    	final boolean major = treatOrderingIDAsMajor(absoluteOrderingID); 
    	
    	if(major && ! isUnordered()) {
	    	swapMajorOrderToMinor();
    	}
    	
    	hintNewOrder(orderedCandidates, major);
    	
    	i_root = recreateTreeFromCandidates();
    	
    	if (major) {
    		_majorOrderingID = absoluteOrderingID;
    	}
    }

	private Tree recreateTreeFromCandidates() {
		Collection4 col = collectCandidates();
		
    	Tree newTree = null;
    	Iterator4 i = col.iterator();
    	while(i.moveNext()){
    		QCandidate candidate = (QCandidate) i.current();
    		candidate._preceding = null;
    		candidate._subsequent = null;
    		candidate._size = 1;
    		newTree = Tree.add(newTree, candidate);
    	}
		return newTree;
	}

	private Collection4 collectCandidates() {
		final Collection4 col = new Collection4();
		i_root.traverse(new Visitor4() {
    		public void visit(Object a_object) {
    			QCandidate candidate = (QCandidate) a_object;
    			col.add(candidate);
    		}
    	});
		return col;
	}

	private void hintNewOrder(Tree orderedCandidates, final boolean major) {
		final int[] currentOrder = { 0 };
    	final QOrder[] lastOrder = {null}; 
    	
    	orderedCandidates.traverse(new Visitor4() {
    		public void visit(Object a_object) {
    			QOrder qo = (QOrder) a_object;
    			if(! qo.isEqual(lastOrder[0])){
    				currentOrder[0]++;
    			} 
    			QCandidate candidate = qo._candidate.getRoot();
    			candidate.hintOrder(currentOrder[0], major);
    			lastOrder[0] = qo;
    		}
    	});
	}

	private void swapMajorOrderToMinor() {
		i_root.traverse(new Visitor4() {
			public void visit(Object obj) {
				QCandidate candidate = (QCandidate)obj;
				Order order = (Order) candidate._order;
				order.swapMajorToMinor();
			}
		});
	}

	private boolean treatOrderingIDAsMajor(int absoluteOrderingID) {
		return (isUnordered()) || (isMoreRelevantOrderingID(absoluteOrderingID));
	}

	private boolean isUnordered() {
		return _majorOrderingID == 0;
	}

	private boolean isMoreRelevantOrderingID(int absoluteOrderingID) {
		return absoluteOrderingID < _majorOrderingID;
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
        final FieldIndexProcessorResult result = processFieldIndexes();
        if(result.foundIndex()){
        	i_root = result.toQCandidate(this);
        }else{
        	loadFromClassIndex();
        }
        evaluate();
    }
    
    public Iterator4 executeSnapshot(Collection4 executionPath){
    	IntIterator4 indexIterator = new IntIterator4Adaptor(iterateIndex(processFieldIndexes()));
    	Tree idRoot = TreeInt.addAll(null, indexIterator);
    	Iterator4 snapshotIterator = new TreeKeyIterator(idRoot);
    	Iterator4 singleObjectQueryIterator  = singleObjectSodaProcessor(snapshotIterator);
		return mapIdsToExecutionPath(singleObjectQueryIterator, executionPath);
    }
    
    private Iterator4 singleObjectSodaProcessor(Iterator4 indexIterator){
    	return new MappingIterator(indexIterator) {
			protected Object map(Object current) {
				int id = ((Integer)current).intValue();
				QCandidate candidate = new QCandidate(QCandidates.this, null, id, true); 
				i_root = candidate; 
				evaluate();
				if(! candidate.include()){
					return MappingIterator.SKIP;
				}
				return current;
			}
		};
    }
    
    public Iterator4 executeLazy(Collection4 executionPath){
    	Iterator4 indexIterator = iterateIndex(processFieldIndexes());
    	Iterator4 singleObjectQueryIterator  = singleObjectSodaProcessor(indexIterator);
		return mapIdsToExecutionPath(singleObjectQueryIterator, executionPath);
    }
    
    private Iterator4 iterateIndex (FieldIndexProcessorResult result ){
    	if(result.noMatch()){
    		return Iterators.EMPTY_ITERATOR;
    	}
    	if(result.foundIndex()){
    		return result.iterateIDs();
    	}
    	if(i_yapClass.isPrimitive()){
    		return Iterators.EMPTY_ITERATOR;
    	}
    	return BTreeClassIndexStrategy.iterate(i_yapClass, i_trans);
    }

	private Iterator4 mapIdsToExecutionPath(Iterator4 singleObjectQueryIterator, Collection4 executionPath) {
		
		if(executionPath == null){
			return singleObjectQueryIterator;
		}
		
		Iterator4 res = singleObjectQueryIterator;
		
		Iterator4 executionPathIterator = executionPath.iterator();
		while(executionPathIterator.moveNext()){
			
			final String fieldName = (String) executionPathIterator.current();
			
			Iterator4 mapIdToFieldIdsIterator = new MappingIterator(res){
				
				protected Object map(Object current) {
					int id = ((Integer)current).intValue();
                    StatefulBuffer reader = stream().readWriterByID(i_trans, id);
                    if (reader == null) {
                    	return MappingIterator.SKIP;
                    }
                    	
                    ObjectHeader oh = new ObjectHeader(stream(), reader);
                    
                    Tree idTree = oh.yapClass().collectFieldIDs(
                            oh._marshallerFamily,
                            oh._headerAttributes,
                            null,
                            reader,
                            fieldName);

					return new TreeKeyIterator(idTree);
				}
				
			};
			
			res = new CompositeIterator4(mapIdToFieldIdsIterator);
			
		}
		return res;
	}
    
	public ObjectContainerBase stream() {
		return i_trans.stream();
	}

	public int classIndexEntryCount() {
		return i_yapClass.indexEntryCount(i_trans);
	}

	private FieldIndexProcessorResult processFieldIndexes() {
		if(i_constraints == null){
			return FieldIndexProcessorResult.NO_INDEX_FOUND;
		}
		return new FieldIndexProcessor(this).run();
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
            return Iterators.EMPTY_ITERATOR;
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
                if (i_field.i_name!=null&&!i_field.i_name.equals(qf.i_name)) {
                    return false;
                }
            }
        }

        if (i_yapClass == null || a_constraint.isNullConstraint()) {
            addConstraint(a_constraint);
            return true;
        }
        ClassMetadata yc = a_constraint.getYapClass();
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
    
    public String toString() {
    	final StringBuffer sb = new StringBuffer();
    	i_root.traverse(new Visitor4() {
			public void visit(Object obj) {
				QCandidate candidate = (QCandidate) obj;
				sb.append(" ");
				sb.append(candidate._key);
			}
		});
    	return sb.toString();
    }

	public void clearOrdering() {
		i_ordered = null;
	}
}
