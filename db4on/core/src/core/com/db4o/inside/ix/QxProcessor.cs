namespace com.db4o.inside.ix
{
	/// <exclude></exclude>
	public class QxProcessor
	{
		private com.db4o.Tree _paths;

		private com.db4o.inside.ix.QxPath _best;

		private int _limit;

		internal virtual void AddPath(com.db4o.inside.ix.QxPath newPath)
		{
			if (_paths == null)
			{
				_paths = newPath;
				return;
			}
			com.db4o.inside.ix.QxPath[] same = new com.db4o.inside.ix.QxPath[] { null };
			_paths.Traverse(new _AnonymousInnerClass32(this, newPath, same));
			if (same[0] != null)
			{
				_paths = _paths.RemoveNode(same[0]);
				newPath.MergeForSameField(same[0]);
			}
			_paths = com.db4o.Tree.Add(_paths, newPath);
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

			public void Visit(object a_object)
			{
				com.db4o.inside.ix.QxPath path = (com.db4o.inside.ix.QxPath)a_object;
				if (path._parent == newPath._parent)
				{
					if (path.OnSameFieldAs(newPath))
					{
						same[0] = path;
					}
				}
			}

			private readonly QxProcessor _enclosing;

			private readonly com.db4o.inside.ix.QxPath newPath;

			private readonly com.db4o.inside.ix.QxPath[] same;
		}

		private void BuildPaths(com.db4o.QCandidates candidates)
		{
			com.db4o.foundation.Iterator4 i = candidates.IterateConstraints();
			while (i.MoveNext())
			{
				com.db4o.QCon qCon = (com.db4o.QCon)i.Current();
				qCon.SetCandidates(candidates);
				if (!qCon.HasOrJoins())
				{
					new com.db4o.inside.ix.QxPath(this, null, qCon, 0).BuildPaths();
				}
			}
		}

		public virtual bool Run(com.db4o.QCandidates candidates, int limit)
		{
			_limit = limit;
			BuildPaths(candidates);
			if (_paths == null)
			{
				return false;
			}
			return ChooseBestPath();
		}

		private bool ChooseBestPath()
		{
			while (_paths != null)
			{
				com.db4o.inside.ix.QxPath path = (com.db4o.inside.ix.QxPath)_paths.First();
				_paths = _paths.RemoveFirst();
				if (path.IsTopLevelComplete())
				{
					_best = path;
					return true;
				}
				path.Load();
			}
			return false;
		}

		public virtual com.db4o.Tree ToQCandidates(com.db4o.QCandidates candidates)
		{
			return _best.ToQCandidates(candidates);
		}

		internal virtual bool ExceedsLimit(int count, int depth)
		{
			int limit = _limit;
			for (int i = 1; i < depth; i++)
			{
				limit = limit / 10;
			}
			return count > limit;
		}
	}
}
