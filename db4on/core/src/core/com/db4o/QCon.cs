namespace com.db4o
{
	/// <summary>Base class for all constraints on queries.</summary>
	/// <remarks>Base class for all constraints on queries.</remarks>
	/// <exclude></exclude>
	public abstract class QCon : com.db4o.query.Constraint, com.db4o.foundation.Visitor4
		, com.db4o.types.Unversioned
	{
		internal static readonly com.db4o.IDGenerator idGenerator = new com.db4o.IDGenerator
			();

		[com.db4o.Transient]
		internal com.db4o.QCandidates i_candidates;

		public com.db4o.foundation.Collection4 i_childrenCandidates;

		public com.db4o.foundation.List4 _children;

		protected com.db4o.QE i_evaluator = com.db4o.QE.DEFAULT;

		public int i_id;

		public com.db4o.foundation.Collection4 i_joins;

		public int i_orderID = 0;

		public com.db4o.QCon i_parent;

		public bool i_removed = false;

		[com.db4o.Transient]
		internal com.db4o.Transaction i_trans;

		public QCon()
		{
		}

		internal QCon(com.db4o.Transaction a_trans)
		{
			i_id = idGenerator.Next();
			i_trans = a_trans;
		}

		internal virtual com.db4o.QCon AddConstraint(com.db4o.QCon a_child)
		{
			_children = new com.db4o.foundation.List4(_children, a_child);
			return a_child;
		}

		public virtual com.db4o.Transaction Transaction()
		{
			return i_trans;
		}

		internal virtual void AddJoin(com.db4o.QConJoin a_join)
		{
			if (i_joins == null)
			{
				i_joins = new com.db4o.foundation.Collection4();
			}
			i_joins.Add(a_join);
		}

		internal virtual com.db4o.QCon AddSharedConstraint(com.db4o.QField a_field, object
			 a_object)
		{
			com.db4o.QConObject newConstraint = new com.db4o.QConObject(i_trans, this, a_field
				, a_object);
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
			if (i_orderID != 0)
			{
				com.db4o.QCon root = GetRoot();
				root.i_candidates.ApplyOrdering(i_candidates.i_ordered, i_orderID);
			}
		}

		internal virtual bool Attach(com.db4o.QQuery query, string a_field)
		{
			com.db4o.QCon qcon = this;
			com.db4o.YapClass yc = GetYapClass();
			bool[] foundField = { false };
			ForEachChildField(a_field, new _AnonymousInnerClass106(this, foundField, query));
			if (foundField[0])
			{
				return true;
			}
			com.db4o.QField qf = null;
			if (yc == null || yc.HoldsAnyClass())
			{
				int[] count = { 0 };
				com.db4o.YapField[] yfs = { null };
				i_trans.Stream().ClassCollection().AttachQueryNode(a_field, new _AnonymousInnerClass124
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
					qf = new com.db4o.QField(i_trans, a_field, null, 0, 0);
				}
			}
			else
			{
				if (yc.ConfigInstantiates())
				{
					i_trans.Stream().i_handlers._diagnosticProcessor.DescendIntoTranslator(yc, a_field
						);
				}
				if (yc != null)
				{
					com.db4o.YapField yf = yc.GetYapField(a_field);
					if (yf != null)
					{
						qf = yf.QField(i_trans);
					}
				}
				if (qf == null)
				{
					qf = new com.db4o.QField(i_trans, a_field, null, 0, 0);
				}
			}
			com.db4o.QConPath qcp = new com.db4o.QConPath(i_trans, qcon, qf);
			query.AddConstraint(qcp);
			qcon.AddConstraint(qcp);
			return true;
		}

		private sealed class _AnonymousInnerClass106 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass106(QCon _enclosing, bool[] foundField, com.db4o.QQuery
				 query)
			{
				this._enclosing = _enclosing;
				this.foundField = foundField;
				this.query = query;
			}

			public void Visit(object obj)
			{
				foundField[0] = true;
				query.AddConstraint((com.db4o.QCon)obj);
			}

			private readonly QCon _enclosing;

			private readonly bool[] foundField;

			private readonly com.db4o.QQuery query;
		}

		private sealed class _AnonymousInnerClass124 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass124(QCon _enclosing, com.db4o.YapField[] yfs, int[] count
				)
			{
				this._enclosing = _enclosing;
				this.yfs = yfs;
				this.count = count;
			}

			public void Visit(object obj)
			{
				yfs[0] = (com.db4o.YapField)((object[])obj)[1];
				count[0]++;
			}

			private readonly QCon _enclosing;

			private readonly com.db4o.YapField[] yfs;

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

		internal virtual void Collect(com.db4o.QCandidates a_candidates)
		{
		}

		public virtual com.db4o.query.Constraint Contains()
		{
			throw NotSupported();
		}

		internal virtual void CreateCandidates(com.db4o.foundation.Collection4 a_candidateCollection
			)
		{
			com.db4o.foundation.Iterator4 j = a_candidateCollection.Iterator();
			while (j.MoveNext())
			{
				com.db4o.QCandidates candidates = (com.db4o.QCandidates)j.Current();
				if (candidates.TryAddConstraint(this))
				{
					i_candidates = candidates;
					return;
				}
			}
			i_candidates = new com.db4o.QCandidates(i_trans, GetYapClass(), GetField());
			i_candidates.AddConstraint(this);
			a_candidateCollection.Add(i_candidates);
		}

		internal virtual void DoNotInclude(com.db4o.QCandidate a_root)
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

		internal virtual bool Evaluate(com.db4o.QCandidate a_candidate)
		{
			throw com.db4o.inside.Exceptions4.VirtualException();
		}

		internal virtual void EvaluateChildren()
		{
			com.db4o.foundation.Iterator4 i = i_childrenCandidates.Iterator();
			while (i.MoveNext())
			{
				((com.db4o.QCandidates)i.Current()).Evaluate();
			}
		}

		internal virtual void EvaluateCollectChildren()
		{
			com.db4o.foundation.Iterator4 i = i_childrenCandidates.Iterator();
			while (i.MoveNext())
			{
				((com.db4o.QCandidates)i.Current()).Collect(i_candidates);
			}
		}

		internal virtual void EvaluateCreateChildrenCandidates()
		{
			i_childrenCandidates = new com.db4o.foundation.Collection4();
			com.db4o.foundation.Iterator4 i = IterateChildren();
			while (i.MoveNext())
			{
				((com.db4o.QCon)i.Current()).CreateCandidates(i_childrenCandidates);
			}
		}

		internal virtual void EvaluateEvaluations()
		{
			com.db4o.foundation.Iterator4 i = IterateChildren();
			while (i.MoveNext())
			{
				((com.db4o.QCon)i.Current()).EvaluateEvaluationsExec(i_candidates, true);
			}
		}

		internal virtual void EvaluateEvaluationsExec(com.db4o.QCandidates a_candidates, 
			bool rereadObject)
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
			com.db4o.foundation.Iterator4 i = IterateChildren();
			while (i.MoveNext())
			{
				com.db4o.QCon qcon = (com.db4o.QCon)i.Current();
				i_candidates.SetCurrentConstraint(qcon);
				qcon.SetCandidates(i_candidates);
				qcon.EvaluateSimpleExec(i_candidates);
				qcon.ApplyOrdering();
			}
			i_candidates.SetCurrentConstraint(null);
		}

		internal virtual void EvaluateSimpleExec(com.db4o.QCandidates a_candidates)
		{
		}

		internal virtual void ExchangeConstraint(com.db4o.QCon a_exchange, com.db4o.QCon 
			a_with)
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
			com.db4o.foundation.Iterator4 i = IterateChildren();
			while (i.MoveNext())
			{
				object obj = i.Current();
				if (obj is com.db4o.QConObject)
				{
					if (((com.db4o.QConObject)obj).i_field.i_name.Equals(name))
					{
						visitor.Visit(obj);
					}
				}
			}
		}

		public virtual com.db4o.QField GetField()
		{
			return null;
		}

		public virtual object GetObject()
		{
			throw NotSupported();
		}

		internal virtual com.db4o.QCon GetRoot()
		{
			if (i_parent != null)
			{
				return i_parent.GetRoot();
			}
			return this;
		}

		internal virtual com.db4o.QCon ProduceTopLevelJoin()
		{
			if (!HasJoins())
			{
				return this;
			}
			com.db4o.foundation.Iterator4 i = IterateJoins();
			if (i_joins.Size() == 1)
			{
				i.MoveNext();
				return ((com.db4o.QCon)i.Current()).ProduceTopLevelJoin();
			}
			com.db4o.foundation.Collection4 col = new com.db4o.foundation.Collection4();
			while (i.MoveNext())
			{
				col.Ensure(((com.db4o.QCon)i.Current()).ProduceTopLevelJoin());
			}
			i = col.Iterator();
			i.MoveNext();
			com.db4o.QCon qcon = (com.db4o.QCon)i.Current();
			if (col.Size() == 1)
			{
				return qcon;
			}
			while (i.MoveNext())
			{
				qcon = (com.db4o.QCon)qcon.And((com.db4o.query.Constraint)i.Current());
			}
			return qcon;
		}

		internal virtual com.db4o.YapClass GetYapClass()
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

		public virtual com.db4o.QCon Parent()
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
			com.db4o.foundation.Iterator4 i = IterateJoins();
			while (i.MoveNext())
			{
				com.db4o.QConJoin join = (com.db4o.QConJoin)i.Current();
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

		public virtual bool HasOrJoinWith(com.db4o.QConObject y)
		{
			com.db4o.foundation.Iterator4 i = IterateJoins();
			while (i.MoveNext())
			{
				com.db4o.QConJoin join = (com.db4o.QConJoin)i.Current();
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

		internal virtual bool HasObjectInParentPath(object obj)
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
			return i_evaluator is com.db4o.QENot;
		}

		internal virtual bool IsNullConstraint()
		{
			return false;
		}

		internal virtual com.db4o.foundation.Iterator4 IterateJoins()
		{
			if (i_joins == null)
			{
				return com.db4o.foundation.Iterator4Impl.EMPTY;
			}
			return i_joins.Iterator();
		}

		public virtual com.db4o.foundation.Iterator4 IterateChildren()
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
			if (!(a_with is com.db4o.QCon))
			{
				return null;
			}
			if (a_with == this)
			{
				return this;
			}
			return Join1((com.db4o.QCon)a_with, a_and);
		}

		internal virtual com.db4o.query.Constraint Join1(com.db4o.QCon a_with, bool a_and
			)
		{
			if (a_with is com.db4o.QConstraints)
			{
				int j = 0;
				com.db4o.foundation.Collection4 joinHooks = new com.db4o.foundation.Collection4();
				com.db4o.query.Constraint[] constraints = ((com.db4o.QConstraints)a_with).ToArray
					();
				for (j = 0; j < constraints.Length; j++)
				{
					joinHooks.Ensure(((com.db4o.QCon)constraints[j]).JoinHook());
				}
				com.db4o.query.Constraint[] joins = new com.db4o.query.Constraint[joinHooks.Size(
					)];
				j = 0;
				com.db4o.foundation.Iterator4 i = joinHooks.Iterator();
				while (i.MoveNext())
				{
					joins[j++] = Join((com.db4o.query.Constraint)i.Current(), a_and);
				}
				return new com.db4o.QConstraints(i_trans, joins);
			}
			com.db4o.QCon myHook = JoinHook();
			com.db4o.QCon otherHook = a_with.JoinHook();
			if (myHook == otherHook)
			{
				return myHook;
			}
			com.db4o.QConJoin cj = new com.db4o.QConJoin(i_trans, myHook, otherHook, a_and);
			myHook.AddJoin(cj);
			otherHook.AddJoin(cj);
			return cj;
		}

		internal virtual com.db4o.QCon JoinHook()
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
			return "";
		}

		internal virtual void Marshall()
		{
			com.db4o.foundation.Iterator4 i = IterateChildren();
			while (i.MoveNext())
			{
				((com.db4o.QCon)i.Current()).Marshall();
			}
		}

		public virtual com.db4o.query.Constraint Not()
		{
			lock (StreamLock())
			{
				if (!(i_evaluator is com.db4o.QENot))
				{
					i_evaluator = new com.db4o.QENot(i_evaluator);
				}
				return this;
			}
		}

		private j4o.lang.RuntimeException NotSupported()
		{
			return new j4o.lang.RuntimeException("Not supported.");
		}

		public virtual bool OnSameFieldAs(com.db4o.QCon other)
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
			com.db4o.foundation.Iterator4 i = IterateJoins();
			while (i.MoveNext())
			{
				com.db4o.QConJoin qcj = (com.db4o.QConJoin)i.Current();
				if (qcj.RemoveForParent(this))
				{
					i_joins.Remove(qcj);
				}
			}
			CheckLastJoinRemoved();
		}

		internal virtual void RemoveJoin(com.db4o.QConJoin a_join)
		{
			i_joins.Remove(a_join);
			CheckLastJoinRemoved();
		}

		internal virtual void RemoveNot()
		{
			if (IsNot())
			{
				i_evaluator = ((com.db4o.QENot)i_evaluator).i_evaluator;
			}
		}

		public virtual void SetCandidates(com.db4o.QCandidates a_candidates)
		{
			i_candidates = a_candidates;
		}

		internal virtual void SetOrdering(int a_ordering)
		{
			i_orderID = a_ordering;
		}

		internal virtual void SetParent(com.db4o.QCon a_newParent)
		{
			i_parent = a_newParent;
		}

		internal virtual com.db4o.QCon ShareParent(object a_object, bool[] removeExisting
			)
		{
			return null;
		}

		internal virtual com.db4o.QConClass ShareParentForClass(com.db4o.reflect.ReflectClass
			 a_class, bool[] removeExisting)
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

		internal virtual void Unmarshall(com.db4o.Transaction a_trans)
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

		private void UnmarshallParent(com.db4o.Transaction a_trans)
		{
			if (i_parent != null)
			{
				i_parent.Unmarshall(a_trans);
			}
		}

		private void UnmarshallChildren(com.db4o.Transaction a_trans)
		{
			com.db4o.foundation.Iterator4 i = IterateChildren();
			while (i.MoveNext())
			{
				((com.db4o.QCon)i.Current()).Unmarshall(a_trans);
			}
		}

		private void UnmarshallJoins(com.db4o.Transaction a_trans)
		{
			if (HasJoins())
			{
				com.db4o.foundation.Iterator4 i = IterateJoins();
				while (i.MoveNext())
				{
					((com.db4o.QCon)i.Current()).Unmarshall(a_trans);
				}
			}
		}

		public virtual void Visit(object obj)
		{
			com.db4o.QCandidate qc = (com.db4o.QCandidate)obj;
			Visit1(qc.GetRoot(), this, Evaluate(qc));
		}

		internal virtual void Visit(com.db4o.QCandidate a_root, bool res)
		{
			Visit1(a_root, this, i_evaluator.Not(res));
		}

		internal virtual void Visit1(com.db4o.QCandidate a_root, com.db4o.QCon a_reason, 
			bool res)
		{
			if (HasJoins())
			{
				com.db4o.foundation.Iterator4 i = IterateJoins();
				while (i.MoveNext())
				{
					a_root.Evaluate(new com.db4o.QPending((com.db4o.QConJoin)i.Current(), this, res));
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

		internal void VisitOnNull(com.db4o.QCandidate a_root)
		{
			com.db4o.foundation.Iterator4 i = IterateChildren();
			while (i.MoveNext())
			{
				((com.db4o.QCon)i.Current()).VisitOnNull(a_root);
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

		public virtual com.db4o.QE Evaluator()
		{
			return i_evaluator;
		}
	}
}
