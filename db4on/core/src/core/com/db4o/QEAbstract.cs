namespace com.db4o
{
	/// <exclude></exclude>
	public abstract class QEAbstract : com.db4o.QE
	{
		internal override com.db4o.QE Add(com.db4o.QE evaluator)
		{
			com.db4o.QE qe = new com.db4o.QEMulti();
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
