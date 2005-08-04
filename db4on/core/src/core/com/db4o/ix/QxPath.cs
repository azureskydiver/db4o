namespace com.db4o.ix
{
	/// <summary>Query Index Path</summary>
	internal class QxPath : com.db4o.TreeInt
	{
		private readonly com.db4o.ix.QxProcessor _processor;

		private com.db4o.QCon _constraint;

		private readonly com.db4o.ix.QxPath _parent;

		private com.db4o.IxTraverser[] _indexTraversers;

		private com.db4o.Tree _candidates;

		internal QxPath(com.db4o.ix.QxProcessor processor, com.db4o.ix.QxPath parent, com.db4o.QCon
			 constraint) : base(0)
		{
			_processor = processor;
			_parent = parent;
			_constraint = constraint;
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
					new com.db4o.ix.QxPath(_processor, this, childConstraint).buildPaths();
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
			_indexTraversers = new com.db4o.IxTraverser[] { new com.db4o.IxTraverser() };
			i_key = ((com.db4o.QConObject)_constraint).findBoundsQuery(_indexTraversers[0]);
			if (i_key >= 0)
			{
				_processor.addPath(this);
			}
		}

		internal virtual void load()
		{
			if (_indexTraversers != null)
			{
				for (int i = 0; i < _indexTraversers.Length; i++)
				{
					_indexTraversers[i].visitAll(new _AnonymousInnerClass70(this));
				}
			}
			if (_parent == null)
			{
				return;
			}
			com.db4o.ix.QxPath parentPath = new com.db4o.ix.QxPath(_processor, _parent._parent
				, _parent._constraint);
			parentPath.processChildCandidates(_candidates);
			return;
		}

		private sealed class _AnonymousInnerClass70 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass70(QxPath _enclosing)
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
				_processor.addPath(this);
				return;
			}
			_indexTraversers = new com.db4o.IxTraverser[candidates.size()];
			int[] ix = new int[] { 0 };
			bool[] err = new bool[] { false };
			candidates.traverse(new _AnonymousInnerClass109(this, ix, err));
			if (err[0])
			{
				return;
			}
			_processor.addPath(this);
		}

		private sealed class _AnonymousInnerClass109 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass109(QxPath _enclosing, int[] ix, bool[] err)
			{
				this._enclosing = _enclosing;
				this.ix = ix;
				this.err = err;
			}

			public void visit(object a_object)
			{
				this._enclosing._indexTraversers[ix[0]] = new com.db4o.IxTraverser();
				int count = this._enclosing._indexTraversers[ix[0]++].findBoundsQuery(this._enclosing
					._constraint, ((com.db4o.TreeInt)a_object).i_key);
				if (count >= 0)
				{
					this._enclosing.i_key += count;
				}
				else
				{
					err[0] = true;
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

		internal virtual com.db4o.Tree toQCandidates(com.db4o.QCandidates candidates)
		{
			return com.db4o.TreeInt.toQCandidate((com.db4o.TreeInt)_candidates, candidates);
		}
	}
}
