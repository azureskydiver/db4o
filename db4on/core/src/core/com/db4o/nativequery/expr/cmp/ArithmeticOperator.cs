namespace com.db4o.nativequery.expr.cmp
{
	public sealed class ArithmeticOperator
	{
		public const int ADD_ID = 0;

		public const int SUBTRACT_ID = 1;

		public const int MULTIPLY_ID = 2;

		public const int DIVIDE_ID = 3;

		public static readonly com.db4o.nativequery.expr.cmp.ArithmeticOperator ADD = new 
			com.db4o.nativequery.expr.cmp.ArithmeticOperator(ADD_ID, "+");

		public static readonly com.db4o.nativequery.expr.cmp.ArithmeticOperator SUBTRACT = 
			new com.db4o.nativequery.expr.cmp.ArithmeticOperator(SUBTRACT_ID, "-");

		public static readonly com.db4o.nativequery.expr.cmp.ArithmeticOperator MULTIPLY = 
			new com.db4o.nativequery.expr.cmp.ArithmeticOperator(MULTIPLY_ID, "*");

		public static readonly com.db4o.nativequery.expr.cmp.ArithmeticOperator DIVIDE = 
			new com.db4o.nativequery.expr.cmp.ArithmeticOperator(DIVIDE_ID, "/");

		private string _op;

		private int _id;

		private ArithmeticOperator(int id, string op)
		{
			_id = id;
			_op = op;
		}

		public int Id()
		{
			return _id;
		}

		public override string ToString()
		{
			return _op;
		}
	}
}
