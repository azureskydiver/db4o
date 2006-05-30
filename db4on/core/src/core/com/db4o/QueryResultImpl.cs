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

		internal object Activate(object obj)
		{
			com.db4o.YapStream stream = i_trans.i_stream;
			stream.BeginEndActivation();
			stream.Activate2(i_trans, obj, stream.i_config.ActivationDepth());
			stream.BeginEndActivation();
			return obj;
		}

		public virtual object Get(int index)
		{
			lock (StreamLock())
			{
				if (index < 0 || index >= Size())
				{
					throw new System.IndexOutOfRangeException();
				}
				int id = i_content[index];
				com.db4o.YapStream stream = i_trans.i_stream;
				object obj = stream.GetByID(id);
				if (obj == null)
				{
					return null;
				}
				return Activate(obj);
			}
		}

		internal void CheckDuplicates()
		{
			i_checkDuplicates = true;
		}

		public virtual long[] GetIDs()
		{
			lock (StreamLock())
			{
				return AsLong();
			}
		}

		public override bool HasNext()
		{
			lock (StreamLock())
			{
				return base.HasNext();
			}
		}

		public virtual object Next()
		{
			lock (StreamLock())
			{
				com.db4o.YapStream stream = i_trans.i_stream;
				stream.CheckClosed();
				if (base.HasNext())
				{
					object ret = stream.GetByID2(i_trans, NextInt());
					if (ret == null)
					{
						return Next();
					}
					return Activate(ret);
				}
				return null;
			}
		}

		public override void Reset()
		{
			lock (StreamLock())
			{
				base.Reset();
			}
		}

		public virtual void Visit(object a_tree)
		{
			com.db4o.QCandidate candidate = (com.db4o.QCandidate)a_tree;
			if (candidate.Include())
			{
				AddKeyCheckDuplicates(candidate._key);
			}
		}

		internal virtual void AddKeyCheckDuplicates(int a_key)
		{
			if (i_checkDuplicates)
			{
				com.db4o.TreeInt newNode = new com.db4o.TreeInt(a_key);
				i_candidates = com.db4o.Tree.Add(i_candidates, newNode);
				if (newNode._size == 0)
				{
					return;
				}
			}
			Add(a_key);
		}

		public virtual object StreamLock()
		{
			return i_trans.i_stream.i_lock;
		}

		public virtual com.db4o.ObjectContainer ObjectContainer()
		{
			return i_trans.i_stream;
		}

		public virtual void Sort(com.db4o.query.QueryComparator cmp)
		{
			Sort(cmp, 0, Size() - 1);
			Reset();
		}

		private void Sort(com.db4o.query.QueryComparator cmp, int from, int to)
		{
			if (to - from < 1)
			{
				return;
			}
			object pivot = Get(to);
			int left = from;
			int right = to;
			while (left < right)
			{
				while (left < right && cmp.Compare(pivot, Get(left)) < 0)
				{
					left++;
				}
				while (left < right && cmp.Compare(pivot, Get(right)) >= 0)
				{
					right--;
				}
				Swap(left, right);
			}
			Swap(to, right);
			Sort(cmp, from, right - 1);
			Sort(cmp, right + 1, to);
		}

		private void Swap(int left, int right)
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
