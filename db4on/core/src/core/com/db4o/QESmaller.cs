namespace com.db4o
{
	/// <exclude></exclude>
	public class QESmaller : com.db4o.QEAbstract
	{
		internal override bool Evaluate(com.db4o.QConObject a_constraint, com.db4o.QCandidate
			 a_candidate, object a_value)
		{
			if (a_value == null)
			{
				return false;
			}
			return a_constraint.GetComparator(a_candidate).IsSmaller(a_value);
		}

		public override void IndexBitMap(bool[] bits)
		{
			bits[com.db4o.QE.SMALLER] = true;
		}
	}
}
