package com.db4o.drs.inside;

import com.db4o.drs.ObjectState;
import com.db4o.drs.ReplicationEvent;

final class ReplicationEventImpl implements ReplicationEvent {

	final ObjectStateImpl _stateInProviderA = new ObjectStateImpl();
	final ObjectStateImpl _stateInProviderB = new ObjectStateImpl();
	boolean _isConflict;

	ObjectState _actionChosenState;
	boolean _actionWasChosen;
	boolean _actionShouldStopTraversal;
	long _creationDate;

	public ObjectState stateInProviderA() {
		return _stateInProviderA;
	}

	public ObjectState stateInProviderB() {
		return _stateInProviderB;
	}

	public long objectCreationDate() {
		return _creationDate;
	}

	public boolean isConflict() {
		return _isConflict;
	}

	public void overrideWith(ObjectState chosen) {
		if (_actionWasChosen) throw new RuntimeException(); //FIXME Use Db4o's standard exception throwing.
		_actionWasChosen = true;
		_actionChosenState = chosen;
	}

	public void stopTraversal() {
		_actionShouldStopTraversal = true;
	}

	void resetAction() {
		_actionChosenState = null;
		_actionWasChosen = false;
		_actionShouldStopTraversal = false;
		_creationDate = -1;
	}

}
