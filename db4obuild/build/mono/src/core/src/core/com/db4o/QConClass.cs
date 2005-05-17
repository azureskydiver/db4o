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
	public class QConClass : com.db4o.QConObject
	{
		[com.db4o.Transient]
		private com.db4o.reflect.ReflectClass _claxx;

		private string _className;

		private bool i_equal;

		public QConClass()
		{
		}

		internal QConClass(com.db4o.Transaction a_trans, com.db4o.QCon a_parent, com.db4o.QField
			 a_field, com.db4o.reflect.ReflectClass claxx) : base(a_trans, a_parent, a_field
			, null)
		{
			if (claxx != null)
			{
				i_yapClass = a_trans.i_stream.getYapClass(claxx, true);
				if (claxx == a_trans.i_stream.i_handlers.ICLASS_OBJECT)
				{
					i_yapClass = (com.db4o.YapClass)((com.db4o.YapClassPrimitive)i_yapClass).i_handler;
				}
			}
			_claxx = claxx;
		}

		internal override bool evaluate(com.db4o.QCandidate a_candidate)
		{
			bool res = true;
			com.db4o.reflect.ReflectClass claxx = a_candidate.classReflector();
			if (claxx == null)
			{
				res = false;
			}
			else
			{
				res = i_equal ? _claxx == claxx : _claxx.isAssignableFrom(claxx);
			}
			return i_evaluator.not(res);
		}

		internal override void evaluateSelf()
		{
			if (i_evaluator.isDefault())
			{
				if (i_orderID == 0 && i_joins == null)
				{
					if (i_yapClass != null && i_candidates.i_yapClass != null)
					{
						if (i_yapClass.getHigherHierarchy(i_candidates.i_yapClass) == i_yapClass)
						{
							return;
						}
					}
				}
			}
			i_candidates.filter(this);
		}

		public override com.db4o.query.Constraint equal()
		{
			lock (streamLock())
			{
				i_equal = true;
				return this;
			}
		}

		internal override bool isNullConstraint()
		{
			return false;
		}

		internal override void marshall()
		{
			base.marshall();
			if (_claxx != null)
			{
				_className = _claxx.getName();
			}
		}

		public override string ToString()
		{
			return base.ToString();
		}

		internal override void unmarshall(com.db4o.Transaction a_trans)
		{
			if (i_trans == null)
			{
				base.unmarshall(a_trans);
				if (_className != null)
				{
					_claxx = a_trans.reflector().forName(_className);
				}
			}
		}
	}
}
