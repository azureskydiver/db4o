namespace com.db4o.inside.query
{
	public interface Db4oNQOptimizer
	{
		void optimize(com.db4o.query.Query query, com.db4o.query.Predicate filter);
	}
}
