namespace com.db4o.@internal.query.processor
{
	/// <exclude></exclude>
	public class QEMulti : com.db4o.@internal.query.processor.QE
	{
		public com.db4o.foundation.Collection4 i_evaluators = new com.db4o.foundation.Collection4
			();

		internal override com.db4o.@internal.query.processor.QE Add(com.db4o.@internal.query.processor.QE
			 evaluator)
		{
			i_evaluators.Ensure(evaluator);
			return this;
		}

		public override bool Identity()
		{
			bool ret = false;
			System.Collections.IEnumerator i = i_evaluators.GetEnumerator();
			while (i.MoveNext())
			{
				if (((com.db4o.@internal.query.processor.QE)i.Current).Identity())
				{
					ret = true;
				}
				else
				{
					return false;
				}
			}
			return ret;
		}

		internal override bool IsDefault()
		{
			return false;
		}

		internal override bool Evaluate(com.db4o.@internal.query.processor.QConObject a_constraint
			, com.db4o.@internal.query.processor.QCandidate a_candidate, object a_value)
		{
			System.Collections.IEnumerator i = i_evaluators.GetEnumerator();
			while (i.MoveNext())
			{
				if (((com.db4o.@internal.query.processor.QE)i.Current).Evaluate(a_constraint, a_candidate
					, a_value))
				{
					return true;
				}
			}
			return false;
		}

		public override void IndexBitMap(bool[] bits)
		{
			System.Collections.IEnumerator i = i_evaluators.GetEnumerator();
			while (i.MoveNext())
			{
				((com.db4o.@internal.query.processor.QE)i.Current).IndexBitMap(bits);
			}
		}

		public override bool SupportsIndex()
		{
			System.Collections.IEnumerator i = i_evaluators.GetEnumerator();
			while (i.MoveNext())
			{
				if (!((com.db4o.@internal.query.processor.QE)i.Current).SupportsIndex())
				{
					return false;
				}
			}
			return true;
		}
	}
}
