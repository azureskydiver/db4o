namespace com.db4o
{
	/// <exclude></exclude>
	internal class QOrder : com.db4o.Tree
	{
		internal readonly com.db4o.QConObject _constraint;

		internal readonly com.db4o.QCandidate _candidate;

		internal QOrder(com.db4o.QConObject a_constraint, com.db4o.QCandidate a_candidate
			)
		{
			_constraint = a_constraint;
			_candidate = a_candidate;
		}

		public override int compare(com.db4o.Tree a_to)
		{
			if (_constraint.i_comparator.isSmaller(_candidate.value()))
			{
				return _constraint.i_orderID;
			}
			if (_constraint.i_comparator.isEqual(_candidate.value()))
			{
				return 0;
			}
			return -_constraint.i_orderID;
		}

		public override object shallowClone()
		{
			com.db4o.QOrder order = new com.db4o.QOrder(_constraint, _candidate);
			base.shallowCloneInternal(order);
			return order;
		}
	}
}
