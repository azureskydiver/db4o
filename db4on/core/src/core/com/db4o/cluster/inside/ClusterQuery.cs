
namespace com.db4o.cluster.inside
{
	/// <exclude></exclude>
	public class ClusterQuery : com.db4o.query.Query
	{
		private readonly com.db4o.cluster.Cluster _cluster;

		private readonly com.db4o.query.Query[] _queries;

		public ClusterQuery(com.db4o.cluster.Cluster cluster, com.db4o.query.Query[] queries
			)
		{
			_cluster = cluster;
			_queries = queries;
		}

		public virtual com.db4o.query.Constraint constrain(object constraint)
		{
			lock (_cluster)
			{
				com.db4o.query.Constraint[] constraints = new com.db4o.query.Constraint[_queries.
					Length];
				for (int i = 0; i < constraints.Length; i++)
				{
					constraints[i] = _queries[i].constrain(constraint);
				}
				return new com.db4o.cluster.inside.ClusterConstraint(_cluster, constraints);
			}
		}

		public virtual com.db4o.query.Constraints constraints()
		{
			lock (_cluster)
			{
				com.db4o.query.Constraint[] constraints = new com.db4o.query.Constraint[_queries.
					Length];
				for (int i = 0; i < constraints.Length; i++)
				{
					constraints[i] = _queries[i].constraints();
				}
				return new com.db4o.cluster.inside.ClusterConstraints(_cluster, constraints);
			}
		}

		public virtual com.db4o.query.Query descend(string fieldName)
		{
			lock (_cluster)
			{
				com.db4o.query.Query[] queries = new com.db4o.query.Query[_queries.Length];
				for (int i = 0; i < queries.Length; i++)
				{
					queries[i] = _queries[i].descend(fieldName);
				}
				return new com.db4o.cluster.inside.ClusterQuery(_cluster, queries);
			}
		}

		public virtual com.db4o.ObjectSet execute()
		{
			lock (_cluster)
			{
				return new com.db4o.inside.query.ObjectSetFacade(new com.db4o.cluster.inside.ClusterQueryResult
					(_cluster, _queries));
			}
		}

		public virtual com.db4o.query.Query orderAscending()
		{
			com.db4o.inside.Exceptions4.notSupported();
			return this;
		}

		public virtual com.db4o.query.Query orderDescending()
		{
			com.db4o.inside.Exceptions4.notSupported();
			return this;
		}
	}
}
