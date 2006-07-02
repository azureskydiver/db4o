namespace com.db4o.diagnostic
{
	/// <summary>Diagnostic, if Native Query can not be run optimized.</summary>
	/// <remarks>Diagnostic, if Native Query can not be run optimized.</remarks>
	public class NativeQueryNotOptimized : com.db4o.diagnostic.DiagnosticBase
	{
		private readonly com.db4o.query.Predicate _predicate;

		public NativeQueryNotOptimized(com.db4o.query.Predicate predicate)
		{
			_predicate = predicate;
		}

		public override object Reason()
		{
			return _predicate;
		}

		public override string Problem()
		{
			return "Native Query Predicate could not be run optimized";
		}

		public override string Solution()
		{
			return "This Native Query was run by instantiating all objects of the candidate class. "
				 + "Consider simplifying the expression in the Native Query method. If you feel that "
				 + "the Native Query processor should understand your code better, you are invited to "
				 + "post yout query code to db4o forums at http://developer.db4o.com/forums";
		}
	}
}
