namespace com.db4o.@internal.query.processor
{
	/// <summary>Array of constraints for queries.</summary>
	/// <remarks>
	/// Array of constraints for queries.
	/// Necessary to be returned to Query#constraints()
	/// </remarks>
	/// <exclude></exclude>
	public class QConstraints : com.db4o.@internal.query.processor.QCon, com.db4o.query.Constraints
	{
		private com.db4o.query.Constraint[] i_constraints;

		internal QConstraints(com.db4o.@internal.Transaction a_trans, com.db4o.query.Constraint[]
			 constraints) : base(a_trans)
		{
			i_constraints = constraints;
		}

		internal override com.db4o.query.Constraint Join(com.db4o.query.Constraint a_with
			, bool a_and)
		{
			lock (StreamLock())
			{
				if (!(a_with is com.db4o.@internal.query.processor.QCon))
				{
					return null;
				}
				return ((com.db4o.@internal.query.processor.QCon)a_with).Join1(this, a_and);
			}
		}

		public virtual com.db4o.query.Constraint[] ToArray()
		{
			lock (StreamLock())
			{
				return i_constraints;
			}
		}

		public override com.db4o.query.Constraint Contains()
		{
			lock (StreamLock())
			{
				for (int i = 0; i < i_constraints.Length; i++)
				{
					i_constraints[i].Contains();
				}
				return this;
			}
		}

		public override com.db4o.query.Constraint Equal()
		{
			lock (StreamLock())
			{
				for (int i = 0; i < i_constraints.Length; i++)
				{
					i_constraints[i].Equal();
				}
				return this;
			}
		}

		public override com.db4o.query.Constraint Greater()
		{
			lock (StreamLock())
			{
				for (int i = 0; i < i_constraints.Length; i++)
				{
					i_constraints[i].Greater();
				}
				return this;
			}
		}

		public override com.db4o.query.Constraint Identity()
		{
			lock (StreamLock())
			{
				for (int i = 0; i < i_constraints.Length; i++)
				{
					i_constraints[i].Identity();
				}
				return this;
			}
		}

		public override com.db4o.query.Constraint Not()
		{
			lock (StreamLock())
			{
				for (int i = 0; i < i_constraints.Length; i++)
				{
					i_constraints[i].Not();
				}
				return this;
			}
		}

		public override com.db4o.query.Constraint Like()
		{
			lock (StreamLock())
			{
				for (int i = 0; i < i_constraints.Length; i++)
				{
					i_constraints[i].Like();
				}
				return this;
			}
		}

		public override com.db4o.query.Constraint StartsWith(bool caseSensitive)
		{
			lock (StreamLock())
			{
				for (int i = 0; i < i_constraints.Length; i++)
				{
					i_constraints[i].StartsWith(caseSensitive);
				}
				return this;
			}
		}

		public override com.db4o.query.Constraint EndsWith(bool caseSensitive)
		{
			lock (StreamLock())
			{
				for (int i = 0; i < i_constraints.Length; i++)
				{
					i_constraints[i].EndsWith(caseSensitive);
				}
				return this;
			}
		}

		public override com.db4o.query.Constraint Smaller()
		{
			lock (StreamLock())
			{
				for (int i = 0; i < i_constraints.Length; i++)
				{
					i_constraints[i].Smaller();
				}
				return this;
			}
		}

		public override object GetObject()
		{
			lock (StreamLock())
			{
				object[] objects = new object[i_constraints.Length];
				for (int i = 0; i < i_constraints.Length; i++)
				{
					objects[i] = i_constraints[i].GetObject();
				}
				return objects;
			}
		}
	}
}
