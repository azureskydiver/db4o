namespace com.db4o.@internal.query.processor
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
	public class QConPath : com.db4o.@internal.query.processor.QConClass
	{
		public QConPath()
		{
		}

		internal QConPath(com.db4o.@internal.Transaction a_trans, com.db4o.@internal.query.processor.QCon
			 a_parent, com.db4o.@internal.query.processor.QField a_field) : base(a_trans, a_parent
			, a_field, null)
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

		internal override bool Evaluate(com.db4o.@internal.query.processor.QCandidate a_candidate
			)
		{
			if (!a_candidate.FieldIsAvailable())
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

		internal override com.db4o.@internal.query.processor.QConClass ShareParentForClass
			(com.db4o.reflect.ReflectClass a_class, bool[] removeExisting)
		{
			if (i_parent == null)
			{
				return null;
			}
			if (!i_field.CanHold(a_class))
			{
				return null;
			}
			com.db4o.@internal.query.processor.QConClass newConstraint = new com.db4o.@internal.query.processor.QConClass
				(i_trans, i_parent, i_field, a_class);
			Morph(removeExisting, newConstraint, a_class);
			return newConstraint;
		}

		internal override com.db4o.@internal.query.processor.QCon ShareParent(object a_object
			, bool[] removeExisting)
		{
			if (i_parent == null)
			{
				return null;
			}
			object obj = i_field.Coerce(a_object);
			if (obj == com.db4o.foundation.No4.INSTANCE)
			{
				com.db4o.@internal.query.processor.QConObject falseConstraint = new com.db4o.@internal.query.processor.QConFalse
					(i_trans, i_parent, i_field);
				Morph(removeExisting, falseConstraint, ReflectClassForObject(obj));
				return falseConstraint;
			}
			com.db4o.@internal.query.processor.QConObject newConstraint = new com.db4o.@internal.query.processor.QConObject
				(i_trans, i_parent, i_field, obj);
			Morph(removeExisting, newConstraint, ReflectClassForObject(obj));
			return newConstraint;
		}

		private com.db4o.reflect.ReflectClass ReflectClassForObject(object obj)
		{
			return i_trans.Reflector().ForObject(obj);
		}

		private void Morph(bool[] removeExisting, com.db4o.@internal.query.processor.QConObject
			 newConstraint, com.db4o.reflect.ReflectClass claxx)
		{
			bool mayMorph = true;
			if (claxx != null)
			{
				com.db4o.@internal.ClassMetadata yc = i_trans.Stream().ProduceYapClass(claxx);
				if (yc != null)
				{
					System.Collections.IEnumerator i = IterateChildren();
					while (i.MoveNext())
					{
						com.db4o.@internal.query.processor.QField qf = ((com.db4o.@internal.query.processor.QCon
							)i.Current).GetField();
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
				System.Collections.IEnumerator j = IterateChildren();
				while (j.MoveNext())
				{
					newConstraint.AddConstraint((com.db4o.@internal.query.processor.QCon)j.Current);
				}
				if (HasJoins())
				{
					System.Collections.IEnumerator k = IterateJoins();
					while (k.MoveNext())
					{
						com.db4o.@internal.query.processor.QConJoin qcj = (com.db4o.@internal.query.processor.QConJoin
							)k.Current;
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
