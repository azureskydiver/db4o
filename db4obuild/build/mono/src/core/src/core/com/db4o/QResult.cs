/* Copyright (C) 2004 - 2005  db4objects Inc.  http://www.db4o.com

This file is part of the db4o open source object database.

db4o is free software; you can redistribute it and/or modify it under
the terms of version 2 of the GNU General Public License as published
by the Free Software Foundation and as clarified by db4objects' GPL 
interpretation policy, available at
http://www.db4o.com/about/company/legalpolicies/gplinterpretation/
Alternatively you can write to db4objects, Inc., 1900 S Norfolk Street,
Suite 350, San Mateo, CA 94403, USA.

db4o is distributed in the hope that it will be useful, but WITHOUT ANY
WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
for more details.

You should have received a copy of the GNU General Public License along
with this program; if not, write to the Free Software Foundation, Inc.,
59 Temple Place - Suite 330, Boston, MA  02111-1307, USA. */
namespace com.db4o
{
	/// <exclude></exclude>
	internal class QResult : com.db4o.IntArrayList, com.db4o.ObjectSet, com.db4o.ext.ExtObjectSet
		, com.db4o.Visitor4
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

		internal void checkDuplicates()
		{
			i_checkDuplicates = true;
		}

		public virtual com.db4o.ext.ExtObjectSet ext()
		{
			return this;
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

		protected virtual object streamLock()
		{
			return i_trans.i_stream.i_lock;
		}
	}
}
