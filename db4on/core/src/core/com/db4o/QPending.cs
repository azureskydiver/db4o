namespace com.db4o
{
	/// <exclude></exclude>
	internal class QPending : com.db4o.Tree
	{
		internal readonly com.db4o.QConJoin i_join;

		internal com.db4o.QCon i_constraint;

		internal int i_result;

		internal const int FALSE = -4;

		internal const int BOTH = 1;

		internal const int TRUE = 2;

		internal QPending(com.db4o.QConJoin a_join, com.db4o.QCon a_constraint, bool a_firstResult
			)
		{
			i_join = a_join;
			i_constraint = a_constraint;
			i_result = a_firstResult ? TRUE : FALSE;
		}

		public override int compare(com.db4o.Tree a_to)
		{
			return i_constraint.i_id - ((com.db4o.QPending)a_to).i_constraint.i_id;
		}

		internal virtual void changeConstraint()
		{
			i_constraint = i_join.getOtherConstraint(i_constraint);
		}
	}
}
