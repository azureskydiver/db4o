namespace com.db4o.@internal.query
{
	public class NQOptimizationInfo
	{
		private com.db4o.query.Predicate _predicate;

		private string _message;

		private object _optimized;

		public NQOptimizationInfo(com.db4o.query.Predicate predicate, string message, object
			 optimized)
		{
			this._predicate = predicate;
			this._message = message;
			this._optimized = optimized;
		}

		public virtual string Message()
		{
			return _message;
		}

		public virtual object Optimized()
		{
			return _optimized;
		}

		public virtual com.db4o.query.Predicate Predicate()
		{
			return _predicate;
		}

		public override string ToString()
		{
			return Message() + "/" + Optimized();
		}
	}
}
