namespace com.db4o
{
	/// <exclude></exclude>
	internal class QOrder : com.db4o.Tree
	{
		internal readonly com.db4o.QConObject i_constraint;

		internal readonly com.db4o.QCandidate i_candidate;

		internal QOrder(com.db4o.QConObject a_constraint, com.db4o.QCandidate a_candidate
			)
		{
			i_constraint = a_constraint;
			i_candidate = a_candidate;
		}

		internal override int compare(com.db4o.Tree a_to)
		{
			if (i_constraint.i_comparator.isSmaller(i_candidate.value()))
			{
				return i_constraint.i_orderID;
			}
			if (i_constraint.i_comparator.isEqual(i_candidate.value()))
			{
				return 0;
			}
			return -i_constraint.i_orderID;
		}
	}
}
