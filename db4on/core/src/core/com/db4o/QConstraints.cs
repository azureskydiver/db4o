namespace com.db4o
{
	/// <summary>Array of constraints for queries.</summary>
	/// <remarks>
	/// Array of constraints for queries.
	/// Necessary to be returned to Query#constraints()
	/// </remarks>
	/// <exclude></exclude>
	public class QConstraints : com.db4o.QCon, com.db4o.query.Constraints
	{
		private com.db4o.query.Constraint[] i_constraints;

		internal QConstraints(com.db4o.Transaction a_trans, com.db4o.query.Constraint[] constraints
			) : base(a_trans)
		{
			i_constraints = constraints;
		}

		internal override com.db4o.query.Constraint join(com.db4o.query.Constraint a_with
			, bool a_and)
		{
			lock (streamLock())
			{
				if (!(a_with is com.db4o.QCon))
				{
					return null;
				}
				return ((com.db4o.QCon)a_with).join1(this, a_and);
			}
		}

		public virtual com.db4o.query.Constraint[] toArray()
		{
			lock (streamLock())
			{
				return i_constraints;
			}
		}

		public override com.db4o.query.Constraint contains()
		{
			lock (streamLock())
			{
				for (int i = 0; i < i_constraints.Length; i++)
				{
					i_constraints[i].contains();
				}
				return this;
			}
		}

		public override com.db4o.query.Constraint equal()
		{
			lock (streamLock())
			{
				for (int i = 0; i < i_constraints.Length; i++)
				{
					i_constraints[i].equal();
				}
				return this;
			}
		}

		public override com.db4o.query.Constraint greater()
		{
			lock (streamLock())
			{
				for (int i = 0; i < i_constraints.Length; i++)
				{
					i_constraints[i].greater();
				}
				return this;
			}
		}

		public override com.db4o.query.Constraint identity()
		{
			lock (streamLock())
			{
				for (int i = 0; i < i_constraints.Length; i++)
				{
					i_constraints[i].identity();
				}
				return this;
			}
		}

		public override com.db4o.query.Constraint not()
		{
			lock (streamLock())
			{
				for (int i = 0; i < i_constraints.Length; i++)
				{
					i_constraints[i].not();
				}
				return this;
			}
		}

		public override com.db4o.query.Constraint like()
		{
			lock (streamLock())
			{
				for (int i = 0; i < i_constraints.Length; i++)
				{
					i_constraints[i].like();
				}
				return this;
			}
		}

		public override com.db4o.query.Constraint smaller()
		{
			lock (streamLock())
			{
				for (int i = 0; i < i_constraints.Length; i++)
				{
					i_constraints[i].smaller();
				}
				return this;
			}
		}

		public override object getObject()
		{
			lock (streamLock())
			{
				object[] objects = new object[i_constraints.Length];
				for (int i = 0; i < i_constraints.Length; i++)
				{
					objects[i] = i_constraints[i].getObject();
				}
				return objects;
			}
		}
	}
}
