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
	public class QENot : com.db4o.QE
	{
		internal com.db4o.QE i_evaluator;

		internal QENot(com.db4o.QE a_evaluator)
		{
			i_evaluator = a_evaluator;
		}

		internal override com.db4o.QE add(com.db4o.QE evaluator)
		{
			if (!(evaluator is com.db4o.QENot))
			{
				i_evaluator = i_evaluator.add(evaluator);
			}
			return this;
		}

		internal override bool identity()
		{
			return i_evaluator.identity();
		}

		internal override bool evaluate(com.db4o.QConObject a_constraint, com.db4o.QCandidate
			 a_candidate, object a_value)
		{
			return !i_evaluator.evaluate(a_constraint, a_candidate, a_value);
		}

		internal override bool not(bool res)
		{
			return !res;
		}

		internal override void indexBitMap(bool[] bits)
		{
			i_evaluator.indexBitMap(bits);
			for (int i = 0; i < 4; i++)
			{
				bits[i] = !bits[i];
			}
		}

		internal override bool supportsIndex()
		{
			return i_evaluator.supportsIndex();
		}
	}
}
