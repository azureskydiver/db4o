namespace com.db4o.@internal.query.processor
{
	/// <exclude></exclude>
	internal class QPending : com.db4o.foundation.Tree
	{
		internal readonly com.db4o.@internal.query.processor.QConJoin _join;

		internal com.db4o.@internal.query.processor.QCon _constraint;

		internal int _result;

		internal const int FALSE = -4;

		internal const int BOTH = 1;

		internal const int TRUE = 2;

		internal QPending(com.db4o.@internal.query.processor.QConJoin a_join, com.db4o.@internal.query.processor.QCon
			 a_constraint, bool a_firstResult)
		{
			_join = a_join;
			_constraint = a_constraint;
			_result = a_firstResult ? TRUE : FALSE;
		}

		public override int Compare(com.db4o.foundation.Tree a_to)
		{
			return _constraint.i_id - ((com.db4o.@internal.query.processor.QPending)a_to)._constraint
				.i_id;
		}

		internal virtual void ChangeConstraint()
		{
			_constraint = _join.GetOtherConstraint(_constraint);
		}

		public override object ShallowClone()
		{
			com.db4o.@internal.query.processor.QPending pending = new com.db4o.@internal.query.processor.QPending
				(_join, _constraint, false);
			pending._result = _result;
			base.ShallowCloneInternal(pending);
			return pending;
		}

		public override object Key()
		{
			throw new System.NotImplementedException();
		}
	}
}
