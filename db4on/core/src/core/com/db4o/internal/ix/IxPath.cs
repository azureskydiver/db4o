namespace com.db4o.@internal.ix
{
	/// <summary>
	/// Index Path to represent a list of traversed index tree entries,
	/// used by IxTraverser
	/// </summary>
	internal class IxPath : com.db4o.foundation.ShallowClone, com.db4o.foundation.Visitor4
	{
		internal int i_comparisonResult;

		internal int[] i_lowerAndUpperMatch;

		internal int i_upperNull = -1;

		internal com.db4o.@internal.ix.IxPath i_next;

		internal com.db4o.@internal.ix.IxTraverser i_traverser;

		internal com.db4o.@internal.ix.IxTree i_tree;

		internal com.db4o.foundation.Visitor4 _visitor;

		internal IxPath(com.db4o.@internal.ix.IxTraverser a_traverser, com.db4o.@internal.ix.IxPath
			 a_next, com.db4o.@internal.ix.IxTree a_tree, int a_comparisonResult, int[] lowerAndUpperMatch
			)
		{
			i_traverser = a_traverser;
			i_next = a_next;
			i_tree = a_tree;
			i_comparisonResult = a_comparisonResult;
			i_lowerAndUpperMatch = lowerAndUpperMatch;
		}

		internal virtual void Add(com.db4o.foundation.Visitor4 visitor)
		{
			if (i_comparisonResult == 0 && i_traverser.i_take[com.db4o.@internal.query.processor.QE
				.EQUAL])
			{
				i_tree.Visit(visitor, i_lowerAndUpperMatch);
			}
		}

		internal virtual void AddPrecedingToCandidatesTree(com.db4o.foundation.Visitor4 visitor
			)
		{
			_visitor = visitor;
			if (i_tree._preceding != null)
			{
				if (i_next == null || i_next.i_tree != i_tree._preceding)
				{
					i_tree._preceding.Traverse(this);
				}
			}
			if (i_lowerAndUpperMatch != null)
			{
				int[] lowerAndUpperMatch = new int[] { i_upperNull, i_lowerAndUpperMatch[0] - 1 };
				i_tree.Visit(visitor, lowerAndUpperMatch);
			}
			else
			{
				if (i_comparisonResult < 0)
				{
					Visit(i_tree);
				}
			}
		}

		internal virtual void AddSubsequentToCandidatesTree(com.db4o.foundation.Visitor4 
			visitor)
		{
			_visitor = visitor;
			if (i_tree._subsequent != null)
			{
				if (i_next == null || i_next.i_tree != i_tree._subsequent)
				{
					i_tree._subsequent.Traverse(this);
				}
			}
			if (i_lowerAndUpperMatch != null)
			{
				int[] lowerAndUpperMatch = new int[] { i_lowerAndUpperMatch[1] + 1, ((com.db4o.@internal.ix.IxFileRange
					)i_tree)._entries - 1 };
				i_tree.Visit(visitor, lowerAndUpperMatch);
			}
			else
			{
				if (i_comparisonResult > 0)
				{
					Visit(i_tree);
				}
			}
		}

		internal virtual com.db4o.@internal.ix.IxPath Append(com.db4o.@internal.ix.IxPath
			 a_head, com.db4o.@internal.ix.IxPath a_tail)
		{
			if (a_head == null)
			{
				return this;
			}
			i_next = a_head;
			return a_tail;
		}

		internal virtual com.db4o.@internal.ix.IxPath Append(com.db4o.@internal.ix.IxTree
			 a_tree, int a_comparisonResult, int[] lowerAndUpperMatch)
		{
			i_next = new com.db4o.@internal.ix.IxPath(i_traverser, null, a_tree, a_comparisonResult
				, lowerAndUpperMatch);
			i_next.i_tree = a_tree;
			return i_next;
		}

		internal virtual bool CarriesTheSame(com.db4o.@internal.ix.IxPath a_path)
		{
			return i_tree == a_path.i_tree;
		}

		private void CheckUpperNull()
		{
			if (i_upperNull == -1)
			{
				i_upperNull = 0;
				i_traverser.i_handler.PrepareComparison(null);
				int res = i_tree.Compare(null);
				if (res != 0)
				{
					return;
				}
				int[] nullMatches = i_tree.LowerAndUpperMatch();
				if (nullMatches[0] == 0)
				{
					i_upperNull = nullMatches[1] + 1;
				}
				else
				{
					i_upperNull = 0;
				}
			}
		}

		public virtual void VisitMatch(com.db4o.@internal.freespace.FreespaceVisitor visitor
			)
		{
			if (i_next != null)
			{
				i_next.VisitMatch(visitor);
			}
			if (visitor.Visited())
			{
				return;
			}
			if (i_comparisonResult != 0)
			{
				return;
			}
			if (i_lowerAndUpperMatch == null)
			{
				i_tree.FreespaceVisit(visitor, 0);
				return;
			}
			if (i_lowerAndUpperMatch[1] < i_lowerAndUpperMatch[0])
			{
				return;
			}
			int ix = i_lowerAndUpperMatch[0];
			if (ix >= 0)
			{
				i_tree.FreespaceVisit(visitor, ix);
			}
		}

		public virtual void VisitPreceding(com.db4o.@internal.freespace.FreespaceVisitor 
			visitor)
		{
			if (i_next != null)
			{
				i_next.VisitPreceding(visitor);
				if (visitor.Visited())
				{
					return;
				}
			}
			if (i_lowerAndUpperMatch != null)
			{
				int ix = i_lowerAndUpperMatch[0] - 1;
				if (ix >= 0)
				{
					i_tree.FreespaceVisit(visitor, ix);
				}
			}
			else
			{
				if (i_comparisonResult < 0)
				{
					i_tree.FreespaceVisit(visitor, 0);
				}
			}
			if (visitor.Visited())
			{
				return;
			}
			if (i_tree._preceding != null)
			{
				if (i_next == null || i_next.i_tree != i_tree._preceding)
				{
					((com.db4o.@internal.ix.IxTree)i_tree._preceding).VisitLast(visitor);
				}
			}
		}

		public virtual void VisitSubsequent(com.db4o.@internal.freespace.FreespaceVisitor
			 visitor)
		{
			if (i_next != null)
			{
				i_next.VisitSubsequent(visitor);
				if (visitor.Visited())
				{
					return;
				}
			}
			if (i_lowerAndUpperMatch != null)
			{
				int ix = i_lowerAndUpperMatch[1] + 1;
				if (ix < ((com.db4o.@internal.ix.IxFileRange)i_tree)._entries)
				{
					i_tree.FreespaceVisit(visitor, ix);
				}
			}
			else
			{
				if (i_comparisonResult > 0)
				{
					i_tree.FreespaceVisit(visitor, 0);
				}
			}
			if (visitor.Visited())
			{
				return;
			}
			if (i_tree._subsequent != null)
			{
				if (i_next == null || i_next.i_tree != i_tree._subsequent)
				{
					((com.db4o.@internal.ix.IxTree)i_tree._subsequent).VisitFirst(visitor);
				}
			}
		}

		internal virtual int CountMatching()
		{
			if (i_comparisonResult == 0)
			{
				if (i_lowerAndUpperMatch == null)
				{
					if (i_tree is com.db4o.@internal.ix.IxRemove)
					{
						return 0;
					}
					return 1;
				}
				return i_lowerAndUpperMatch[1] - i_lowerAndUpperMatch[0] + 1;
			}
			return 0;
		}

		internal virtual int CountPreceding(bool a_takenulls)
		{
			int preceding = 0;
			if (i_tree._preceding != null)
			{
				if (i_next == null || i_next.i_tree != i_tree._preceding)
				{
					preceding += i_tree._preceding.Size();
				}
			}
			if (i_lowerAndUpperMatch != null)
			{
				if (a_takenulls)
				{
					i_upperNull = 0;
				}
				else
				{
					CheckUpperNull();
				}
				preceding += i_lowerAndUpperMatch[0] - i_upperNull;
			}
			else
			{
				if (i_comparisonResult < 0 && !(i_tree is com.db4o.@internal.ix.IxRemove))
				{
					preceding++;
				}
			}
			return preceding;
		}

		internal virtual int CountSubsequent()
		{
			int subsequent = 0;
			if (i_tree._subsequent != null)
			{
				if (i_next == null || i_next.i_tree != i_tree._subsequent)
				{
					subsequent += i_tree._subsequent.Size();
				}
			}
			if (i_lowerAndUpperMatch != null)
			{
				subsequent += ((com.db4o.@internal.ix.IxFileRange)i_tree)._entries - i_lowerAndUpperMatch
					[1] - 1;
			}
			else
			{
				if (i_comparisonResult > 0 && !(i_tree is com.db4o.@internal.ix.IxRemove))
				{
					subsequent++;
				}
			}
			return subsequent;
		}

		public virtual object ShallowClone()
		{
			int[] lowerAndUpperMatch = null;
			if (i_lowerAndUpperMatch != null)
			{
				lowerAndUpperMatch = new int[] { i_lowerAndUpperMatch[0], i_lowerAndUpperMatch[1]
					 };
			}
			com.db4o.@internal.ix.IxPath ret = new com.db4o.@internal.ix.IxPath(i_traverser, 
				i_next, i_tree, i_comparisonResult, lowerAndUpperMatch);
			ret.i_upperNull = i_upperNull;
			ret._visitor = _visitor;
			return ret;
		}

		public override string ToString()
		{
			return base.ToString();
			return i_tree.ToString();
		}

		public virtual void Visit(object a_object)
		{
			((com.db4o.foundation.Visitor4)a_object).Visit(_visitor);
		}
	}
}
