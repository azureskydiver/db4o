namespace com.db4o.inside.query
{
	public interface Db4oQueryExecutionListener
	{
		void notifyQueryExecuted(com.db4o.query.Predicate filter, string msg);
	}
}
