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
	public class QConJoin : com.db4o.QCon
	{
		internal bool i_and;

		internal com.db4o.QCon i_constraint1;

		internal com.db4o.QCon i_constraint2;

		public QConJoin()
		{
		}

		internal QConJoin(com.db4o.Transaction a_trans, com.db4o.QCon a_c1, com.db4o.QCon
			 a_c2, bool a_and) : base(a_trans)
		{
			i_constraint1 = a_c1;
			i_constraint2 = a_c2;
			i_and = a_and;
		}

		internal override void doNotInclude(com.db4o.QCandidate a_root)
		{
			i_constraint1.doNotInclude(a_root);
			i_constraint2.doNotInclude(a_root);
		}

		internal override void exchangeConstraint(com.db4o.QCon a_exchange, com.db4o.QCon
			 a_with)
		{
			base.exchangeConstraint(a_exchange, a_with);
			if (a_exchange == i_constraint1)
			{
				i_constraint1 = a_with;
			}
			if (a_exchange == i_constraint2)
			{
				i_constraint2 = a_with;
			}
		}

		internal virtual void evaluatePending(com.db4o.QCandidate a_root, com.db4o.QPending
			 a_pending, com.db4o.QPending a_secondPending, int a_secondResult)
		{
			bool res = i_evaluator.not(i_and ? ((a_pending.i_result + a_secondResult) > 0) : 
				(a_pending.i_result + a_secondResult) > -4);
			if (i_joins != null)
			{
				com.db4o.Iterator4 i = i_joins.iterator();
				while (i.hasNext())
				{
					com.db4o.QConJoin qcj = (com.db4o.QConJoin)i.next();
					a_root.evaluate(new com.db4o.QPending(qcj, this, res));
				}
			}
			else
			{
				if (!res)
				{
					i_constraint1.doNotInclude(a_root);
					i_constraint2.doNotInclude(a_root);
				}
			}
		}

		internal virtual com.db4o.QCon getOtherConstraint(com.db4o.QCon a_constraint)
		{
			if (a_constraint == i_constraint1)
			{
				return i_constraint2;
			}
			else
			{
				if (a_constraint == i_constraint2)
				{
					return i_constraint1;
				}
			}
			return null;
		}

		internal override string logObject()
		{
			return "";
		}

		internal virtual bool removeForParent(com.db4o.QCon a_constraint)
		{
			if (i_and)
			{
				com.db4o.QCon other = getOtherConstraint(a_constraint);
				other.removeJoin(this);
				other.remove();
				return true;
			}
			return false;
		}

		public override string ToString()
		{
			return base.ToString();
		}
	}
}
