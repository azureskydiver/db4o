namespace com.db4o
{
	/// <exclude></exclude>
	internal class QResult : com.db4o.IntArrayList, com.db4o.foundation.Visitor4
	{
		internal com.db4o.Tree i_candidates;

		internal bool i_checkDuplicates;

		internal readonly com.db4o.Transaction i_trans;

		internal QResult(com.db4o.Transaction a_trans)
		{
			i_trans = a_trans;
		}

		internal QResult(com.db4o.Transaction trans, int initialSize) : base(initialSize)
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
					throw new System.IndexOutOfRangeException("Index " + index + " not within bounds."
						);
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

		internal virtual object streamLock()
		{
			return i_trans.i_stream.i_lock;
		}
	}
}
