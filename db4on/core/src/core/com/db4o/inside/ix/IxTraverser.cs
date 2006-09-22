namespace com.db4o.inside.ix
{
	/// <exclude></exclude>
	public class IxTraverser
	{
		private com.db4o.inside.ix.IxPath i_appendHead;

		private com.db4o.inside.ix.IxPath i_appendTail;

		private com.db4o.inside.ix.IxPath i_greatHead;

		private com.db4o.inside.ix.IxPath i_greatTail;

		internal com.db4o.inside.ix.Indexable4 i_handler;

		private com.db4o.inside.ix.IxPath i_smallHead;

		private com.db4o.inside.ix.IxPath i_smallTail;

		internal bool[] i_take;

		private void Add(com.db4o.foundation.Visitor4 visitor, com.db4o.inside.ix.IxPath 
			a_previousPath, com.db4o.inside.ix.IxPath a_great, com.db4o.inside.ix.IxPath a_small
			)
		{
			AddPathTree(visitor, a_previousPath);
			if (a_great != null && a_small != null && a_great.CarriesTheSame(a_small))
			{
				Add(visitor, a_great, a_great.i_next, a_small.i_next);
				return;
			}
			AddGreater(visitor, a_small);
			AddSmaller(visitor, a_great);
		}

		private void AddAll(com.db4o.foundation.Visitor4 visitor, com.db4o.foundation.Tree
			 a_tree)
		{
			if (a_tree != null)
			{
				((com.db4o.inside.ix.IxTree)a_tree).Visit(visitor, null);
				AddAll(visitor, a_tree._preceding);
				AddAll(visitor, a_tree._subsequent);
			}
		}

		private void AddGreater(com.db4o.foundation.Visitor4 visitor, com.db4o.inside.ix.IxPath
			 a_path)
		{
			if (a_path != null)
			{
				if (a_path.i_next == null)
				{
					AddSubsequent(visitor, a_path);
				}
				else
				{
					if (a_path.i_next.i_tree == a_path.i_tree._preceding)
					{
						AddSubsequent(visitor, a_path);
					}
					else
					{
						AddPathTree(visitor, a_path);
					}
					AddGreater(visitor, a_path.i_next);
				}
			}
		}

		private void AddPathTree(com.db4o.foundation.Visitor4 visitor, com.db4o.inside.ix.IxPath
			 a_path)
		{
			if (a_path != null)
			{
				a_path.Add(visitor);
			}
		}

		private void AddPreceding(com.db4o.foundation.Visitor4 visitor, com.db4o.inside.ix.IxPath
			 a_path)
		{
			AddPathTree(visitor, a_path);
			AddAll(visitor, a_path.i_tree._preceding);
		}

		private void AddSmaller(com.db4o.foundation.Visitor4 visitor, com.db4o.inside.ix.IxPath
			 a_path)
		{
			if (a_path != null)
			{
				if (a_path.i_next == null)
				{
					AddPreceding(visitor, a_path);
				}
				else
				{
					if (a_path.i_next.i_tree == a_path.i_tree._subsequent)
					{
						AddPreceding(visitor, a_path);
					}
					else
					{
						AddPathTree(visitor, a_path);
					}
					AddSmaller(visitor, a_path.i_next);
				}
			}
		}

		private void AddSubsequent(com.db4o.foundation.Visitor4 visitor, com.db4o.inside.ix.IxPath
			 a_path)
		{
			AddPathTree(visitor, a_path);
			AddAll(visitor, a_path.i_tree._subsequent);
		}

		private int CountGreater(com.db4o.inside.ix.IxPath a_path, int a_sum)
		{
			if (a_path.i_next == null)
			{
				return a_sum + CountSubsequent(a_path);
			}
			if (a_path.i_next.i_tree == a_path.i_tree._preceding)
			{
				a_sum += CountSubsequent(a_path);
			}
			else
			{
				a_sum += a_path.CountMatching();
			}
			return CountGreater(a_path.i_next, a_sum);
		}

		private int CountPreceding(com.db4o.inside.ix.IxPath a_path)
		{
			return com.db4o.foundation.Tree.Size(a_path.i_tree._preceding) + a_path.CountMatching
				();
		}

		private int CountSmaller(com.db4o.inside.ix.IxPath a_path, int a_sum)
		{
			if (a_path.i_next == null)
			{
				return a_sum + CountPreceding(a_path);
			}
			if (a_path.i_next.i_tree == a_path.i_tree._subsequent)
			{
				a_sum += CountPreceding(a_path);
			}
			else
			{
				a_sum += a_path.CountMatching();
			}
			return CountSmaller(a_path.i_next, a_sum);
		}

		private int CountSpan(com.db4o.inside.ix.IxPath a_previousPath, com.db4o.inside.ix.IxPath
			 a_great, com.db4o.inside.ix.IxPath a_small)
		{
			if (a_great == null)
			{
				if (a_small == null)
				{
					return a_previousPath.CountMatching();
				}
				return CountGreater(a_small, a_previousPath.CountMatching());
			}
			else
			{
				if (a_small == null)
				{
					return CountSmaller(a_great, a_previousPath.CountMatching());
				}
			}
			if (a_great.CarriesTheSame(a_small))
			{
				return CountSpan(a_great, a_great.i_next, a_small.i_next);
			}
			return a_previousPath.CountMatching() + CountGreater(a_small, 0) + CountSmaller(a_great
				, 0);
		}

		private int CountSubsequent(com.db4o.inside.ix.IxPath a_path)
		{
			return com.db4o.foundation.Tree.Size(a_path.i_tree._subsequent) + a_path.CountMatching
				();
		}

		private void DelayedAppend(com.db4o.inside.ix.IxTree a_tree, int a_comparisonResult
			, int[] lowerAndUpperMatch)
		{
			if (i_appendHead == null)
			{
				i_appendHead = new com.db4o.inside.ix.IxPath(this, null, a_tree, a_comparisonResult
					, lowerAndUpperMatch);
				i_appendTail = i_appendHead;
			}
			else
			{
				i_appendTail = i_appendTail.Append(a_tree, a_comparisonResult, lowerAndUpperMatch
					);
			}
		}

		private void FindBoth()
		{
			if (i_greatTail.i_comparisonResult == 0)
			{
				FindSmallestEqualFromEqual((com.db4o.inside.ix.IxTree)i_greatTail.i_tree._preceding
					);
				ResetDelayedAppend();
				FindGreatestEqualFromEqual((com.db4o.inside.ix.IxTree)i_greatTail.i_tree._subsequent
					);
			}
			else
			{
				if (i_greatTail.i_comparisonResult < 0)
				{
					FindBoth1((com.db4o.inside.ix.IxTree)i_greatTail.i_tree._subsequent);
				}
				else
				{
					FindBoth1((com.db4o.inside.ix.IxTree)i_greatTail.i_tree._preceding);
				}
			}
		}

		private void FindBoth1(com.db4o.inside.ix.IxTree a_tree)
		{
			if (a_tree != null)
			{
				int res = a_tree.Compare(null);
				int[] lowerAndUpperMatch = a_tree.LowerAndUpperMatch();
				i_greatTail = i_greatTail.Append(a_tree, res, lowerAndUpperMatch);
				i_smallTail = i_smallTail.Append(a_tree, res, lowerAndUpperMatch);
				FindBoth();
			}
		}

		private void FindNullPath1(com.db4o.inside.ix.IxPath[] headTail)
		{
			if (headTail[1].i_comparisonResult == 0)
			{
				FindGreatestNullFromNull(headTail, (com.db4o.inside.ix.IxTree)headTail[1].i_tree.
					_subsequent);
			}
			else
			{
				if (headTail[1].i_comparisonResult < 0)
				{
					FindNullPath2(headTail, (com.db4o.inside.ix.IxTree)headTail[1].i_tree._subsequent
						);
				}
				else
				{
					FindNullPath2(headTail, (com.db4o.inside.ix.IxTree)headTail[1].i_tree._preceding);
				}
			}
		}

		private void FindNullPath2(com.db4o.inside.ix.IxPath[] headTail, com.db4o.inside.ix.IxTree
			 tree)
		{
			if (tree != null)
			{
				int res = tree.Compare(null);
				headTail[1] = headTail[1].Append(tree, res, tree.LowerAndUpperMatch());
				FindNullPath1(headTail);
			}
		}

		private void FindGreatestNullFromNull(com.db4o.inside.ix.IxPath[] headTail, com.db4o.inside.ix.IxTree
			 tree)
		{
			if (tree != null)
			{
				int res = tree.Compare(null);
				DelayedAppend(tree, res, tree.LowerAndUpperMatch());
				if (res == 0)
				{
					headTail[1] = headTail[1].Append(i_appendHead, i_appendTail);
					ResetDelayedAppend();
				}
				if (res > 0)
				{
					FindGreatestNullFromNull(headTail, (com.db4o.inside.ix.IxTree)tree._preceding);
				}
				else
				{
					FindGreatestNullFromNull(headTail, (com.db4o.inside.ix.IxTree)tree._subsequent);
				}
			}
		}

		public virtual int FindBounds(object a_constraint, com.db4o.inside.ix.IxTree a_tree
			)
		{
			if (a_tree != null)
			{
				i_handler = a_tree.Handler();
				i_handler.PrepareComparison(a_constraint);
				int res = a_tree.Compare(null);
				i_greatHead = new com.db4o.inside.ix.IxPath(this, null, a_tree, res, a_tree.LowerAndUpperMatch
					());
				i_greatTail = i_greatHead;
				i_smallHead = (com.db4o.inside.ix.IxPath)i_greatHead.ShallowClone();
				i_smallTail = i_smallHead;
				FindBoth();
				int span = 0;
				if (i_take[com.db4o.QE.EQUAL])
				{
					span += CountSpan(i_greatHead, i_greatHead.i_next, i_smallHead.i_next);
				}
				if (i_take[com.db4o.QE.SMALLER])
				{
					com.db4o.inside.ix.IxPath head = i_smallHead;
					while (head != null)
					{
						span += head.CountPreceding(i_take[com.db4o.QE.NULLS]);
						head = head.i_next;
					}
				}
				if (i_take[com.db4o.QE.GREATER])
				{
					com.db4o.inside.ix.IxPath head = i_greatHead;
					while (head != null)
					{
						span += head.CountSubsequent();
						head = head.i_next;
					}
				}
				return span;
			}
			return 0;
		}

		public virtual int FindBoundsExactMatch(object a_constraint, com.db4o.inside.ix.IxTree
			 a_tree)
		{
			i_take = new bool[] { false, false, false, false };
			i_take[com.db4o.QE.EQUAL] = true;
			return FindBounds(a_constraint, a_tree);
		}

		private void FindGreatestEqualFromEqual(com.db4o.inside.ix.IxTree a_tree)
		{
			if (a_tree != null)
			{
				int res = a_tree.Compare(null);
				DelayedAppend(a_tree, res, a_tree.LowerAndUpperMatch());
				if (res == 0)
				{
					i_greatTail = i_greatTail.Append(i_appendHead, i_appendTail);
					ResetDelayedAppend();
				}
				if (res > 0)
				{
					FindGreatestEqualFromEqual((com.db4o.inside.ix.IxTree)a_tree._preceding);
				}
				else
				{
					FindGreatestEqualFromEqual((com.db4o.inside.ix.IxTree)a_tree._subsequent);
				}
			}
		}

		private void FindSmallestEqualFromEqual(com.db4o.inside.ix.IxTree a_tree)
		{
			if (a_tree != null)
			{
				int res = a_tree.Compare(null);
				DelayedAppend(a_tree, res, a_tree.LowerAndUpperMatch());
				if (res == 0)
				{
					i_smallTail = i_smallTail.Append(i_appendHead, i_appendTail);
					ResetDelayedAppend();
				}
				if (res < 0)
				{
					FindSmallestEqualFromEqual((com.db4o.inside.ix.IxTree)a_tree._subsequent);
				}
				else
				{
					FindSmallestEqualFromEqual((com.db4o.inside.ix.IxTree)a_tree._preceding);
				}
			}
		}

		private void ResetDelayedAppend()
		{
			i_appendHead = null;
			i_appendTail = null;
		}

		public virtual void VisitAll(com.db4o.foundation.Visitor4 visitor)
		{
			if (i_take[com.db4o.QE.EQUAL])
			{
				if (i_greatHead != null)
				{
					Add(visitor, i_greatHead, i_greatHead.i_next, i_smallHead.i_next);
				}
			}
			if (i_take[com.db4o.QE.SMALLER])
			{
				com.db4o.inside.ix.IxPath head = i_smallHead;
				while (head != null)
				{
					head.AddPrecedingToCandidatesTree(visitor);
					head = head.i_next;
				}
			}
			if (i_take[com.db4o.QE.GREATER])
			{
				com.db4o.inside.ix.IxPath head = i_greatHead;
				while (head != null)
				{
					head.AddSubsequentToCandidatesTree(visitor);
					head = head.i_next;
				}
			}
		}

		public virtual void VisitPreceding(com.db4o.inside.freespace.FreespaceVisitor visitor
			)
		{
			if (i_smallHead != null)
			{
				i_smallHead.VisitPreceding(visitor);
			}
		}

		public virtual void VisitSubsequent(com.db4o.inside.freespace.FreespaceVisitor visitor
			)
		{
			if (i_greatHead != null)
			{
				i_greatHead.VisitSubsequent(visitor);
			}
		}

		public virtual void VisitMatch(com.db4o.inside.freespace.FreespaceVisitor visitor
			)
		{
			if (i_smallHead != null)
			{
				i_smallHead.VisitMatch(visitor);
			}
		}
	}
}
