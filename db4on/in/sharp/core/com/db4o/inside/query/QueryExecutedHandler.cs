namespace com.db4o.inside.query
{
	public enum QueryExecutionKind
	{
		Unoptimized,
		DynamicallyOptimized
	}

	public class QueryExecutedEventArgs : System.EventArgs
	{
		private object _predicate;
		private QueryExecutionKind _kind;

		public QueryExecutedEventArgs(object predicate, QueryExecutionKind kind)
		{
			_predicate = predicate;
			_kind = kind;
		}

		public object Predicate
		{
			get { return _predicate; }
		}

		public QueryExecutionKind ExecutionKind
		{
			get { return _kind; }
		}
	}

	public delegate void QueryExecutedHandler(object sender, QueryExecutedEventArgs args);
}