
namespace com.db4o.inside.query
{
	/// <exclude></exclude>
	public class PredicateQuery : j4o.io.Serializable
	{
		private readonly com.db4o.query.Predicate _predicate;

		[com.db4o.Transient]
		private readonly com.db4o.YapStream _stream;

		public PredicateQuery(com.db4o.YapStream stream, com.db4o.query.Predicate predicate
			)
		{
			_stream = stream;
			_predicate = predicate;
		}

		public virtual com.db4o.ObjectSet execute()
		{
			com.db4o.query.Query q = _stream.query();
			q.constrain(_predicate.getExtent());
			q.constrain(new com.db4o.inside.query.PredicateEvaluation(_predicate));
			return q.execute();
		}
	}
}
