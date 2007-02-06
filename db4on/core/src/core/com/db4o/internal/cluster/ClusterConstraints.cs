namespace com.db4o.@internal.cluster
{
	/// <exclude></exclude>
	public class ClusterConstraints : com.db4o.@internal.cluster.ClusterConstraint, com.db4o.query.Constraints
	{
		public ClusterConstraints(com.db4o.cluster.Cluster cluster, com.db4o.query.Constraint[]
			 constraints) : base(cluster, constraints)
		{
		}

		public virtual com.db4o.query.Constraint[] ToArray()
		{
			lock (_cluster)
			{
				com.db4o.foundation.Collection4 all = new com.db4o.foundation.Collection4();
				for (int i = 0; i < _constraints.Length; i++)
				{
					com.db4o.@internal.cluster.ClusterConstraint c = (com.db4o.@internal.cluster.ClusterConstraint
						)_constraints[i];
					for (int j = 0; j < c._constraints.Length; j++)
					{
						all.Add(c._constraints[j]);
					}
				}
				com.db4o.query.Constraint[] res = new com.db4o.query.Constraint[all.Size()];
				all.ToArray(res);
				return res;
			}
		}
	}
}
