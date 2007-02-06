namespace com.db4o.@internal.query.processor
{
	/// <exclude></exclude>
	public class QEGreater : com.db4o.@internal.query.processor.QEAbstract
	{
		internal override bool Evaluate(com.db4o.@internal.query.processor.QConObject a_constraint
			, com.db4o.@internal.query.processor.QCandidate a_candidate, object a_value)
		{
			if (a_value == null)
			{
				return false;
			}
			return a_constraint.GetComparator(a_candidate).IsGreater(a_value);
		}

		public override void IndexBitMap(bool[] bits)
		{
			bits[com.db4o.@internal.query.processor.QE.GREATER] = true;
		}
	}
}
