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
	/// <summary>Base class for all constraints.</summary>
	/// <remarks>Base class for all constraints.</remarks>
	/// <exclude></exclude>
	public abstract class QCon : com.db4o.query.Constraint, com.db4o.Visitor4
	{
		internal static readonly com.db4o.IDGenerator idGenerator = new com.db4o.IDGenerator
			();

		[com.db4o.Transient]
		internal com.db4o.QCandidates i_candidates;

		internal com.db4o.Collection4 i_childrenCandidates;

		internal com.db4o.List4 i_subConstraints;

		internal com.db4o.QE i_evaluator = com.db4o.QE.DEFAULT;

		internal int i_id;

		internal com.db4o.Collection4 i_joins;

		internal int i_orderID = 0;

		internal com.db4o.QCon i_parent;

		private bool i_removed = false;

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
			i_subConstraints = new com.db4o.List4(i_subConstraints, a_child);
			return a_child;
		}

		internal virtual void addJoin(com.db4o.QConJoin a_join)
		{
			if (i_joins == null)
			{
				i_joins = new com.db4o.Collection4();
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
			forEachChildField(a_field, new _AnonymousInnerClass99(this, foundField, query));
			if (foundField[0])
			{
				return true;
			}
			com.db4o.QField qf = null;
			if (yc == null || yc.holdsAnyClass())
			{
				int[] count = { 0 };
				com.db4o.YapField[] yfs = { null };
				i_trans.i_stream.i_classCollection.yapFields(a_field, new _AnonymousInnerClass117
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

		private sealed class _AnonymousInnerClass99 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass99(QCon _enclosing, bool[] foundField, com.db4o.QQuery
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

		private sealed class _AnonymousInnerClass117 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass117(QCon _enclosing, com.db4o.YapField[] yfs, int[] count
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

		internal virtual int candidateCountByIndex()
		{
			return -1;
		}

		internal virtual int candidateCountByIndex(int depth)
		{
			return -1;
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

		internal virtual void createCandidates(com.db4o.Collection4 a_candidateCollection
			)
		{
			com.db4o.Iterator4 j = a_candidateCollection.iterator();
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
			com.db4o.Iterator4 i = i_childrenCandidates.iterator();
			while (i.hasNext())
			{
				((com.db4o.QCandidates)i.next()).evaluate();
			}
		}

		internal virtual void evaluateCollectChildren()
		{
			com.db4o.Iterator4 i = i_childrenCandidates.iterator();
			while (i.hasNext())
			{
				((com.db4o.QCandidates)i.next()).collect(i_candidates);
			}
		}

		internal virtual void evaluateCreateChildrenCandidates()
		{
			i_childrenCandidates = new com.db4o.Collection4();
			if (i_subConstraints != null)
			{
				com.db4o.Iterator4 i = new com.db4o.Iterator4(i_subConstraints);
				while (i.hasNext())
				{
					((com.db4o.QCon)i.next()).createCandidates(i_childrenCandidates);
				}
			}
		}

		internal virtual void evaluateEvaluations()
		{
			if (i_subConstraints != null)
			{
				com.db4o.Iterator4 i = new com.db4o.Iterator4(i_subConstraints);
				while (i.hasNext())
				{
					((com.db4o.QCon)i.next()).evaluateEvaluationsExec(i_candidates, true);
				}
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
			if (i_subConstraints == null)
			{
				return;
			}
			com.db4o.Iterator4 i = new com.db4o.Iterator4(i_subConstraints);
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
			com.db4o.List4 previous = null;
			com.db4o.List4 current = i_subConstraints;
			while (current != null)
			{
				if (current.i_object == a_exchange)
				{
					if (previous == null)
					{
						i_subConstraints = current.i_next;
					}
					else
					{
						previous.i_next = current.i_next;
					}
				}
				previous = current;
				current = current.i_next;
			}
			i_subConstraints = new com.db4o.List4(i_subConstraints, a_with);
		}

		internal virtual void forEachChildField(string name, com.db4o.Visitor4 visitor)
		{
			if (i_subConstraints == null)
			{
				return;
			}
			com.db4o.Iterator4 i = new com.db4o.Iterator4(i_subConstraints);
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
			if (i_joins == null)
			{
				return this;
			}
			com.db4o.Iterator4 i = i_joins.iterator();
			if (i_joins.size() == 1)
			{
				return ((com.db4o.QCon)i.next()).getTopLevelJoin();
			}
			com.db4o.Collection4 col = new com.db4o.Collection4();
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

		public virtual void identityEvaluation()
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
				com.db4o.Collection4 joinHooks = new com.db4o.Collection4();
				com.db4o.query.Constraint[] constraints = ((com.db4o.QConstraints)a_with).toArray
					();
				for (j = 0; j < constraints.Length; j++)
				{
					joinHooks.ensure(((com.db4o.QCon)constraints[j]).joinHook());
				}
				com.db4o.query.Constraint[] joins = new com.db4o.query.Constraint[joinHooks.size(
					)];
				j = 0;
				com.db4o.Iterator4 i = joinHooks.iterator();
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

		internal virtual com.db4o.Tree loadFromBestChildIndex(com.db4o.QCandidates a_candidates
			)
		{
			return null;
		}

		internal virtual com.db4o.Tree loadFromIndex(com.db4o.QCandidates a_candidates)
		{
			return null;
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
			if (i_subConstraints != null)
			{
				com.db4o.Iterator4 i = new com.db4o.Iterator4(i_subConstraints);
				while (i.hasNext())
				{
					((com.db4o.QCon)i.next()).marshall();
				}
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
			if (i_joins == null)
			{
				return;
			}
			com.db4o.Iterator4 i = i_joins.iterator();
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

		internal virtual void setCandidates(com.db4o.QCandidates a_candidates)
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
			if (i_parent != null)
			{
				i_parent.unmarshall(a_trans);
			}
			if (i_joins != null)
			{
				com.db4o.Iterator4 i = i_joins.iterator();
				while (i.hasNext())
				{
					((com.db4o.QCon)i.next()).unmarshall(a_trans);
				}
			}
			if (i_subConstraints != null)
			{
				com.db4o.Iterator4 i = new com.db4o.Iterator4(i_subConstraints);
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
			if (i_joins != null)
			{
				com.db4o.Iterator4 i = i_joins.iterator();
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
			if (i_subConstraints != null)
			{
				com.db4o.Iterator4 i = new com.db4o.Iterator4(i_subConstraints);
				while (i.hasNext())
				{
					((com.db4o.QCon)i.next()).visitOnNull(a_root);
				}
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
