/* Copyright (C) 2004 - 2011  Versant Inc.  http://www.db4o.com */

package com.db4o.internal.query.processor;

import com.db4o.foundation.*;
import com.db4o.internal.classindex.*;
import com.db4o.internal.fieldindex.*;
import com.db4o.internal.query.processor.QCandidates.*;

/**
 * @exclude
 */
public class QueryResultCandidates {
	
	private FieldIndexProcessorResult _fieldIndexProcessorResult;
	
	private QCandidate _candidates;

	private QCandidates _qCandidates;
	
	ClassIndexStrategy _classIndex;
	
	public QueryResultCandidates(QCandidates qCandidates){
		_qCandidates = qCandidates;
	}

	public void add(QCandidate candidate) {
		_candidates = Tree.add(_candidates, candidate);
	}

	public void fieldIndexProcessorResult(FieldIndexProcessorResult result) {
		_fieldIndexProcessorResult = result;
    	_candidates = (QCandidate) result.toQCandidate(_qCandidates);
	}

	public void singleCandidate(QCandidate candidate) {
		_candidates = candidate;
	}
	
    boolean filter(Visitor4 visitor) {
        if (_candidates != null) {
        	_candidates.traverse(visitor);
        	_candidates = (QCandidate) _candidates.filter(new Predicate4() {
                public boolean match(Object a_candidate) {
                    return ((QCandidate) a_candidate)._include;
                }
            });
        }
        return _candidates != null;
    }

	public void loadFromClassIndex(ClassIndexStrategy index) {
		_classIndex = index;
    	final TreeIntBuilder result = new TreeIntBuilder();
		index.traverseAll(_qCandidates.transaction(), new Visitor4() {
    		public void visit(Object obj) {
    			result.add(new QCandidate(_qCandidates, null, ((Integer)obj).intValue()));
    		}
    	});
		_candidates = (QCandidate) result.tree;
	}
	
    void traverse(Visitor4 visitor) {
        if(_candidates != null){
            _candidates.traverse(visitor);
        }
    }



}
