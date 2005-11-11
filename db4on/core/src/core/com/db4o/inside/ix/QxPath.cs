namespace com.db4o.inside.ix
{
	/// <summary>Query Index Path</summary>
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

		internal virtual void buildPaths()
		{
			int id = _constraint.identityID();
			if (id > 0)
			{
				processChildCandidates(new com.db4o.TreeInt(id));
				return;
			}
			bool isLeaf = true;
			com.db4o.foundation.Iterator4 i = _constraint.iterateChildren();
			while (i.hasNext())
			{
				isLeaf = false;
				com.db4o.QCon childConstraint = (com.db4o.QCon)i.next();
				if (childConstraint.canLoadByIndex())
				{
					new com.db4o.inside.ix.QxPath(_processor, this, childConstraint, _depth + 1).buildPaths
						();
				}
			}
			if (!isLeaf)
			{
				return;
			}
			if (!_constraint.canLoadByIndex())
			{
				return;
			}
			if (!_constraint.canBeIndexLeaf())
			{
				return;
			}
			_indexTraversers = new com.db4o.inside.ix.IxTraverser[] { new com.db4o.inside.ix.IxTraverser
				() };
			i_key = ((com.db4o.QConObject)_constraint).findBoundsQuery(_indexTraversers[0]);
			if (i_key < 0)
			{
				return;
			}
			if (i_key > 0)
			{
				_ixPaths = new com.db4o.inside.ix.NIxPaths[] { _indexTraversers[0].convert() };
				expectNixCount(_ixPaths[0], i_key);
			}
			_processor.addPath(this);
		}

		private void expectNixCount(com.db4o.inside.ix.NIxPaths ixPaths, int count)
		{
		}

		internal virtual void load()
		{
			loadFromIndexTraversers();
			loadFromNixPaths();
			if (_parent == null)
			{
				return;
			}
			if (_processor.exceedsLimit(com.db4o.Tree.size(_nCandidates), _depth))
			{
				return;
			}
			com.db4o.inside.ix.QxPath parentPath = new com.db4o.inside.ix.QxPath(_processor, 
				_parent._parent, _parent._constraint, _depth - 1);
			parentPath.processChildCandidates(_nCandidates);
		}

		private void loadFromIndexTraversers()
		{
			if (_indexTraversers == null)
			{
				return;
			}
			for (int i = 0; i < _indexTraversers.Length; i++)
			{
				_indexTraversers[i].visitAll(new _AnonymousInnerClass130(this));
			}
		}

		private sealed class _AnonymousInnerClass130 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass130(QxPath _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object a_object)
			{
				int id = ((int)a_object);
				if (this._enclosing._candidates == null)
				{
					this._enclosing._candidates = new com.db4o.TreeInt(id);
				}
				else
				{
					this._enclosing._candidates = this._enclosing._candidates.add(new com.db4o.TreeInt
						(id));
				}
			}

			private readonly QxPath _enclosing;
		}

		private void loadFromNixPaths()
		{
			if (_ixPaths == null)
			{
				return;
			}
			for (int i = 0; i < _ixPaths.Length; i++)
			{
				if (_ixPaths[i] != null)
				{
					_ixPaths[i].traverse(new _AnonymousInnerClass152(this));
				}
			}
			compareLoadedNixPaths();
		}

		private sealed class _AnonymousInnerClass152 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass152(QxPath _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object a_object)
			{
				int id = ((int)a_object);
				if (this._enclosing._nCandidates == null)
				{
					this._enclosing._nCandidates = new com.db4o.TreeInt(id);
				}
				else
				{
					this._enclosing._nCandidates = this._enclosing._nCandidates.add(new com.db4o.TreeInt
						(id));
				}
			}

			private readonly QxPath _enclosing;
		}

		private void compareLoadedNixPaths()
		{
			return;
			if (com.db4o.Tree.size(_candidates) != com.db4o.Tree.size(_nCandidates))
			{
				j4o.lang.JavaSystem.err.println("Different index tree size");
				j4o.lang.JavaSystem.err.println("" + com.db4o.Tree.size(_candidates) + ", " + com.db4o.Tree
					.size(_nCandidates));
				return;
			}
			com.db4o.Tree.traverse(_nCandidates, new _AnonymousInnerClass180(this));
		}

		private sealed class _AnonymousInnerClass180 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass180(QxPath _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object a_object)
			{
				if (this._enclosing._candidates.find((com.db4o.Tree)a_object) == null)
				{
					j4o.lang.JavaSystem.err.println("Element not in old tree");
					j4o.lang.JavaSystem.err.println(a_object);
				}
			}

			private readonly QxPath _enclosing;
		}

		internal virtual void processChildCandidates(com.db4o.Tree candidates)
		{
			if (candidates == null)
			{
				_processor.addPath(this);
				return;
			}
			if (_parent == null)
			{
				_candidates = candidates;
				_nCandidates = candidates;
				_processor.addPath(this);
				return;
			}
			_indexTraversers = new com.db4o.inside.ix.IxTraverser[candidates.size()];
			_ixPaths = new com.db4o.inside.ix.NIxPaths[candidates.size()];
			int[] ix = new int[] { 0 };
			bool[] err = new bool[] { false };
			candidates.traverse(new _AnonymousInnerClass214(this, ix, err));
			if (err[0])
			{
				return;
			}
			_processor.addPath(this);
		}

		private sealed class _AnonymousInnerClass214 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass214(QxPath _enclosing, int[] ix, bool[] err)
			{
				this._enclosing = _enclosing;
				this.ix = ix;
				this.err = err;
			}

			public void visit(object a_object)
			{
				int idx = ix[0]++;
				this._enclosing._indexTraversers[idx] = new com.db4o.inside.ix.IxTraverser();
				int count = this._enclosing._indexTraversers[idx].findBoundsQuery(this._enclosing
					._constraint, ((com.db4o.TreeInt)a_object).i_key);
				if (count >= 0)
				{
					this._enclosing.i_key += count;
				}
				else
				{
					err[0] = true;
				}
				if (count > 0)
				{
					this._enclosing._ixPaths[idx] = this._enclosing._indexTraversers[idx].convert();
					this._enclosing.expectNixCount(this._enclosing._ixPaths[idx], count);
				}
			}

			private readonly QxPath _enclosing;

			private readonly int[] ix;

			private readonly bool[] err;
		}

		public virtual bool isTopLevelComplete()
		{
			if (_parent == null)
			{
				return true;
			}
			return false;
		}

		internal virtual bool onSameFieldAs(com.db4o.inside.ix.QxPath other)
		{
			return _constraint.onSameFieldAs(other._constraint);
		}

		internal virtual com.db4o.Tree toQCandidates(com.db4o.QCandidates candidates)
		{
			return com.db4o.TreeInt.toQCandidate((com.db4o.TreeInt)_nCandidates, candidates);
			return com.db4o.TreeInt.toQCandidate((com.db4o.TreeInt)_candidates, candidates);
		}

		internal virtual void mergeForSameField(com.db4o.inside.ix.QxPath other)
		{
			if (other._ixPaths == null)
			{
				return;
			}
			int oldCount = _ixPaths[0].count();
			for (int i = 0; i < other._ixPaths.Length; i++)
			{
				if (other._ixPaths[i] != null)
				{
					other._ixPaths[i]._paths.traverse(new _AnonymousInnerClass278(this));
				}
			}
			int newCount = _ixPaths[0].count();
			i_key += newCount - oldCount;
		}

		private sealed class _AnonymousInnerClass278 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass278(QxPath _enclosing)
			{
				this._enclosing = _enclosing;
			}

			public void visit(object a_object)
			{
				this._enclosing._ixPaths[0].add((com.db4o.inside.ix.NIxPath)a_object);
			}

			private readonly QxPath _enclosing;
		}
	}
}
