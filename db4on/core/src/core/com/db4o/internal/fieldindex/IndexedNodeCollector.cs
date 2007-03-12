namespace com.db4o.@internal.fieldindex
{
	public class IndexedNodeCollector
	{
		private readonly com.db4o.foundation.Collection4 _nodes;

		private readonly com.db4o.foundation.Hashtable4 _nodeCache;

		public IndexedNodeCollector(com.db4o.@internal.query.processor.QCandidates candidates
			)
		{
			_nodes = new com.db4o.foundation.Collection4();
			_nodeCache = new com.db4o.foundation.Hashtable4();
			CollectIndexedNodes(candidates);
		}

		public virtual System.Collections.IEnumerator GetNodes()
		{
			return _nodes.GetEnumerator();
		}

		private void CollectIndexedNodes(com.db4o.@internal.query.processor.QCandidates candidates
			)
		{
			CollectIndexedNodes(candidates.IterateConstraints());
			ImplicitlyAndJoinsOnSameField();
		}

		private void ImplicitlyAndJoinsOnSameField()
		{
			object[] nodes = _nodes.ToArray();
			for (int i = 0; i < nodes.Length; i++)
			{
				object node = nodes[i];
				if (node is com.db4o.@internal.fieldindex.OrIndexedLeaf)
				{
					com.db4o.@internal.fieldindex.OrIndexedLeaf current = (com.db4o.@internal.fieldindex.OrIndexedLeaf
						)node;
					com.db4o.@internal.fieldindex.OrIndexedLeaf other = FindJoinOnSameFieldAtSameLevel
						(current);
					if (null != other)
					{
						nodes[com.db4o.foundation.Arrays4.IndexOf(nodes, other)] = null;
						CollectImplicitAnd(current.GetConstraint(), current, other);
					}
				}
			}
		}

		private com.db4o.@internal.fieldindex.OrIndexedLeaf FindJoinOnSameFieldAtSameLevel
			(com.db4o.@internal.fieldindex.OrIndexedLeaf join)
		{
			System.Collections.IEnumerator i = _nodes.GetEnumerator();
			while (i.MoveNext())
			{
				if (i.Current == join)
				{
					continue;
				}
				if (i.Current is com.db4o.@internal.fieldindex.OrIndexedLeaf)
				{
					com.db4o.@internal.fieldindex.OrIndexedLeaf current = (com.db4o.@internal.fieldindex.OrIndexedLeaf
						)i.Current;
					if (current.GetIndex() == join.GetIndex() && ParentConstraint(current) == ParentConstraint
						(join))
					{
						return current;
					}
				}
			}
			return null;
		}

		private object ParentConstraint(com.db4o.@internal.fieldindex.OrIndexedLeaf node)
		{
			return node.GetConstraint().Parent();
		}

		private void CollectIndexedNodes(System.Collections.IEnumerator qcons)
		{
			while (qcons.MoveNext())
			{
				com.db4o.@internal.query.processor.QCon qcon = (com.db4o.@internal.query.processor.QCon
					)qcons.Current;
				if (IsCached(qcon))
				{
					continue;
				}
				if (IsLeaf(qcon))
				{
					if (qcon.CanLoadByIndex() && qcon.CanBeIndexLeaf())
					{
						com.db4o.@internal.query.processor.QConObject conObject = (com.db4o.@internal.query.processor.QConObject
							)qcon;
						if (conObject.HasJoins())
						{
							CollectJoinedNode(conObject);
						}
						else
						{
							CollectStandaloneNode(conObject);
						}
					}
				}
				else
				{
					if (!qcon.HasJoins())
					{
						CollectIndexedNodes(qcon.IterateChildren());
					}
				}
			}
		}

		private bool IsCached(com.db4o.@internal.query.processor.QCon qcon)
		{
			return null != _nodeCache.Get(qcon);
		}

		private void CollectStandaloneNode(com.db4o.@internal.query.processor.QConObject 
			conObject)
		{
			com.db4o.@internal.fieldindex.IndexedLeaf existing = FindLeafOnSameField(conObject
				);
			if (existing != null)
			{
				CollectImplicitAnd(conObject, existing, new com.db4o.@internal.fieldindex.IndexedLeaf
					(conObject));
			}
			else
			{
				_nodes.Add(new com.db4o.@internal.fieldindex.IndexedLeaf(conObject));
			}
		}

		private void CollectJoinedNode(com.db4o.@internal.query.processor.QConObject constraintWithJoins
			)
		{
			com.db4o.foundation.Collection4 joins = CollectTopLevelJoins(constraintWithJoins);
			if (!CanJoinsBeSearchedByIndex(joins))
			{
				return;
			}
			if (1 == joins.Size())
			{
				_nodes.Add(NodeForConstraint((com.db4o.@internal.query.processor.QCon)joins.SingleElement
					()));
				return;
			}
			CollectImplicitlyAndingJoins(joins, constraintWithJoins);
		}

		private bool AllHaveSamePath(com.db4o.foundation.Collection4 leaves)
		{
			System.Collections.IEnumerator i = leaves.GetEnumerator();
			i.MoveNext();
			com.db4o.@internal.query.processor.QCon first = (com.db4o.@internal.query.processor.QCon
				)i.Current;
			while (i.MoveNext())
			{
				if (!HaveSamePath(first, (com.db4o.@internal.query.processor.QCon)i.Current))
				{
					return false;
				}
			}
			return true;
		}

		private bool HaveSamePath(com.db4o.@internal.query.processor.QCon x, com.db4o.@internal.query.processor.QCon
			 y)
		{
			if (x == y)
			{
				return true;
			}
			if (!x.OnSameFieldAs(y))
			{
				return false;
			}
			if (!x.HasParent())
			{
				return !y.HasParent();
			}
			return HaveSamePath(x.Parent(), y.Parent());
		}

		private com.db4o.foundation.Collection4 CollectLeaves(com.db4o.foundation.Collection4
			 joins)
		{
			com.db4o.foundation.Collection4 leaves = new com.db4o.foundation.Collection4();
			CollectLeaves(leaves, joins);
			return leaves;
		}

		private void CollectLeaves(com.db4o.foundation.Collection4 leaves, com.db4o.foundation.Collection4
			 joins)
		{
			System.Collections.IEnumerator i = joins.GetEnumerator();
			while (i.MoveNext())
			{
				com.db4o.@internal.query.processor.QConJoin join = ((com.db4o.@internal.query.processor.QConJoin
					)i.Current);
				CollectLeavesFromJoin(leaves, join);
			}
		}

		private void CollectLeavesFromJoin(com.db4o.foundation.Collection4 leaves, com.db4o.@internal.query.processor.QConJoin
			 join)
		{
			CollectLeavesFromJoinConstraint(leaves, join.i_constraint1);
			CollectLeavesFromJoinConstraint(leaves, join.i_constraint2);
		}

		private void CollectLeavesFromJoinConstraint(com.db4o.foundation.Collection4 leaves
			, com.db4o.@internal.query.processor.QCon constraint)
		{
			if (constraint is com.db4o.@internal.query.processor.QConJoin)
			{
				CollectLeavesFromJoin(leaves, (com.db4o.@internal.query.processor.QConJoin)constraint
					);
			}
			else
			{
				if (!leaves.ContainsByIdentity(constraint))
				{
					leaves.Add(constraint);
				}
			}
		}

		private bool CanJoinsBeSearchedByIndex(com.db4o.foundation.Collection4 joins)
		{
			com.db4o.foundation.Collection4 leaves = CollectLeaves(joins);
			return AllHaveSamePath(leaves) && AllCanBeSearchedByIndex(leaves);
		}

		private bool AllCanBeSearchedByIndex(com.db4o.foundation.Collection4 leaves)
		{
			System.Collections.IEnumerator i = leaves.GetEnumerator();
			while (i.MoveNext())
			{
				com.db4o.@internal.query.processor.QCon leaf = ((com.db4o.@internal.query.processor.QCon
					)i.Current);
				if (!leaf.CanLoadByIndex())
				{
					return false;
				}
			}
			return true;
		}

		private void CollectImplicitlyAndingJoins(com.db4o.foundation.Collection4 joins, 
			com.db4o.@internal.query.processor.QConObject constraintWithJoins)
		{
			System.Collections.IEnumerator i = joins.GetEnumerator();
			i.MoveNext();
			com.db4o.@internal.fieldindex.IndexedNodeWithRange last = NodeForConstraint((com.db4o.@internal.query.processor.QCon
				)i.Current);
			while (i.MoveNext())
			{
				com.db4o.@internal.fieldindex.IndexedNodeWithRange node = NodeForConstraint((com.db4o.@internal.query.processor.QCon
					)i.Current);
				last = new com.db4o.@internal.fieldindex.AndIndexedLeaf(constraintWithJoins, node
					, last);
				_nodes.Add(last);
			}
		}

		private com.db4o.foundation.Collection4 CollectTopLevelJoins(com.db4o.@internal.query.processor.QConObject
			 constraintWithJoins)
		{
			com.db4o.foundation.Collection4 joins = new com.db4o.foundation.Collection4();
			CollectTopLevelJoins(joins, constraintWithJoins);
			return joins;
		}

		private void CollectTopLevelJoins(com.db4o.foundation.Collection4 joins, com.db4o.@internal.query.processor.QCon
			 constraintWithJoins)
		{
			System.Collections.IEnumerator i = constraintWithJoins.i_joins.GetEnumerator();
			while (i.MoveNext())
			{
				com.db4o.@internal.query.processor.QConJoin join = (com.db4o.@internal.query.processor.QConJoin
					)i.Current;
				if (!join.HasJoins())
				{
					if (!joins.ContainsByIdentity(join))
					{
						joins.Add(join);
					}
				}
				else
				{
					CollectTopLevelJoins(joins, join);
				}
			}
		}

		private com.db4o.@internal.fieldindex.IndexedNodeWithRange NewNodeForConstraint(com.db4o.@internal.query.processor.QConJoin
			 join)
		{
			com.db4o.@internal.fieldindex.IndexedNodeWithRange c1 = NodeForConstraint(join.i_constraint1
				);
			com.db4o.@internal.fieldindex.IndexedNodeWithRange c2 = NodeForConstraint(join.i_constraint2
				);
			if (join.IsOr())
			{
				return new com.db4o.@internal.fieldindex.OrIndexedLeaf(FindLeafForJoin(join), c1, 
					c2);
			}
			return new com.db4o.@internal.fieldindex.AndIndexedLeaf(join.i_constraint1, c1, c2
				);
		}

		private com.db4o.@internal.query.processor.QCon FindLeafForJoin(com.db4o.@internal.query.processor.QConJoin
			 join)
		{
			if (join.i_constraint1 is com.db4o.@internal.query.processor.QConObject)
			{
				return join.i_constraint1;
			}
			com.db4o.@internal.query.processor.QCon con = join.i_constraint2;
			if (con is com.db4o.@internal.query.processor.QConObject)
			{
				return con;
			}
			return FindLeafForJoin((com.db4o.@internal.query.processor.QConJoin)con);
		}

		private com.db4o.@internal.fieldindex.IndexedNodeWithRange NodeForConstraint(com.db4o.@internal.query.processor.QCon
			 con)
		{
			com.db4o.@internal.fieldindex.IndexedNodeWithRange node = (com.db4o.@internal.fieldindex.IndexedNodeWithRange
				)_nodeCache.Get(con);
			if (null != node || _nodeCache.ContainsKey(con))
			{
				return node;
			}
			node = NewNodeForConstraint(con);
			_nodeCache.Put(con, node);
			return node;
		}

		private com.db4o.@internal.fieldindex.IndexedNodeWithRange NewNodeForConstraint(com.db4o.@internal.query.processor.QCon
			 con)
		{
			if (con is com.db4o.@internal.query.processor.QConJoin)
			{
				return NewNodeForConstraint((com.db4o.@internal.query.processor.QConJoin)con);
			}
			return new com.db4o.@internal.fieldindex.IndexedLeaf((com.db4o.@internal.query.processor.QConObject
				)con);
		}

		private void CollectImplicitAnd(com.db4o.@internal.query.processor.QCon constraint
			, com.db4o.@internal.fieldindex.IndexedNodeWithRange x, com.db4o.@internal.fieldindex.IndexedNodeWithRange
			 y)
		{
			_nodes.Remove(x);
			_nodes.Remove(y);
			_nodes.Add(new com.db4o.@internal.fieldindex.AndIndexedLeaf(constraint, x, y));
		}

		private com.db4o.@internal.fieldindex.IndexedLeaf FindLeafOnSameField(com.db4o.@internal.query.processor.QConObject
			 conObject)
		{
			System.Collections.IEnumerator i = _nodes.GetEnumerator();
			while (i.MoveNext())
			{
				if (i.Current is com.db4o.@internal.fieldindex.IndexedLeaf)
				{
					com.db4o.@internal.fieldindex.IndexedLeaf leaf = (com.db4o.@internal.fieldindex.IndexedLeaf
						)i.Current;
					if (conObject.OnSameFieldAs(leaf.Constraint()))
					{
						return leaf;
					}
				}
			}
			return null;
		}

		private bool IsLeaf(com.db4o.@internal.query.processor.QCon qcon)
		{
			return !qcon.HasChildren();
		}
	}
}
