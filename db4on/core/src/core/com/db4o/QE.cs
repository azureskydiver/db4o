namespace com.db4o
{
	/// <summary>Query Evaluator - Represents such things as &gt;, &gt;=, &lt;, &lt;=, EQUAL, LIKE, etc.
	/// 	</summary>
	/// <remarks>Query Evaluator - Represents such things as &gt;, &gt;=, &lt;, &lt;=, EQUAL, LIKE, etc.
	/// 	</remarks>
	/// <exclude></exclude>
	public class QE : com.db4o.types.Unversioned
	{
		internal static readonly com.db4o.QE DEFAULT = new com.db4o.QE();

		public const int NULLS = 0;

		public const int SMALLER = 1;

		public const int EQUAL = 2;

		public const int GREATER = 3;

		internal virtual com.db4o.QE Add(com.db4o.QE evaluator)
		{
			return evaluator;
		}

		public virtual bool Identity()
		{
			return false;
		}

		internal virtual bool IsDefault()
		{
			return true;
		}

		internal virtual bool Evaluate(com.db4o.QConObject a_constraint, com.db4o.QCandidate
			 a_candidate, object a_value)
		{
			if (a_value == null)
			{
				return a_constraint.GetComparator(a_candidate) is com.db4o.Null;
			}
			return a_constraint.GetComparator(a_candidate).IsEqual(a_value);
		}

		public override bool Equals(object obj)
		{
			return j4o.lang.JavaSystem.GetClassForObject(obj) == j4o.lang.JavaSystem.GetClassForObject
				(this);
		}

		internal virtual bool Not(bool res)
		{
			return res;
		}

		/// <summary>Specifies which part of the index to take.</summary>
		/// <remarks>
		/// Specifies which part of the index to take.
		/// Array elements:
		/// [0] - smaller
		/// [1] - equal
		/// [2] - greater
		/// [3] - nulls
		/// </remarks>
		/// <param name="bits"></param>
		public virtual void IndexBitMap(bool[] bits)
		{
			bits[com.db4o.QE.EQUAL] = true;
		}

		public virtual bool SupportsIndex()
		{
			return true;
		}
	}
}
