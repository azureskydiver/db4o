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
	/// <summary>Holds the tree of QCandidate objects and the list of QContraints during query evaluation.
	/// 	</summary>
	/// <remarks>
	/// Holds the tree of QCandidate objects and the list of QContraints during query evaluation.
	/// The query work (adding and removing nodes) happens here.
	/// Candidates during query evaluation. QCandidate objects are stored in i_root
	/// </remarks>
	/// <exclude></exclude>
	public sealed class QCandidates : com.db4o.Visitor4
	{
		internal readonly com.db4o.Transaction i_trans;

		private com.db4o.Tree i_root;

		internal com.db4o.List4 i_constraints;

		internal com.db4o.YapClass i_yapClass;

		private com.db4o.QField i_field;

		internal com.db4o.QCon i_currentConstraint;

		internal com.db4o.Tree i_ordered;

		private int i_orderID;

		internal QCandidates(com.db4o.Transaction a_trans, com.db4o.YapClass a_yapClass, 
			com.db4o.QField a_field)
		{
			i_trans = a_trans;
			i_yapClass = a_yapClass;
			i_field = a_field;
			if (a_field == null || a_field.i_yapField == null || !(a_field.i_yapField.getHandler
				() is com.db4o.YapClass))
			{
				return;
			}
			com.db4o.YapClass yc = (com.db4o.YapClass)a_field.i_yapField.getHandler();
			if (i_yapClass == null)
			{
				i_yapClass = yc;
			}
			else
			{
				yc = i_yapClass.getHigherOrCommonHierarchy(yc);
				if (yc != null)
				{
					i_yapClass = yc;
				}
			}
		}

		internal com.db4o.QCandidate addByIdentity(com.db4o.QCandidate candidate)
		{
			i_root = com.db4o.Tree.add(i_root, candidate);
			if (candidate.i_size == 0)
			{
				return candidate.getRoot();
			}
			return candidate;
		}

		internal void addConstraint(com.db4o.QCon a_constraint)
		{
			i_constraints = new com.db4o.List4(i_constraints, a_constraint);
		}

		internal void addOrder(com.db4o.QOrder a_order)
		{
			i_ordered = com.db4o.Tree.add(i_ordered, a_order);
		}

		internal void applyOrdering(com.db4o.Tree a_ordered, int a_orderID)
		{
			if (a_ordered == null || i_root == null)
			{
				return;
			}
			if (a_orderID > 0)
			{
				a_orderID = -a_orderID;
			}
			bool major = (a_orderID - i_orderID) < 0;
			if (major)
			{
				i_orderID = a_orderID;
			}
			int[] placement = { 0 };
			i_root.traverse(new _AnonymousInnerClass101(this, major, placement));
			placement[0] = 1;
			a_ordered.traverse(new _AnonymousInnerClass110(this, placement, major));
			com.db4o.Collection4 col = new com.db4o.Collection4();
			i_root.traverse(new _AnonymousInnerClass121(this, col));
			com.db4o.Tree[] newTree = { null };
			com.db4o.Iterator4 i = col.iterator();
			while (i.hasNext())
			{
				com.db4o.QCandidate candidate = (com.db4o.QCandidate)i.next();
				candidate.i_preceding = null;
				candidate.i_subsequent = null;
				candidate.i_size = 1;
				newTree[0] = com.db4o.Tree.add(newTree[0], candidate);
			}
			i_root = newTree[0];
		}

		private sealed class _AnonymousInnerClass101 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass101(QCandidates _enclosing, bool major, int[] placement
				)
			{
				this._enclosing = _enclosing;
				this.major = major;
				this.placement = placement;
			}

			public void visit(object a_object)
			{
				((com.db4o.QCandidate)a_object).hintOrder(0, major);
				((com.db4o.QCandidate)a_object).hintOrder(placement[0]++, !major);
			}

			private readonly QCandidates _enclosing;

			private readonly bool major;

			private readonly int[] placement;
		}

		private sealed class _AnonymousInnerClass110 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass110(QCandidates _enclosing, int[] placement, bool major
				)
			{
				this._enclosing = _enclosing;
				this.placement = placement;
				this.major = major;
			}

			public void visit(object a_object)
			{
				com.db4o.QOrder qo = (com.db4o.QOrder)a_object;
				com.db4o.QCandidate candidate = qo.i_candidate.getRoot();
				candidate.hintOrder(placement[0]++, major);
			}

			private readonly QCandidates _enclosing;

			private readonly int[] placement;

			private readonly bool major;
		}

		private sealed class _AnonymousInnerClass121 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass121(QCandidates _enclosing, com.db4o.Collection4 col)
			{
				this._enclosing = _enclosing;
				this.col = col;
			}

			public void visit(object a_object)
			{
				com.db4o.QCandidate candidate = (com.db4o.QCandidate)a_object;
				col.add(candidate);
			}

			private readonly QCandidates _enclosing;

			private readonly com.db4o.Collection4 col;
		}

		internal void collect(com.db4o.QCandidates a_candidates)
		{
			if (i_constraints != null)
			{
				com.db4o.Iterator4 i = new com.db4o.Iterator4(i_constraints);
				while (i.hasNext())
				{
					com.db4o.QCon qCon = (com.db4o.QCon)i.next();
					setCurrentConstraint(qCon);
					qCon.collect(a_candidates);
				}
			}
			setCurrentConstraint(null);
		}

		internal void execute()
		{
			com.db4o.QCon bestIndex = null;
			if (i_constraints != null)
			{
				int bestIndexHit = int.MaxValue;
				com.db4o.Iterator4 i = new com.db4o.Iterator4(i_constraints);
				while (i.hasNext())
				{
					com.db4o.QCon qCon = (com.db4o.QCon)i.next();
					qCon.setCandidates(this);
					qCon.identityEvaluation();
					int indexHit = qCon.candidateCountByIndex();
					if (indexHit >= 0 && indexHit < bestIndexHit)
					{
						bestIndex = qCon;
					}
				}
				if (bestIndex != null)
				{
					i_root = bestIndex.loadFromBestChildIndex(this);
				}
			}
			if (bestIndex == null)
			{
				loadFromClassIndex();
			}
			evaluate();
		}

		internal void evaluate()
		{
			if (i_constraints == null)
			{
				return;
			}
			com.db4o.Iterator4 i = new com.db4o.Iterator4(i_constraints);
			while (i.hasNext())
			{
				((com.db4o.QCon)i.next()).evaluateSelf();
			}
			i = new com.db4o.Iterator4(i_constraints);
			while (i.hasNext())
			{
				((com.db4o.QCon)i.next()).evaluateSimpleChildren();
			}
			i = new com.db4o.Iterator4(i_constraints);
			while (i.hasNext())
			{
				((com.db4o.QCon)i.next()).evaluateEvaluations();
			}
			i = new com.db4o.Iterator4(i_constraints);
			while (i.hasNext())
			{
				((com.db4o.QCon)i.next()).evaluateCreateChildrenCandidates();
			}
			i = new com.db4o.Iterator4(i_constraints);
			while (i.hasNext())
			{
				((com.db4o.QCon)i.next()).evaluateCollectChildren();
			}
			i = new com.db4o.Iterator4(i_constraints);
			while (i.hasNext())
			{
				((com.db4o.QCon)i.next()).evaluateChildren();
			}
		}

		internal bool isEmpty()
		{
			bool[] ret = new bool[] { true };
			traverse(new _AnonymousInnerClass218(this, ret));
			return ret[0];
		}

		private sealed class _AnonymousInnerClass218 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass218(QCandidates _enclosing, bool[] ret)
			{
				this._enclosing = _enclosing;
				this.ret = ret;
			}

			public void visit(object obj)
			{
				if (((com.db4o.QCandidate)obj).i_include)
				{
					ret[0] = false;
				}
			}

			private readonly QCandidates _enclosing;

			private readonly bool[] ret;
		}

		internal bool filter(com.db4o.Visitor4 a_host)
		{
			if (i_root != null)
			{
				i_root.traverse(a_host);
				i_root = i_root.filter(new _AnonymousInnerClass231(this));
			}
			return i_root != null;
		}

		private sealed class _AnonymousInnerClass231 : com.db4o.VisitorBoolean
		{
			public _AnonymousInnerClass231(QCandidates _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public bool isVisit(object a_candidate)
			{
				return ((com.db4o.QCandidate)a_candidate).i_include;
			}

			private readonly QCandidates _enclosing;
		}

		internal void loadFromClassIndex()
		{
			if (!isEmpty())
			{
				return;
			}
			com.db4o.QCandidates finalThis = this;
			if (i_yapClass.getIndex() != null)
			{
				com.db4o.Tree[] newRoot = { com.db4o.TreeInt.toQCandidate(i_yapClass.getIndexRoot
					(), this) };
				i_trans.traverseAddedClassIDs(i_yapClass.getID(), new _AnonymousInnerClass251(this
					, newRoot, finalThis));
				i_trans.traverseRemovedClassIDs(i_yapClass.getID(), new _AnonymousInnerClass262(this
					, newRoot, finalThis));
				i_root = newRoot[0];
			}
		}

		private sealed class _AnonymousInnerClass251 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass251(QCandidates _enclosing, com.db4o.Tree[] newRoot, com.db4o.QCandidates
				 finalThis)
			{
				this._enclosing = _enclosing;
				this.newRoot = newRoot;
				this.finalThis = finalThis;
			}

			public void visit(object obj)
			{
				newRoot[0] = com.db4o.Tree.add(newRoot[0], new com.db4o.QCandidate(finalThis, ((com.db4o.TreeInt
					)obj).i_key, true));
			}

			private readonly QCandidates _enclosing;

			private readonly com.db4o.Tree[] newRoot;

			private readonly com.db4o.QCandidates finalThis;
		}

		private sealed class _AnonymousInnerClass262 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass262(QCandidates _enclosing, com.db4o.Tree[] newRoot, com.db4o.QCandidates
				 finalThis)
			{
				this._enclosing = _enclosing;
				this.newRoot = newRoot;
				this.finalThis = finalThis;
			}

			public void visit(object obj)
			{
				newRoot[0] = com.db4o.Tree.removeLike(newRoot[0], new com.db4o.QCandidate(finalThis
					, ((com.db4o.TreeInt)obj).i_key, true));
			}

			private readonly QCandidates _enclosing;

			private readonly com.db4o.Tree[] newRoot;

			private readonly com.db4o.QCandidates finalThis;
		}

		internal void setCurrentConstraint(com.db4o.QCon a_constraint)
		{
			i_currentConstraint = a_constraint;
		}

		internal void traverse(com.db4o.Visitor4 a_visitor)
		{
			if (i_root != null)
			{
				i_root.traverse(a_visitor);
			}
		}

		internal bool tryAddConstraint(com.db4o.QCon a_constraint)
		{
			if (i_field != null)
			{
				com.db4o.QField qf = a_constraint.getField();
				if (qf != null)
				{
					if (i_field.i_name != qf.i_name)
					{
						return false;
					}
				}
			}
			if (i_yapClass == null || a_constraint.isNullConstraint())
			{
				addConstraint(a_constraint);
				return true;
			}
			com.db4o.YapClass yc = a_constraint.getYapClass();
			if (yc != null)
			{
				yc = i_yapClass.getHigherOrCommonHierarchy(yc);
				if (yc != null)
				{
					i_yapClass = yc;
					addConstraint(a_constraint);
					return true;
				}
			}
			return false;
		}

		public void visit(object a_tree)
		{
			com.db4o.QCandidate parent = (com.db4o.QCandidate)a_tree;
			if (parent.createChild(this))
			{
				return;
			}
			if (i_constraints != null)
			{
				com.db4o.Iterator4 i = new com.db4o.Iterator4(i_constraints);
				while (i.hasNext())
				{
					((com.db4o.QCon)i.next()).visitOnNull(parent.getRoot());
				}
			}
		}
	}
}
