namespace com.db4o.cluster.inside
{
	/// <exclude></exclude>
	public class ClusterConstraints : com.db4o.cluster.inside.ClusterConstraint, com.db4o.query.Constraints
	{
		public ClusterConstraints(com.db4o.cluster.Cluster cluster, com.db4o.query.Constraint[]
			 constraints) : base(cluster, constraints)
		{
		}

		public virtual com.db4o.query.Constraint[] toArray()
		{
			lock (_cluster)
			{
				com.db4o.foundation.Collection4 all = new com.db4o.foundation.Collection4();
				for (int i = 0; i < _constraints.Length; i++)
				{
					com.db4o.cluster.inside.ClusterConstraint c = (com.db4o.cluster.inside.ClusterConstraint
						)_constraints[i];
					for (int j = 0; j < c._constraints.Length; j++)
					{
						all.add(c._constraints[j]);
					}
				}
				com.db4o.query.Constraint[] res = new com.db4o.query.Constraint[all.size()];
				all.toArray(res);
				return res;
			}
		}
	}
}
