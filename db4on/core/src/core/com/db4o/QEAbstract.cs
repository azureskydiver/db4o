
namespace com.db4o
{
	/// <exclude></exclude>
	public abstract class QEAbstract : com.db4o.QE
	{
		internal override com.db4o.QE add(com.db4o.QE evaluator)
		{
			com.db4o.QE qe = new com.db4o.QEMulti();
			qe.add(this);
			qe.add(evaluator);
			return qe;
		}

		internal override bool isDefault()
		{
			return false;
		}
	}
}
