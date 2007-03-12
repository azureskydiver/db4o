namespace com.db4o.@internal.query.processor
{
	/// <exclude></exclude>
	internal class QOrder : com.db4o.foundation.Tree
	{
		internal static int equalityIDGenerator = 1;

		internal readonly com.db4o.@internal.query.processor.QConObject _constraint;

		internal readonly com.db4o.@internal.query.processor.QCandidate _candidate;

		private int _equalityID;

		internal QOrder(com.db4o.@internal.query.processor.QConObject a_constraint, com.db4o.@internal.query.processor.QCandidate
			 a_candidate)
		{
			_constraint = a_constraint;
			_candidate = a_candidate;
		}

		public virtual bool IsEqual(com.db4o.@internal.query.processor.QOrder other)
		{
			if (other == null)
			{
				return false;
			}
			return _equalityID != 0 && _equalityID == other._equalityID;
		}

		public override int Compare(com.db4o.foundation.Tree a_to)
		{
			int res = InternalCompare();
			if (res != 0)
			{
				return res;
			}
			com.db4o.@internal.query.processor.QOrder other = (com.db4o.@internal.query.processor.QOrder
				)a_to;
			int equalityID = _equalityID;
			if (equalityID == 0)
			{
				if (other._equalityID != 0)
				{
					equalityID = other._equalityID;
				}
			}
			if (equalityID == 0)
			{
				equalityID = GenerateEqualityID();
			}
			_equalityID = equalityID;
			other._equalityID = equalityID;
			return res;
		}

		private int InternalCompare()
		{
			if (_constraint.i_comparator.IsSmaller(_candidate.Value()))
			{
				return -_constraint.Ordering();
			}
			if (_constraint.i_comparator.IsEqual(_candidate.Value()))
			{
				return 0;
			}
			return _constraint.Ordering();
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

		private static int GenerateEqualityID()
		{
			equalityIDGenerator++;
			if (equalityIDGenerator < 1)
			{
				equalityIDGenerator = 1;
			}
			return equalityIDGenerator;
		}
	}
}
