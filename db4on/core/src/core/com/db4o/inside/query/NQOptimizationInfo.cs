namespace com.db4o.inside.query
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

		public virtual string message()
		{
			return _message;
		}

		public virtual object optimized()
		{
			return _optimized;
		}

		public virtual com.db4o.query.Predicate predicate()
		{
			return _predicate;
		}

		public override string ToString()
		{
			return message() + "/" + optimized();
		}
	}
}
