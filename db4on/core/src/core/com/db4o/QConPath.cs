
namespace com.db4o
{
	/// <summary>
	/// Placeholder for a constraint, only necessary to attach children
	/// to the query graph.
	/// </summary>
	/// <remarks>
	/// Placeholder for a constraint, only necessary to attach children
	/// to the query graph.
	/// Added upon a call to Query#descend(), if there is no
	/// other place to hook up a new constraint.
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
			return !hasChildren();
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
				if (yc != null)
				{
					com.db4o.foundation.Iterator4 i = iterateChildren();
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
				com.db4o.foundation.Iterator4 j = iterateChildren();
				while (j.hasNext())
				{
					newConstraint.addConstraint((com.db4o.QCon)j.next());
				}
				if (hasJoins())
				{
					com.db4o.foundation.Iterator4 k = iterateJoins();
					while (k.hasNext())
					{
						com.db4o.QConJoin qcj = (com.db4o.QConJoin)k.next();
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
