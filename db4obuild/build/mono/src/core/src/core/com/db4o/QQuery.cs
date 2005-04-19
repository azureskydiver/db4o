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
	/// <summary>QQuery is the users hook on our graph.</summary>
	/// <remarks>
	/// QQuery is the users hook on our graph.
	/// A QQuery is defined by it's constraints.
	/// </remarks>
	/// <exclude></exclude>
	public class QQuery : com.db4o.query.Query
	{
		[com.db4o.Transient]
		private static readonly com.db4o.IDGenerator i_orderingGenerator = new com.db4o.IDGenerator
			();

		[com.db4o.Transient]
		internal com.db4o.Transaction i_trans;

		private com.db4o.Collection4 i_constraints = new com.db4o.Collection4();

		private com.db4o.QQuery i_parent;

		private string i_field;

		public QQuery()
		{
		}

		internal QQuery(com.db4o.Transaction a_trans, com.db4o.QQuery a_parent, string a_field
			)
		{
			i_trans = a_trans;
			i_parent = a_parent;
			i_field = a_field;
		}

		internal virtual void addConstraint(com.db4o.QCon a_constraint)
		{
			i_constraints.add(a_constraint);
		}

		private void addConstraint(com.db4o.Collection4 col, object obj)
		{
			bool found = false;
			com.db4o.Iterator4 j = i_constraints.iterator();
			while (j.hasNext())
			{
				com.db4o.QCon existingConstraint = (com.db4o.QCon)j.next();
				bool[] removeExisting = { false };
				com.db4o.QCon newConstraint = existingConstraint.shareParent(obj, removeExisting);
				if (newConstraint != null)
				{
					addConstraint(newConstraint);
					col.add(newConstraint);
					if (removeExisting[0])
					{
						removeConstraint(existingConstraint);
					}
					found = true;
				}
			}
			if (!found)
			{
				com.db4o.QConObject newConstraint = new com.db4o.QConObject(i_trans, null, null, 
					obj);
				addConstraint(newConstraint);
				col.add(newConstraint);
			}
		}

		/// <summary>Search for slot that corresponds to class.</summary>
		/// <remarks>
		/// Search for slot that corresponds to class. <br />If not found add it.
		/// <br />Constrain it. <br />
		/// </remarks>
		public virtual com.db4o.query.Constraint constrain(object example)
		{
			lock (streamLock())
			{
				com.db4o.reflect.ReflectClass claxx = null;
				example = com.db4o.Platform.getClassForType(example);
				com.db4o.reflect.Reflector reflector = i_trans.reflector();
				if (example is com.db4o.reflect.ReflectClass)
				{
					claxx = (com.db4o.reflect.ReflectClass)example;
				}
				else
				{
					if (example is j4o.lang.Class)
					{
						claxx = reflector.forClass((j4o.lang.Class)example);
					}
				}
				if (claxx != null)
				{
					com.db4o.Collection4 col = new com.db4o.Collection4();
					if (claxx.isInterface())
					{
						com.db4o.Collection4 classes = i_trans.i_stream.i_classCollection.forInterface(claxx
							);
						if (classes.size() == 0)
						{
							return null;
						}
						com.db4o.Iterator4 i = classes.iterator();
						com.db4o.query.Constraint constr = null;
						while (i.hasNext())
						{
							com.db4o.YapClass yapClass = (com.db4o.YapClass)i.next();
							com.db4o.reflect.ReflectClass yapClassClaxx = yapClass.classReflector();
							if (yapClassClaxx != null)
							{
								if (!yapClassClaxx.isInterface())
								{
									if (constr == null)
									{
										constr = constrain(yapClassClaxx);
									}
									else
									{
										constr = constr.or(constrain(yapClass.classReflector()));
									}
								}
							}
						}
						return constr;
					}
					com.db4o.Iterator4 constraintsIterator = i_constraints.iterator();
					while (constraintsIterator.hasNext())
					{
						com.db4o.QCon existingConstraint = (com.db4o.QConObject)constraintsIterator.next(
							);
						bool[] removeExisting = { false };
						com.db4o.QCon newConstraint = existingConstraint.shareParentForClass(claxx, removeExisting
							);
						if (newConstraint != null)
						{
							addConstraint(newConstraint);
							col.add(newConstraint);
							if (removeExisting[0])
							{
								removeConstraint(existingConstraint);
							}
						}
					}
					if (col.size() == 0)
					{
						com.db4o.QConClass qcc = new com.db4o.QConClass(i_trans, null, null, claxx);
						addConstraint(qcc);
						return qcc;
					}
					if (col.size() == 1)
					{
						return (com.db4o.query.Constraint)col.iterator().next();
					}
					com.db4o.query.Constraint[] constraintArray = new com.db4o.query.Constraint[col.size
						()];
					col.toArray(constraintArray);
					return new com.db4o.QConstraints(i_trans, constraintArray);
				}
				com.db4o.QEvaluation eval = com.db4o.Platform.evaluationCreate(i_trans, example);
				if (eval != null)
				{
					com.db4o.Iterator4 i = i_constraints.iterator();
					while (i.hasNext())
					{
						((com.db4o.QCon)i.next()).addConstraint(eval);
					}
					return null;
				}
				com.db4o.Collection4 constraints = new com.db4o.Collection4();
				addConstraint(constraints, example);
				return toConstraint(constraints);
			}
		}

		public virtual com.db4o.query.Constraints constraints()
		{
			lock (streamLock())
			{
				com.db4o.query.Constraint[] constraints = new com.db4o.query.Constraint[i_constraints
					.size()];
				i_constraints.toArray(constraints);
				return new com.db4o.QConstraints(i_trans, constraints);
			}
		}

		public virtual com.db4o.query.Query descend(string a_field)
		{
			lock (streamLock())
			{
				com.db4o.QQuery query = new com.db4o.QQuery(i_trans, this, a_field);
				int[] run = { 1 };
				if (!descend1(query, a_field, run))
				{
					if (run[0] == 1)
					{
						run[0] = 2;
						if (!descend1(query, a_field, run))
						{
							return null;
						}
					}
				}
				return query;
			}
		}

		private bool descend1(com.db4o.QQuery query, string a_field, int[] run)
		{
			bool[] foundClass = { false };
			if (run[0] == 2 || i_constraints.size() == 0)
			{
				run[0] = 0;
				bool[] anyClassCollected = { false };
				i_trans.i_stream.i_classCollection.yapFields(a_field, new _AnonymousInnerClass193
					(this, anyClassCollected));
			}
			com.db4o.Iterator4 i = i_constraints.iterator();
			while (i.hasNext())
			{
				if (((com.db4o.QCon)i.next()).attach(query, a_field))
				{
					foundClass[0] = true;
				}
			}
			return foundClass[0];
		}

		private sealed class _AnonymousInnerClass193 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass193(QQuery _enclosing, bool[] anyClassCollected)
			{
				this._enclosing = _enclosing;
				this.anyClassCollected = anyClassCollected;
			}

			public void visit(object obj)
			{
				object[] pair = ((object[])obj);
				com.db4o.YapClass parentYc = (com.db4o.YapClass)pair[0];
				com.db4o.YapField yf = (com.db4o.YapField)pair[1];
				com.db4o.YapClass childYc = yf.getFieldYapClass(this._enclosing.i_trans.i_stream);
				bool take = true;
				if (childYc is com.db4o.YapClassAny)
				{
					if (anyClassCollected[0])
					{
						take = false;
					}
					else
					{
						anyClassCollected[0] = true;
					}
				}
				if (take)
				{
					com.db4o.QConClass qcc = new com.db4o.QConClass(this._enclosing.i_trans, null, yf
						.qField(this._enclosing.i_trans), parentYc.classReflector());
					this._enclosing.addConstraint(qcc);
				}
			}

			private readonly QQuery _enclosing;

			private readonly bool[] anyClassCollected;
		}

		public virtual com.db4o.ObjectSet execute()
		{
			lock (streamLock())
			{
				com.db4o.ObjectSet result = classOnlyQuery();
				if (result != null)
				{
					result.reset();
					return result;
				}
				result = new com.db4o.QResult(i_trans);
				execute1((com.db4o.QResult)result);
				return result;
			}
		}

		private com.db4o.ObjectSet classOnlyQuery()
		{
			if (i_constraints.size() != 1)
			{
				return null;
			}
			com.db4o.query.Constraint constr = (com.db4o.query.Constraint)i_constraints.iterator
				().next();
			if (j4o.lang.Class.getClassForObject(constr) != j4o.lang.Class.getClassForType(typeof(
				com.db4o.QConClass)))
			{
				return null;
			}
			com.db4o.QConClass clazzconstr = (com.db4o.QConClass)constr;
			com.db4o.YapClass clazz = clazzconstr.i_yapClass;
			if (clazz == null)
			{
				return null;
			}
			if (clazzconstr.i_subConstraints != null || clazz.isArray())
			{
				return null;
			}
			com.db4o.ClassIndex classIndex = clazz.getIndex();
			if (classIndex == null)
			{
				return null;
			}
			if (i_trans.i_stream.isClient())
			{
				long[] ids = classIndex.getInternalIDs(i_trans, clazz.getID());
				com.db4o.QResultClient resClient = new com.db4o.QResultClient(i_trans, ids.Length
					);
				for (int i = 0; i < ids.Length; i++)
				{
					resClient.add((int)ids[i]);
				}
				return resClient;
			}
			com.db4o.Tree tree = classIndex.cloneForYapClass(i_trans, clazz.getID());
			if (tree == null)
			{
				return new com.db4o.QResult(i_trans);
			}
			com.db4o.QResult resLocal = new com.db4o.QResult(i_trans, tree.size());
			tree.traverse(new _AnonymousInnerClass291(this, resLocal));
			return resLocal;
		}

		private sealed class _AnonymousInnerClass291 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass291(QQuery _enclosing, com.db4o.QResult resLocal)
			{
				this._enclosing = _enclosing;
				this.resLocal = resLocal;
			}

			public void visit(object a_object)
			{
				resLocal.add(((com.db4o.TreeInt)a_object).i_key);
			}

			private readonly QQuery _enclosing;

			private readonly com.db4o.QResult resLocal;
		}

		internal virtual void execute1(com.db4o.QResult result)
		{
			if (i_trans.i_stream.isClient())
			{
				marshall();
				((com.db4o.YapClient)i_trans.i_stream).queryExecute(this, result);
			}
			else
			{
				executeLocal(result);
			}
		}

		internal virtual void executeLocal(com.db4o.QResult result)
		{
			bool checkDuplicates = false;
			bool topLevel = true;
			com.db4o.List4 candidateCollection = null;
			com.db4o.Iterator4 i = i_constraints.iterator();
			while (i.hasNext())
			{
				com.db4o.QCon qcon = (com.db4o.QCon)i.next();
				com.db4o.QCon old = qcon;
				bool found = false;
				qcon = qcon.getRoot();
				if (qcon != old)
				{
					checkDuplicates = true;
					topLevel = false;
				}
				com.db4o.YapClass yc = qcon.getYapClass();
				if (yc != null)
				{
					if (candidateCollection != null)
					{
						com.db4o.Iterator4 j = new com.db4o.Iterator4(candidateCollection);
						while (j.hasNext())
						{
							com.db4o.QCandidates candidates = (com.db4o.QCandidates)j.next();
							if (candidates.tryAddConstraint(qcon))
							{
								found = true;
								break;
							}
						}
					}
					if (!found)
					{
						com.db4o.QCandidates candidates = new com.db4o.QCandidates(i_trans, qcon.getYapClass
							(), null);
						candidates.addConstraint(qcon);
						candidateCollection = new com.db4o.List4(candidateCollection, candidates);
					}
				}
			}
			if (candidateCollection != null)
			{
				i = new com.db4o.Iterator4(candidateCollection);
				while (i.hasNext())
				{
					((com.db4o.QCandidates)i.next()).execute();
				}
				if (candidateCollection.i_next != null)
				{
					checkDuplicates = true;
				}
				if (checkDuplicates)
				{
					result.checkDuplicates();
				}
				i = new com.db4o.Iterator4(candidateCollection);
				while (i.hasNext())
				{
					com.db4o.QCandidates candidates = (com.db4o.QCandidates)i.next();
					if (topLevel)
					{
						candidates.traverse(result);
					}
					else
					{
						com.db4o.QQuery q = this;
						com.db4o.Collection4 fieldPath = new com.db4o.Collection4();
						while (q.i_parent != null)
						{
							fieldPath.add(q.i_field);
							q = q.i_parent;
						}
						candidates.traverse(new _AnonymousInnerClass378(this, fieldPath, result));
					}
				}
			}
			result.reset();
		}

		private sealed class _AnonymousInnerClass378 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass378(QQuery _enclosing, com.db4o.Collection4 fieldPath, 
				com.db4o.QResult result)
			{
				this._enclosing = _enclosing;
				this.fieldPath = fieldPath;
				this.result = result;
			}

			public void visit(object a_object)
			{
				com.db4o.QCandidate candidate = (com.db4o.QCandidate)a_object;
				if (candidate.include())
				{
					com.db4o.TreeInt ids = new com.db4o.TreeInt(candidate.i_key);
					com.db4o.TreeInt[] idsNew = new com.db4o.TreeInt[1];
					com.db4o.Iterator4 itPath = fieldPath.iterator();
					while (itPath.hasNext())
					{
						idsNew[0] = null;
						string fieldName = (string)(itPath.next());
						if (ids != null)
						{
							ids.traverse(new _AnonymousInnerClass389(this, idsNew, fieldName));
						}
						ids = idsNew[0];
					}
					if (ids != null)
					{
						ids.traverse(new _AnonymousInnerClass413(this, result));
					}
				}
			}

			private sealed class _AnonymousInnerClass389 : com.db4o.Visitor4
			{
				public _AnonymousInnerClass389(_AnonymousInnerClass378 _enclosing, com.db4o.TreeInt[]
					 idsNew, string fieldName)
				{
					this._enclosing = _enclosing;
					this.idsNew = idsNew;
					this.fieldName = fieldName;
				}

				public void visit(object treeInt)
				{
					int id = ((com.db4o.TreeInt)treeInt).i_key;
					com.db4o.YapWriter reader = this._enclosing._enclosing.i_trans.i_stream.readWriterByID
						(this._enclosing._enclosing.i_trans, id);
					if (reader != null)
					{
						com.db4o.YapClass yc = this._enclosing._enclosing.i_trans.i_stream.getYapClass(reader
							.readInt());
						idsNew[0] = yc.collectFieldIDs(idsNew[0], reader, fieldName);
					}
				}

				private readonly _AnonymousInnerClass378 _enclosing;

				private readonly com.db4o.TreeInt[] idsNew;

				private readonly string fieldName;
			}

			private sealed class _AnonymousInnerClass413 : com.db4o.Visitor4
			{
				public _AnonymousInnerClass413(_AnonymousInnerClass378 _enclosing, com.db4o.QResult
					 result)
				{
					this._enclosing = _enclosing;
					this.result = result;
				}

				public void visit(object treeInt)
				{
					result.addKeyCheckDuplicates(((com.db4o.TreeInt)treeInt).i_key);
				}

				private readonly _AnonymousInnerClass378 _enclosing;

				private readonly com.db4o.QResult result;
			}

			private readonly QQuery _enclosing;

			private readonly com.db4o.Collection4 fieldPath;

			private readonly com.db4o.QResult result;
		}

		internal virtual com.db4o.Transaction getTransaction()
		{
			return i_trans;
		}

		public virtual com.db4o.query.Query orderAscending()
		{
			lock (streamLock())
			{
				setOrdering(i_orderingGenerator.next());
				return this;
			}
		}

		public virtual com.db4o.query.Query orderDescending()
		{
			lock (streamLock())
			{
				setOrdering(-i_orderingGenerator.next());
				return this;
			}
		}

		private void setOrdering(int ordering)
		{
			com.db4o.Iterator4 i = i_constraints.iterator();
			while (i.hasNext())
			{
				((com.db4o.QCon)i.next()).setOrdering(ordering);
			}
		}

		internal virtual void marshall()
		{
			com.db4o.Iterator4 i = i_constraints.iterator();
			while (i.hasNext())
			{
				((com.db4o.QCon)i.next()).getRoot().marshall();
			}
		}

		internal virtual void removeConstraint(com.db4o.QCon a_constraint)
		{
			i_constraints.remove(a_constraint);
		}

		internal virtual void unmarshall(com.db4o.Transaction a_trans)
		{
			i_trans = a_trans;
			com.db4o.Iterator4 i = i_constraints.iterator();
			while (i.hasNext())
			{
				((com.db4o.QCon)i.next()).unmarshall(a_trans);
			}
		}

		internal virtual com.db4o.query.Constraint toConstraint(com.db4o.Collection4 constraints
			)
		{
			com.db4o.Iterator4 i = constraints.iterator();
			if (constraints.size() == 1)
			{
				return (com.db4o.query.Constraint)i.next();
			}
			else
			{
				if (constraints.size() > 0)
				{
					com.db4o.query.Constraint[] constraintArray = new com.db4o.query.Constraint[constraints
						.size()];
					constraints.toArray(constraintArray);
					return new com.db4o.QConstraints(i_trans, constraintArray);
				}
			}
			return null;
		}

		protected virtual object streamLock()
		{
			return i_trans.i_stream.i_lock;
		}
	}
}
