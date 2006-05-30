namespace com.db4o.inside.cluster
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

		public virtual com.db4o.query.Constraint Constrain(object constraint)
		{
			lock (_cluster)
			{
				com.db4o.query.Constraint[] constraints = new com.db4o.query.Constraint[_queries.
					Length];
				for (int i = 0; i < constraints.Length; i++)
				{
					constraints[i] = _queries[i].Constrain(constraint);
				}
				return new com.db4o.inside.cluster.ClusterConstraint(_cluster, constraints);
			}
		}

		public virtual com.db4o.query.Constraints Constraints()
		{
			lock (_cluster)
			{
				com.db4o.query.Constraint[] constraints = new com.db4o.query.Constraint[_queries.
					Length];
				for (int i = 0; i < constraints.Length; i++)
				{
					constraints[i] = _queries[i].Constraints();
				}
				return new com.db4o.inside.cluster.ClusterConstraints(_cluster, constraints);
			}
		}

		public virtual com.db4o.query.Query Descend(string fieldName)
		{
			lock (_cluster)
			{
				com.db4o.query.Query[] queries = new com.db4o.query.Query[_queries.Length];
				for (int i = 0; i < queries.Length; i++)
				{
					queries[i] = _queries[i].Descend(fieldName);
				}
				return new com.db4o.inside.cluster.ClusterQuery(_cluster, queries);
			}
		}

		public virtual com.db4o.ObjectSet Execute()
		{
			lock (_cluster)
			{
				return new com.db4o.inside.query.ObjectSetFacade(new com.db4o.inside.cluster.ClusterQueryResult
					(_cluster, _queries));
			}
		}

		public virtual com.db4o.query.Query OrderAscending()
		{
			com.db4o.inside.Exceptions4.NotSupported();
			return this;
		}

		public virtual com.db4o.query.Query OrderDescending()
		{
			com.db4o.inside.Exceptions4.NotSupported();
			return this;
		}

		public virtual com.db4o.query.Query SortBy(com.db4o.query.QueryComparator comparator
			)
		{
			com.db4o.inside.Exceptions4.NotSupported();
			return this;
		}
	}
}
