namespace com.db4o
{
	/// <summary>Object constraint on queries</summary>
	/// <exclude></exclude>
	public class QConObject : com.db4o.QCon
	{
		public object i_object;

		public int i_objectID;

		[com.db4o.Transient]
		internal com.db4o.YapClass i_yapClass;

		public int i_yapClassID;

		public com.db4o.QField i_field;

		[com.db4o.Transient]
		internal com.db4o.YapComparable i_comparator;

		public com.db4o.config.ObjectAttribute i_attributeProvider;

		[com.db4o.Transient]
		private bool i_selfComparison = false;

		[com.db4o.Transient]
		private bool i_loadedFromIndex;

		public QConObject()
		{
		}

		internal QConObject(com.db4o.Transaction a_trans, com.db4o.QCon a_parent, com.db4o.QField
			 a_field, object a_object) : base(a_trans)
		{
			i_parent = a_parent;
			if (a_object is com.db4o.config.Compare)
			{
				a_object = ((com.db4o.config.Compare)a_object).Compare();
			}
			i_object = a_object;
			i_field = a_field;
			AssociateYapClass(a_trans, a_object);
		}

		private void AssociateYapClass(com.db4o.Transaction a_trans, object a_object)
		{
			if (a_object == null)
			{
				i_object = null;
				i_comparator = com.db4o.Null.INSTANCE;
				i_yapClass = null;
			}
			else
			{
				i_yapClass = a_trans.Stream().GetYapClass(a_trans.Reflector().ForObject(a_object)
					, true);
				if (i_yapClass != null)
				{
					i_object = i_yapClass.GetComparableObject(a_object);
					if (a_object != i_object)
					{
						i_attributeProvider = i_yapClass.i_config.QueryAttributeProvider();
						i_yapClass = a_trans.Stream().GetYapClass(a_trans.Reflector().ForObject(i_object)
							, true);
					}
					if (i_yapClass != null)
					{
						i_yapClass.CollectConstraints(a_trans, this, i_object, new _AnonymousInnerClass83
							(this));
					}
					else
					{
						AssociateYapClass(a_trans, null);
					}
				}
				else
				{
					AssociateYapClass(a_trans, null);
				}
			}
		}

		private sealed class _AnonymousInnerClass83 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass83(QConObject _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object obj)
			{
				this._enclosing.AddConstraint((com.db4o.QCon)obj);
			}

			private readonly QConObject _enclosing;
		}

		public override bool CanBeIndexLeaf()
		{
			return i_yapClass != null && i_yapClass.IsPrimitive();
		}

		public override bool CanLoadByIndex()
		{
			if (i_field == null)
			{
				return false;
			}
			if (i_field.i_yapField == null)
			{
				return false;
			}
			if (!i_field.i_yapField.HasIndex())
			{
				return false;
			}
			if (!i_evaluator.SupportsIndex())
			{
				return false;
			}
			return i_field.i_yapField.CanLoadByIndex();
		}

		internal override void CreateCandidates(com.db4o.foundation.Collection4 a_candidateCollection
			)
		{
			if (i_loadedFromIndex && !HasChildren())
			{
				return;
			}
			base.CreateCandidates(a_candidateCollection);
		}

		internal override bool Evaluate(com.db4o.QCandidate a_candidate)
		{
			try
			{
				return a_candidate.Evaluate(this, i_evaluator);
			}
			catch (System.Exception e)
			{
				return false;
			}
		}

		internal override void EvaluateEvaluationsExec(com.db4o.QCandidates a_candidates, 
			bool rereadObject)
		{
			if (i_field.IsSimple())
			{
				bool hasEvaluation = false;
				com.db4o.foundation.Iterator4 i = IterateChildren();
				while (i.MoveNext())
				{
					if (i.Current() is com.db4o.QConEvaluation)
					{
						hasEvaluation = true;
						break;
					}
				}
				if (hasEvaluation)
				{
					a_candidates.Traverse(i_field);
					com.db4o.foundation.Iterator4 j = IterateChildren();
					while (j.MoveNext())
					{
						((com.db4o.QCon)j.Current()).EvaluateEvaluationsExec(a_candidates, false);
					}
				}
			}
		}

		internal override void EvaluateSelf()
		{
			if (i_yapClass != null)
			{
				if (!(i_yapClass is com.db4o.YapClassPrimitive))
				{
					if (!i_evaluator.Identity())
					{
						if (i_yapClass == i_candidates.i_yapClass)
						{
							if (i_evaluator.IsDefault() && (!HasJoins()))
							{
								return;
							}
						}
						i_selfComparison = true;
					}
					i_comparator = i_yapClass.PrepareComparison(i_object);
				}
			}
			base.EvaluateSelf();
			i_selfComparison = false;
		}

		internal override void Collect(com.db4o.QCandidates a_candidates)
		{
			if (i_field.IsClass())
			{
				a_candidates.Traverse(i_field);
				a_candidates.Filter(i_candidates);
			}
		}

		internal override void EvaluateSimpleExec(com.db4o.QCandidates a_candidates)
		{
			if (i_orderID != 0 || !i_loadedFromIndex)
			{
				if (i_field.IsSimple() || IsNullConstraint())
				{
					a_candidates.Traverse(i_field);
					PrepareComparison(i_field);
					a_candidates.Filter(this);
				}
			}
		}

		internal virtual com.db4o.YapComparable GetComparator(com.db4o.QCandidate a_candidate
			)
		{
			if (i_comparator == null)
			{
				return a_candidate.PrepareComparison(i_trans.Stream(), i_object);
			}
			return i_comparator;
		}

		internal override com.db4o.YapClass GetYapClass()
		{
			return i_yapClass;
		}

		public override com.db4o.QField GetField()
		{
			return i_field;
		}

		internal virtual int GetObjectID()
		{
			if (i_objectID == 0)
			{
				i_objectID = i_trans.Stream().GetID1(i_trans, i_object);
				if (i_objectID == 0)
				{
					i_objectID = -1;
				}
			}
			return i_objectID;
		}

		internal override bool HasObjectInParentPath(object obj)
		{
			if (obj == i_object)
			{
				return true;
			}
			return base.HasObjectInParentPath(obj);
		}

		public override int IdentityID()
		{
			if (i_evaluator.Identity())
			{
				int id = GetObjectID();
				if (id != 0)
				{
					if (!(i_evaluator is com.db4o.QENot))
					{
						return id;
					}
				}
			}
			return 0;
		}

		internal override bool IsNullConstraint()
		{
			return i_object == null;
		}

		internal override void Log(string indent)
		{
		}

		internal override string LogObject()
		{
			return "";
		}

		internal override void Marshall()
		{
			base.Marshall();
			GetObjectID();
			if (i_yapClass != null)
			{
				i_yapClassID = i_yapClass.GetID();
			}
		}

		public override bool OnSameFieldAs(com.db4o.QCon other)
		{
			if (!(other is com.db4o.QConObject))
			{
				return false;
			}
			return i_field == ((com.db4o.QConObject)other).i_field;
		}

		internal virtual void PrepareComparison(com.db4o.QField a_field)
		{
			if (IsNullConstraint() & !a_field.IsArray())
			{
				i_comparator = com.db4o.Null.INSTANCE;
			}
			else
			{
				i_comparator = a_field.PrepareComparison(i_object);
			}
		}

		internal override void RemoveChildrenJoins()
		{
			base.RemoveChildrenJoins();
			_children = null;
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
				return null;
			}
			return i_parent.AddSharedConstraint(i_field, obj);
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
			i_parent.AddConstraint(newConstraint);
			return newConstraint;
		}

		internal object Translate(object candidate)
		{
			if (i_attributeProvider != null)
			{
				i_candidates.i_trans.Stream().Activate1(i_candidates.i_trans, candidate);
				return i_attributeProvider.Attribute(candidate);
			}
			return candidate;
		}

		internal override void Unmarshall(com.db4o.Transaction a_trans)
		{
			if (i_trans == null)
			{
				base.Unmarshall(a_trans);
				if (i_object == null)
				{
					i_comparator = com.db4o.Null.INSTANCE;
				}
				if (i_yapClassID != 0)
				{
					i_yapClass = a_trans.Stream().GetYapClass(i_yapClassID);
				}
				if (i_field != null)
				{
					i_field.Unmarshall(a_trans);
				}
				if (i_objectID != 0)
				{
					object obj = a_trans.Stream().GetByID(i_objectID);
					if (obj != null)
					{
						i_object = obj;
					}
				}
			}
		}

		public override void Visit(object obj)
		{
			com.db4o.QCandidate qc = (com.db4o.QCandidate)obj;
			bool res = true;
			bool processed = false;
			if (i_selfComparison)
			{
				com.db4o.YapClass yc = qc.ReadYapClass();
				if (yc != null)
				{
					res = i_evaluator.Not(i_yapClass.GetHigherHierarchy(yc) == i_yapClass);
					processed = true;
				}
			}
			if (!processed)
			{
				res = Evaluate(qc);
			}
			if (i_orderID != 0 && res)
			{
				object cmp = qc.Value();
				if (cmp != null && i_field != null)
				{
					com.db4o.YapComparable comparatorBackup = i_comparator;
					i_comparator = i_field.PrepareComparison(qc.Value());
					i_candidates.AddOrder(new com.db4o.QOrder(this, qc));
					i_comparator = comparatorBackup.PrepareComparison(i_object);
				}
			}
			Visit1(qc.GetRoot(), this, res);
		}

		public override com.db4o.query.Constraint Contains()
		{
			lock (StreamLock())
			{
				i_evaluator = i_evaluator.Add(new com.db4o.QEContains(true));
				return this;
			}
		}

		public override com.db4o.query.Constraint Equal()
		{
			lock (StreamLock())
			{
				i_evaluator = i_evaluator.Add(new com.db4o.QEEqual());
				return this;
			}
		}

		public override object GetObject()
		{
			lock (StreamLock())
			{
				return i_object;
			}
		}

		public override com.db4o.query.Constraint Greater()
		{
			lock (StreamLock())
			{
				i_evaluator = i_evaluator.Add(new com.db4o.QEGreater());
				return this;
			}
		}

		public override com.db4o.query.Constraint Identity()
		{
			lock (StreamLock())
			{
				int id = GetObjectID();
				if (!(id > 0))
				{
					i_objectID = 0;
					com.db4o.inside.Exceptions4.ThrowRuntimeException(51);
				}
				RemoveChildrenJoins();
				i_evaluator = i_evaluator.Add(new com.db4o.QEIdentity());
				return this;
			}
		}

		public override com.db4o.query.Constraint Like()
		{
			lock (StreamLock())
			{
				i_evaluator = i_evaluator.Add(new com.db4o.QEContains(false));
				return this;
			}
		}

		public override com.db4o.query.Constraint Smaller()
		{
			lock (StreamLock())
			{
				i_evaluator = i_evaluator.Add(new com.db4o.QESmaller());
				return this;
			}
		}

		public override com.db4o.query.Constraint StartsWith(bool caseSensitive)
		{
			lock (StreamLock())
			{
				i_evaluator = i_evaluator.Add(new com.db4o.QEStartsWith(caseSensitive));
				return this;
			}
		}

		public override com.db4o.query.Constraint EndsWith(bool caseSensitive)
		{
			lock (StreamLock())
			{
				i_evaluator = i_evaluator.Add(new com.db4o.QEEndsWith(caseSensitive));
				return this;
			}
		}

		public override string ToString()
		{
			return base.ToString();
			string str = "QConObject ";
			if (i_object != null)
			{
				str += i_object.ToString();
			}
			return str;
		}
	}
}
