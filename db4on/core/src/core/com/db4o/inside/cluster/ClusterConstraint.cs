namespace com.db4o.inside.cluster
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

		private com.db4o.inside.cluster.ClusterConstraint compatible(com.db4o.query.Constraint
			 with)
		{
			if (!(with is com.db4o.inside.cluster.ClusterConstraint))
			{
				throw new System.ArgumentException();
			}
			com.db4o.inside.cluster.ClusterConstraint other = (com.db4o.inside.cluster.ClusterConstraint
				)with;
			if (other._constraints.Length != _constraints.Length)
			{
				throw new System.ArgumentException();
			}
			return other;
		}

		public virtual com.db4o.query.Constraint and(com.db4o.query.Constraint with)
		{
			return join(with, true);
		}

		public virtual com.db4o.query.Constraint or(com.db4o.query.Constraint with)
		{
			return join(with, false);
		}

		private com.db4o.query.Constraint join(com.db4o.query.Constraint with, bool isAnd
			)
		{
			lock (_cluster)
			{
				com.db4o.inside.cluster.ClusterConstraint other = compatible(with);
				com.db4o.query.Constraint[] newConstraints = new com.db4o.query.Constraint[_constraints
					.Length];
				for (int i = 0; i < _constraints.Length; i++)
				{
					newConstraints[i] = isAnd ? _constraints[i].and(other._constraints[i]) : _constraints
						[i].or(other._constraints[i]);
				}
				return new com.db4o.inside.cluster.ClusterConstraint(_cluster, newConstraints);
			}
		}

		public virtual com.db4o.query.Constraint equal()
		{
			lock (_cluster)
			{
				for (int i = 0; i < _constraints.Length; i++)
				{
					_constraints[i].equal();
				}
				return this;
			}
		}

		public virtual com.db4o.query.Constraint greater()
		{
			lock (_cluster)
			{
				for (int i = 0; i < _constraints.Length; i++)
				{
					_constraints[i].greater();
				}
				return this;
			}
		}

		public virtual com.db4o.query.Constraint smaller()
		{
			lock (_cluster)
			{
				for (int i = 0; i < _constraints.Length; i++)
				{
					_constraints[i].smaller();
				}
				return this;
			}
		}

		public virtual com.db4o.query.Constraint identity()
		{
			lock (_cluster)
			{
				for (int i = 0; i < _constraints.Length; i++)
				{
					_constraints[i].identity();
				}
				return this;
			}
		}

		public virtual com.db4o.query.Constraint like()
		{
			lock (_cluster)
			{
				for (int i = 0; i < _constraints.Length; i++)
				{
					_constraints[i].like();
				}
				return this;
			}
		}

		public virtual com.db4o.query.Constraint startsWith(bool caseSensitive)
		{
			lock (_cluster)
			{
				for (int i = 0; i < _constraints.Length; i++)
				{
					_constraints[i].startsWith(caseSensitive);
				}
				return this;
			}
		}

		public virtual com.db4o.query.Constraint endsWith(bool caseSensitive)
		{
			lock (_cluster)
			{
				for (int i = 0; i < _constraints.Length; i++)
				{
					_constraints[i].endsWith(caseSensitive);
				}
				return this;
			}
		}

		public virtual com.db4o.query.Constraint contains()
		{
			lock (_cluster)
			{
				for (int i = 0; i < _constraints.Length; i++)
				{
					_constraints[i].contains();
				}
				return this;
			}
		}

		public virtual com.db4o.query.Constraint not()
		{
			lock (_cluster)
			{
				for (int i = 0; i < _constraints.Length; i++)
				{
					_constraints[i].not();
				}
				return this;
			}
		}

		public virtual object getObject()
		{
			com.db4o.inside.Exceptions4.notSupported();
			return null;
		}
	}
}
