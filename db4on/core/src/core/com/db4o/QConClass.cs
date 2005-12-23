namespace com.db4o
{
	/// <summary>Class constraint on queries</summary>
	/// <exclude></exclude>
	public class QConClass : com.db4o.QConObject
	{
		[com.db4o.Transient]
		private com.db4o.reflect.ReflectClass _claxx;

		public string _className;

		public bool i_equal;

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
				if (claxx.Equals(a_trans.i_stream.i_handlers.ICLASS_OBJECT))
				{
					i_yapClass = (com.db4o.YapClass)((com.db4o.YapClassPrimitive)i_yapClass).i_handler;
				}
			}
			_claxx = claxx;
		}

		public override bool canBeIndexLeaf()
		{
			return false;
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
				res = i_equal ? _claxx.Equals(claxx) : _claxx.isAssignableFrom(claxx);
			}
			return i_evaluator.not(res);
		}

		internal override void evaluateSelf()
		{
			if (i_evaluator.isDefault())
			{
				if (i_orderID == 0 && !hasJoins())
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

		internal override string logObject()
		{
			return "";
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
