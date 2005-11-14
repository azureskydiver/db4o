namespace com.db4o.inside.ix
{
	/// <exclude></exclude>
	public class QxProcessor
	{
		private com.db4o.Tree _paths;

		private com.db4o.inside.ix.QxPath _best;

		private int _limit;

		internal virtual void addPath(com.db4o.inside.ix.QxPath newPath)
		{
			if (_paths == null)
			{
				_paths = newPath;
				return;
			}
			com.db4o.inside.ix.QxPath[] same = new com.db4o.inside.ix.QxPath[] { null };
			_paths.traverse(new _AnonymousInnerClass32(this, newPath, same));
			if (same[0] != null)
			{
				_paths = _paths.removeNode(same[0]);
				newPath.mergeForSameField(same[0]);
			}
			_paths = com.db4o.Tree.add(_paths, newPath);
		}

		private sealed class _AnonymousInnerClass32 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass32(QxProcessor _enclosing, com.db4o.inside.ix.QxPath newPath
				, com.db4o.inside.ix.QxPath[] same)
			{
				this._enclosing = _enclosing;
				this.newPath = newPath;
				this.same = same;
			}

			public void visit(object a_object)
			{
				com.db4o.inside.ix.QxPath path = (com.db4o.inside.ix.QxPath)a_object;
				if (path._parent == newPath._parent)
				{
					if (path.onSameFieldAs(newPath))
					{
						same[0] = path;
					}
				}
			}

			private readonly QxProcessor _enclosing;

			private readonly com.db4o.inside.ix.QxPath newPath;

			private readonly com.db4o.inside.ix.QxPath[] same;
		}

		private void buildPaths(com.db4o.QCandidates candidates)
		{
			com.db4o.foundation.Iterator4 i = candidates.iterateConstraints();
			while (i.hasNext())
			{
				com.db4o.QCon qCon = (com.db4o.QCon)i.next();
				qCon.setCandidates(candidates);
				if (!qCon.hasOrJoins())
				{
					new com.db4o.inside.ix.QxPath(this, null, qCon, 0).buildPaths();
				}
			}
		}

		public virtual bool run(com.db4o.QCandidates candidates, int limit)
		{
			_limit = limit;
			buildPaths(candidates);
			if (_paths == null)
			{
				return false;
			}
			return chooseBestPath();
		}

		private bool chooseBestPath()
		{
			while (_paths != null)
			{
				com.db4o.inside.ix.QxPath path = (com.db4o.inside.ix.QxPath)_paths.first();
				_paths = _paths.removeFirst();
				if (path.isTopLevelComplete())
				{
					_best = path;
					return true;
				}
				path.load();
			}
			return false;
		}

		public virtual com.db4o.Tree toQCandidates(com.db4o.QCandidates candidates)
		{
			return _best.toQCandidates(candidates);
		}

		internal virtual bool exceedsLimit(int count, int depth)
		{
			int limit = _limit;
			for (int i = 0; i < depth; i++)
			{
				limit = limit / 10;
			}
			return count > limit;
		}
	}
}
