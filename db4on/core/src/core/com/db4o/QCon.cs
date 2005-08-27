
namespace com.db4o
{
	/// <summary>Base class for all constraints on queries.</summary>
	/// <remarks>Base class for all constraints on queries.</remarks>
	/// <exclude></exclude>
	public abstract class QCon : com.db4o.query.Constraint, com.db4o.foundation.Visitor4
	{
		internal static readonly com.db4o.IDGenerator idGenerator = new com.db4o.IDGenerator
			();

		[com.db4o.Transient]
		internal com.db4o.QCandidates i_candidates;

		public com.db4o.foundation.Collection4 i_childrenCandidates;

		public com.db4o.foundation.List4 _children;

		public com.db4o.QE i_evaluator = com.db4o.QE.DEFAULT;

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
			i_id = idGenerator.next();
			i_trans = a_trans;
		}

		internal virtual com.db4o.QCon addConstraint(com.db4o.QCon a_child)
		{
			_children = new com.db4o.foundation.List4(_children, a_child);
			return a_child;
		}

		internal virtual void addJoin(com.db4o.QConJoin a_join)
		{
			if (i_joins == null)
			{
				i_joins = new com.db4o.foundation.Collection4();
			}
			i_joins.add(a_join);
		}

		internal virtual com.db4o.QCon addSharedConstraint(com.db4o.QField a_field, object
			 a_object)
		{
			com.db4o.QConObject newConstraint = new com.db4o.QConObject(i_trans, this, a_field
				, a_object);
			addConstraint(newConstraint);
			return newConstraint;
		}

		public virtual com.db4o.query.Constraint and(com.db4o.query.Constraint andWith)
		{
			lock (streamLock())
			{
				return join(andWith, true);
			}
		}

		internal virtual void applyOrdering()
		{
			if (i_orderID != 0)
			{
				com.db4o.QCon root = getRoot();
				root.i_candidates.applyOrdering(i_candidates.i_ordered, i_orderID);
			}
		}

		internal virtual bool attach(com.db4o.QQuery query, string a_field)
		{
			com.db4o.QCon qcon = this;
			com.db4o.YapClass yc = getYapClass();
			bool[] foundField = { false };
			forEachChildField(a_field, new _AnonymousInnerClass100(this, foundField, query));
			if (foundField[0])
			{
				return true;
			}
			com.db4o.QField qf = null;
			if (yc == null || yc.holdsAnyClass())
			{
				int[] count = { 0 };
				com.db4o.YapField[] yfs = { null };
				i_trans.i_stream.i_classCollection.yapFields(a_field, new _AnonymousInnerClass118
					(this, yfs, count));
				if (count[0] == 0)
				{
					return false;
				}
				if (count[0] == 1)
				{
					qf = yfs[0].qField(i_trans);
				}
				else
				{
					qf = new com.db4o.QField(i_trans, a_field, null, 0, 0);
				}
			}
			else
			{
				if (yc != null)
				{
					com.db4o.YapField yf = yc.getYapField(a_field);
					if (yf != null)
					{
						qf = yf.qField(i_trans);
					}
				}
				if (qf == null)
				{
					qf = new com.db4o.QField(i_trans, a_field, null, 0, 0);
				}
			}
			com.db4o.QConPath qcp = new com.db4o.QConPath(i_trans, qcon, qf);
			query.addConstraint(qcp);
			qcon.addConstraint(qcp);
			return true;
		}

		private sealed class _AnonymousInnerClass100 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass100(QCon _enclosing, bool[] foundField, com.db4o.QQuery
				 query)
			{
				this._enclosing = _enclosing;
				this.foundField = foundField;
				this.query = query;
			}

			public void visit(object obj)
			{
				foundField[0] = true;
				query.addConstraint((com.db4o.QCon)obj);
			}

			private readonly QCon _enclosing;

			private readonly bool[] foundField;

			private readonly com.db4o.QQuery query;
		}

		private sealed class _AnonymousInnerClass118 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass118(QCon _enclosing, com.db4o.YapField[] yfs, int[] count
				)
			{
				this._enclosing = _enclosing;
				this.yfs = yfs;
				this.count = count;
			}

			public void visit(object obj)
			{
				yfs[0] = (com.db4o.YapField)((object[])obj)[1];
				count[0]++;
			}

			private readonly QCon _enclosing;

			private readonly com.db4o.YapField[] yfs;

			private readonly int[] count;
		}

		public virtual bool canBeIndexLeaf()
		{
			return false;
		}

		public virtual bool canLoadByIndex()
		{
			return false;
		}

		internal virtual void checkLastJoinRemoved()
		{
			if (i_joins.size() == 0)
			{
				i_joins = null;
			}
		}

		internal virtual void collect(com.db4o.QCandidates a_candidates)
		{
		}

		public virtual com.db4o.query.Constraint contains()
		{
			throw notSupported();
		}

		internal virtual void createCandidates(com.db4o.foundation.Collection4 a_candidateCollection
			)
		{
			com.db4o.foundation.Iterator4 j = a_candidateCollection.iterator();
			while (j.hasNext())
			{
				com.db4o.QCandidates candidates = (com.db4o.QCandidates)j.next();
				if (candidates.tryAddConstraint(this))
				{
					i_candidates = candidates;
					return;
				}
			}
			i_candidates = new com.db4o.QCandidates(i_trans, getYapClass(), getField());
			i_candidates.addConstraint(this);
			a_candidateCollection.add(i_candidates);
		}

		internal virtual void doNotInclude(com.db4o.QCandidate a_root)
		{
			if (i_parent != null)
			{
				i_parent.visit1(a_root, this, false);
			}
			else
			{
				a_root.doNotInclude();
			}
		}

		public virtual com.db4o.query.Constraint equal()
		{
			throw notSupported();
		}

		internal virtual bool evaluate(com.db4o.QCandidate a_candidate)
		{
			throw com.db4o.YapConst.virtualException();
		}

		internal virtual void evaluateChildren()
		{
			com.db4o.foundation.Iterator4 i = i_childrenCandidates.iterator();
			while (i.hasNext())
			{
				((com.db4o.QCandidates)i.next()).evaluate();
			}
		}

		internal virtual void evaluateCollectChildren()
		{
			com.db4o.foundation.Iterator4 i = i_childrenCandidates.iterator();
			while (i.hasNext())
			{
				((com.db4o.QCandidates)i.next()).collect(i_candidates);
			}
		}

		internal virtual void evaluateCreateChildrenCandidates()
		{
			i_childrenCandidates = new com.db4o.foundation.Collection4();
			com.db4o.foundation.Iterator4 i = iterateChildren();
			while (i.hasNext())
			{
				((com.db4o.QCon)i.next()).createCandidates(i_childrenCandidates);
			}
		}

		internal virtual void evaluateEvaluations()
		{
			com.db4o.foundation.Iterator4 i = iterateChildren();
			while (i.hasNext())
			{
				((com.db4o.QCon)i.next()).evaluateEvaluationsExec(i_candidates, true);
			}
		}

		internal virtual void evaluateEvaluationsExec(com.db4o.QCandidates a_candidates, 
			bool rereadObject)
		{
		}

		internal virtual void evaluateSelf()
		{
			i_candidates.filter(this);
		}

		internal virtual void evaluateSimpleChildren()
		{
			if (_children == null)
			{
				return;
			}
			com.db4o.foundation.Iterator4 i = iterateChildren();
			while (i.hasNext())
			{
				com.db4o.QCon qcon = (com.db4o.QCon)i.next();
				i_candidates.setCurrentConstraint(qcon);
				qcon.setCandidates(i_candidates);
				qcon.evaluateSimpleExec(i_candidates);
				qcon.applyOrdering();
			}
			i_candidates.setCurrentConstraint(null);
		}

		internal virtual void evaluateSimpleExec(com.db4o.QCandidates a_candidates)
		{
		}

		internal virtual void exchangeConstraint(com.db4o.QCon a_exchange, com.db4o.QCon 
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

		internal virtual void forEachChildField(string name, com.db4o.foundation.Visitor4
			 visitor)
		{
			com.db4o.foundation.Iterator4 i = iterateChildren();
			while (i.hasNext())
			{
				object obj = i.next();
				if (obj is com.db4o.QConObject)
				{
					if (((com.db4o.QConObject)obj).i_field.i_name.Equals(name))
					{
						visitor.visit(obj);
					}
				}
			}
		}

		internal virtual com.db4o.QField getField()
		{
			return null;
		}

		public virtual object getObject()
		{
			throw notSupported();
		}

		internal virtual com.db4o.QCon getRoot()
		{
			if (i_parent != null)
			{
				return i_parent.getRoot();
			}
			return this;
		}

		internal virtual com.db4o.QCon getTopLevelJoin()
		{
			if (!hasJoins())
			{
				return this;
			}
			com.db4o.foundation.Iterator4 i = iterateJoins();
			if (i_joins.size() == 1)
			{
				return ((com.db4o.QCon)i.next()).getTopLevelJoin();
			}
			com.db4o.foundation.Collection4 col = new com.db4o.foundation.Collection4();
			while (i.hasNext())
			{
				col.ensure(((com.db4o.QCon)i.next()).getTopLevelJoin());
			}
			i = col.iterator();
			com.db4o.QCon qcon = (com.db4o.QCon)i.next();
			if (col.size() == 1)
			{
				return qcon;
			}
			while (i.hasNext())
			{
				qcon = (com.db4o.QCon)qcon.and((com.db4o.query.Constraint)i.next());
			}
			return qcon;
		}

		internal virtual com.db4o.YapClass getYapClass()
		{
			return null;
		}

		public virtual com.db4o.query.Constraint greater()
		{
			throw notSupported();
		}

		public virtual bool hasChildren()
		{
			return _children != null;
		}

		public virtual bool hasJoins()
		{
			if (i_joins == null)
			{
				return false;
			}
			return i_joins.size() > 0;
		}

		internal virtual bool hasObjectInParentPath(object obj)
		{
			if (i_parent != null)
			{
				return i_parent.hasObjectInParentPath(obj);
			}
			return false;
		}

		public virtual com.db4o.query.Constraint identity()
		{
			throw notSupported();
		}

		public virtual int identityID()
		{
			return 0;
		}

		public virtual com.db4o.IxTree indexRoot()
		{
			throw com.db4o.YapConst.virtualException();
		}

		internal virtual bool isNot()
		{
			return i_evaluator is com.db4o.QENot;
		}

		internal virtual bool isNullConstraint()
		{
			return false;
		}

		internal virtual com.db4o.foundation.Iterator4 iterateJoins()
		{
			if (i_joins == null)
			{
				return com.db4o.foundation.Iterator4.EMPTY;
			}
			return i_joins.iterator();
		}

		public virtual com.db4o.foundation.Iterator4 iterateChildren()
		{
			if (_children == null)
			{
				return com.db4o.foundation.Iterator4.EMPTY;
			}
			return new com.db4o.foundation.Iterator4(_children);
		}

		internal virtual com.db4o.query.Constraint join(com.db4o.query.Constraint a_with, 
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
			return join1((com.db4o.QCon)a_with, a_and);
		}

		internal virtual com.db4o.query.Constraint join1(com.db4o.QCon a_with, bool a_and
			)
		{
			if (a_with is com.db4o.QConstraints)
			{
				int j = 0;
				com.db4o.foundation.Collection4 joinHooks = new com.db4o.foundation.Collection4();
				com.db4o.query.Constraint[] constraints = ((com.db4o.QConstraints)a_with).toArray
					();
				for (j = 0; j < constraints.Length; j++)
				{
					joinHooks.ensure(((com.db4o.QCon)constraints[j]).joinHook());
				}
				com.db4o.query.Constraint[] joins = new com.db4o.query.Constraint[joinHooks.size(
					)];
				j = 0;
				com.db4o.foundation.Iterator4 i = joinHooks.iterator();
				while (i.hasNext())
				{
					joins[j++] = join((com.db4o.query.Constraint)i.next(), a_and);
				}
				return new com.db4o.QConstraints(i_trans, joins);
			}
			com.db4o.QCon myHook = joinHook();
			com.db4o.QCon otherHook = a_with.joinHook();
			if (myHook == otherHook)
			{
				return myHook;
			}
			com.db4o.QConJoin cj = new com.db4o.QConJoin(i_trans, myHook, otherHook, a_and);
			myHook.addJoin(cj);
			otherHook.addJoin(cj);
			return cj;
		}

		internal virtual com.db4o.QCon joinHook()
		{
			return getTopLevelJoin();
		}

		public virtual com.db4o.query.Constraint like()
		{
			throw notSupported();
		}

		internal virtual void log(string indent)
		{
		}

		internal virtual string logObject()
		{
			return "";
		}

		internal virtual void marshall()
		{
			com.db4o.foundation.Iterator4 i = iterateChildren();
			while (i.hasNext())
			{
				((com.db4o.QCon)i.next()).marshall();
			}
		}

		public virtual com.db4o.query.Constraint not()
		{
			lock (streamLock())
			{
				if (!(i_evaluator is com.db4o.QENot))
				{
					i_evaluator = new com.db4o.QENot(i_evaluator);
				}
				return this;
			}
		}

		private j4o.lang.RuntimeException notSupported()
		{
			return new j4o.lang.RuntimeException("Not supported.");
		}

		public virtual com.db4o.query.Constraint or(com.db4o.query.Constraint orWith)
		{
			lock (streamLock())
			{
				return join(orWith, false);
			}
		}

		internal virtual bool remove()
		{
			if (!i_removed)
			{
				i_removed = true;
				removeChildrenJoins();
				return true;
			}
			return false;
		}

		internal virtual void removeChildrenJoins()
		{
			if (!hasJoins())
			{
				return;
			}
			com.db4o.foundation.Iterator4 i = iterateJoins();
			while (i.hasNext())
			{
				com.db4o.QConJoin qcj = (com.db4o.QConJoin)i.next();
				if (qcj.removeForParent(this))
				{
					i_joins.remove(qcj);
				}
			}
			checkLastJoinRemoved();
		}

		internal virtual void removeJoin(com.db4o.QConJoin a_join)
		{
			i_joins.remove(a_join);
			checkLastJoinRemoved();
		}

		internal virtual void removeNot()
		{
			if (isNot())
			{
				i_evaluator = ((com.db4o.QENot)i_evaluator).i_evaluator;
			}
		}

		public virtual void setCandidates(com.db4o.QCandidates a_candidates)
		{
			i_candidates = a_candidates;
		}

		internal virtual void setOrdering(int a_ordering)
		{
			i_orderID = a_ordering;
		}

		internal virtual void setParent(com.db4o.QCon a_newParent)
		{
			i_parent = a_newParent;
		}

		internal virtual com.db4o.QCon shareParent(object a_object, bool[] removeExisting
			)
		{
			return null;
		}

		internal virtual com.db4o.QConClass shareParentForClass(com.db4o.reflect.ReflectClass
			 a_class, bool[] removeExisting)
		{
			return null;
		}

		public virtual com.db4o.query.Constraint smaller()
		{
			throw notSupported();
		}

		protected virtual object streamLock()
		{
			return i_trans.i_stream.i_lock;
		}

		internal virtual bool supportsOrdering()
		{
			return false;
		}

		internal virtual void unmarshall(com.db4o.Transaction a_trans)
		{
			if (i_trans != null)
			{
				return;
			}
			i_trans = a_trans;
			unmarshallParent(a_trans);
			unmarshallJoins(a_trans);
			unmarshallChildren(a_trans);
		}

		private void unmarshallParent(com.db4o.Transaction a_trans)
		{
			if (i_parent != null)
			{
				i_parent.unmarshall(a_trans);
			}
		}

		private void unmarshallChildren(com.db4o.Transaction a_trans)
		{
			com.db4o.foundation.Iterator4 i = iterateChildren();
			while (i.hasNext())
			{
				((com.db4o.QCon)i.next()).unmarshall(a_trans);
			}
		}

		private void unmarshallJoins(com.db4o.Transaction a_trans)
		{
			if (hasJoins())
			{
				com.db4o.foundation.Iterator4 i = iterateJoins();
				while (i.hasNext())
				{
					((com.db4o.QCon)i.next()).unmarshall(a_trans);
				}
			}
		}

		public virtual void visit(object obj)
		{
			com.db4o.QCandidate qc = (com.db4o.QCandidate)obj;
			visit1(qc.getRoot(), this, evaluate(qc));
		}

		internal virtual void visit(com.db4o.QCandidate a_root, bool res)
		{
			visit1(a_root, this, i_evaluator.not(res));
		}

		internal virtual void visit1(com.db4o.QCandidate a_root, com.db4o.QCon a_reason, 
			bool res)
		{
			if (hasJoins())
			{
				com.db4o.foundation.Iterator4 i = iterateJoins();
				while (i.hasNext())
				{
					a_root.evaluate(new com.db4o.QPending((com.db4o.QConJoin)i.next(), this, res));
				}
			}
			else
			{
				if (!res)
				{
					doNotInclude(a_root);
				}
			}
		}

		internal void visitOnNull(com.db4o.QCandidate a_root)
		{
			com.db4o.foundation.Iterator4 i = iterateChildren();
			while (i.hasNext())
			{
				((com.db4o.QCon)i.next()).visitOnNull(a_root);
			}
			if (visitSelfOnNull())
			{
				visit(a_root, isNullConstraint());
			}
		}

		internal virtual bool visitSelfOnNull()
		{
			return true;
		}
	}
}
