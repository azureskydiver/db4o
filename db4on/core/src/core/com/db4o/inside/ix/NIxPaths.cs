namespace com.db4o.inside.ix
{
	/// <summary>
	/// A note on the logic of #count() and #traverse():
	/// Within the visitor we are always looking at two NIxPath: last[0] and current.
	/// </summary>
	/// <remarks>
	/// A note on the logic of #count() and #traverse():
	/// Within the visitor we are always looking at two NIxPath: last[0] and current.
	/// Each run of the visitor takes care of all nodes:
	/// - smaller than last[0] for the first run only
	/// - equal to last[0]
	/// - between last[0] and current
	/// - but *NOT* equal to current, which is handled in the next run.
	/// </remarks>
	/// <exclude></exclude>
	public class NIxPaths
	{
		internal com.db4o.Tree _paths;

		internal virtual void add(com.db4o.inside.ix.NIxPath path)
		{
			path._size = 1;
			path._preceding = null;
			path._subsequent = null;
			_paths = com.db4o.Tree.add(_paths, path);
		}

		internal virtual void removeRedundancies()
		{
			com.db4o.foundation.Collection4 add = new com.db4o.foundation.Collection4();
			bool[] stop = new bool[] { false };
			_paths.traverse(new _AnonymousInnerClass41(this, stop, add));
			_paths = null;
			com.db4o.foundation.Iterator4 i = add.iterator();
			while (i.hasNext())
			{
				this.add((com.db4o.inside.ix.NIxPath)i.next());
			}
		}

		private sealed class _AnonymousInnerClass41 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass41(NIxPaths _enclosing, bool[] stop, com.db4o.foundation.Collection4
				 add)
			{
				this._enclosing = _enclosing;
				this.stop = stop;
				this.add = add;
			}

			public void visit(object a_object)
			{
				if (!stop[0])
				{
					com.db4o.inside.ix.NIxPath path = (com.db4o.inside.ix.NIxPath)a_object;
					if (!path._takePreceding)
					{
						add.clear();
					}
					add.add(path);
					if (!path._takeSubsequent)
					{
						stop[0] = true;
					}
				}
			}

			private readonly NIxPaths _enclosing;

			private readonly bool[] stop;

			private readonly com.db4o.foundation.Collection4 add;
		}

		internal virtual int count()
		{
			com.db4o.inside.ix.NIxPath[] last = new com.db4o.inside.ix.NIxPath[] { null };
			int[] sum = new int[] { 0 };
			_paths.traverse(new _AnonymousInnerClass66(this, last, sum));
			if (last[0]._takeMatches)
			{
				sum[0] += countAllMatching(last[0]._head);
			}
			if (last[0]._takeSubsequent)
			{
				sum[0] += countAllSubsequent(last[0]._head);
			}
			return sum[0];
		}

		private sealed class _AnonymousInnerClass66 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass66(NIxPaths _enclosing, com.db4o.inside.ix.NIxPath[] last
				, int[] sum)
			{
				this._enclosing = _enclosing;
				this.last = last;
				this.sum = sum;
			}

			public void visit(object a_object)
			{
				com.db4o.inside.ix.NIxPath current = (com.db4o.inside.ix.NIxPath)a_object;
				if (last[0] == null)
				{
					if (current._takePreceding)
					{
						sum[0] += this._enclosing.countAllPreceding(current._head);
					}
				}
				else
				{
					if ((last[0]._takeSubsequent || last[0]._takeMatches) && (current._takePreceding 
						|| current._takeMatches))
					{
						sum[0] += this._enclosing.countSpan(current, last[0], current._head, last[0]._head
							, current._head._next, last[0]._head._next, 0);
					}
					else
					{
						if (last[0]._takeMatches)
						{
							sum[0] += this._enclosing.countAllMatching(last[0]._head);
						}
					}
				}
				last[0] = current;
			}

			private readonly NIxPaths _enclosing;

			private readonly com.db4o.inside.ix.NIxPath[] last;

			private readonly int[] sum;
		}

		private int countAllPreceding(com.db4o.inside.ix.NIxPathNode head)
		{
			int count = 0;
			while (head != null)
			{
				count += head.countPreceding();
				head = head._next;
			}
			return count;
		}

		private int countAllMatching(com.db4o.inside.ix.NIxPathNode head)
		{
			int count = 0;
			while (head != null)
			{
				count += head.countMatching();
				head = head._next;
			}
			return count;
		}

		private int countAllSubsequent(com.db4o.inside.ix.NIxPathNode head)
		{
			int count = 0;
			while (head != null)
			{
				count += head.countSubsequent();
				head = head._next;
			}
			return count;
		}

		/// <summary>see documentation to this class for behaviour *</summary>
		private int countSpan(com.db4o.inside.ix.NIxPath greatPath, com.db4o.inside.ix.NIxPath
			 smallPath, com.db4o.inside.ix.NIxPathNode a_previousGreat, com.db4o.inside.ix.NIxPathNode
			 a_previousSmall, com.db4o.inside.ix.NIxPathNode a_great, com.db4o.inside.ix.NIxPathNode
			 a_small, int sum)
		{
			sum += a_previousGreat.countSpan(greatPath, smallPath, a_previousSmall);
			if (a_great != null && a_great.carriesTheSame(a_small))
			{
				return countSpan(greatPath, smallPath, a_great, a_small, a_great._next, a_small._next
					, sum);
			}
			return sum + countGreater(a_small, 0) + countSmaller(a_great, 0);
		}

		private int countSmaller(com.db4o.inside.ix.NIxPathNode a_path, int a_sum)
		{
			if (a_path == null)
			{
				return a_sum;
			}
			if (a_path._next == null)
			{
				return a_sum + countPreceding(a_path);
			}
			if (a_path._next._tree == a_path._tree._subsequent)
			{
				a_sum += countPreceding(a_path);
			}
			else
			{
				a_sum += a_path.countMatching();
			}
			return countSmaller(a_path._next, a_sum);
		}

		private int countGreater(com.db4o.inside.ix.NIxPathNode a_path, int a_sum)
		{
			if (a_path == null)
			{
				return a_sum;
			}
			if (a_path._next == null)
			{
				return a_sum + countSubsequent(a_path);
			}
			if (a_path._next._tree == a_path._tree._preceding)
			{
				a_sum += countSubsequent(a_path);
			}
			else
			{
				a_sum += a_path.countMatching();
			}
			return countGreater(a_path._next, a_sum);
		}

		private int countPreceding(com.db4o.inside.ix.NIxPathNode a_path)
		{
			return com.db4o.Tree.size(a_path._tree._preceding) + a_path.countMatching();
		}

		private int countSubsequent(com.db4o.inside.ix.NIxPathNode a_path)
		{
			return com.db4o.Tree.size(a_path._tree._subsequent) + a_path.countMatching();
		}

		internal virtual void traverse(com.db4o.foundation.Visitor4 visitor)
		{
			com.db4o.inside.ix.NIxPath[] last = new com.db4o.inside.ix.NIxPath[] { null };
			com.db4o.foundation.Visitor4Dispatch dispatcher = new com.db4o.foundation.Visitor4Dispatch
				(visitor);
			_paths.traverse(new _AnonymousInnerClass173(this, last, dispatcher));
			if (last[0]._takeMatches)
			{
				traverseAllMatching(last[0]._head, dispatcher);
			}
			if (last[0]._takeSubsequent)
			{
				traverseAllSubsequent(last[0]._head, dispatcher);
			}
		}

		private sealed class _AnonymousInnerClass173 : com.db4o.foundation.Visitor4
		{
			public _AnonymousInnerClass173(NIxPaths _enclosing, com.db4o.inside.ix.NIxPath[] 
				last, com.db4o.foundation.Visitor4Dispatch dispatcher)
			{
				this._enclosing = _enclosing;
				this.last = last;
				this.dispatcher = dispatcher;
			}

			public void visit(object a_object)
			{
				com.db4o.inside.ix.NIxPath current = (com.db4o.inside.ix.NIxPath)a_object;
				if (last[0] == null)
				{
					if (current._takePreceding)
					{
						this._enclosing.traverseAllPreceding(current._head, dispatcher);
					}
				}
				else
				{
					if ((last[0]._takeSubsequent || last[0]._takeMatches) && (current._takePreceding 
						|| current._takeMatches))
					{
						this._enclosing.traverseSpan(current, last[0], current._head, last[0]._head, current
							._head._next, last[0]._head._next, dispatcher);
					}
					else
					{
						if (last[0]._takeMatches)
						{
							this._enclosing.traverseAllMatching(last[0]._head, dispatcher);
						}
					}
				}
				last[0] = current;
			}

			private readonly NIxPaths _enclosing;

			private readonly com.db4o.inside.ix.NIxPath[] last;

			private readonly com.db4o.foundation.Visitor4Dispatch dispatcher;
		}

		private void traverseAllPreceding(com.db4o.inside.ix.NIxPathNode head, com.db4o.foundation.Visitor4Dispatch
			 dispatcher)
		{
			while (head != null)
			{
				head.traversePreceding(dispatcher);
				head = head._next;
			}
		}

		private void traverseAllMatching(com.db4o.inside.ix.NIxPathNode head, com.db4o.foundation.Visitor4Dispatch
			 dispatcher)
		{
			while (head != null)
			{
				head.traverseMatching(dispatcher);
				head = head._next;
			}
		}

		private void traverseAllSubsequent(com.db4o.inside.ix.NIxPathNode head, com.db4o.foundation.Visitor4Dispatch
			 dispatcher)
		{
			while (head != null)
			{
				head.traverseSubsequent(dispatcher);
				head = head._next;
			}
		}

		/// <summary>see documentation to this class for behaviour *</summary>
		private void traverseSpan(com.db4o.inside.ix.NIxPath greatPath, com.db4o.inside.ix.NIxPath
			 smallPath, com.db4o.inside.ix.NIxPathNode a_previousGreat, com.db4o.inside.ix.NIxPathNode
			 a_previousSmall, com.db4o.inside.ix.NIxPathNode a_great, com.db4o.inside.ix.NIxPathNode
			 a_small, com.db4o.foundation.Visitor4Dispatch dispatcher)
		{
			a_previousGreat.traverseSpan(greatPath, smallPath, a_previousSmall, dispatcher);
			if (a_great != null && a_great.carriesTheSame(a_small))
			{
				traverseSpan(greatPath, smallPath, a_great, a_small, a_great._next, a_small._next
					, dispatcher);
				return;
			}
			traverseGreater(a_small, dispatcher);
			traverseSmaller(a_great, dispatcher);
		}

		private void traverseSmaller(com.db4o.inside.ix.NIxPathNode a_path, com.db4o.foundation.Visitor4Dispatch
			 dispatcher)
		{
			if (a_path == null)
			{
				return;
			}
			if (a_path._next == null)
			{
				traversePreceding(a_path, dispatcher);
				return;
			}
			if (a_path._next._tree == a_path._tree._subsequent)
			{
				traversePreceding(a_path, dispatcher);
			}
			else
			{
				a_path.traverseMatching(dispatcher);
			}
			traverseSmaller(a_path._next, dispatcher);
		}

		private void traverseGreater(com.db4o.inside.ix.NIxPathNode a_path, com.db4o.foundation.Visitor4Dispatch
			 dispatcher)
		{
			if (a_path == null)
			{
				return;
			}
			if (a_path._next == null)
			{
				traverseSubsequent(a_path, dispatcher);
				return;
			}
			if (a_path._next._tree == a_path._tree._preceding)
			{
				traverseSubsequent(a_path, dispatcher);
			}
			else
			{
				a_path.traverseMatching(dispatcher);
			}
			traverseGreater(a_path._next, dispatcher);
		}

		private void traversePreceding(com.db4o.inside.ix.NIxPathNode a_path, com.db4o.foundation.Visitor4Dispatch
			 dispatcher)
		{
			a_path.traverseMatching(dispatcher);
			com.db4o.Tree.traverse(a_path._tree._preceding, dispatcher);
		}

		private void traverseSubsequent(com.db4o.inside.ix.NIxPathNode a_path, com.db4o.foundation.Visitor4Dispatch
			 dispatcher)
		{
			a_path.traverseMatching(dispatcher);
			com.db4o.Tree.traverse(a_path._tree._subsequent, dispatcher);
		}
	}
}
