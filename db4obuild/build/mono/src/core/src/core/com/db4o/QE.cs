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
	/// <summary>Query Evaluator - Represents such things as &gt;, &gt;=, &lt;, &lt;=, EQUAL, LIKE, etc.
	/// 	</summary>
	/// <remarks>Query Evaluator - Represents such things as &gt;, &gt;=, &lt;, &lt;=, EQUAL, LIKE, etc.
	/// 	</remarks>
	/// <exclude></exclude>
	public class QE
	{
		internal static readonly com.db4o.QE DEFAULT = new com.db4o.QE();

		internal virtual com.db4o.QE add(com.db4o.QE evaluator)
		{
			return evaluator;
		}

		internal virtual bool identity()
		{
			return false;
		}

		internal virtual bool evaluate(com.db4o.QConObject a_constraint, com.db4o.QCandidate
			 a_candidate, object a_value)
		{
			if (a_value == null)
			{
				return a_constraint.getComparator(a_candidate) is com.db4o.Null;
			}
			return a_constraint.getComparator(a_candidate).isEqual(a_value);
		}

		public override bool Equals(object obj)
		{
			return j4o.lang.Class.getClassForObject(obj) == j4o.lang.Class.getClassForObject(
				this);
		}

		internal virtual bool not(bool res)
		{
			return res;
		}

		/// <summary>Specifies which part of the index to take.</summary>
		/// <remarks>
		/// Specifies which part of the index to take.
		/// Array elements:
		/// [0] - smaller
		/// [1] - equal
		/// [2] - greater
		/// [3] - nulls
		/// </remarks>
		/// <param name="bits"></param>
		internal virtual void indexBitMap(bool[] bits)
		{
			bits[1] = true;
		}

		internal virtual bool supportsIndex()
		{
			return true;
		}
	}
}
