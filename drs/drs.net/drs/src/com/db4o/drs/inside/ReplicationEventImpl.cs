namespace com.db4o.drs.inside
{
	internal sealed class ReplicationEventImpl : com.db4o.drs.ReplicationEvent
	{
		internal readonly com.db4o.drs.inside.ObjectStateImpl _stateInProviderA = new com.db4o.drs.inside.ObjectStateImpl
			();

		internal readonly com.db4o.drs.inside.ObjectStateImpl _stateInProviderB = new com.db4o.drs.inside.ObjectStateImpl
			();

		internal bool _isConflict;

		internal com.db4o.drs.ObjectState _actionChosenState;

		internal bool _actionWasChosen;

		internal bool _actionShouldStopTraversal;

		internal long _creationDate;

		public com.db4o.drs.ObjectState StateInProviderA()
		{
			return _stateInProviderA;
		}

		public com.db4o.drs.ObjectState StateInProviderB()
		{
			return _stateInProviderB;
		}

		public long ObjectCreationDate()
		{
			return _creationDate;
		}

		public bool IsConflict()
		{
			return _isConflict;
		}

		public void OverrideWith(com.db4o.drs.ObjectState chosen)
		{
			if (_actionWasChosen)
			{
				throw new j4o.lang.RuntimeException();
			}
			_actionWasChosen = true;
			_actionChosenState = chosen;
		}

		public void StopTraversal()
		{
			_actionShouldStopTraversal = true;
		}

		internal void ResetAction()
		{
			_actionChosenState = null;
			_actionWasChosen = false;
			_actionShouldStopTraversal = false;
			_creationDate = -1;
		}
	}
}
