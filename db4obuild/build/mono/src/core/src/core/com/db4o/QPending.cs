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
	internal class QPending : com.db4o.Tree
	{
		internal readonly com.db4o.QConJoin i_join;

		internal com.db4o.QCon i_constraint;

		internal int i_result;

		internal const int FALSE = -4;

		internal const int BOTH = 1;

		internal const int TRUE = 2;

		internal QPending(com.db4o.QConJoin a_join, com.db4o.QCon a_constraint, bool a_firstResult
			)
		{
			i_join = a_join;
			i_constraint = a_constraint;
			i_result = a_firstResult ? TRUE : FALSE;
		}

		internal override int compare(com.db4o.Tree a_to)
		{
			return i_constraint.i_id - ((com.db4o.QPending)a_to).i_constraint.i_id;
		}

		internal virtual void changeConstraint()
		{
			i_constraint = i_join.getOtherConstraint(i_constraint);
		}
	}
}
