namespace com.db4o.@internal.query
{
	public interface Db4oNQOptimizer
	{
		object Optimize(com.db4o.query.Query query, com.db4o.query.Predicate filter);
	}
}
