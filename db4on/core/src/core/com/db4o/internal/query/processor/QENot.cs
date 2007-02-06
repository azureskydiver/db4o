namespace com.db4o.@internal.query.processor
{
	/// <exclude></exclude>
	public class QENot : com.db4o.@internal.query.processor.QE
	{
		public com.db4o.@internal.query.processor.QE i_evaluator;

		public QENot()
		{
		}

		internal QENot(com.db4o.@internal.query.processor.QE a_evaluator)
		{
			i_evaluator = a_evaluator;
		}

		internal override com.db4o.@internal.query.processor.QE Add(com.db4o.@internal.query.processor.QE
			 evaluator)
		{
			if (!(evaluator is com.db4o.@internal.query.processor.QENot))
			{
				i_evaluator = i_evaluator.Add(evaluator);
			}
			return this;
		}

		public override bool Identity()
		{
			return i_evaluator.Identity();
		}

		internal override bool IsDefault()
		{
			return false;
		}

		internal override bool Evaluate(com.db4o.@internal.query.processor.QConObject a_constraint
			, com.db4o.@internal.query.processor.QCandidate a_candidate, object a_value)
		{
			return !i_evaluator.Evaluate(a_constraint, a_candidate, a_value);
		}

		internal override bool Not(bool res)
		{
			return !res;
		}

		public override void IndexBitMap(bool[] bits)
		{
			i_evaluator.IndexBitMap(bits);
			for (int i = 0; i < 4; i++)
			{
				bits[i] = !bits[i];
			}
		}

		public override bool SupportsIndex()
		{
			return i_evaluator.SupportsIndex();
		}
	}
}
