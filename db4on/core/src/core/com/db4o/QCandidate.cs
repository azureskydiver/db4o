namespace com.db4o
{
	/// <summary>Represents an actual object in the database.</summary>
	/// <remarks>
	/// Represents an actual object in the database. Forms a tree structure, indexed
	/// by id. Can have dependents that are doNotInclude'd in the query result when
	/// this is doNotInclude'd.
	/// </remarks>
	/// <exclude></exclude>
	public class QCandidate : com.db4o.TreeInt, com.db4o.query.Candidate, com.db4o.Orderable
	{
		internal com.db4o.YapReader _bytes;

		internal readonly com.db4o.QCandidates _candidates;

		private com.db4o.foundation.List4 _dependants;

		internal bool _include = true;

		private object _member;

		internal com.db4o.Orderable _order;

		internal com.db4o.Tree _pendingJoins;

		private com.db4o.QCandidate _root;

		internal com.db4o.YapClass _yapClass;

		internal com.db4o.YapField _yapField;

		private QCandidate(com.db4o.QCandidates qcandidates) : base(0)
		{
			_candidates = qcandidates;
		}

		private QCandidate() : this(null)
		{
		}

		internal QCandidate(com.db4o.QCandidates candidates, object obj, int id, bool include
			) : base(id)
		{
			_candidates = candidates;
			_order = this;
			_member = obj;
			_include = include;
		}

		public override object shallowClone()
		{
			com.db4o.QCandidate qcan = new com.db4o.QCandidate(_candidates);
			qcan._bytes = _bytes;
			qcan._dependants = _dependants;
			qcan._include = _include;
			qcan._member = _member;
			qcan._order = _order;
			qcan._pendingJoins = _pendingJoins;
			qcan._root = _root;
			qcan._yapClass = _yapClass;
			qcan._yapField = _yapField;
			return base.shallowCloneInternal(qcan);
		}

		internal virtual void addDependant(com.db4o.QCandidate a_candidate)
		{
			_dependants = new com.db4o.foundation.List4(_dependants, a_candidate);
		}

		private void checkInstanceOfCompare()
		{
			if (_member is com.db4o.config.Compare)
			{
				_member = ((com.db4o.config.Compare)_member).compare();
				com.db4o.YapFile stream = getStream();
				_yapClass = stream.getYapClass(stream.reflector().forObject(_member), false);
				_key = (int)stream.getID(_member);
				_bytes = stream.readReaderByID(getTransaction(), _key);
			}
		}

		public override int compare(com.db4o.Tree a_to)
		{
			return _order.compareTo(((com.db4o.QCandidate)a_to)._order);
		}

		public virtual int compareTo(object a_object)
		{
			return _key - ((com.db4o.TreeInt)a_object)._key;
		}

		internal virtual bool createChild(com.db4o.QCandidates a_candidates)
		{
			if (!_include)
			{
				return false;
			}
			com.db4o.QCandidate candidate = null;
			if (_yapField != null)
			{
				com.db4o.TypeHandler4 handler = _yapField.getHandler();
				if (handler != null)
				{
					com.db4o.YapReader[] arrayBytes = { _bytes };
					com.db4o.TypeHandler4 arrayWrapper = handler.readArrayWrapper(getTransaction(), arrayBytes
						);
					if (arrayWrapper != null)
					{
						int offset = arrayBytes[0]._offset;
						bool outerRes = true;
						com.db4o.foundation.Iterator4 i = a_candidates.iterateConstraints();
						while (i.hasNext())
						{
							com.db4o.QCon qcon = (com.db4o.QCon)i.next();
							com.db4o.QField qf = qcon.getField();
							if (qf == null || qf.i_name.Equals(_yapField.getName()))
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
								candidates.traverse(new _AnonymousInnerClass166(this, innerRes, isNot, pending));
								if (isNot)
								{
									qcon.not();
								}
								if (pending[0] != null)
								{
									pending[0].traverse(new _AnonymousInnerClass235(this));
								}
								if (!innerRes[0])
								{
									qcon.visit(getRoot(), qcon.i_evaluator.not(false));
									outerRes = false;
								}
								qcon.setParent(tempParent);
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
				if (_yapField != null)
				{
					com.db4o.TypeHandler4 handler = _yapField.getHandler();
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

		private sealed class _AnonymousInnerClass166 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass166(QCandidate _enclosing, bool[] innerRes, bool isNot
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
				if (cand._pendingJoins != null)
				{
					cand._pendingJoins.traverse(new _AnonymousInnerClass179(this, pending));
				}
			}

			private sealed class _AnonymousInnerClass179 : com.db4o.foundation.Visitor4
			{
				public _AnonymousInnerClass179(_AnonymousInnerClass166 _enclosing, com.db4o.Tree[]
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
						if (oldPending._result != newPending._result)
						{
							oldPending._result = com.db4o.QPending.BOTH;
						}
					}
					else
					{
						pending[0] = com.db4o.Tree.add(pending[0], newPending);
					}
				}

				private readonly _AnonymousInnerClass166 _enclosing;

				private readonly com.db4o.Tree[] pending;
			}

			private readonly QCandidate _enclosing;

			private readonly bool[] innerRes;

			private readonly bool isNot;

			private readonly com.db4o.Tree[] pending;
		}

		private sealed class _AnonymousInnerClass235 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass235(QCandidate _enclosing)
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
			_include = false;
			if (_dependants != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(_dependants
					);
				_dependants = null;
				while (i.hasNext())
				{
					((com.db4o.QCandidate)i.next()).doNotInclude();
				}
			}
		}

		public override bool duplicates()
		{
			return _order.hasDuplicates();
		}

		internal virtual bool evaluate(com.db4o.QConObject a_constraint, com.db4o.QE a_evaluator
			)
		{
			if (a_evaluator.identity())
			{
				return a_evaluator.evaluate(a_constraint, this, null);
			}
			if (_member == null)
			{
				_member = value();
			}
			return a_evaluator.evaluate(a_constraint, this, a_constraint.translate(_member));
		}

		internal virtual bool evaluate(com.db4o.QPending a_pending)
		{
			com.db4o.QPending oldPending = (com.db4o.QPending)com.db4o.Tree.find(_pendingJoins
				, a_pending);
			if (oldPending == null)
			{
				a_pending.changeConstraint();
				_pendingJoins = com.db4o.Tree.add(_pendingJoins, a_pending);
				return true;
			}
			else
			{
				_pendingJoins = _pendingJoins.removeNode(oldPending);
				oldPending._join.evaluatePending(this, oldPending, a_pending, a_pending._result);
				return false;
			}
		}

		internal virtual com.db4o.reflect.ReflectClass classReflector()
		{
			readYapClass();
			if (_yapClass == null)
			{
				return null;
			}
			return _yapClass.classReflector();
		}

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
			return _root == null ? this : _root;
		}

		private com.db4o.YapFile getStream()
		{
			return getTransaction().i_file;
		}

		private com.db4o.Transaction getTransaction()
		{
			return _candidates.i_trans;
		}

		public virtual bool hasDuplicates()
		{
			return _root != null;
		}

		public virtual void hintOrder(int a_order, bool a_major)
		{
			_order = new com.db4o.Order();
			_order.hintOrder(a_order, a_major);
		}

		public virtual bool include()
		{
			return _include;
		}

		/// <summary>For external interface use only.</summary>
		/// <remarks>
		/// For external interface use only. Call doNotInclude() internally so
		/// dependancies can be checked.
		/// </remarks>
		public virtual void include(bool flag)
		{
			_include = flag;
		}

		internal override void isDuplicateOf(com.db4o.Tree a_tree)
		{
			_size = 0;
			_root = (com.db4o.QCandidate)a_tree;
		}

		private com.db4o.reflect.ReflectClass memberClass()
		{
			return getTransaction().reflector().forObject(_member);
		}

		internal virtual com.db4o.YapComparable prepareComparison(com.db4o.YapStream a_stream
			, object a_constraint)
		{
			if (_yapField != null)
			{
				return _yapField.prepareComparison(a_constraint);
			}
			if (_yapClass == null)
			{
				com.db4o.YapClass yc = null;
				if (_bytes != null)
				{
					yc = a_stream.getYapClass(a_stream.reflector().forObject(a_constraint), true);
				}
				else
				{
					if (_member != null)
					{
						yc = a_stream.getYapClass(a_stream.reflector().forObject(_member), false);
					}
				}
				if (yc != null)
				{
					if (_member != null && j4o.lang.Class.getClassForObject(_member).isArray())
					{
						com.db4o.TypeHandler4 ydt = (com.db4o.TypeHandler4)yc.prepareComparison(a_constraint
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
				return _yapClass.prepareComparison(a_constraint);
			}
		}

		private void read()
		{
			if (_include)
			{
				if (_bytes == null)
				{
					if (_key > 0)
					{
						_bytes = getStream().readReaderByID(getTransaction(), _key);
						if (_bytes == null)
						{
							_include = false;
						}
					}
					else
					{
						_include = false;
					}
				}
			}
		}

		private com.db4o.QCandidate readSubCandidate(com.db4o.QCandidates candidateCollection
			)
		{
			int id = 0;
			read();
			if (_bytes != null)
			{
				int offset = _bytes._offset;
				try
				{
					id = _bytes.readInt();
				}
				catch (System.Exception e)
				{
					return null;
				}
				_bytes._offset = offset;
				if (id != 0)
				{
					com.db4o.QCandidate candidate = new com.db4o.QCandidate(candidateCollection, null
						, id, true);
					candidate._root = getRoot();
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
				_member = trans.i_stream.getByID1(trans, _key);
				if (_member != null && (a_activate || _member is com.db4o.config.Compare))
				{
					trans.i_stream.activate1(trans, _member);
					checkInstanceOfCompare();
				}
			}
		}

		internal virtual com.db4o.YapClass readYapClass()
		{
			if (_yapClass == null)
			{
				read();
				if (_bytes != null)
				{
					_bytes._offset = 0;
					com.db4o.YapStream stream = getStream();
					_yapClass = stream.getYapClass(_bytes.readInt());
					if (_yapClass != null)
					{
						if (stream.i_handlers.ICLASS_COMPARE.isAssignableFrom(_yapClass.classReflector())
							)
						{
							readThis(false);
						}
					}
				}
			}
			return _yapClass;
		}

		public override string ToString()
		{
			return base.ToString();
			string str = "QCandidate ";
			if (_yapClass != null)
			{
				str += "\n   YapClass " + _yapClass.getName();
			}
			if (_yapField != null)
			{
				str += "\n   YapField " + _yapField.getName();
			}
			if (_member != null)
			{
				str += "\n   Member " + _member.ToString();
			}
			if (_root != null)
			{
				str += "\n  rooted by:\n";
				str += _root.ToString();
			}
			else
			{
				str += "\n  ROOT";
			}
			return str;
		}

		internal virtual void useField(com.db4o.QField a_field)
		{
			read();
			if (_bytes == null)
			{
				_yapField = null;
			}
			else
			{
				readYapClass();
				_member = null;
				if (a_field == null)
				{
					_yapField = null;
				}
				else
				{
					if (_yapClass == null)
					{
						_yapField = null;
					}
					else
					{
						_yapField = a_field.getYapField(_yapClass);
						if (_yapField == null | !_yapClass.findOffset(_bytes, _yapField))
						{
							if (_yapClass.holdsAnyClass())
							{
								_yapField = null;
							}
							else
							{
								_yapField = new com.db4o.YapFieldNull();
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
			if (_member == null)
			{
				if (_yapField == null)
				{
					readThis(a_activate);
				}
				else
				{
					int offset = _bytes._offset;
					try
					{
						_member = _yapField.readQuery(getTransaction(), _bytes);
					}
					catch (com.db4o.CorruptionException ce)
					{
						_member = null;
					}
					_bytes._offset = offset;
					checkInstanceOfCompare();
				}
			}
			return _member;
		}
	}
}
