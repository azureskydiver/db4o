namespace com.db4o
{
	/// <exclude></exclude>
	public class QEContains : com.db4o.QEAbstract
	{
		internal override bool evaluate(com.db4o.QConObject a_constraint, com.db4o.QCandidate
			 a_candidate, object a_value)
		{
			if (a_value != null)
			{
				if (a_value is com.db4o.YapReader)
				{
					a_value = ((com.db4o.YapReader)a_value).toString(a_constraint.i_trans);
				}
				return a_value.ToString().IndexOf(a_constraint.i_object.ToString()) > -1;
			}
			return a_constraint.i_object.Equals(null);
		}

		internal override bool supportsIndex()
		{
			return false;
		}
	}
}
