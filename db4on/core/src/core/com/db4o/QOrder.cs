namespace com.db4o
{
	/// <exclude></exclude>
	internal class QOrder : com.db4o.foundation.Tree
	{
		internal readonly com.db4o.QConObject _constraint;

		internal readonly com.db4o.QCandidate _candidate;

		internal QOrder(com.db4o.QConObject a_constraint, com.db4o.QCandidate a_candidate
			)
		{
			_constraint = a_constraint;
			_candidate = a_candidate;
		}

		public override int Compare(com.db4o.foundation.Tree a_to)
		{
			if (_constraint.i_comparator.IsSmaller(_candidate.Value()))
			{
				return _constraint.i_orderID;
			}
			if (_constraint.i_comparator.IsEqual(_candidate.Value()))
			{
				return 0;
			}
			return -_constraint.i_orderID;
		}

		public override object ShallowClone()
		{
			com.db4o.QOrder order = new com.db4o.QOrder(_constraint, _candidate);
			base.ShallowCloneInternal(order);
			return order;
		}

		public override object Key()
		{
			throw new System.NotImplementedException();
		}
	}
}
