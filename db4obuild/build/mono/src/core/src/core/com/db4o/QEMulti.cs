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
	public class QEMulti : com.db4o.QE
	{
		internal com.db4o.Collection4 i_evaluators = new com.db4o.Collection4();

		internal override com.db4o.QE add(com.db4o.QE evaluator)
		{
			i_evaluators.ensure(evaluator);
			return this;
		}

		internal override bool identity()
		{
			bool ret = false;
			com.db4o.Iterator4 i = i_evaluators.iterator();
			while (i.hasNext())
			{
				if (((com.db4o.QE)i.next()).identity())
				{
					ret = true;
				}
				else
				{
					return false;
				}
			}
			return ret;
		}

		internal override bool evaluate(com.db4o.QConObject a_constraint, com.db4o.QCandidate
			 a_candidate, object a_value)
		{
			com.db4o.Iterator4 i = i_evaluators.iterator();
			while (i.hasNext())
			{
				if (((com.db4o.QE)i.next()).evaluate(a_constraint, a_candidate, a_value))
				{
					return true;
				}
			}
			return false;
		}

		internal override void indexBitMap(bool[] bits)
		{
			com.db4o.Iterator4 i = i_evaluators.iterator();
			while (i.hasNext())
			{
				((com.db4o.QE)i.next()).indexBitMap(bits);
			}
		}

		internal override bool supportsIndex()
		{
			com.db4o.Iterator4 i = i_evaluators.iterator();
			while (i.hasNext())
			{
				if (!((com.db4o.QE)i.next()).supportsIndex())
				{
					return false;
				}
			}
			return true;
		}
	}
}
