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
	/// <summary>Object constraint</summary>
	/// <exclude></exclude>
	public class QConObject : com.db4o.QCon
	{
		internal object i_object;

		internal int i_objectID;

		[com.db4o.Transient]
		internal com.db4o.YapClass i_yapClass;

		internal int i_yapClassID;

		internal com.db4o.QField i_field;

		[com.db4o.Transient]
		internal com.db4o.YapComparable i_comparator;

		internal com.db4o.config.ObjectAttribute i_attributeProvider;

		[com.db4o.Transient]
		private bool i_selfComparison = false;

		[com.db4o.Transient]
		private com.db4o.IxTraverser i_indexTraverser;

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
						i_attributeProvider = i_yapClass.i_config.i_queryAttributeProvider;
						i_yapClass = a_trans.i_stream.getYapClass(a_trans.reflector().forObject(i_object)
							, true);
					}
					if (i_yapClass != null)
					{
						i_yapClass.collectConstraints(a_trans, this, i_object, new _AnonymousInnerClass75
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

		private sealed class _AnonymousInnerClass75 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass75(QConObject _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object obj)
			{
				this._enclosing.addConstraint((com.db4o.QCon)obj);
			}

			private readonly QConObject _enclosing;
		}

		internal override int candidateCountByIndex()
		{
			int count = -1;
			if (i_joins == null)
			{
				if (i_subConstraints != null)
				{
					com.db4o.Iterator4 i = new com.db4o.Iterator4(i_subConstraints);
					while (i.hasNext())
					{
						com.db4o.QCon qCon = (com.db4o.QCon)i.next();
						int newCount = qCon.candidateCountByIndex(1);
						if (newCount >= 0)
						{
							if (count == -1 || newCount < count)
							{
								i_indexConstraint = qCon;
								count = newCount;
							}
						}
					}
				}
			}
			return count;
		}

		internal override int candidateCountByIndex(int depth)
		{
			int count = -1;
			if (depth == 1)
			{
				if (i_joins == null && i_field != null && i_field.i_yapField != null && i_field.i_yapField
					.hasIndex() && i_field.i_yapField.canLoadByIndex(this, i_evaluator))
				{
					i_indexTraverser = new com.db4o.IxTraverser();
					count = i_indexTraverser.findBoundsQuery(this, (com.db4o.IxTree)i_field.i_yapField
						.getIndexRoot(i_trans));
				}
			}
			return count;
		}

		internal override void createCandidates(com.db4o.Collection4 a_candidateCollection
			)
		{
			if (i_loadedFromIndex && i_subConstraints == null)
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
				if (i_subConstraints != null)
				{
					com.db4o.Iterator4 i = new com.db4o.Iterator4(i_subConstraints);
					while (i.hasNext())
					{
						if (i.next() is com.db4o.QEvaluation)
						{
							hasEvaluation = true;
							break;
						}
					}
				}
				if (hasEvaluation)
				{
					a_candidates.traverse(i_field);
					com.db4o.Iterator4 i = new com.db4o.Iterator4(i_subConstraints);
					while (i.hasNext())
					{
						((com.db4o.QCon)i.next()).evaluateEvaluationsExec(a_candidates, false);
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
							if (i_evaluator == com.db4o.QE.DEFAULT && (i_joins == null))
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

		public override void identityEvaluation()
		{
			if (i_evaluator.identity())
			{
				int id = getObjectID();
				if (id != 0)
				{
					i_candidates.addByIdentity(new com.db4o.QCandidate(i_candidates, null, id, !(i_evaluator
						 is com.db4o.QENot)));
				}
			}
		}

		internal override bool isNullConstraint()
		{
			return i_object == null;
		}

		internal override com.db4o.Tree loadFromBestChildIndex(com.db4o.QCandidates a_candidates
			)
		{
			return i_indexConstraint.loadFromIndex(a_candidates);
		}

		internal override com.db4o.Tree loadFromIndex(com.db4o.QCandidates a_candidates)
		{
			i_loadedFromIndex = true;
			return i_indexTraverser.getMatches(a_candidates);
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
			i_subConstraints = null;
		}

		internal override com.db4o.QCon shareParent(object a_object, bool[] removeExisting
			)
		{
			if (i_parent != null)
			{
				if (i_field.canHold(a_object))
				{
					return i_parent.addSharedConstraint(i_field, a_object);
				}
			}
			return null;
		}

		internal override com.db4o.QConClass shareParentForClass(com.db4o.reflect.ReflectClass
			 a_class, bool[] removeExisting)
		{
			if (i_parent != null)
			{
				if (i_field.canHold(a_class))
				{
					com.db4o.QConClass newConstraint = new com.db4o.QConClass(i_trans, i_parent, i_field
						, a_class);
					i_parent.addConstraint(newConstraint);
					return newConstraint;
				}
			}
			return null;
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
				i_evaluator = i_evaluator.add(new com.db4o.QEContains());
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
					com.db4o.Db4o.throwRuntimeException(51);
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
				i_evaluator = i_evaluator.add(new com.db4o.QELike());
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

		public override string ToString()
		{
			return base.ToString();
		}
	}
}
