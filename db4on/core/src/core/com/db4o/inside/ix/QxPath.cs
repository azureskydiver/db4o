namespace com.db4o.inside.ix
{
	/// <summary>
	/// Query Index Path
	/// Stuff to know:
	/// Derived from TreeInt to be able to sort.
	/// </summary>
	/// <remarks>
	/// Query Index Path
	/// Stuff to know:
	/// Derived from TreeInt to be able to sort.
	/// _key in TreeInt == count found
	/// </remarks>
	internal class QxPath : com.db4o.TreeInt
	{
		private readonly com.db4o.inside.ix.QxProcessor _processor;

		private com.db4o.QCon _constraint;

		internal readonly com.db4o.inside.ix.QxPath _parent;

		private com.db4o.inside.ix.IxTraverser[] _indexTraversers;

		private com.db4o.inside.ix.NIxPaths[] _ixPaths;

		private com.db4o.Tree _nCandidates;

		private com.db4o.Tree _candidates;

		private readonly int _depth;

		internal QxPath(com.db4o.inside.ix.QxProcessor processor, com.db4o.inside.ix.QxPath
			 parent, com.db4o.QCon constraint, int depth) : base(0)
		{
			_processor = processor;
			_parent = parent;
			_constraint = constraint;
			_depth = depth;
		}

		public override object ShallowClone()
		{
			com.db4o.inside.ix.QxPath qpath = new com.db4o.inside.ix.QxPath(_processor, _parent
				, _constraint, _depth);
			qpath._indexTraversers = _indexTraversers;
			qpath._ixPaths = _ixPaths;
			qpath._nCandidates = _nCandidates;
			qpath._candidates = _candidates;
			return base.ShallowCloneInternal(qpath);
		}

		internal virtual void BuildPaths()
		{
			int id = _constraint.IdentityID();
			if (id > 0)
			{
				ProcessChildCandidates(new com.db4o.TreeInt(id));
				return;
			}
			bool isLeaf = true;
			com.db4o.foundation.Iterator4 i = _constraint.IterateChildren();
			while (i.MoveNext())
			{
				isLeaf = false;
				com.db4o.QCon childConstraint = (com.db4o.QCon)i.Current();
				if (childConstraint.CanLoadByIndex())
				{
					new com.db4o.inside.ix.QxPath(_processor, this, childConstraint, _depth + 1).BuildPaths
						();
				}
			}
			if (!isLeaf)
			{
				return;
			}
			if (!_constraint.CanLoadByIndex())
			{
				return;
			}
			if (!_constraint.CanBeIndexLeaf())
			{
				return;
			}
			_indexTraversers = new com.db4o.inside.ix.IxTraverser[] { new com.db4o.inside.ix.IxTraverser
				() };
			_key = ((com.db4o.QConObject)_constraint).FindBoundsQuery(_indexTraversers[0]);
			if (_key < 0)
			{
				return;
			}
			if (_key > 0)
			{
				_ixPaths = new com.db4o.inside.ix.NIxPaths[] { _indexTraversers[0].Convert() };
				ExpectNixCount(_ixPaths[0], _key);
			}
			_processor.AddPath(this);
		}

		private void ExpectNixCount(com.db4o.inside.ix.NIxPaths ixPaths, int count)
		{
		}

		internal virtual void Load()
		{
			LoadFromNixPaths();
			if (_parent == null)
			{
				return;
			}
			if (_processor.ExceedsLimit(com.db4o.Tree.Size(_nCandidates), _depth))
			{
				return;
			}
			com.db4o.inside.ix.QxPath parentPath = new com.db4o.inside.ix.QxPath(_processor, 
				_parent._parent, _parent._constraint, _depth - 1);
			parentPath.ProcessChildCandidates(_nCandidates);
		}

		private void LoadFromIndexTraversers()
		{
			if (_indexTraversers == null)
			{
				return;
			}
			for (int i = 0; i < _indexTraversers.Length; i++)
			{
				_indexTraversers[i].VisitAll(new _AnonymousInnerClass148(this));
			}
		}

		private sealed class _AnonymousInnerClass148 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass148(QxPath _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				int id = ((int)a_object);
				if (this._enclosing._candidates == null)
				{
					this._enclosing._candidates = new com.db4o.TreeInt(id);
				}
				else
				{
					this._enclosing._candidates = this._enclosing._candidates.Add(new com.db4o.TreeInt
						(id));
				}
			}

			private readonly QxPath _enclosing;
		}

		private void LoadFromNixPaths()
		{
			if (_ixPaths == null)
			{
				return;
			}
			for (int i = 0; i < _ixPaths.Length; i++)
			{
				if (_ixPaths[i] != null)
				{
					_ixPaths[i].Traverse(new _AnonymousInnerClass170(this));
				}
			}
			CompareLoadedNixPaths();
		}

		private sealed class _AnonymousInnerClass170 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass170(QxPath _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				int id = ((int)a_object);
				if (this._enclosing._nCandidates == null)
				{
					this._enclosing._nCandidates = new com.db4o.TreeInt(id);
				}
				else
				{
					this._enclosing._nCandidates = this._enclosing._nCandidates.Add(new com.db4o.TreeInt
						(id));
				}
			}

			private readonly QxPath _enclosing;
		}

		private void CompareLoadedNixPaths()
		{
			return;
			if (com.db4o.Tree.Size(_candidates) != com.db4o.Tree.Size(_nCandidates))
			{
				j4o.lang.JavaSystem.err.Println("Different index tree size");
				j4o.lang.JavaSystem.err.Println("" + com.db4o.Tree.Size(_candidates) + ", " + com.db4o.Tree
					.Size(_nCandidates));
				return;
			}
			com.db4o.Tree.Traverse(_nCandidates, new _AnonymousInnerClass199(this));
		}

		private sealed class _AnonymousInnerClass199 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass199(QxPath _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				if (this._enclosing._candidates.Find((com.db4o.Tree)a_object) == null)
				{
					j4o.lang.JavaSystem.err.Println("Element not in old tree");
					j4o.lang.JavaSystem.err.Println(a_object);
				}
			}

			private readonly QxPath _enclosing;
		}

		internal virtual void ProcessChildCandidates(com.db4o.Tree candidates)
		{
			if (candidates == null)
			{
				_processor.AddPath(this);
				return;
			}
			if (_parent == null)
			{
				_candidates = candidates;
				_nCandidates = candidates;
				_processor.AddPath(this);
				return;
			}
			_indexTraversers = new com.db4o.inside.ix.IxTraverser[candidates.Size()];
			_ixPaths = new com.db4o.inside.ix.NIxPaths[candidates.Size()];
			int[] ix = new int[] { 0 };
			bool[] err = new bool[] { false };
			candidates.Traverse(new _AnonymousInnerClass232(this, ix, err));
			if (err[0])
			{
				return;
			}
			_processor.AddPath(this);
		}

		private sealed class _AnonymousInnerClass232 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass232(QxPath _enclosing, int[] ix, bool[] err)
			{
				this._enclosing = _enclosing;
				this.ix = ix;
				this.err = err;
			}

			public void Visit(object a_object)
			{
				int idx = ix[0]++;
				this._enclosing._indexTraversers[idx] = new com.db4o.inside.ix.IxTraverser();
				int count = this._enclosing._indexTraversers[idx].FindBoundsQuery(this._enclosing
					._constraint, ((com.db4o.TreeInt)a_object)._key);
				if (count >= 0)
				{
					this._enclosing._key += count;
				}
				else
				{
					err[0] = true;
				}
				if (count > 0)
				{
					this._enclosing._ixPaths[idx] = this._enclosing._indexTraversers[idx].Convert();
					this._enclosing.ExpectNixCount(this._enclosing._ixPaths[idx], count);
				}
			}

			private readonly QxPath _enclosing;

			private readonly int[] ix;

			private readonly bool[] err;
		}

		public virtual bool IsTopLevelComplete()
		{
			if (_parent == null)
			{
				return true;
			}
			return false;
		}

		internal virtual bool OnSameFieldAs(com.db4o.inside.ix.QxPath other)
		{
			return _constraint.OnSameFieldAs(other._constraint);
		}

		internal virtual com.db4o.Tree ToQCandidates(com.db4o.QCandidates candidates)
		{
			return com.db4o.TreeInt.ToQCandidate((com.db4o.TreeInt)_nCandidates, candidates);
			return com.db4o.TreeInt.ToQCandidate((com.db4o.TreeInt)_candidates, candidates);
		}

		internal virtual void MergeForSameField(com.db4o.inside.ix.QxPath other)
		{
			if (other._ixPaths == null)
			{
				return;
			}
			int oldCount = _ixPaths[0].Count();
			for (int i = 0; i < other._ixPaths.Length; i++)
			{
				if (other._ixPaths[i] != null)
				{
					other._ixPaths[i]._paths.Traverse(new _AnonymousInnerClass295(this));
				}
			}
			int newCount = _ixPaths[0].Count();
			_key += newCount - oldCount;
		}

		private sealed class _AnonymousInnerClass295 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass295(QxPath _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void Visit(object a_object)
			{
				this._enclosing._ixPaths[0].Add((com.db4o.inside.ix.NIxPath)a_object);
			}

			private readonly QxPath _enclosing;
		}
	}
}
