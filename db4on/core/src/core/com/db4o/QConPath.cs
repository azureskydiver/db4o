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
				i_yapClass = a_field.GetYapClass();
			}
		}

		public override bool CanLoadByIndex()
		{
			return false;
		}

		internal override bool Evaluate(com.db4o.QCandidate a_candidate)
		{
			if (a_candidate.ClassReflector() == null)
			{
				VisitOnNull(a_candidate.GetRoot());
			}
			return true;
		}

		internal override void EvaluateSelf()
		{
		}

		internal override bool IsNullConstraint()
		{
			return !HasChildren();
		}

		internal override com.db4o.QConClass ShareParentForClass(com.db4o.reflect.ReflectClass
			 a_class, bool[] removeExisting)
		{
			if (i_parent == null)
			{
				return null;
			}
			if (!i_field.CanHold(a_class))
			{
				return null;
			}
			com.db4o.QConClass newConstraint = new com.db4o.QConClass(i_trans, i_parent, i_field
				, a_class);
			Morph(removeExisting, newConstraint, a_class);
			return newConstraint;
		}

		internal override com.db4o.QCon ShareParent(object a_object, bool[] removeExisting
			)
		{
			if (i_parent == null)
			{
				return null;
			}
			object obj = i_field.Coerce(a_object);
			if (obj == com.db4o.foundation.No4.INSTANCE)
			{
				com.db4o.QConObject falseConstraint = new com.db4o.QConFalse(i_trans, i_parent, i_field
					);
				Morph(removeExisting, falseConstraint, ReflectClassForObject(obj));
				return falseConstraint;
			}
			com.db4o.QConObject newConstraint = new com.db4o.QConObject(i_trans, i_parent, i_field
				, obj);
			Morph(removeExisting, newConstraint, ReflectClassForObject(obj));
			return newConstraint;
		}

		private com.db4o.reflect.ReflectClass ReflectClassForObject(object obj)
		{
			return i_trans.Reflector().ForObject(obj);
		}

		private void Morph(bool[] removeExisting, com.db4o.QConObject newConstraint, com.db4o.reflect.ReflectClass
			 claxx)
		{
			bool mayMorph = true;
			if (claxx != null)
			{
				com.db4o.YapClass yc = i_trans.Stream().GetYapClass(claxx, true);
				if (yc != null)
				{
					com.db4o.foundation.Iterator4 i = IterateChildren();
					while (i.MoveNext())
					{
						com.db4o.QField qf = ((com.db4o.QCon)i.Current()).GetField();
						if (!yc.HasField(i_trans.Stream(), qf.i_name))
						{
							mayMorph = false;
							break;
						}
					}
				}
			}
			if (mayMorph)
			{
				com.db4o.foundation.Iterator4 j = IterateChildren();
				while (j.MoveNext())
				{
					newConstraint.AddConstraint((com.db4o.QCon)j.Current());
				}
				if (HasJoins())
				{
					com.db4o.foundation.Iterator4 k = IterateJoins();
					while (k.MoveNext())
					{
						com.db4o.QConJoin qcj = (com.db4o.QConJoin)k.Current();
						qcj.ExchangeConstraint(this, newConstraint);
						newConstraint.AddJoin(qcj);
					}
				}
				i_parent.ExchangeConstraint(this, newConstraint);
				removeExisting[0] = true;
			}
			else
			{
				i_parent.AddConstraint(newConstraint);
			}
		}

		internal sealed override bool VisitSelfOnNull()
		{
			return false;
		}

		public override string ToString()
		{
			return base.ToString();
			return "QConPath " + base.ToString();
		}
	}
}
