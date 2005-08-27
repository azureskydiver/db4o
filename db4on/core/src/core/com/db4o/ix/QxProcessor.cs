
namespace com.db4o.ix
{
	/// <exclude></exclude>
	public class QxProcessor
	{
		private com.db4o.Tree _paths;

		private com.db4o.ix.QxPath _best;

		internal virtual void addPath(com.db4o.ix.QxPath path)
		{
			_paths = com.db4o.Tree.add(_paths, path);
		}

		private void buildPaths(com.db4o.QCandidates candidates)
		{
			com.db4o.foundation.Iterator4 i = candidates.iterateConstraints();
			while (i.hasNext())
			{
				com.db4o.QCon qCon = (com.db4o.QCon)i.next();
				qCon.setCandidates(candidates);
				if (!qCon.hasJoins())
				{
					new com.db4o.ix.QxPath(this, null, qCon).buildPaths();
				}
			}
		}

		public virtual bool run(com.db4o.QCandidates candidates)
		{
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
				com.db4o.ix.QxPath path = (com.db4o.ix.QxPath)_paths.first();
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
	}
}
