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
		private com.db4o.inside.ix.IxTraverser i_indexTraverser;

		[com.db4o.Transient]
		private com.db4o.QCon i_indexConstraint;

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
				a_object = ((com.db4o.config.Compare)a_object).compare();
			}
			i_object = a_object;
			i_field = a_field;
			associateYapClass(a_trans, a_object);
		}

		private void associateYapClass(com.db4o.Transaction a_trans, object a_object)
		{
			if (a_object == null)
			{
				i_object = null;
				i_comparator = com.db4o.Null.INSTANCE;
				i_yapClass = null;
			}
			else
			{
				i_yapClass = a_trans.i_stream.getYapClass(a_trans.reflector().forObject(a_object)
					, true);
				if (i_yapClass != null)
				{
					i_object = i_yapClass.getComparableObject(a_object);
					if (a_object != i_object)
					{
						i_attributeProvider = i_yapClass.i_config.queryAttributeProvider();
						i_yapClass = a_trans.i_stream.getYapClass(a_trans.reflector().forObject(i_object)
							, true);
					}
					if (i_yapClass != null)
					{
						i_yapClass.collectConstraints(a_trans, this, i_object, new _AnonymousInnerClass88
							(this));
					}
					else
					{
						associateYapClass(a_trans, null);
					}
				}
				else
				{
					associateYapClass(a_trans, null);
				}
			}
		}

		private sealed class _AnonymousInnerClass88 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass88(QConObject _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object obj)
			{
				this._enclosing.addConstraint((com.db4o.QCon)obj);
			}

			private readonly QConObject _enclosing;
		}

		public override bool canBeIndexLeaf()
		{
			return i_yapClass != null && i_yapClass.isPrimitive();
		}

		public override bool canLoadByIndex()
		{
			if (i_field == null)
			{
				return false;
			}
			if (i_field.i_yapField == null)
			{
				return false;
			}
			if (!i_field.i_yapField.hasIndex())
			{
				return false;
			}
			if (hasOrJoins())
			{
				return false;
			}
			return i_field.i_yapField.canLoadByIndex(this, i_evaluator);
		}

		internal override void createCandidates(com.db4o.foundation.Collection4 a_candidateCollection
			)
		{
			if (i_loadedFromIndex && !hasChildren())
			{
				return;
			}
			base.createCandidates(a_candidateCollection);
		}

		internal override bool evaluate(com.db4o.QCandidate a_candidate)
		{
			try
			{
				return a_candidate.evaluate(this, i_evaluator);
			}
			catch (System.Exception e)
			{
				return false;
			}
		}

		internal override void evaluateEvaluationsExec(com.db4o.QCandidates a_candidates, 
			bool rereadObject)
		{
			if (i_field.isSimple())
			{
				bool hasEvaluation = false;
				com.db4o.foundation.Iterator4 i = iterateChildren();
				while (i.hasNext())
				{
					if (i.next() is com.db4o.QConEvaluation)
					{
						hasEvaluation = true;
						break;
					}
				}
				if (hasEvaluation)
				{
					a_candidates.traverse(i_field);
					com.db4o.foundation.Iterator4 j = iterateChildren();
					while (j.hasNext())
					{
						((com.db4o.QCon)j.next()).evaluateEvaluationsExec(a_candidates, false);
					}
				}
			}
		}

		internal override void evaluateSelf()
		{
			if (i_yapClass != null)
			{
				if (!(i_yapClass is com.db4o.YapClassPrimitive))
				{
					if (!i_evaluator.identity())
					{
						if (i_yapClass == i_candidates.i_yapClass)
						{
							if (i_evaluator.isDefault() && (!hasJoins()))
							{
								return;
							}
						}
						i_selfComparison = true;
					}
					i_comparator = i_yapClass.prepareComparison(i_object);
				}
			}
			base.evaluateSelf();
			i_selfComparison = false;
		}

		internal override void collect(com.db4o.QCandidates a_candidates)
		{
			if (i_field.isClass())
			{
				a_candidates.traverse(i_field);
				a_candidates.filter(i_candidates);
			}
		}

		internal override void evaluateSimpleExec(com.db4o.QCandidates a_candidates)
		{
			if (i_orderID != 0 || !i_loadedFromIndex)
			{
				if (i_field.isSimple() || isNullConstraint())
				{
					a_candidates.traverse(i_field);
					prepareComparison(i_field);
					a_candidates.filter(this);
				}
			}
		}

		public virtual int findBoundsQuery(com.db4o.inside.ix.IxTraverser traverser)
		{
			return traverser.findBoundsQuery(this, i_object);
		}

		internal virtual com.db4o.YapComparable getComparator(com.db4o.QCandidate a_candidate
			)
		{
			if (i_comparator == null)
			{
				return a_candidate.prepareComparison(i_trans.i_stream, i_object);
			}
			return i_comparator;
		}

		internal override com.db4o.YapClass getYapClass()
		{
			return i_yapClass;
		}

		internal override com.db4o.QField getField()
		{
			return i_field;
		}

		internal virtual int getObjectID()
		{
			if (i_objectID == 0)
			{
				i_objectID = i_trans.i_stream.getID1(i_trans, i_object);
				if (i_objectID == 0)
				{
					i_objectID = -1;
				}
			}
			return i_objectID;
		}

		internal override bool hasObjectInParentPath(object obj)
		{
			if (obj == i_object)
			{
				return true;
			}
			return base.hasObjectInParentPath(obj);
		}

		public override int identityID()
		{
			if (i_evaluator.identity())
			{
				int id = getObjectID();
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

		public override com.db4o.inside.ix.IxTree indexRoot()
		{
			return (com.db4o.inside.ix.IxTree)i_field.i_yapField.getIndexRoot(i_trans);
		}

		internal override bool isNullConstraint()
		{
			return i_object == null;
		}

		internal override void log(string indent)
		{
		}

		internal override string logObject()
		{
			return "";
		}

		internal override void marshall()
		{
			base.marshall();
			getObjectID();
			if (i_yapClass != null)
			{
				i_yapClassID = i_yapClass.getID();
			}
		}

		public override bool onSameFieldAs(com.db4o.QCon other)
		{
			if (!(other is com.db4o.QConObject))
			{
				return false;
			}
			return i_field == ((com.db4o.QConObject)other).i_field;
		}

		internal virtual void prepareComparison(com.db4o.QField a_field)
		{
			if (isNullConstraint() & !a_field.isArray())
			{
				i_comparator = com.db4o.Null.INSTANCE;
			}
			else
			{
				i_comparator = a_field.prepareComparison(i_object);
			}
		}

		internal override void removeChildrenJoins()
		{
			base.removeChildrenJoins();
			_children = null;
		}

		internal override com.db4o.QCon shareParent(object a_object, bool[] removeExisting
			)
		{
			if (i_parent == null)
			{
				return null;
			}
			object obj = i_field.coerce(a_object);
			if (obj == com.db4o.foundation.No4.INSTANCE)
			{
				return null;
			}
			return i_parent.addSharedConstraint(i_field, obj);
		}

		internal override com.db4o.QConClass shareParentForClass(com.db4o.reflect.ReflectClass
			 a_class, bool[] removeExisting)
		{
			if (i_parent == null)
			{
				return null;
			}
			if (!i_field.canHold(a_class))
			{
				return null;
			}
			com.db4o.QConClass newConstraint = new com.db4o.QConClass(i_trans, i_parent, i_field
				, a_class);
			i_parent.addConstraint(newConstraint);
			return newConstraint;
		}

		internal object translate(object candidate)
		{
			if (i_attributeProvider != null)
			{
				i_candidates.i_trans.i_stream.activate1(i_candidates.i_trans, candidate);
				return i_attributeProvider.attribute(candidate);
			}
			return candidate;
		}

		internal override void unmarshall(com.db4o.Transaction a_trans)
		{
			if (i_trans == null)
			{
				base.unmarshall(a_trans);
				if (i_object == null)
				{
					i_comparator = com.db4o.Null.INSTANCE;
				}
				if (i_yapClassID != 0)
				{
					i_yapClass = a_trans.i_stream.getYapClass(i_yapClassID);
				}
				if (i_field != null)
				{
					i_field.unmarshall(a_trans);
				}
				if (i_objectID != 0)
				{
					object obj = a_trans.i_stream.getByID(i_objectID);
					if (obj != null)
					{
						i_object = obj;
					}
				}
			}
		}

		public override void visit(object obj)
		{
			com.db4o.QCandidate qc = (com.db4o.QCandidate)obj;
			bool res = true;
			bool processed = false;
			if (i_selfComparison)
			{
				com.db4o.YapClass yc = qc.readYapClass();
				if (yc != null)
				{
					res = i_evaluator.not(i_yapClass.getHigherHierarchy(yc) == i_yapClass);
					processed = true;
				}
			}
			if (!processed)
			{
				res = evaluate(qc);
			}
			if (i_orderID != 0 && res)
			{
				object cmp = qc.value();
				if (cmp != null && i_field != null)
				{
					com.db4o.YapComparable comparatorBackup = i_comparator;
					i_comparator = i_field.prepareComparison(qc.value());
					i_candidates.addOrder(new com.db4o.QOrder(this, qc));
					i_comparator = comparatorBackup.prepareComparison(i_object);
				}
			}
			visit1(qc.getRoot(), this, res);
		}

		public override com.db4o.query.Constraint contains()
		{
			lock (streamLock())
			{
				i_evaluator = i_evaluator.add(new com.db4o.QEContains(true));
				return this;
			}
		}

		public override com.db4o.query.Constraint equal()
		{
			lock (streamLock())
			{
				i_evaluator = i_evaluator.add(new com.db4o.QEEqual());
				return this;
			}
		}

		public override object getObject()
		{
			lock (streamLock())
			{
				return i_object;
			}
		}

		public override com.db4o.query.Constraint greater()
		{
			lock (streamLock())
			{
				i_evaluator = i_evaluator.add(new com.db4o.QEGreater());
				return this;
			}
		}

		public override com.db4o.query.Constraint identity()
		{
			lock (streamLock())
			{
				int id = getObjectID();
				if (!(id > 0))
				{
					i_objectID = 0;
					com.db4o.inside.Exceptions4.throwRuntimeException(51);
				}
				removeChildrenJoins();
				i_evaluator = i_evaluator.add(new com.db4o.QEIdentity());
				return this;
			}
		}

		public override com.db4o.query.Constraint like()
		{
			lock (streamLock())
			{
				i_evaluator = i_evaluator.add(new com.db4o.QEContains(false));
				return this;
			}
		}

		public override com.db4o.query.Constraint smaller()
		{
			lock (streamLock())
			{
				i_evaluator = i_evaluator.add(new com.db4o.QESmaller());
				return this;
			}
		}

		public override com.db4o.query.Constraint startsWith(bool caseSensitive)
		{
			lock (streamLock())
			{
				i_evaluator = i_evaluator.add(new com.db4o.QEStartsWith(caseSensitive));
				return this;
			}
		}

		public override com.db4o.query.Constraint endsWith(bool caseSensitive)
		{
			lock (streamLock())
			{
				i_evaluator = i_evaluator.add(new com.db4o.QEEndsWith(caseSensitive));
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
