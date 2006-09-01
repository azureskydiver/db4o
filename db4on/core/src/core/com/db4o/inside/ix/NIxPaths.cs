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

		internal virtual void Add(com.db4o.inside.ix.NIxPath path)
		{
			path._size = 1;
			path._preceding = null;
			path._subsequent = null;
			_paths = com.db4o.Tree.Add(_paths, path);
		}

		internal virtual void RemoveRedundancies()
		{
			com.db4o.foundation.Collection4 add = new com.db4o.foundation.Collection4();
			bool[] stop = new bool[] { false };
			_paths.Traverse(new _AnonymousInnerClass41(this, stop, add));
			_paths = null;
			com.db4o.foundation.Iterator4 i = add.Iterator();
			while (i.MoveNext())
			{
				this.Add((com.db4o.inside.ix.NIxPath)i.Current());
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

			public void Visit(object a_object)
			{
				if (!stop[0])
				{
					com.db4o.inside.ix.NIxPath path = (com.db4o.inside.ix.NIxPath)a_object;
					if (!path._takePreceding)
					{
						add.Clear();
					}
					add.Add(path);
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

		internal virtual int Count()
		{
			com.db4o.inside.ix.NIxPath[] last = new com.db4o.inside.ix.NIxPath[] { null };
			int[] sum = new int[] { 0 };
			_paths.Traverse(new _AnonymousInnerClass66(this, last, sum));
			if (last[0]._takeMatches)
			{
				sum[0] += CountAllMatching(last[0]._head);
			}
			if (last[0]._takeSubsequent)
			{
				sum[0] += CountAllSubsequent(last[0]._head);
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

			public void Visit(object a_object)
			{
				com.db4o.inside.ix.NIxPath current = (com.db4o.inside.ix.NIxPath)a_object;
				if (last[0] == null)
				{
					if (current._takePreceding)
					{
						sum[0] += this._enclosing.CountAllPreceding(current._head);
					}
				}
				else
				{
					if ((last[0]._takeSubsequent || last[0]._takeMatches) && (current._takePreceding 
						|| current._takeMatches))
					{
						sum[0] += this._enclosing.CountSpan(current, last[0], current._head, last[0]._head
							, current._head._next, last[0]._head._next, 0);
					}
					else
					{
						if (last[0]._takeMatches)
						{
							sum[0] += this._enclosing.CountAllMatching(last[0]._head);
						}
					}
				}
				last[0] = current;
			}

			private readonly NIxPaths _enclosing;

			private readonly com.db4o.inside.ix.NIxPath[] last;

			private readonly int[] sum;
		}

		private int CountAllPreceding(com.db4o.inside.ix.NIxPathNode head)
		{
			int count = 0;
			while (head != null)
			{
				count += head.CountPreceding();
				head = head._next;
			}
			return count;
		}

		private int CountAllMatching(com.db4o.inside.ix.NIxPathNode head)
		{
			int count = 0;
			while (head != null)
			{
				count += head.CountMatching();
				head = head._next;
			}
			return count;
		}

		private int CountAllSubsequent(com.db4o.inside.ix.NIxPathNode head)
		{
			int count = 0;
			while (head != null)
			{
				count += head.CountSubsequent();
				head = head._next;
			}
			return count;
		}

		/// <summary>see documentation to this class for behaviour *</summary>
		private int CountSpan(com.db4o.inside.ix.NIxPath greatPath, com.db4o.inside.ix.NIxPath
			 smallPath, com.db4o.inside.ix.NIxPathNode a_previousGreat, com.db4o.inside.ix.NIxPathNode
			 a_previousSmall, com.db4o.inside.ix.NIxPathNode a_great, com.db4o.inside.ix.NIxPathNode
			 a_small, int sum)
		{
			sum += a_previousGreat.CountSpan(greatPath, smallPath, a_previousSmall);
			if (a_great != null && a_great.CarriesTheSame(a_small))
			{
				return CountSpan(greatPath, smallPath, a_great, a_small, a_great._next, a_small._next
					, sum);
			}
			return sum + CountGreater(a_small, 0) + CountSmaller(a_great, 0);
		}

		private int CountSmaller(com.db4o.inside.ix.NIxPathNode a_path, int a_sum)
		{
			if (a_path == null)
			{
				return a_sum;
			}
			if (a_path._next == null)
			{
				return a_sum + CountPreceding(a_path);
			}
			if (a_path._next._tree == a_path._tree._subsequent)
			{
				a_sum += CountPreceding(a_path);
			}
			else
			{
				a_sum += a_path.CountMatching();
			}
			return CountSmaller(a_path._next, a_sum);
		}

		private int CountGreater(com.db4o.inside.ix.NIxPathNode a_path, int a_sum)
		{
			if (a_path == null)
			{
				return a_sum;
			}
			if (a_path._next == null)
			{
				return a_sum + CountSubsequent(a_path);
			}
			if (a_path._next._tree == a_path._tree._preceding)
			{
				a_sum += CountSubsequent(a_path);
			}
			else
			{
				a_sum += a_path.CountMatching();
			}
			return CountGreater(a_path._next, a_sum);
		}

		private int CountPreceding(com.db4o.inside.ix.NIxPathNode a_path)
		{
			return com.db4o.Tree.Size(a_path._tree._preceding) + a_path.CountMatching();
		}

		private int CountSubsequent(com.db4o.inside.ix.NIxPathNode a_path)
		{
			return com.db4o.Tree.Size(a_path._tree._subsequent) + a_path.CountMatching();
		}

		internal virtual void Traverse(com.db4o.foundation.Visitor4 visitor)
		{
			com.db4o.inside.ix.NIxPath[] last = new com.db4o.inside.ix.NIxPath[] { null };
			com.db4o.foundation.Visitor4Dispatch dispatcher = new com.db4o.foundation.Visitor4Dispatch
				(visitor);
			_paths.Traverse(new _AnonymousInnerClass173(this, last, dispatcher));
			if (last[0]._takeMatches)
			{
				TraverseAllMatching(last[0]._head, dispatcher);
			}
			if (last[0]._takeSubsequent)
			{
				TraverseAllSubsequent(last[0]._head, dispatcher);
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

			public void Visit(object a_object)
			{
				com.db4o.inside.ix.NIxPath current = (com.db4o.inside.ix.NIxPath)a_object;
				if (last[0] == null)
				{
					if (current._takePreceding)
					{
						this._enclosing.TraverseAllPreceding(current._head, dispatcher);
					}
				}
				else
				{
					if ((last[0]._takeSubsequent || last[0]._takeMatches) && (current._takePreceding 
						|| current._takeMatches))
					{
						this._enclosing.TraverseSpan(current, last[0], current._head, last[0]._head, current
							._head._next, last[0]._head._next, dispatcher);
					}
					else
					{
						if (last[0]._takeMatches)
						{
							this._enclosing.TraverseAllMatching(last[0]._head, dispatcher);
						}
					}
				}
				last[0] = current;
			}

			private readonly NIxPaths _enclosing;

			private readonly com.db4o.inside.ix.NIxPath[] last;

			private readonly com.db4o.foundation.Visitor4Dispatch dispatcher;
		}

		private void TraverseAllPreceding(com.db4o.inside.ix.NIxPathNode head, com.db4o.foundation.Visitor4Dispatch
			 dispatcher)
		{
			while (head != null)
			{
				head.TraversePreceding(dispatcher);
				head = head._next;
			}
		}

		private void TraverseAllMatching(com.db4o.inside.ix.NIxPathNode head, com.db4o.foundation.Visitor4Dispatch
			 dispatcher)
		{
			while (head != null)
			{
				head.TraverseMatching(dispatcher);
				head = head._next;
			}
		}

		private void TraverseAllSubsequent(com.db4o.inside.ix.NIxPathNode head, com.db4o.foundation.Visitor4Dispatch
			 dispatcher)
		{
			while (head != null)
			{
				head.TraverseSubsequent(dispatcher);
				head = head._next;
			}
		}

		/// <summary>see documentation to this class for behaviour *</summary>
		private void TraverseSpan(com.db4o.inside.ix.NIxPath greatPath, com.db4o.inside.ix.NIxPath
			 smallPath, com.db4o.inside.ix.NIxPathNode a_previousGreat, com.db4o.inside.ix.NIxPathNode
			 a_previousSmall, com.db4o.inside.ix.NIxPathNode a_great, com.db4o.inside.ix.NIxPathNode
			 a_small, com.db4o.foundation.Visitor4Dispatch dispatcher)
		{
			a_previousGreat.TraverseSpan(greatPath, smallPath, a_previousSmall, dispatcher);
			if (a_great != null && a_great.CarriesTheSame(a_small))
			{
				TraverseSpan(greatPath, smallPath, a_great, a_small, a_great._next, a_small._next
					, dispatcher);
				return;
			}
			TraverseGreater(a_small, dispatcher);
			TraverseSmaller(a_great, dispatcher);
		}

		private void TraverseSmaller(com.db4o.inside.ix.NIxPathNode a_path, com.db4o.foundation.Visitor4Dispatch
			 dispatcher)
		{
			if (a_path == null)
			{
				return;
			}
			if (a_path._next == null)
			{
				TraversePreceding(a_path, dispatcher);
				return;
			}
			if (a_path._next._tree == a_path._tree._subsequent)
			{
				TraversePreceding(a_path, dispatcher);
			}
			else
			{
				a_path.TraverseMatching(dispatcher);
			}
			TraverseSmaller(a_path._next, dispatcher);
		}

		private void TraverseGreater(com.db4o.inside.ix.NIxPathNode a_path, com.db4o.foundation.Visitor4Dispatch
			 dispatcher)
		{
			if (a_path == null)
			{
				return;
			}
			if (a_path._next == null)
			{
				TraverseSubsequent(a_path, dispatcher);
				return;
			}
			if (a_path._next._tree == a_path._tree._preceding)
			{
				TraverseSubsequent(a_path, dispatcher);
			}
			else
			{
				a_path.TraverseMatching(dispatcher);
			}
			TraverseGreater(a_path._next, dispatcher);
		}

		private void TraversePreceding(com.db4o.inside.ix.NIxPathNode a_path, com.db4o.foundation.Visitor4Dispatch
			 dispatcher)
		{
			a_path.TraverseMatching(dispatcher);
			com.db4o.Tree.Traverse(a_path._tree._preceding, dispatcher);
		}

		private void TraverseSubsequent(com.db4o.inside.ix.NIxPathNode a_path, com.db4o.foundation.Visitor4Dispatch
			 dispatcher)
		{
			a_path.TraverseMatching(dispatcher);
			com.db4o.Tree.Traverse(a_path._tree._subsequent, dispatcher);
		}
	}
}
