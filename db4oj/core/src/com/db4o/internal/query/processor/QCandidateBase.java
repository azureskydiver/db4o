/* Copyright (C) 2004 - 2010  Versant Inc.  http://www.db4o.com */
package com.db4o.internal.query.processor;

import com.db4o.*;
import com.db4o.foundation.*;
import com.db4o.internal.*;

public abstract class QCandidateBase extends TreeInt implements InternalCandidate {

	protected final QCandidates _candidates;
	protected List4 _dependants;
	boolean _include = true;
	Tree _pendingJoins;
	QCandidateBase _root;

	public QCandidateBase(QCandidates candidates, int id) {
		super(id);
		if (DTrace.enabled) {
			DTrace.CREATE_CANDIDATE.log(id);
		}
        _candidates = candidates;
        
        if(id == 0){
            _key = candidates.generateCandidateId();
        }
        if(Debug4.queries){
            System.out.println("Candidate identified ID:" + _key );
        }
	}

	protected void addDependant(QCandidateBase a_candidate) {
		_dependants = new List4(_dependants, a_candidate);
	}

	void doNotInclude() {
		include(false);
		if (_dependants != null) {
			Iterator4 i = new Iterator4Impl(_dependants);
			_dependants = null;
			while (i.moveNext()) {
				((QCandidateBase) i.current()).doNotInclude();
			}
		}
	}

	boolean evaluate(QPending a_pending) {
	
		if (Debug4.queries) {
			System.out.println("Pending arrived Join: " + a_pending._join.id()
					+ " Constraint:" + a_pending._constraint.id() + " res:"
					+ a_pending._result);
		}
	
		QPending oldPending = (QPending) Tree.find(_pendingJoins, a_pending);
	
		if (oldPending == null) {
			a_pending.changeConstraint();
			_pendingJoins = Tree.add(_pendingJoins, a_pending.internalClonePayload());
			return true;
		} 
		_pendingJoins = _pendingJoins.removeNode(oldPending);
		oldPending._join.evaluatePending(this, oldPending, a_pending._result);
		return false;
	}

	public ObjectContainer objectContainer() {
		return container();
	}

	public QCandidateBase getRoot() {
		return _root == null ? this : _root;
	}

	final LocalObjectContainer container() {
		return transaction().localContainer();
	}

	public final LocalTransaction transaction() {
		return _candidates.i_trans;
	}

	public boolean include() {
		return _include;
	}

	/**
	 * For external interface use only. Call doNotInclude() internally so
	 * dependancies can be checked.
	 */
	public void include(boolean flag) {
		// TODO:
		// Internal and external flag may need to be handled seperately.
		_include = flag;
		if(Debug4.queries){
		    System.out.println("Candidate include " + flag + " ID: " + _key);
	    }
	
	}

	@Override
	public Tree onAttemptToAddDuplicate(Tree oldNode) {
		_size = 0;
		_root = (QCandidateBase) oldNode;
		return oldNode;
	}

	@Override
	public boolean duplicates() {
		return _root != null;
	}

	public QCandidates candidates() {
		return _candidates;
	}
}