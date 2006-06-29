namespace com.db4o.inside.query
{
	public interface Db4oQueryExecutionListener
	{
		void NotifyQueryExecuted(com.db4o.inside.query.NQOptimizationInfo info);
	}
}
