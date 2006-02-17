namespace com.db4o.inside.ix
{
	/// <summary>
	/// Index Path to represent a list of traversed index tree entries,
	/// used by IxTraverser
	/// </summary>
	internal class IxPath : j4o.lang.Cloneable, com.db4o.foundation.Visitor4
	{
		internal int i_comparisonResult;

		internal int[] i_lowerAndUpperMatch;

		internal int i_upperNull = -1;

		internal com.db4o.inside.ix.IxPath i_next;

		internal com.db4o.inside.ix.IxTraverser i_traverser;

		internal com.db4o.inside.ix.IxTree i_tree;

		internal com.db4o.foundation.Visitor4 _visitor;

		internal IxPath(com.db4o.inside.ix.IxTraverser a_traverser, com.db4o.inside.ix.IxPath
			 a_next, com.db4o.inside.ix.IxTree a_tree, int a_comparisonResult, int[] lowerAndUpperMatch
			)
		{
			i_traverser = a_traverser;
			i_next = a_next;
			i_tree = a_tree;
			i_comparisonResult = a_comparisonResult;
			i_lowerAndUpperMatch = lowerAndUpperMatch;
		}

		public virtual com.db4o.inside.ix.NIxPathNode convert()
		{
			com.db4o.inside.ix.NIxPathNode res = new com.db4o.inside.ix.NIxPathNode();
			res._comparisonResult = i_comparisonResult;
			res._lowerAndUpperMatch = i_lowerAndUpperMatch;
			res._tree = i_tree;
			if (i_next != null)
			{
				res._next = i_next.convert();
			}
			return res;
		}

		internal virtual void add(com.db4o.foundation.Visitor4 visitor)
		{
			if (i_comparisonResult == 0 && i_traverser.i_take[com.db4o.inside.ix.IxTraverser.
				EQUAL])
			{
				i_tree.visit(visitor, i_lowerAndUpperMatch);
			}
		}

		internal virtual void addPrecedingToCandidatesTree(com.db4o.foundation.Visitor4 visitor
			)
		{
			_visitor = visitor;
			if (i_tree.i_preceding != null)
			{
				if (i_next == null || i_next.i_tree != i_tree.i_preceding)
				{
					i_tree.i_preceding.traverse(this);
				}
			}
			if (i_lowerAndUpperMatch != null)
			{
				int[] lowerAndUpperMatch = new int[] { i_upperNull, i_lowerAndUpperMatch[0] - 1 };
				i_tree.visit(visitor, lowerAndUpperMatch);
			}
			else
			{
				if (i_comparisonResult < 0)
				{
					visit(i_tree);
				}
			}
		}

		internal virtual void addSubsequentToCandidatesTree(com.db4o.foundation.Visitor4 
			visitor)
		{
			_visitor = visitor;
			if (i_tree.i_subsequent != null)
			{
				if (i_next == null || i_next.i_tree != i_tree.i_subsequent)
				{
					i_tree.i_subsequent.traverse(this);
				}
			}
			if (i_lowerAndUpperMatch != null)
			{
				int[] lowerAndUpperMatch = new int[] { i_lowerAndUpperMatch[1] + 1, ((com.db4o.inside.ix.IxFileRange
					)i_tree)._entries - 1 };
				i_tree.visit(visitor, lowerAndUpperMatch);
			}
			else
			{
				if (i_comparisonResult > 0)
				{
					visit(i_tree);
				}
			}
		}

		internal virtual com.db4o.inside.ix.IxPath append(com.db4o.inside.ix.IxPath a_head
			, com.db4o.inside.ix.IxPath a_tail)
		{
			if (a_head == null)
			{
				return this;
			}
			i_next = a_head;
			return a_tail;
		}

		internal virtual com.db4o.inside.ix.IxPath append(com.db4o.inside.ix.IxTree a_tree
			, int a_comparisonResult, int[] lowerAndUpperMatch)
		{
			i_next = new com.db4o.inside.ix.IxPath(i_traverser, null, a_tree, a_comparisonResult
				, lowerAndUpperMatch);
			i_next.i_tree = a_tree;
			return i_next;
		}

		internal virtual bool carriesTheSame(com.db4o.inside.ix.IxPath a_path)
		{
			return i_tree == a_path.i_tree;
		}

		private void checkUpperNull()
		{
			if (i_upperNull == -1)
			{
				i_upperNull = 0;
				i_traverser.i_handler.prepareComparison(null);
				int res = i_tree.compare(null);
				if (res != 0)
				{
					return;
				}
				int[] nullMatches = i_tree.lowerAndUpperMatch();
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

		public virtual void visitMatch(com.db4o.inside.freespace.FreespaceVisitor visitor
			)
		{
			if (i_next != null)
			{
				i_next.visitMatch(visitor);
			}
			if (visitor.visited())
			{
				return;
			}
			if (i_comparisonResult != 0)
			{
				return;
			}
			if (i_lowerAndUpperMatch == null)
			{
				i_tree.freespaceVisit(visitor, 0);
				return;
			}
			if (i_lowerAndUpperMatch[1] < i_lowerAndUpperMatch[0])
			{
				return;
			}
			int ix = i_lowerAndUpperMatch[0];
			if (ix >= 0)
			{
				i_tree.freespaceVisit(visitor, ix);
			}
		}

		public virtual void visitPreceding(com.db4o.inside.freespace.FreespaceVisitor visitor
			)
		{
			if (i_next != null)
			{
				i_next.visitPreceding(visitor);
				if (visitor.visited())
				{
					return;
				}
			}
			if (i_lowerAndUpperMatch != null)
			{
				int ix = i_lowerAndUpperMatch[0] - 1;
				if (ix >= 0)
				{
					i_tree.freespaceVisit(visitor, ix);
				}
			}
			else
			{
				if (i_comparisonResult < 0)
				{
					i_tree.freespaceVisit(visitor, 0);
				}
			}
			if (visitor.visited())
			{
				return;
			}
			if (i_tree.i_preceding != null)
			{
				if (i_next == null || i_next.i_tree != i_tree.i_preceding)
				{
					((com.db4o.inside.ix.IxTree)i_tree.i_preceding).visitLast(visitor);
				}
			}
		}

		public virtual void visitSubsequent(com.db4o.inside.freespace.FreespaceVisitor visitor
			)
		{
			if (i_next != null)
			{
				i_next.visitSubsequent(visitor);
				if (visitor.visited())
				{
					return;
				}
			}
			if (i_lowerAndUpperMatch != null)
			{
				int ix = i_lowerAndUpperMatch[1] + 1;
				if (ix < ((com.db4o.inside.ix.IxFileRange)i_tree)._entries)
				{
					i_tree.freespaceVisit(visitor, ix);
				}
			}
			else
			{
				if (i_comparisonResult > 0)
				{
					i_tree.freespaceVisit(visitor, 0);
				}
			}
			if (visitor.visited())
			{
				return;
			}
			if (i_tree.i_subsequent != null)
			{
				if (i_next == null || i_next.i_tree != i_tree.i_subsequent)
				{
					((com.db4o.inside.ix.IxTree)i_tree.i_subsequent).visitFirst(visitor);
				}
			}
		}

		internal virtual int countMatching()
		{
			if (i_comparisonResult == 0)
			{
				if (i_lowerAndUpperMatch == null)
				{
					if (i_tree is com.db4o.inside.ix.IxRemove)
					{
						return 0;
					}
					return 1;
				}
				return i_lowerAndUpperMatch[1] - i_lowerAndUpperMatch[0] + 1;
			}
			return 0;
		}

		internal virtual int countPreceding(bool a_takenulls)
		{
			int preceding = 0;
			if (i_tree.i_preceding != null)
			{
				if (i_next == null || i_next.i_tree != i_tree.i_preceding)
				{
					preceding += i_tree.i_preceding.size();
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
					checkUpperNull();
				}
				preceding += i_lowerAndUpperMatch[0] - i_upperNull;
			}
			else
			{
				if (i_comparisonResult < 0 && !(i_tree is com.db4o.inside.ix.IxRemove))
				{
					preceding++;
				}
			}
			return preceding;
		}

		internal virtual int countSubsequent()
		{
			int subsequent = 0;
			if (i_tree.i_subsequent != null)
			{
				if (i_next == null || i_next.i_tree != i_tree.i_subsequent)
				{
					subsequent += i_tree.i_subsequent.size();
				}
			}
			if (i_lowerAndUpperMatch != null)
			{
				subsequent += ((com.db4o.inside.ix.IxFileRange)i_tree)._entries - i_lowerAndUpperMatch
					[1] - 1;
			}
			else
			{
				if (i_comparisonResult > 0 && !(i_tree is com.db4o.inside.ix.IxRemove))
				{
					subsequent++;
				}
			}
			return subsequent;
		}

		internal virtual com.db4o.inside.ix.IxPath shallowClone()
		{
			try
			{
				return (com.db4o.inside.ix.IxPath)j4o.lang.JavaSystem.clone(this);
			}
			catch (j4o.lang.CloneNotSupportedException e)
			{
			}
			return null;
		}

		public override string ToString()
		{
			return base.ToString();
			return i_tree.ToString();
		}

		public virtual void visit(object a_object)
		{
			((com.db4o.foundation.Visitor4)a_object).visit(_visitor);
		}
	}
}
