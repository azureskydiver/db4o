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
	/// <summary>non-constraint, only necessary to attach children.</summary>
	/// <remarks>
	/// non-constraint, only necessary to attach children.
	/// Added upon call to Query#descendant, if there is no
	/// other place to hook in.
	/// </remarks>
	/// <exclude></exclude>
	public class QConPath : com.db4o.QConClass
	{
		public QConPath()
		{
		}

		internal QConPath(com.db4o.Transaction a_trans, com.db4o.QCon a_parent, com.db4o.QField
			 a_field) : base(a_trans, a_parent, a_field, null)
		{
			if (a_field != null)
			{
				i_yapClass = a_field.getYapClass();
			}
		}

		internal override int candidateCountByIndex(int depth)
		{
			return -1;
		}

		internal override bool evaluate(com.db4o.QCandidate a_candidate)
		{
			if (a_candidate.classReflector() == null)
			{
				visitOnNull(a_candidate.getRoot());
			}
			return true;
		}

		internal override void evaluateSelf()
		{
		}

		internal override bool isNullConstraint()
		{
			return i_subConstraints == null;
		}

		internal override com.db4o.QConClass shareParentForClass(com.db4o.reflect.ReflectClass
			 a_class, bool[] removeExisting)
		{
			if (i_parent != null)
			{
				if (i_field.canHold(a_class))
				{
					com.db4o.QConClass newConstraint = new com.db4o.QConClass(i_trans, i_parent, i_field
						, a_class);
					morph(removeExisting, newConstraint, a_class);
					return newConstraint;
				}
			}
			return null;
		}

		internal override com.db4o.QCon shareParent(object a_object, bool[] removeExisting
			)
		{
			if (i_parent != null)
			{
				if (i_field.canHold(a_object))
				{
					com.db4o.QConObject newConstraint = new com.db4o.QConObject(i_trans, i_parent, i_field
						, a_object);
					com.db4o.reflect.ReflectClass claxx = i_trans.reflector().forObject(a_object);
					morph(removeExisting, newConstraint, claxx);
					return newConstraint;
				}
			}
			return null;
		}

		private void morph(bool[] removeExisting, com.db4o.QConObject newConstraint, com.db4o.reflect.ReflectClass
			 claxx)
		{
			bool mayMorph = true;
			if (claxx != null)
			{
				com.db4o.YapClass yc = i_trans.i_stream.getYapClass(claxx, true);
				if (yc != null && i_subConstraints != null)
				{
					com.db4o.Iterator4 i = new com.db4o.Iterator4(i_subConstraints);
					while (i.hasNext())
					{
						com.db4o.QField qf = ((com.db4o.QCon)i.next()).getField();
						if (!yc.hasField(i_trans.i_stream, qf.i_name))
						{
							mayMorph = false;
							break;
						}
					}
				}
			}
			if (mayMorph)
			{
				if (i_subConstraints != null)
				{
					com.db4o.Iterator4 i = new com.db4o.Iterator4(i_subConstraints);
					while (i.hasNext())
					{
						newConstraint.addConstraint((com.db4o.QCon)i.next());
					}
				}
				if (i_joins != null)
				{
					com.db4o.Iterator4 i = i_joins.iterator();
					while (i.hasNext())
					{
						com.db4o.QConJoin qcj = (com.db4o.QConJoin)i.next();
						qcj.exchangeConstraint(this, newConstraint);
						newConstraint.addJoin(qcj);
					}
				}
				i_parent.exchangeConstraint(this, newConstraint);
				removeExisting[0] = true;
			}
			else
			{
				i_parent.addConstraint(newConstraint);
			}
		}

		internal sealed override bool visitSelfOnNull()
		{
			return false;
		}

		public override string ToString()
		{
			return base.ToString();
		}
	}
}
