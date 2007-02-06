namespace com.db4o.@internal.query.processor
{
	/// <exclude></exclude>
	internal class QOrder : com.db4o.foundation.Tree
	{
		internal readonly com.db4o.@internal.query.processor.QConObject _constraint;

		internal readonly com.db4o.@internal.query.processor.QCandidate _candidate;

		internal QOrder(com.db4o.@internal.query.processor.QConObject a_constraint, com.db4o.@internal.query.processor.QCandidate
			 a_candidate)
		{
			_constraint = a_constraint;
			_candidate = a_candidate;
		}

		public override int Compare(com.db4o.foundation.Tree a_to)
		{
			if (_constraint.i_comparator.IsSmaller(_candidate.Value()))
			{
				return _constraint.Ordering();
			}
			if (_constraint.i_comparator.IsEqual(_candidate.Value()))
			{
				return 0;
			}
			return -_constraint.Ordering();
		}

		public override object ShallowClone()
		{
			com.db4o.@internal.query.processor.QOrder order = new com.db4o.@internal.query.processor.QOrder
				(_constraint, _candidate);
			base.ShallowCloneInternal(order);
			return order;
		}

		public override object Key()
		{
			throw new System.NotImplementedException();
		}
	}
}
