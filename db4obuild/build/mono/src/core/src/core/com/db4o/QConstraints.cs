/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
namespace com.db4o
{
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
