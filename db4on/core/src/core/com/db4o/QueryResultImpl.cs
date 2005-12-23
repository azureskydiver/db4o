namespace com.db4o
{
	/// <exclude></exclude>
	internal class QueryResultImpl : com.db4o.IntArrayList, com.db4o.foundation.Visitor4
		, com.db4o.inside.query.QueryResult
	{
		internal com.db4o.Tree i_candidates;

		internal bool i_checkDuplicates;

		internal readonly com.db4o.Transaction i_trans;

		internal QueryResultImpl(com.db4o.Transaction a_trans)
		{
			i_trans = a_trans;
		}

		internal QueryResultImpl(com.db4o.Transaction trans, int initialSize) : base(initialSize
			)
		{
			i_trans = trans;
		}

		internal object activate(object obj)
		{
			com.db4o.YapStream stream = i_trans.i_stream;
			stream.beginEndActivation();
			stream.activate2(i_trans, obj, stream.i_config.i_activationDepth);
			stream.beginEndActivation();
			return obj;
		}

		public virtual object get(int index)
		{
			lock (streamLock())
			{
				if (index < 0 || index >= size())
				{
					throw new System.IndexOutOfRangeException();
				}
				int id = i_content[index];
				com.db4o.YapStream stream = i_trans.i_stream;
				object obj = stream.getByID(id);
				if (obj == null)
				{
					return null;
				}
				return activate(obj);
			}
		}

		internal void checkDuplicates()
		{
			i_checkDuplicates = true;
		}

		public virtual long[] getIDs()
		{
			lock (streamLock())
			{
				return asLong();
			}
		}

		public override bool hasNext()
		{
			lock (streamLock())
			{
				return base.hasNext();
			}
		}

		public virtual object next()
		{
			lock (streamLock())
			{
				com.db4o.YapStream stream = i_trans.i_stream;
				stream.checkClosed();
				if (base.hasNext())
				{
					object ret = stream.getByID2(i_trans, nextInt());
					if (ret == null)
					{
						return next();
					}
					return activate(ret);
				}
				return null;
			}
		}

		public override void reset()
		{
			lock (streamLock())
			{
				base.reset();
			}
		}

		public virtual void visit(object a_tree)
		{
			com.db4o.QCandidate candidate = (com.db4o.QCandidate)a_tree;
			if (candidate.include())
			{
				addKeyCheckDuplicates(candidate.i_key);
			}
		}

		internal virtual void addKeyCheckDuplicates(int a_key)
		{
			if (i_checkDuplicates)
			{
				com.db4o.TreeInt newNode = new com.db4o.TreeInt(a_key);
				i_candidates = com.db4o.Tree.add(i_candidates, newNode);
				if (newNode.i_size == 0)
				{
					return;
				}
			}
			add(a_key);
		}

		public virtual object streamLock()
		{
			return i_trans.i_stream.i_lock;
		}

		public virtual com.db4o.ObjectContainer objectContainer()
		{
			return i_trans.i_stream;
		}

		public virtual void sort(com.db4o.query.QueryComparator cmp)
		{
			sort(cmp, 0, size() - 1);
			reset();
		}

		private void sort(com.db4o.query.QueryComparator cmp, int from, int to)
		{
			if (to - from < 1)
			{
				return;
			}
			object pivot = get(to);
			int left = from;
			int right = to;
			while (left < right)
			{
				while (left < right && cmp.compare(pivot, get(left)) < 0)
				{
					left++;
				}
				while (left < right && cmp.compare(pivot, get(right)) >= 0)
				{
					right--;
				}
				swap(left, right);
			}
			swap(to, right);
			sort(cmp, from, right - 1);
			sort(cmp, right + 1, to);
		}

		private void swap(int left, int right)
		{
			if (left != right)
			{
				int swap = i_content[left];
				i_content[left] = i_content[right];
				i_content[right] = swap;
			}
		}
	}
}
