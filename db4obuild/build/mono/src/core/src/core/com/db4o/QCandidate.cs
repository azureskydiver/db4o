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
	/// <summary>Represents an actual object in the database.</summary>
	/// <remarks>
	/// Represents an actual object in the database. Forms a tree structure,
	/// indexed by id. Can have dependents that are doNotInclude'd in the
	/// query result when this is doNotInclude'd.
	/// </remarks>
	/// <exclude></exclude>
	internal class QCandidate : com.db4o.TreeInt, com.db4o.query.Candidate, com.db4o.Orderable
	{
		internal com.db4o.YapReader i_bytes;

		internal readonly com.db4o.QCandidates i_candidates;

		private com.db4o.List4 i_dependants;

		internal bool i_include = true;

		private object i_member;

		internal com.db4o.Orderable i_order;

		internal com.db4o.Tree i_pendingJoins;

		private com.db4o.QCandidate i_root;

		internal com.db4o.YapClass i_yapClass;

		internal com.db4o.YapField i_yapField;

		private QCandidate() : base(0)
		{
			i_candidates = null;
		}

		internal QCandidate(com.db4o.QCandidates candidates, object obj, int id, bool include
			) : base(id)
		{
			i_candidates = candidates;
			i_order = this;
			i_member = obj;
			i_include = include;
		}

		internal virtual void addDependant(com.db4o.QCandidate a_candidate)
		{
			i_dependants = new com.db4o.List4(i_dependants, a_candidate);
		}

		private void checkInstanceOfCompare()
		{
			if (i_member is com.db4o.config.Compare)
			{
				i_member = ((com.db4o.config.Compare)i_member).compare();
				com.db4o.YapFile stream = getStream();
				i_yapClass = stream.getYapClass(stream.reflector().forObject(i_member), false);
				i_key = (int)stream.getID(i_member);
				i_bytes = stream.readReaderByID(getTransaction(), i_key);
			}
		}

		/// <summary><Tree Code>***</summary>
		internal override int compare(com.db4o.Tree a_to)
		{
			return i_order.compareTo(((com.db4o.QCandidate)a_to).i_order);
		}

		public virtual int compareTo(object a_object)
		{
			return i_key - ((com.db4o.TreeInt)a_object).i_key;
		}

		internal virtual bool createChild(com.db4o.QCandidates a_candidates)
		{
			if (!i_include)
			{
				return false;
			}
			com.db4o.QCandidate candidate = null;
			if (i_yapField != null)
			{
				com.db4o.YapDataType handler = i_yapField.getHandler();
				if (handler != null)
				{
					com.db4o.YapReader[] arrayBytes = { i_bytes };
					com.db4o.YapDataType arrayWrapper = handler.readArrayWrapper(getTransaction(), arrayBytes
						);
					if (arrayWrapper != null)
					{
						int offset = arrayBytes[0]._offset;
						bool outerRes = true;
						if (a_candidates.i_constraints != null)
						{
							com.db4o.Iterator4 i = new com.db4o.Iterator4(a_candidates.i_constraints);
							while (i.hasNext())
							{
								com.db4o.QCon qcon = (com.db4o.QCon)i.next();
								com.db4o.QField qf = qcon.getField();
								if (qf == null || qf.i_name.Equals(i_yapField.getName()))
								{
									com.db4o.QCon tempParent = qcon.i_parent;
									qcon.setParent(null);
									com.db4o.QCandidates candidates = new com.db4o.QCandidates(a_candidates.i_trans, 
										null, qf);
									candidates.addConstraint(qcon);
									qcon.setCandidates(candidates);
									arrayWrapper.readCandidates(arrayBytes[0], candidates);
									arrayBytes[0]._offset = offset;
									bool isNot = qcon.isNot();
									if (isNot)
									{
										qcon.removeNot();
									}
									candidates.evaluate();
									com.db4o.Tree[] pending = new com.db4o.Tree[1];
									bool[] innerRes = { isNot };
									candidates.traverse(new _AnonymousInnerClass146(this, innerRes, isNot, pending));
									if (isNot)
									{
										qcon.not();
									}
									if (pending[0] != null)
									{
										pending[0].traverse(new _AnonymousInnerClass211(this));
									}
									if (!innerRes[0])
									{
										qcon.visit(getRoot(), qcon.i_evaluator.not(false));
										outerRes = false;
									}
									qcon.setParent(tempParent);
								}
							}
						}
						return outerRes;
					}
					if (handler.getType() == com.db4o.YapConst.TYPE_SIMPLE)
					{
						a_candidates.i_currentConstraint.visit(this);
						return true;
					}
				}
			}
			if (candidate == null)
			{
				candidate = readSubCandidate(a_candidates);
				if (candidate == null)
				{
					return false;
				}
			}
			if (a_candidates.i_yapClass != null && a_candidates.i_yapClass.isStrongTyped())
			{
				if (i_yapField != null)
				{
					com.db4o.YapDataType handler = i_yapField.getHandler();
					if (handler != null && (handler.getType() == com.db4o.YapConst.TYPE_CLASS))
					{
						com.db4o.YapClass yc = (com.db4o.YapClass)handler;
						if (yc is com.db4o.YapClassAny)
						{
							yc = candidate.readYapClass();
						}
						if (!yc.canHold(a_candidates.i_yapClass.classReflector()))
						{
							return false;
						}
					}
				}
			}
			addDependant(a_candidates.addByIdentity(candidate));
			return true;
		}

		private sealed class _AnonymousInnerClass146 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass146(QCandidate _enclosing, bool[] innerRes, bool isNot
				, com.db4o.Tree[] pending)
			{
				this._enclosing = _enclosing;
				this.innerRes = innerRes;
				this.isNot = isNot;
				this.pending = pending;
			}

			public void visit(object obj)
			{
				com.db4o.QCandidate cand = (com.db4o.QCandidate)obj;
				if (cand.include())
				{
					innerRes[0] = !isNot;
				}
				if (cand.i_pendingJoins != null)
				{
					cand.i_pendingJoins.traverse(new _AnonymousInnerClass158(this, pending));
				}
			}

			private sealed class _AnonymousInnerClass158 : com.db4o.Visitor4
			{
				public _AnonymousInnerClass158(_AnonymousInnerClass146 _enclosing, com.db4o.Tree[]
					 pending)
				{
					this._enclosing = _enclosing;
					this.pending = pending;
				}

				public void visit(object a_object)
				{
					com.db4o.QPending newPending = (com.db4o.QPending)a_object;
					newPending.changeConstraint();
					com.db4o.QPending oldPending = (com.db4o.QPending)com.db4o.Tree.find(pending[0], 
						newPending);
					if (oldPending != null)
					{
						if (oldPending.i_result != newPending.i_result)
						{
							oldPending.i_result = com.db4o.QPending.BOTH;
						}
					}
					else
					{
						pending[0] = com.db4o.Tree.add(pending[0], newPending);
					}
				}

				private readonly _AnonymousInnerClass146 _enclosing;

				private readonly com.db4o.Tree[] pending;
			}

			private readonly QCandidate _enclosing;

			private readonly bool[] innerRes;

			private readonly bool isNot;

			private readonly com.db4o.Tree[] pending;
		}

		private sealed class _AnonymousInnerClass211 : com.db4o.Visitor4
		{
			public _AnonymousInnerClass211(QCandidate _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object a_object)
			{
				this._enclosing.getRoot().evaluate((com.db4o.QPending)a_object);
			}

			private readonly QCandidate _enclosing;
		}

		internal virtual void doNotInclude()
		{
			i_include = false;
			if (i_dependants != null)
			{
				com.db4o.Iterator4 i = new com.db4o.Iterator4(i_dependants);
				i_dependants = null;
				while (i.hasNext())
				{
					((com.db4o.QCandidate)i.next()).doNotInclude();
				}
			}
		}

		internal override bool duplicates()
		{
			return i_order.hasDuplicates();
		}

		internal virtual bool evaluate(com.db4o.QConObject a_constraint, com.db4o.QE a_evaluator
			)
		{
			if (i_member == null)
			{
				i_member = value();
			}
			return a_evaluator.evaluate(a_constraint, this, a_constraint.translate(i_member));
		}

		internal virtual bool evaluate(com.db4o.QPending a_pending)
		{
			com.db4o.QPending oldPending = (com.db4o.QPending)com.db4o.Tree.find(i_pendingJoins
				, a_pending);
			if (oldPending == null)
			{
				a_pending.changeConstraint();
				i_pendingJoins = com.db4o.Tree.add(i_pendingJoins, a_pending);
				return true;
			}
			else
			{
				i_pendingJoins = i_pendingJoins.removeNode(oldPending);
				oldPending.i_join.evaluatePending(this, oldPending, a_pending, a_pending.i_result
					);
				return false;
			}
		}

		internal virtual com.db4o.reflect.ReflectClass classReflector()
		{
			readYapClass();
			if (i_yapClass == null)
			{
				return null;
			}
			return i_yapClass.classReflector();
		}

		/// <summary><Candidate interface code>***</summary>
		public virtual com.db4o.ObjectContainer objectContainer()
		{
			return getStream();
		}

		public virtual object getObject()
		{
			object obj = value(true);
			if (obj is com.db4o.YapReader)
			{
				com.db4o.YapReader reader = (com.db4o.YapReader)obj;
				int offset = reader._offset;
				obj = reader.toString(getTransaction());
				reader._offset = offset;
			}
			return obj;
		}

		internal virtual com.db4o.QCandidate getRoot()
		{
			return i_root == null ? this : i_root;
		}

		private com.db4o.YapFile getStream()
		{
			return getTransaction().i_file;
		}

		private com.db4o.Transaction getTransaction()
		{
			return i_candidates.i_trans;
		}

		public virtual bool hasDuplicates()
		{
			return i_root != null;
		}

		public virtual void hintOrder(int a_order, bool a_major)
		{
			i_order = new com.db4o.Order();
			i_order.hintOrder(a_order, a_major);
		}

		public virtual bool include()
		{
			return i_include;
		}

		/// <summary>For external interface use only.</summary>
		/// <remarks>
		/// For external interface use only. Call doNotInclude() internally so
		/// dependancies can be checked.
		/// </remarks>
		public virtual void include(bool flag)
		{
			i_include = flag;
		}

		internal override void isDuplicateOf(com.db4o.Tree a_tree)
		{
			i_size = 0;
			i_root = (com.db4o.QCandidate)a_tree;
		}

		private com.db4o.reflect.ReflectClass memberClass()
		{
			return getTransaction().reflector().forObject(i_member);
		}

		internal virtual com.db4o.YapComparable prepareComparison(com.db4o.YapStream a_stream
			, object a_constraint)
		{
			if (i_yapField != null)
			{
				return i_yapField.prepareComparison(a_constraint);
			}
			if (i_yapClass == null)
			{
				com.db4o.YapClass yc = null;
				if (i_bytes != null)
				{
					yc = a_stream.getYapClass(a_stream.reflector().forObject(a_constraint), true);
				}
				else
				{
					if (i_member != null)
					{
						yc = a_stream.getYapClass(a_stream.reflector().forObject(i_member), false);
					}
				}
				if (yc != null)
				{
					if (i_member != null && j4o.lang.Class.getClassForObject(i_member).isArray())
					{
						com.db4o.YapDataType ydt = (com.db4o.YapDataType)yc.prepareComparison(a_constraint
							);
						if (a_stream.reflector().array().isNDimensional(memberClass()))
						{
							com.db4o.YapArrayN yan = new com.db4o.YapArrayN(a_stream, ydt, false);
							return yan;
						}
						else
						{
							com.db4o.YapArray ya = new com.db4o.YapArray(a_stream, ydt, false);
							return ya;
						}
					}
					else
					{
						return yc.prepareComparison(a_constraint);
					}
				}
				return null;
			}
			else
			{
				return i_yapClass.prepareComparison(a_constraint);
			}
		}

		private void read()
		{
			if (i_include)
			{
				if (i_bytes == null)
				{
					if (i_key > 0)
					{
						i_bytes = getStream().readReaderByID(getTransaction(), i_key);
						if (i_bytes == null)
						{
							i_include = false;
						}
					}
					else
					{
						i_include = false;
					}
				}
			}
		}

		private com.db4o.QCandidate readSubCandidate(com.db4o.QCandidates candidateCollection
			)
		{
			int id = 0;
			read();
			if (i_bytes != null)
			{
				int offset = i_bytes._offset;
				try
				{
					id = i_bytes.readInt();
				}
				catch (System.Exception e)
				{
					return null;
				}
				i_bytes._offset = offset;
				if (id != 0)
				{
					com.db4o.QCandidate candidate = new com.db4o.QCandidate(candidateCollection, null
						, id, true);
					candidate.i_root = getRoot();
					return candidate;
				}
			}
			return null;
		}

		private void readThis(bool a_activate)
		{
			read();
			com.db4o.Transaction trans = getTransaction();
			if (trans != null)
			{
				i_member = trans.i_stream.getByID1(trans, i_key);
				if (i_member != null && (a_activate || i_member is com.db4o.config.Compare))
				{
					trans.i_stream.activate1(trans, i_member);
					checkInstanceOfCompare();
				}
			}
		}

		internal virtual com.db4o.YapClass readYapClass()
		{
			if (i_yapClass == null)
			{
				read();
				if (i_bytes != null)
				{
					i_bytes._offset = 0;
					com.db4o.YapStream stream = getStream();
					i_yapClass = stream.getYapClass(i_bytes.readInt());
					if (i_yapClass != null)
					{
						if (stream.i_handlers.ICLASS_COMPARE.isAssignableFrom(i_yapClass.classReflector()
							))
						{
							readThis(false);
						}
					}
				}
			}
			return i_yapClass;
		}

		public override string ToString()
		{
			return base.ToString();
		}

		internal virtual void useField(com.db4o.QField a_field)
		{
			read();
			if (i_bytes == null)
			{
				i_yapField = null;
			}
			else
			{
				readYapClass();
				i_member = null;
				if (a_field == null)
				{
					i_yapField = null;
				}
				else
				{
					if (i_yapClass == null)
					{
						i_yapField = null;
					}
					else
					{
						i_yapField = a_field.getYapField(i_yapClass);
						if (i_yapField == null | !i_yapClass.findOffset(i_bytes, i_yapField))
						{
							if (i_yapClass.holdsAnyClass())
							{
								i_yapField = null;
							}
							else
							{
								i_yapField = new com.db4o.YapFieldNull();
							}
						}
					}
				}
			}
		}

		internal virtual object value()
		{
			return value(false);
		}

		internal virtual object value(bool a_activate)
		{
			if (i_member == null)
			{
				if (i_yapField == null)
				{
					readThis(a_activate);
				}
				else
				{
					int offset = i_bytes._offset;
					try
					{
						i_member = i_yapField.readQuery(getTransaction(), i_bytes);
					}
					catch (com.db4o.CorruptionException ce)
					{
						i_member = null;
					}
					i_bytes._offset = offset;
					checkInstanceOfCompare();
				}
			}
			return i_member;
		}
	}
}
