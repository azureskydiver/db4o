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

		internal com.db4o.inside.marshall.MarshallerFamily _marshallerFamily;

		private QCandidate(com.db4o.QCandidates qcandidates) : base(0)
		{
			_candidates = qcandidates;
		}

		private QCandidate() : this(null)
		{
		}

		public QCandidate(com.db4o.QCandidates candidates, object obj, int id, bool include
			) : base(id)
		{
			_candidates = candidates;
			_order = this;
			_member = obj;
			_include = include;
			if (id == 0)
			{
				_key = candidates.GenerateCandidateId();
			}
		}

		public override object ShallowClone()
		{
			com.db4o.QCandidate qcan = new com.db4o.QCandidate(_candidates);
			qcan.SetBytes(_bytes);
			qcan._dependants = _dependants;
			qcan._include = _include;
			qcan._member = _member;
			qcan._order = _order;
			qcan._pendingJoins = _pendingJoins;
			qcan._root = _root;
			qcan._yapClass = _yapClass;
			qcan._yapField = _yapField;
			return base.ShallowCloneInternal(qcan);
		}

		internal virtual void AddDependant(com.db4o.QCandidate a_candidate)
		{
			_dependants = new com.db4o.foundation.List4(_dependants, a_candidate);
		}

		private void CheckInstanceOfCompare()
		{
			if (_member is com.db4o.config.Compare)
			{
				_member = ((com.db4o.config.Compare)_member).Compare();
				com.db4o.YapFile stream = GetStream();
				_yapClass = stream.GetYapClass(stream.Reflector().ForObject(_member), false);
				_key = (int)stream.GetID(_member);
				SetBytes(stream.ReadReaderByID(GetTransaction(), _key));
			}
		}

		public override int Compare(com.db4o.Tree a_to)
		{
			return _order.CompareTo(((com.db4o.QCandidate)a_to)._order);
		}

		public virtual int CompareTo(object a_object)
		{
			return _key - ((com.db4o.TreeInt)a_object)._key;
		}

		internal virtual bool CreateChild(com.db4o.QCandidates a_candidates)
		{
			if (!_include)
			{
				return false;
			}
			com.db4o.QCandidate candidate = null;
			if (_yapField != null)
			{
				com.db4o.TypeHandler4 handler = _yapField.GetHandler();
				if (handler != null)
				{
					com.db4o.YapReader[] arrayBytes = { _bytes };
					com.db4o.TypeHandler4 arrayHandler = handler.ReadArrayHandler(GetTransaction(), _marshallerFamily
						, arrayBytes);
					if (arrayHandler != null)
					{
						int offset = arrayBytes[0]._offset;
						bool outerRes = true;
						com.db4o.foundation.Iterator4 i = a_candidates.IterateConstraints();
						while (i.MoveNext())
						{
							com.db4o.QCon qcon = (com.db4o.QCon)i.Current();
							com.db4o.QField qf = qcon.GetField();
							if (qf == null || qf.i_name.Equals(_yapField.GetName()))
							{
								com.db4o.QCon tempParent = qcon.i_parent;
								qcon.SetParent(null);
								com.db4o.QCandidates candidates = new com.db4o.QCandidates(a_candidates.i_trans, 
									null, qf);
								candidates.AddConstraint(qcon);
								qcon.SetCandidates(candidates);
								arrayHandler.ReadCandidates(_marshallerFamily, arrayBytes[0], candidates);
								arrayBytes[0]._offset = offset;
								bool isNot = qcon.IsNot();
								if (isNot)
								{
									qcon.RemoveNot();
								}
								candidates.Evaluate();
								com.db4o.Tree[] pending = new com.db4o.Tree[1];
								bool[] innerRes = { isNot };
								candidates.Traverse(new _AnonymousInnerClass173(this, innerRes, isNot, pending));
								if (isNot)
								{
									qcon.Not();
								}
								if (pending[0] != null)
								{
									pending[0].Traverse(new _AnonymousInnerClass242(this));
								}
								if (!innerRes[0])
								{
									qcon.Visit(GetRoot(), qcon.i_evaluator.Not(false));
									outerRes = false;
								}
								qcon.SetParent(tempParent);
							}
						}
						return outerRes;
					}
					if (handler.GetTypeID() == com.db4o.YapConst.TYPE_SIMPLE)
					{
						a_candidates.i_currentConstraint.Visit(this);
						return true;
					}
				}
			}
			if (_yapField == null || _yapField is com.db4o.YapFieldNull)
			{
				return false;
			}
			if (candidate == null)
			{
				_yapClass.FindOffset(_bytes, _yapField);
				candidate = ReadSubCandidate(a_candidates);
				if (candidate == null)
				{
					return false;
				}
			}
			if (a_candidates.i_yapClass != null && a_candidates.i_yapClass.IsStrongTyped())
			{
				if (_yapField != null)
				{
					com.db4o.TypeHandler4 handler = _yapField.GetHandler();
					if (handler != null && (handler.GetTypeID() == com.db4o.YapConst.TYPE_CLASS))
					{
						com.db4o.YapClass yc = (com.db4o.YapClass)handler;
						if (yc is com.db4o.YapClassAny)
						{
							yc = candidate.ReadYapClass();
						}
						if (yc == null)
						{
							return false;
						}
						if (!yc.CanHold(a_candidates.i_yapClass.ClassReflector()))
						{
							return false;
						}
					}
				}
			}
			AddDependant(a_candidates.AddByIdentity(candidate));
			return true;
		}

		private sealed class _AnonymousInnerClass173 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass173(QCandidate _enclosing, bool[] innerRes, bool isNot
				, com.db4o.Tree[] pending)
			{
				this._enclosing = _enclosing;
				this.innerRes = innerRes;
				this.isNot = isNot;
				this.pending = pending;
			}

			public void Visit(object obj)
			{
				com.db4o.QCandidate cand = (com.db4o.QCandidate)obj;
				if (cand.Include())
				{
					innerRes[0] = !isNot;
				}
				if (cand._pendingJoins != null)
				{
					cand._pendingJoins.Traverse(new _AnonymousInnerClass186(this, pending));
				}
			}

			private sealed class _AnonymousInnerClass186 : com.db4o.foundation.Visitor4
			{
				public _AnonymousInnerClass186(_AnonymousInnerClass173 _enclosing, com.db4o.Tree[]
					 pending)
				{
					this._enclosing = _enclosing;
					this.pending = pending;
				}

				public void Visit(object a_object)
				{
					com.db4o.QPending newPending = (com.db4o.QPending)a_object;
					newPending.ChangeConstraint();
					com.db4o.QPending oldPending = (com.db4o.QPending)com.db4o.Tree.Find(pending[0], 
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
						pending[0] = com.db4o.Tree.Add(pending[0], newPending);
					}
				}

				private readonly _AnonymousInnerClass173 _enclosing;

				private readonly com.db4o.Tree[] pending;
			}

			private readonly QCandidate _enclosing;

			private readonly bool[] innerRes;

			private readonly bool isNot;

			private readonly com.db4o.Tree[] pending;
		}

		private sealed class _AnonymousInnerClass242 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass242(QCandidate _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				this._enclosing.GetRoot().Evaluate((com.db4o.QPending)a_object);
			}

			private readonly QCandidate _enclosing;
		}

		internal virtual void DoNotInclude()
		{
			_include = false;
			if (_dependants != null)
			{
				com.db4o.foundation.Iterator4 i = new com.db4o.foundation.Iterator4Impl(_dependants
					);
				_dependants = null;
				while (i.MoveNext())
				{
					((com.db4o.QCandidate)i.Current()).DoNotInclude();
				}
			}
		}

		public override bool Duplicates()
		{
			return _order.HasDuplicates();
		}

		internal virtual bool Evaluate(com.db4o.QConObject a_constraint, com.db4o.QE a_evaluator
			)
		{
			if (a_evaluator.Identity())
			{
				return a_evaluator.Evaluate(a_constraint, this, null);
			}
			if (_member == null)
			{
				_member = Value();
			}
			return a_evaluator.Evaluate(a_constraint, this, a_constraint.Translate(_member));
		}

		internal virtual bool Evaluate(com.db4o.QPending a_pending)
		{
			com.db4o.QPending oldPending = (com.db4o.QPending)com.db4o.Tree.Find(_pendingJoins
				, a_pending);
			if (oldPending == null)
			{
				a_pending.ChangeConstraint();
				_pendingJoins = com.db4o.Tree.Add(_pendingJoins, a_pending);
				return true;
			}
			_pendingJoins = _pendingJoins.RemoveNode(oldPending);
			oldPending._join.EvaluatePending(this, oldPending, a_pending, a_pending._result);
			return false;
		}

		internal virtual com.db4o.reflect.ReflectClass ClassReflector()
		{
			ReadYapClass();
			if (_yapClass == null)
			{
				return null;
			}
			return _yapClass.ClassReflector();
		}

		public virtual com.db4o.ObjectContainer ObjectContainer()
		{
			return GetStream();
		}

		public virtual object GetObject()
		{
			object obj = Value(true);
			if (obj is com.db4o.YapReader)
			{
				com.db4o.YapReader reader = (com.db4o.YapReader)obj;
				int offset = reader._offset;
				obj = _marshallerFamily._string.ReadFromOwnSlot(GetStream(), reader);
				reader._offset = offset;
			}
			return obj;
		}

		internal virtual com.db4o.QCandidate GetRoot()
		{
			return _root == null ? this : _root;
		}

		private com.db4o.YapFile GetStream()
		{
			return GetTransaction().i_file;
		}

		private com.db4o.Transaction GetTransaction()
		{
			return _candidates.i_trans;
		}

		public virtual bool HasDuplicates()
		{
			return _root != null;
		}

		public virtual void HintOrder(int a_order, bool a_major)
		{
			_order = new com.db4o.Order();
			_order.HintOrder(a_order, a_major);
		}

		public virtual bool Include()
		{
			return _include;
		}

		/// <summary>For external interface use only.</summary>
		/// <remarks>
		/// For external interface use only. Call doNotInclude() internally so
		/// dependancies can be checked.
		/// </remarks>
		public virtual void Include(bool flag)
		{
			_include = flag;
		}

		internal override void IsDuplicateOf(com.db4o.Tree a_tree)
		{
			_size = 0;
			_root = (com.db4o.QCandidate)a_tree;
		}

		private com.db4o.reflect.ReflectClass MemberClass()
		{
			return GetTransaction().Reflector().ForObject(_member);
		}

		internal virtual com.db4o.YapComparable PrepareComparison(com.db4o.YapStream a_stream
			, object a_constraint)
		{
			if (_yapField != null)
			{
				return _yapField.PrepareComparison(a_constraint);
			}
			if (_yapClass == null)
			{
				com.db4o.YapClass yc = null;
				if (_bytes != null)
				{
					yc = a_stream.GetYapClass(a_stream.Reflector().ForObject(a_constraint), true);
				}
				else
				{
					if (_member != null)
					{
						yc = a_stream.GetYapClass(a_stream.Reflector().ForObject(_member), false);
					}
				}
				if (yc != null)
				{
					if (_member != null && j4o.lang.Class.GetClassForObject(_member).IsArray())
					{
						com.db4o.TypeHandler4 ydt = (com.db4o.TypeHandler4)yc.PrepareComparison(a_constraint
							);
						if (a_stream.Reflector().Array().IsNDimensional(MemberClass()))
						{
							com.db4o.YapArrayN yan = new com.db4o.YapArrayN(a_stream, ydt, false);
							return yan;
						}
						com.db4o.YapArray ya = new com.db4o.YapArray(a_stream, ydt, false);
						return ya;
					}
					return yc.PrepareComparison(a_constraint);
				}
				return null;
			}
			return _yapClass.PrepareComparison(a_constraint);
		}

		private void Read()
		{
			if (_include)
			{
				if (_bytes == null)
				{
					if (_key > 0)
					{
						SetBytes(GetStream().ReadReaderByID(GetTransaction(), _key));
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

		private com.db4o.QCandidate ReadSubCandidate(com.db4o.QCandidates candidateCollection
			)
		{
			Read();
			if (_bytes != null)
			{
				com.db4o.QCandidate subCandidate = null;
				int offset = _bytes._offset;
				try
				{
					subCandidate = _yapField.i_handler.ReadSubCandidate(_marshallerFamily, _bytes, candidateCollection
						, false);
				}
				catch (System.Exception e)
				{
					return null;
				}
				_bytes._offset = offset;
				if (subCandidate != null)
				{
					subCandidate._root = GetRoot();
					return subCandidate;
				}
			}
			return null;
		}

		private void ReadThis(bool a_activate)
		{
			Read();
			com.db4o.Transaction trans = GetTransaction();
			if (trans != null)
			{
				_member = trans.Stream().GetByID1(trans, _key);
				if (_member != null && (a_activate || _member is com.db4o.config.Compare))
				{
					trans.Stream().Activate1(trans, _member);
					CheckInstanceOfCompare();
				}
			}
		}

		internal virtual com.db4o.YapClass ReadYapClass()
		{
			if (_yapClass == null)
			{
				Read();
				if (_bytes != null)
				{
					_bytes._offset = 0;
					com.db4o.YapStream stream = GetStream();
					com.db4o.inside.marshall.ObjectHeader objectHeader = new com.db4o.inside.marshall.ObjectHeader
						(stream, _bytes);
					_yapClass = objectHeader._yapClass;
					if (_yapClass != null)
					{
						if (stream.i_handlers.ICLASS_COMPARE.IsAssignableFrom(_yapClass.ClassReflector())
							)
						{
							ReadThis(false);
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
				str += "\n   YapClass " + _yapClass.GetName();
			}
			if (_yapField != null)
			{
				str += "\n   YapField " + _yapField.GetName();
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

		internal virtual void UseField(com.db4o.QField a_field)
		{
			Read();
			if (_bytes == null)
			{
				_yapField = null;
				return;
			}
			ReadYapClass();
			_member = null;
			if (a_field == null)
			{
				_yapField = null;
				return;
			}
			if (_yapClass == null)
			{
				_yapField = null;
				return;
			}
			_yapField = a_field.GetYapField(_yapClass);
			_marshallerFamily = _yapClass.FindOffset(_bytes, _yapField);
			if (_yapField == null || _marshallerFamily == null)
			{
				if (_yapClass.HoldsAnyClass())
				{
					_yapField = null;
				}
				else
				{
					_yapField = new com.db4o.YapFieldNull();
				}
			}
		}

		internal virtual object Value()
		{
			return Value(false);
		}

		internal virtual object Value(bool a_activate)
		{
			if (_member == null)
			{
				if (_yapField == null)
				{
					ReadThis(a_activate);
				}
				else
				{
					int offset = _bytes._offset;
					try
					{
						_member = _yapField.ReadQuery(GetTransaction(), _marshallerFamily, _bytes);
					}
					catch (com.db4o.CorruptionException ce)
					{
						_member = null;
					}
					_bytes._offset = offset;
					CheckInstanceOfCompare();
				}
			}
			return _member;
		}

		internal virtual void SetBytes(com.db4o.YapReader bytes)
		{
			_bytes = bytes;
		}
	}
}
