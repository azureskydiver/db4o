namespace com.db4o.@internal.cluster
{
	/// <exclude></exclude>
	public class ClusterConstraint : com.db4o.query.Constraint
	{
		internal readonly com.db4o.cluster.Cluster _cluster;

		internal readonly com.db4o.query.Constraint[] _constraints;

		public ClusterConstraint(com.db4o.cluster.Cluster cluster, com.db4o.query.Constraint[]
			 constraints)
		{
			_cluster = cluster;
			_constraints = constraints;
		}

		private com.db4o.@internal.cluster.ClusterConstraint Compatible(com.db4o.query.Constraint
			 with)
		{
			if (!(with is com.db4o.@internal.cluster.ClusterConstraint))
			{
				throw new System.ArgumentException();
			}
			com.db4o.@internal.cluster.ClusterConstraint other = (com.db4o.@internal.cluster.ClusterConstraint
				)with;
			if (other._constraints.Length != _constraints.Length)
			{
				throw new System.ArgumentException();
			}
			return other;
		}

		public virtual com.db4o.query.Constraint And(com.db4o.query.Constraint with)
		{
			return Join(with, true);
		}

		public virtual com.db4o.query.Constraint Or(com.db4o.query.Constraint with)
		{
			return Join(with, false);
		}

		private com.db4o.query.Constraint Join(com.db4o.query.Constraint with, bool isAnd
			)
		{
			lock (_cluster)
			{
				com.db4o.@internal.cluster.ClusterConstraint other = Compatible(with);
				com.db4o.query.Constraint[] newConstraints = new com.db4o.query.Constraint[_constraints
					.Length];
				for (int i = 0; i < _constraints.Length; i++)
				{
					newConstraints[i] = isAnd ? _constraints[i].And(other._constraints[i]) : _constraints
						[i].Or(other._constraints[i]);
				}
				return new com.db4o.@internal.cluster.ClusterConstraint(_cluster, newConstraints);
			}
		}

		public virtual com.db4o.query.Constraint Equal()
		{
			lock (_cluster)
			{
				for (int i = 0; i < _constraints.Length; i++)
				{
					_constraints[i].Equal();
				}
				return this;
			}
		}

		public virtual com.db4o.query.Constraint Greater()
		{
			lock (_cluster)
			{
				for (int i = 0; i < _constraints.Length; i++)
				{
					_constraints[i].Greater();
				}
				return this;
			}
		}

		public virtual com.db4o.query.Constraint Smaller()
		{
			lock (_cluster)
			{
				for (int i = 0; i < _constraints.Length; i++)
				{
					_constraints[i].Smaller();
				}
				return this;
			}
		}

		public virtual com.db4o.query.Constraint Identity()
		{
			lock (_cluster)
			{
				for (int i = 0; i < _constraints.Length; i++)
				{
					_constraints[i].Identity();
				}
				return this;
			}
		}

		public virtual com.db4o.query.Constraint Like()
		{
			lock (_cluster)
			{
				for (int i = 0; i < _constraints.Length; i++)
				{
					_constraints[i].Like();
				}
				return this;
			}
		}

		public virtual com.db4o.query.Constraint StartsWith(bool caseSensitive)
		{
			lock (_cluster)
			{
				for (int i = 0; i < _constraints.Length; i++)
				{
					_constraints[i].StartsWith(caseSensitive);
				}
				return this;
			}
		}

		public virtual com.db4o.query.Constraint EndsWith(bool caseSensitive)
		{
			lock (_cluster)
			{
				for (int i = 0; i < _constraints.Length; i++)
				{
					_constraints[i].EndsWith(caseSensitive);
				}
				return this;
			}
		}

		public virtual com.db4o.query.Constraint Contains()
		{
			lock (_cluster)
			{
				for (int i = 0; i < _constraints.Length; i++)
				{
					_constraints[i].Contains();
				}
				return this;
			}
		}

		public virtual com.db4o.query.Constraint Not()
		{
			lock (_cluster)
			{
				for (int i = 0; i < _constraints.Length; i++)
				{
					_constraints[i].Not();
				}
				return this;
			}
		}

		public virtual object GetObject()
		{
			throw new System.NotSupportedException();
		}
	}
}
