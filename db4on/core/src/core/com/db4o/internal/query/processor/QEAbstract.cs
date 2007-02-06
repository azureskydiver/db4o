namespace com.db4o.@internal.query.processor
{
	/// <exclude></exclude>
	public abstract class QEAbstract : com.db4o.@internal.query.processor.QE
	{
		internal override com.db4o.@internal.query.processor.QE Add(com.db4o.@internal.query.processor.QE
			 evaluator)
		{
			com.db4o.@internal.query.processor.QE qe = new com.db4o.@internal.query.processor.QEMulti
				();
			qe.Add(this);
			qe.Add(evaluator);
			return qe;
		}

		internal override bool IsDefault()
		{
			return false;
		}
	}
}
