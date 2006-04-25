namespace com.db4o
{
	/// <exclude></exclude>
	internal class QPending : com.db4o.Tree
	{
		internal readonly com.db4o.QConJoin _join;

		internal com.db4o.QCon _constraint;

		internal int _result;

		internal const int FALSE = -4;

		internal const int BOTH = 1;

		internal const int TRUE = 2;

		internal QPending(com.db4o.QConJoin a_join, com.db4o.QCon a_constraint, bool a_firstResult
			)
		{
			_join = a_join;
			_constraint = a_constraint;
			_result = a_firstResult ? TRUE : FALSE;
		}

		public override int compare(com.db4o.Tree a_to)
		{
			return _constraint.i_id - ((com.db4o.QPending)a_to)._constraint.i_id;
		}

		internal virtual void changeConstraint()
		{
			_constraint = _join.getOtherConstraint(_constraint);
		}

		public override object shallowClone()
		{
			com.db4o.QPending pending = new com.db4o.QPending(_join, _constraint, false);
			pending._result = _result;
			base.shallowCloneInternal(pending);
			return pending;
		}
	}
}
