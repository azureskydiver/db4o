namespace com.db4o.@internal.query.processor
{
	/// <summary>Base class for all constraints on queries.</summary>
	/// <remarks>Base class for all constraints on queries.</remarks>
	/// <exclude></exclude>
	public abstract class QCon : com.db4o.query.Constraint, com.db4o.foundation.Visitor4
		, com.db4o.types.Unversioned
	{
		internal static readonly com.db4o.@internal.IDGenerator idGenerator = new com.db4o.@internal.IDGenerator
			();

		[System.NonSerialized]
		internal com.db4o.@internal.query.processor.QCandidates i_candidates;

		public com.db4o.foundation.Collection4 i_childrenCandidates;

		public com.db4o.foundation.List4 _children;

		public com.db4o.@internal.query.processor.QE i_evaluator = com.db4o.@internal.query.processor.QE
			.DEFAULT;

		public int i_id;

		public com.db4o.foundation.Collection4 i_joins;

		private int i_orderID = 0;

		public com.db4o.@internal.query.processor.QCon i_parent;

		public bool i_removed = false;

		[System.NonSerialized]
		internal com.db4o.@internal.Transaction i_trans;

		public QCon()
		{
		}

		internal QCon(com.db4o.@internal.Transaction a_trans)
		{
			i_id = idGenerator.Next();
			i_trans = a_trans;
		}

		internal virtual com.db4o.@internal.query.processor.QCon AddConstraint(com.db4o.@internal.query.processor.QCon
			 a_child)
		{
			_children = new com.db4o.foundation.List4(_children, a_child);
			return a_child;
		}

		public virtual com.db4o.@internal.Transaction Transaction()
		{
			return i_trans;
		}

		internal virtual void AddJoin(com.db4o.@internal.query.processor.QConJoin a_join)
		{
			if (i_joins == null)
			{
				i_joins = new com.db4o.foundation.Collection4();
			}
			i_joins.Add(a_join);
		}

		internal virtual com.db4o.@internal.query.processor.QCon AddSharedConstraint(com.db4o.@internal.query.processor.QField
			 a_field, object a_object)
		{
			com.db4o.@internal.query.processor.QConObject newConstraint = new com.db4o.@internal.query.processor.QConObject
				(i_trans, this, a_field, a_object);
			AddConstraint(newConstraint);
			return newConstraint;
		}

		public virtual com.db4o.query.Constraint And(com.db4o.query.Constraint andWith)
		{
			lock (StreamLock())
			{
				return Join(andWith, true);
			}
		}

		internal virtual void ApplyOrdering()
		{
			if (HasOrdering())
			{
				com.db4o.@internal.query.processor.QCon root = GetRoot();
				root.i_candidates.ApplyOrdering(i_candidates.i_ordered, i_orderID);
			}
		}

		internal virtual bool Attach(com.db4o.@internal.query.processor.QQuery query, string
			 a_field)
		{
			com.db4o.@internal.query.processor.QCon qcon = this;
			com.db4o.@internal.ClassMetadata yc = GetYapClass();
			bool[] foundField = { false };
			ForEachChildField(a_field, new _AnonymousInnerClass108(this, foundField, query));
			if (foundField[0])
			{
				return true;
			}
			com.db4o.@internal.query.processor.QField qf = null;
			if (yc == null || yc.HoldsAnyClass())
			{
				int[] count = { 0 };
				com.db4o.@internal.FieldMetadata[] yfs = { null };
				i_trans.Stream().ClassCollection().AttachQueryNode(a_field, new _AnonymousInnerClass126
					(this, yfs, count));
				if (count[0] == 0)
				{
					return false;
				}
				if (count[0] == 1)
				{
					qf = yfs[0].QField(i_trans);
				}
				else
				{
					qf = new com.db4o.@internal.query.processor.QField(i_trans, a_field, null, 0, 0);
				}
			}
			else
			{
				if (yc.ConfigInstantiates())
				{
					i_trans.Stream().i_handlers._diagnosticProcessor.DescendIntoTranslator(yc, a_field
						);
				}
				com.db4o.@internal.FieldMetadata yf = yc.GetYapField(a_field);
				if (yf != null)
				{
					qf = yf.QField(i_trans);
				}
				if (qf == null)
				{
					qf = new com.db4o.@internal.query.processor.QField(i_trans, a_field, null, 0, 0);
				}
			}
			com.db4o.@internal.query.processor.QConPath qcp = new com.db4o.@internal.query.processor.QConPath
				(i_trans, qcon, qf);
			query.AddConstraint(qcp);
			qcon.AddConstraint(qcp);
			return true;
		}

		private sealed class _AnonymousInnerClass108 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass108(QCon _enclosing, bool[] foundField, com.db4o.@internal.query.processor.QQuery
				 query)
			{
				this._enclosing = _enclosing;
				this.foundField = foundField;
				this.query = query;
			}

			public void Visit(object obj)
			{
				foundField[0] = true;
				query.AddConstraint((com.db4o.@internal.query.processor.QCon)obj);
			}

			private readonly QCon _enclosing;

			private readonly bool[] foundField;

			private readonly com.db4o.@internal.query.processor.QQuery query;
		}

		private sealed class _AnonymousInnerClass126 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass126(QCon _enclosing, com.db4o.@internal.FieldMetadata[]
				 yfs, int[] count)
			{
				this._enclosing = _enclosing;
				this.yfs = yfs;
				this.count = count;
			}

			public void Visit(object obj)
			{
				yfs[0] = (com.db4o.@internal.FieldMetadata)((object[])obj)[1];
				count[0]++;
			}

			private readonly QCon _enclosing;

			private readonly com.db4o.@internal.FieldMetadata[] yfs;

			private readonly int[] count;
		}

		public virtual bool CanBeIndexLeaf()
		{
			return false;
		}

		public virtual bool CanLoadByIndex()
		{
			return false;
		}

		internal virtual void CheckLastJoinRemoved()
		{
			if (i_joins.Size() == 0)
			{
				i_joins = null;
			}
		}

		internal virtual void Collect(com.db4o.@internal.query.processor.QCandidates a_candidates
			)
		{
		}

		public virtual com.db4o.query.Constraint Contains()
		{
			throw NotSupported();
		}

		internal virtual void CreateCandidates(com.db4o.foundation.Collection4 a_candidateCollection
			)
		{
			System.Collections.IEnumerator j = a_candidateCollection.GetEnumerator();
			while (j.MoveNext())
			{
				com.db4o.@internal.query.processor.QCandidates candidates = (com.db4o.@internal.query.processor.QCandidates
					)j.Current;
				if (candidates.TryAddConstraint(this))
				{
					i_candidates = candidates;
					return;
				}
			}
			i_candidates = new com.db4o.@internal.query.processor.QCandidates(i_trans, GetYapClass
				(), GetField());
			i_candidates.AddConstraint(this);
			a_candidateCollection.Add(i_candidates);
		}

		internal virtual void DoNotInclude(com.db4o.@internal.query.processor.QCandidate 
			a_root)
		{
			if (i_parent != null)
			{
				i_parent.Visit1(a_root, this, false);
			}
			else
			{
				a_root.DoNotInclude();
			}
		}

		public virtual com.db4o.query.Constraint Equal()
		{
			throw NotSupported();
		}

		internal virtual bool Evaluate(com.db4o.@internal.query.processor.QCandidate a_candidate
			)
		{
			throw com.db4o.@internal.Exceptions4.VirtualException();
		}

		internal virtual void EvaluateChildren()
		{
			System.Collections.IEnumerator i = i_childrenCandidates.GetEnumerator();
			while (i.MoveNext())
			{
				((com.db4o.@internal.query.processor.QCandidates)i.Current).Evaluate();
			}
		}

		internal virtual void EvaluateCollectChildren()
		{
			System.Collections.IEnumerator i = i_childrenCandidates.GetEnumerator();
			while (i.MoveNext())
			{
				((com.db4o.@internal.query.processor.QCandidates)i.Current).Collect(i_candidates);
			}
		}

		internal virtual void EvaluateCreateChildrenCandidates()
		{
			i_childrenCandidates = new com.db4o.foundation.Collection4();
			System.Collections.IEnumerator i = IterateChildren();
			while (i.MoveNext())
			{
				((com.db4o.@internal.query.processor.QCon)i.Current).CreateCandidates(i_childrenCandidates
					);
			}
		}

		internal virtual void EvaluateEvaluations()
		{
			System.Collections.IEnumerator i = IterateChildren();
			while (i.MoveNext())
			{
				((com.db4o.@internal.query.processor.QCon)i.Current).EvaluateEvaluationsExec(i_candidates
					, true);
			}
		}

		internal virtual void EvaluateEvaluationsExec(com.db4o.@internal.query.processor.QCandidates
			 a_candidates, bool rereadObject)
		{
		}

		internal virtual void EvaluateSelf()
		{
			i_candidates.Filter(this);
		}

		internal virtual void EvaluateSimpleChildren()
		{
			if (_children == null)
			{
				return;
			}
			System.Collections.IEnumerator i = IterateChildren();
			while (i.MoveNext())
			{
				com.db4o.@internal.query.processor.QCon qcon = (com.db4o.@internal.query.processor.QCon
					)i.Current;
				i_candidates.SetCurrentConstraint(qcon);
				qcon.SetCandidates(i_candidates);
				qcon.EvaluateSimpleExec(i_candidates);
				qcon.ApplyOrdering();
			}
			i_candidates.SetCurrentConstraint(null);
		}

		internal virtual void EvaluateSimpleExec(com.db4o.@internal.query.processor.QCandidates
			 a_candidates)
		{
		}

		internal virtual void ExchangeConstraint(com.db4o.@internal.query.processor.QCon 
			a_exchange, com.db4o.@internal.query.processor.QCon a_with)
		{
			com.db4o.foundation.List4 previous = null;
			com.db4o.foundation.List4 current = _children;
			while (current != null)
			{
				if (current._element == a_exchange)
				{
					if (previous == null)
					{
						_children = current._next;
					}
					else
					{
						previous._next = current._next;
					}
				}
				previous = current;
				current = current._next;
			}
			_children = new com.db4o.foundation.List4(_children, a_with);
		}

		internal virtual void ForEachChildField(string name, com.db4o.foundation.Visitor4
			 visitor)
		{
			System.Collections.IEnumerator i = IterateChildren();
			while (i.MoveNext())
			{
				object obj = i.Current;
				if (obj is com.db4o.@internal.query.processor.QConObject)
				{
					if (((com.db4o.@internal.query.processor.QConObject)obj).i_field.i_name.Equals(name
						))
					{
						visitor.Visit(obj);
					}
				}
			}
		}

		public virtual com.db4o.@internal.query.processor.QField GetField()
		{
			return null;
		}

		public virtual object GetObject()
		{
			throw NotSupported();
		}

		internal virtual com.db4o.@internal.query.processor.QCon GetRoot()
		{
			if (i_parent != null)
			{
				return i_parent.GetRoot();
			}
			return this;
		}

		internal virtual com.db4o.@internal.query.processor.QCon ProduceTopLevelJoin()
		{
			if (!HasJoins())
			{
				return this;
			}
			System.Collections.IEnumerator i = IterateJoins();
			if (i_joins.Size() == 1)
			{
				i.MoveNext();
				return ((com.db4o.@internal.query.processor.QCon)i.Current).ProduceTopLevelJoin();
			}
			com.db4o.foundation.Collection4 col = new com.db4o.foundation.Collection4();
			while (i.MoveNext())
			{
				col.Ensure(((com.db4o.@internal.query.processor.QCon)i.Current).ProduceTopLevelJoin
					());
			}
			i = col.GetEnumerator();
			i.MoveNext();
			com.db4o.@internal.query.processor.QCon qcon = (com.db4o.@internal.query.processor.QCon
				)i.Current;
			if (col.Size() == 1)
			{
				return qcon;
			}
			while (i.MoveNext())
			{
				qcon = (com.db4o.@internal.query.processor.QCon)qcon.And((com.db4o.query.Constraint
					)i.Current);
			}
			return qcon;
		}

		internal virtual com.db4o.@internal.ClassMetadata GetYapClass()
		{
			return null;
		}

		public virtual com.db4o.query.Constraint Greater()
		{
			throw NotSupported();
		}

		public virtual bool HasChildren()
		{
			return _children != null;
		}

		public virtual bool HasParent()
		{
			return i_parent != null;
		}

		public virtual com.db4o.@internal.query.processor.QCon Parent()
		{
			return i_parent;
		}

		public virtual bool HasOrJoins()
		{
			com.db4o.foundation.Collection4 lookedAt = new com.db4o.foundation.Collection4();
			return HasOrJoins(lookedAt);
		}

		internal virtual bool HasOrJoins(com.db4o.foundation.Collection4 lookedAt)
		{
			if (lookedAt.ContainsByIdentity(this))
			{
				return false;
			}
			lookedAt.Add(this);
			if (i_joins == null)
			{
				return false;
			}
			System.Collections.IEnumerator i = IterateJoins();
			while (i.MoveNext())
			{
				com.db4o.@internal.query.processor.QConJoin join = (com.db4o.@internal.query.processor.QConJoin
					)i.Current;
				if (join.IsOr())
				{
					return true;
				}
				if (join.HasOrJoins(lookedAt))
				{
					return true;
				}
			}
			return false;
		}

		public virtual bool HasOrJoinWith(com.db4o.@internal.query.processor.QConObject y
			)
		{
			System.Collections.IEnumerator i = IterateJoins();
			while (i.MoveNext())
			{
				com.db4o.@internal.query.processor.QConJoin join = (com.db4o.@internal.query.processor.QConJoin
					)i.Current;
				if (join.IsOr())
				{
					if (y == join.GetOtherConstraint(this))
					{
						return true;
					}
				}
			}
			return false;
		}

		public virtual bool HasJoins()
		{
			if (i_joins == null)
			{
				return false;
			}
			return i_joins.Size() > 0;
		}

		public virtual bool HasObjectInParentPath(object obj)
		{
			if (i_parent != null)
			{
				return i_parent.HasObjectInParentPath(obj);
			}
			return false;
		}

		public virtual com.db4o.query.Constraint Identity()
		{
			throw NotSupported();
		}

		public virtual int IdentityID()
		{
			return 0;
		}

		internal virtual bool IsNot()
		{
			return i_evaluator is com.db4o.@internal.query.processor.QENot;
		}

		internal virtual bool IsNullConstraint()
		{
			return false;
		}

		internal virtual System.Collections.IEnumerator IterateJoins()
		{
			if (i_joins == null)
			{
				return com.db4o.foundation.Iterator4Impl.EMPTY;
			}
			return i_joins.GetEnumerator();
		}

		public virtual System.Collections.IEnumerator IterateChildren()
		{
			if (_children == null)
			{
				return com.db4o.foundation.Iterator4Impl.EMPTY;
			}
			return new com.db4o.foundation.Iterator4Impl(_children);
		}

		internal virtual com.db4o.query.Constraint Join(com.db4o.query.Constraint a_with, 
			bool a_and)
		{
			if (!(a_with is com.db4o.@internal.query.processor.QCon))
			{
				return null;
			}
			if (a_with == this)
			{
				return this;
			}
			return Join1((com.db4o.@internal.query.processor.QCon)a_with, a_and);
		}

		internal virtual com.db4o.query.Constraint Join1(com.db4o.@internal.query.processor.QCon
			 a_with, bool a_and)
		{
			if (a_with is com.db4o.@internal.query.processor.QConstraints)
			{
				int j = 0;
				com.db4o.foundation.Collection4 joinHooks = new com.db4o.foundation.Collection4();
				com.db4o.query.Constraint[] constraints = ((com.db4o.@internal.query.processor.QConstraints
					)a_with).ToArray();
				for (j = 0; j < constraints.Length; j++)
				{
					joinHooks.Ensure(((com.db4o.@internal.query.processor.QCon)constraints[j]).JoinHook
						());
				}
				com.db4o.query.Constraint[] joins = new com.db4o.query.Constraint[joinHooks.Size(
					)];
				j = 0;
				System.Collections.IEnumerator i = joinHooks.GetEnumerator();
				while (i.MoveNext())
				{
					joins[j++] = Join((com.db4o.query.Constraint)i.Current, a_and);
				}
				return new com.db4o.@internal.query.processor.QConstraints(i_trans, joins);
			}
			com.db4o.@internal.query.processor.QCon myHook = JoinHook();
			com.db4o.@internal.query.processor.QCon otherHook = a_with.JoinHook();
			if (myHook == otherHook)
			{
				return myHook;
			}
			com.db4o.@internal.query.processor.QConJoin cj = new com.db4o.@internal.query.processor.QConJoin
				(i_trans, myHook, otherHook, a_and);
			myHook.AddJoin(cj);
			otherHook.AddJoin(cj);
			return cj;
		}

		internal virtual com.db4o.@internal.query.processor.QCon JoinHook()
		{
			return ProduceTopLevelJoin();
		}

		public virtual com.db4o.query.Constraint Like()
		{
			throw NotSupported();
		}

		public virtual com.db4o.query.Constraint StartsWith(bool caseSensitive)
		{
			throw NotSupported();
		}

		public virtual com.db4o.query.Constraint EndsWith(bool caseSensitive)
		{
			throw NotSupported();
		}

		internal virtual void Log(string indent)
		{
		}

		internal virtual string LogObject()
		{
			return string.Empty;
		}

		internal virtual void Marshall()
		{
			System.Collections.IEnumerator i = IterateChildren();
			while (i.MoveNext())
			{
				((com.db4o.@internal.query.processor.QCon)i.Current).Marshall();
			}
		}

		public virtual com.db4o.query.Constraint Not()
		{
			lock (StreamLock())
			{
				if (!(i_evaluator is com.db4o.@internal.query.processor.QENot))
				{
					i_evaluator = new com.db4o.@internal.query.processor.QENot(i_evaluator);
				}
				return this;
			}
		}

		private System.Exception NotSupported()
		{
			return new System.Exception("Not supported.");
		}

		public virtual bool OnSameFieldAs(com.db4o.@internal.query.processor.QCon other)
		{
			return false;
		}

		public virtual com.db4o.query.Constraint Or(com.db4o.query.Constraint orWith)
		{
			lock (StreamLock())
			{
				return Join(orWith, false);
			}
		}

		internal virtual bool Remove()
		{
			if (!i_removed)
			{
				i_removed = true;
				RemoveChildrenJoins();
				return true;
			}
			return false;
		}

		internal virtual void RemoveChildrenJoins()
		{
			if (!HasJoins())
			{
				return;
			}
			System.Collections.IEnumerator i = IterateJoins();
			while (i.MoveNext())
			{
				com.db4o.@internal.query.processor.QConJoin qcj = (com.db4o.@internal.query.processor.QConJoin
					)i.Current;
				if (qcj.RemoveForParent(this))
				{
					i_joins.Remove(qcj);
				}
			}
			CheckLastJoinRemoved();
		}

		internal virtual void RemoveJoin(com.db4o.@internal.query.processor.QConJoin a_join
			)
		{
			i_joins.Remove(a_join);
			CheckLastJoinRemoved();
		}

		internal virtual void RemoveNot()
		{
			if (IsNot())
			{
				i_evaluator = ((com.db4o.@internal.query.processor.QENot)i_evaluator).i_evaluator;
			}
		}

		public virtual void SetCandidates(com.db4o.@internal.query.processor.QCandidates 
			a_candidates)
		{
			i_candidates = a_candidates;
		}

		internal virtual void SetOrdering(int a_ordering)
		{
			i_orderID = a_ordering;
		}

		public virtual int Ordering()
		{
			return i_orderID;
		}

		internal virtual void SetParent(com.db4o.@internal.query.processor.QCon a_newParent
			)
		{
			i_parent = a_newParent;
		}

		internal virtual com.db4o.@internal.query.processor.QCon ShareParent(object a_object
			, bool[] removeExisting)
		{
			return null;
		}

		internal virtual com.db4o.@internal.query.processor.QConClass ShareParentForClass
			(com.db4o.reflect.ReflectClass a_class, bool[] removeExisting)
		{
			return null;
		}

		public virtual com.db4o.query.Constraint Smaller()
		{
			throw NotSupported();
		}

		protected virtual object StreamLock()
		{
			return i_trans.Stream().i_lock;
		}

		internal virtual bool SupportsOrdering()
		{
			return false;
		}

		internal virtual void Unmarshall(com.db4o.@internal.Transaction a_trans)
		{
			if (i_trans != null)
			{
				return;
			}
			i_trans = a_trans;
			UnmarshallParent(a_trans);
			UnmarshallJoins(a_trans);
			UnmarshallChildren(a_trans);
		}

		private void UnmarshallParent(com.db4o.@internal.Transaction a_trans)
		{
			if (i_parent != null)
			{
				i_parent.Unmarshall(a_trans);
			}
		}

		private void UnmarshallChildren(com.db4o.@internal.Transaction a_trans)
		{
			System.Collections.IEnumerator i = IterateChildren();
			while (i.MoveNext())
			{
				((com.db4o.@internal.query.processor.QCon)i.Current).Unmarshall(a_trans);
			}
		}

		private void UnmarshallJoins(com.db4o.@internal.Transaction a_trans)
		{
			if (HasJoins())
			{
				System.Collections.IEnumerator i = IterateJoins();
				while (i.MoveNext())
				{
					((com.db4o.@internal.query.processor.QCon)i.Current).Unmarshall(a_trans);
				}
			}
		}

		public virtual void Visit(object obj)
		{
			com.db4o.@internal.query.processor.QCandidate qc = (com.db4o.@internal.query.processor.QCandidate
				)obj;
			Visit1(qc.GetRoot(), this, Evaluate(qc));
		}

		internal virtual void Visit(com.db4o.@internal.query.processor.QCandidate a_root, 
			bool res)
		{
			Visit1(a_root, this, i_evaluator.Not(res));
		}

		internal virtual void Visit1(com.db4o.@internal.query.processor.QCandidate a_root
			, com.db4o.@internal.query.processor.QCon a_reason, bool res)
		{
			if (HasJoins())
			{
				System.Collections.IEnumerator i = IterateJoins();
				while (i.MoveNext())
				{
					a_root.Evaluate(new com.db4o.@internal.query.processor.QPending((com.db4o.@internal.query.processor.QConJoin
						)i.Current, this, res));
				}
			}
			else
			{
				if (!res)
				{
					DoNotInclude(a_root);
				}
			}
		}

		internal void VisitOnNull(com.db4o.@internal.query.processor.QCandidate a_root)
		{
			System.Collections.IEnumerator i = IterateChildren();
			while (i.MoveNext())
			{
				((com.db4o.@internal.query.processor.QCon)i.Current).VisitOnNull(a_root);
			}
			if (VisitSelfOnNull())
			{
				Visit(a_root, IsNullConstraint());
			}
		}

		internal virtual bool VisitSelfOnNull()
		{
			return true;
		}

		public virtual com.db4o.@internal.query.processor.QE Evaluator()
		{
			return i_evaluator;
		}

		public virtual bool RequiresSort()
		{
			if (HasOrdering())
			{
				return true;
			}
			System.Collections.IEnumerator i = IterateChildren();
			while (i.MoveNext())
			{
				if (((com.db4o.@internal.query.processor.QCon)i.Current).RequiresSort())
				{
					return true;
				}
			}
			return false;
		}

		protected virtual bool HasOrdering()
		{
			return i_orderID != 0;
		}
	}
}
