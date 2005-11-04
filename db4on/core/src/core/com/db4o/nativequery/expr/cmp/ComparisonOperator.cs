namespace com.db4o.nativequery.expr.cmp
{
	public sealed class ComparisonOperator
	{
		public const int EQUALS_ID = 0;

		public const int SMALLER_ID = 1;

		public const int GREATER_ID = 2;

		public static readonly com.db4o.nativequery.expr.cmp.ComparisonOperator EQUALS = 
			new com.db4o.nativequery.expr.cmp.ComparisonOperator(EQUALS_ID, "==", true);

		public static readonly com.db4o.nativequery.expr.cmp.ComparisonOperator SMALLER = 
			new com.db4o.nativequery.expr.cmp.ComparisonOperator(SMALLER_ID, "<", false);

		public static readonly com.db4o.nativequery.expr.cmp.ComparisonOperator GREATER = 
			new com.db4o.nativequery.expr.cmp.ComparisonOperator(GREATER_ID, ">", false);

		private int _id;

		private string _op;

		private bool _symmetric;

		private ComparisonOperator(int id, string op, bool symmetric)
		{
			_id = id;
			_op = op;
			_symmetric = symmetric;
		}

		public int id()
		{
			return _id;
		}

		public override string ToString()
		{
			return _op;
		}

		public bool isSymmetric()
		{
			return _symmetric;
		}
	}
}
