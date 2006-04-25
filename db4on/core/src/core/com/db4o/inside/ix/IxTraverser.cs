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

		public const int NULLS = 0;

		public const int SMALLER = 1;

		public const int EQUAL = 2;

		public const int GREATER = 3;

		private void add(com.db4o.foundation.Visitor4 visitor, com.db4o.inside.ix.IxPath 
			a_previousPath, com.db4o.inside.ix.IxPath a_great, com.db4o.inside.ix.IxPath a_small
			)
		{
			addPathTree(visitor, a_previousPath);
			if (a_great != null && a_small != null && a_great.carriesTheSame(a_small))
			{
				add(visitor, a_great, a_great.i_next, a_small.i_next);
				return;
			}
			addGreater(visitor, a_small);
			addSmaller(visitor, a_great);
		}

		private void addAll(com.db4o.foundation.Visitor4 visitor, com.db4o.Tree a_tree)
		{
			if (a_tree != null)
			{
				((com.db4o.inside.ix.IxTree)a_tree).visit(visitor, null);
				addAll(visitor, a_tree._preceding);
				addAll(visitor, a_tree._subsequent);
			}
		}

		private void addGreater(com.db4o.foundation.Visitor4 visitor, com.db4o.inside.ix.IxPath
			 a_path)
		{
			if (a_path != null)
			{
				if (a_path.i_next == null)
				{
					addSubsequent(visitor, a_path);
				}
				else
				{
					if (a_path.i_next.i_tree == a_path.i_tree._preceding)
					{
						addSubsequent(visitor, a_path);
					}
					else
					{
						addPathTree(visitor, a_path);
					}
					addGreater(visitor, a_path.i_next);
				}
			}
		}

		private void addPathTree(com.db4o.foundation.Visitor4 visitor, com.db4o.inside.ix.IxPath
			 a_path)
		{
			if (a_path != null)
			{
				a_path.add(visitor);
			}
		}

		private void addPreceding(com.db4o.foundation.Visitor4 visitor, com.db4o.inside.ix.IxPath
			 a_path)
		{
			addPathTree(visitor, a_path);
			addAll(visitor, a_path.i_tree._preceding);
		}

		private void addSmaller(com.db4o.foundation.Visitor4 visitor, com.db4o.inside.ix.IxPath
			 a_path)
		{
			if (a_path != null)
			{
				if (a_path.i_next == null)
				{
					addPreceding(visitor, a_path);
				}
				else
				{
					if (a_path.i_next.i_tree == a_path.i_tree._subsequent)
					{
						addPreceding(visitor, a_path);
					}
					else
					{
						addPathTree(visitor, a_path);
					}
					addSmaller(visitor, a_path.i_next);
				}
			}
		}

		private void addSubsequent(com.db4o.foundation.Visitor4 visitor, com.db4o.inside.ix.IxPath
			 a_path)
		{
			addPathTree(visitor, a_path);
			addAll(visitor, a_path.i_tree._subsequent);
		}

		private com.db4o.inside.ix.NIxPath createNIxPath(com.db4o.inside.ix.NIxPathNode head
			, bool takePreceding, bool takeMatches, bool takeSubsequent, int pathType)
		{
			com.db4o.inside.ix.NIxPath np = new com.db4o.inside.ix.NIxPath(head, takePreceding
				, takeMatches, takeSubsequent, pathType);
			return np;
		}

		public virtual com.db4o.inside.ix.NIxPaths convert()
		{
			com.db4o.inside.ix.NIxPaths res = new com.db4o.inside.ix.NIxPaths();
			if (i_take[NULLS] || i_take[SMALLER] || i_take[EQUAL])
			{
				com.db4o.inside.ix.NIxPath smaller = createNIxPath(i_smallHead.convert(), i_take[
					SMALLER], i_take[EQUAL], i_take[GREATER], SMALLER);
				res.add(smaller);
			}
			if (i_take[EQUAL] || i_take[GREATER])
			{
				com.db4o.inside.ix.NIxPath greater = createNIxPath(i_greatHead.convert(), i_take[
					SMALLER], i_take[EQUAL], i_take[GREATER], GREATER);
				res.add(greater);
			}
			if (i_take[SMALLER] || i_take[NULLS])
			{
				if (i_smallHead != null)
				{
					if (i_smallHead.i_tree.index()._nullHandling)
					{
						com.db4o.inside.ix.IxPath nullPath = findNullPath();
						com.db4o.inside.ix.NIxPath np = createNIxPath(nullPath.convert(), i_take[NULLS], 
							i_take[NULLS], i_take[SMALLER], NULLS);
						res.add(np);
					}
				}
			}
			return res;
		}

		private int countGreater(com.db4o.inside.ix.IxPath a_path, int a_sum)
		{
			if (a_path.i_next == null)
			{
				return a_sum + countSubsequent(a_path);
			}
			else
			{
				if (a_path.i_next.i_tree == a_path.i_tree._preceding)
				{
					a_sum += countSubsequent(a_path);
				}
				else
				{
					a_sum += a_path.countMatching();
				}
				return countGreater(a_path.i_next, a_sum);
			}
		}

		private int countPreceding(com.db4o.inside.ix.IxPath a_path)
		{
			return com.db4o.Tree.size(a_path.i_tree._preceding) + a_path.countMatching();
		}

		private int countSmaller(com.db4o.inside.ix.IxPath a_path, int a_sum)
		{
			if (a_path.i_next == null)
			{
				return a_sum + countPreceding(a_path);
			}
			else
			{
				if (a_path.i_next.i_tree == a_path.i_tree._subsequent)
				{
					a_sum += countPreceding(a_path);
				}
				else
				{
					a_sum += a_path.countMatching();
				}
				return countSmaller(a_path.i_next, a_sum);
			}
		}

		private int countSpan(com.db4o.inside.ix.IxPath a_previousPath, com.db4o.inside.ix.IxPath
			 a_great, com.db4o.inside.ix.IxPath a_small)
		{
			if (a_great == null)
			{
				if (a_small == null)
				{
					return a_previousPath.countMatching();
				}
				else
				{
					return countGreater(a_small, a_previousPath.countMatching());
				}
			}
			else
			{
				if (a_small == null)
				{
					return countSmaller(a_great, a_previousPath.countMatching());
				}
			}
			if (a_great.carriesTheSame(a_small))
			{
				return countSpan(a_great, a_great.i_next, a_small.i_next);
			}
			return a_previousPath.countMatching() + countGreater(a_small, 0) + countSmaller(a_great
				, 0);
		}

		private int countSubsequent(com.db4o.inside.ix.IxPath a_path)
		{
			return com.db4o.Tree.size(a_path.i_tree._subsequent) + a_path.countMatching();
		}

		private void delayedAppend(com.db4o.inside.ix.IxTree a_tree, int a_comparisonResult
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
				i_appendTail = i_appendTail.append(a_tree, a_comparisonResult, lowerAndUpperMatch
					);
			}
		}

		private void findBoth()
		{
			if (i_greatTail.i_comparisonResult == 0)
			{
				findSmallestEqualFromEqual((com.db4o.inside.ix.IxTree)i_greatTail.i_tree._preceding
					);
				resetDelayedAppend();
				findGreatestEqualFromEqual((com.db4o.inside.ix.IxTree)i_greatTail.i_tree._subsequent
					);
			}
			else
			{
				if (i_greatTail.i_comparisonResult < 0)
				{
					findBoth1((com.db4o.inside.ix.IxTree)i_greatTail.i_tree._subsequent);
				}
				else
				{
					findBoth1((com.db4o.inside.ix.IxTree)i_greatTail.i_tree._preceding);
				}
			}
		}

		private void findBoth1(com.db4o.inside.ix.IxTree a_tree)
		{
			if (a_tree != null)
			{
				int res = a_tree.compare(null);
				int[] lowerAndUpperMatch = a_tree.lowerAndUpperMatch();
				i_greatTail = i_greatTail.append(a_tree, res, lowerAndUpperMatch);
				i_smallTail = i_smallTail.append(a_tree, res, lowerAndUpperMatch);
				findBoth();
			}
		}

		private com.db4o.inside.ix.IxPath findNullPath()
		{
			resetDelayedAppend();
			i_handler.prepareComparison(null);
			com.db4o.inside.ix.IxTree tree = i_greatHead.i_tree;
			int res = tree.compare(null);
			com.db4o.inside.ix.IxPath nullHead = new com.db4o.inside.ix.IxPath(this, null, tree
				, res, tree.lowerAndUpperMatch());
			com.db4o.inside.ix.IxPath[] headTail = new com.db4o.inside.ix.IxPath[] { nullHead
				, nullHead };
			findNullPath1(headTail);
			return headTail[0];
		}

		private void findNullPath1(com.db4o.inside.ix.IxPath[] headTail)
		{
			if (headTail[1].i_comparisonResult == 0)
			{
				findGreatestNullFromNull(headTail, (com.db4o.inside.ix.IxTree)headTail[1].i_tree.
					_subsequent);
			}
			else
			{
				if (headTail[1].i_comparisonResult < 0)
				{
					findNullPath2(headTail, (com.db4o.inside.ix.IxTree)headTail[1].i_tree._subsequent
						);
				}
				else
				{
					findNullPath2(headTail, (com.db4o.inside.ix.IxTree)headTail[1].i_tree._preceding);
				}
			}
		}

		private void findNullPath2(com.db4o.inside.ix.IxPath[] headTail, com.db4o.inside.ix.IxTree
			 tree)
		{
			if (tree != null)
			{
				int res = tree.compare(null);
				headTail[1] = headTail[1].append(tree, res, tree.lowerAndUpperMatch());
				findNullPath1(headTail);
			}
		}

		private void findGreatestNullFromNull(com.db4o.inside.ix.IxPath[] headTail, com.db4o.inside.ix.IxTree
			 tree)
		{
			if (tree != null)
			{
				int res = tree.compare(null);
				delayedAppend(tree, res, tree.lowerAndUpperMatch());
				if (res == 0)
				{
					headTail[1] = headTail[1].append(i_appendHead, i_appendTail);
					resetDelayedAppend();
				}
				if (res > 0)
				{
					findGreatestNullFromNull(headTail, (com.db4o.inside.ix.IxTree)tree._preceding);
				}
				else
				{
					findGreatestNullFromNull(headTail, (com.db4o.inside.ix.IxTree)tree._subsequent);
				}
			}
		}

		public virtual int findBounds(object a_constraint, com.db4o.inside.ix.IxTree a_tree
			)
		{
			if (a_tree != null)
			{
				i_handler = a_tree.handler();
				i_handler.prepareComparison(a_constraint);
				int res = a_tree.compare(null);
				i_greatHead = new com.db4o.inside.ix.IxPath(this, null, a_tree, res, a_tree.lowerAndUpperMatch
					());
				i_greatTail = i_greatHead;
				i_smallHead = (com.db4o.inside.ix.IxPath)i_greatHead.shallowClone();
				i_smallTail = i_smallHead;
				findBoth();
				int span = 0;
				if (i_take[EQUAL])
				{
					span += countSpan(i_greatHead, i_greatHead.i_next, i_smallHead.i_next);
				}
				if (i_take[SMALLER])
				{
					com.db4o.inside.ix.IxPath head = i_smallHead;
					while (head != null)
					{
						span += head.countPreceding(i_take[NULLS]);
						head = head.i_next;
					}
				}
				if (i_take[GREATER])
				{
					com.db4o.inside.ix.IxPath head = i_greatHead;
					while (head != null)
					{
						span += head.countSubsequent();
						head = head.i_next;
					}
				}
				return span;
			}
			return 0;
		}

		public virtual int findBoundsExactMatch(object a_constraint, com.db4o.inside.ix.IxTree
			 a_tree)
		{
			i_take = new bool[] { false, false, false, false };
			i_take[EQUAL] = true;
			return findBounds(a_constraint, a_tree);
		}

		public virtual int findBoundsQuery(com.db4o.QCon a_qcon, object constraint)
		{
			if (!a_qcon.i_evaluator.supportsIndex())
			{
				return -1;
			}
			i_take = new bool[] { false, false, false, false };
			a_qcon.i_evaluator.indexBitMap(i_take);
			return findBounds(constraint, a_qcon.indexRoot());
		}

		private void findGreatestEqual(com.db4o.inside.ix.IxTree a_tree)
		{
			int res = a_tree.compare(null);
			i_greatTail = i_greatTail.append(a_tree, res, a_tree.lowerAndUpperMatch());
			if (res == 0)
			{
				findGreatestEqualFromEqual(a_tree);
			}
			else
			{
				if (res < 0)
				{
					if (a_tree._subsequent != null)
					{
						findGreatestEqual((com.db4o.inside.ix.IxTree)a_tree._subsequent);
					}
				}
				else
				{
					if (a_tree._preceding != null)
					{
						findGreatestEqual((com.db4o.inside.ix.IxTree)a_tree._preceding);
					}
				}
			}
		}

		private void findGreatestEqualFromEqual(com.db4o.inside.ix.IxTree a_tree)
		{
			if (a_tree != null)
			{
				int res = a_tree.compare(null);
				delayedAppend(a_tree, res, a_tree.lowerAndUpperMatch());
				if (res == 0)
				{
					i_greatTail = i_greatTail.append(i_appendHead, i_appendTail);
					resetDelayedAppend();
				}
				if (res > 0)
				{
					findGreatestEqualFromEqual((com.db4o.inside.ix.IxTree)a_tree._preceding);
				}
				else
				{
					findGreatestEqualFromEqual((com.db4o.inside.ix.IxTree)a_tree._subsequent);
				}
			}
		}

		private void findSmallestEqual(com.db4o.inside.ix.IxTree a_tree)
		{
			int res = a_tree.compare(null);
			i_smallTail = i_smallTail.append(a_tree, res, a_tree.lowerAndUpperMatch());
			if (res == 0)
			{
				findSmallestEqualFromEqual(a_tree);
			}
			else
			{
				if (res < 0)
				{
					if (a_tree._subsequent != null)
					{
						findSmallestEqual((com.db4o.inside.ix.IxTree)a_tree._subsequent);
					}
				}
				else
				{
					if (a_tree._preceding != null)
					{
						findSmallestEqual((com.db4o.inside.ix.IxTree)a_tree._preceding);
					}
				}
			}
		}

		private void findSmallestEqualFromEqual(com.db4o.inside.ix.IxTree a_tree)
		{
			if (a_tree != null)
			{
				int res = a_tree.compare(null);
				delayedAppend(a_tree, res, a_tree.lowerAndUpperMatch());
				if (res == 0)
				{
					i_smallTail = i_smallTail.append(i_appendHead, i_appendTail);
					resetDelayedAppend();
				}
				if (res < 0)
				{
					findSmallestEqualFromEqual((com.db4o.inside.ix.IxTree)a_tree._subsequent);
				}
				else
				{
					findSmallestEqualFromEqual((com.db4o.inside.ix.IxTree)a_tree._preceding);
				}
			}
		}

		private void resetDelayedAppend()
		{
			i_appendHead = null;
			i_appendTail = null;
		}

		public virtual void visitAll(com.db4o.foundation.Visitor4 visitor)
		{
			if (i_take[EQUAL])
			{
				if (i_greatHead != null)
				{
					add(visitor, i_greatHead, i_greatHead.i_next, i_smallHead.i_next);
				}
			}
			if (i_take[SMALLER])
			{
				com.db4o.inside.ix.IxPath head = i_smallHead;
				while (head != null)
				{
					head.addPrecedingToCandidatesTree(visitor);
					head = head.i_next;
				}
			}
			if (i_take[GREATER])
			{
				com.db4o.inside.ix.IxPath head = i_greatHead;
				while (head != null)
				{
					head.addSubsequentToCandidatesTree(visitor);
					head = head.i_next;
				}
			}
		}

		public virtual void visitPreceding(com.db4o.inside.freespace.FreespaceVisitor visitor
			)
		{
			if (i_smallHead != null)
			{
				i_smallHead.visitPreceding(visitor);
			}
		}

		public virtual void visitSubsequent(com.db4o.inside.freespace.FreespaceVisitor visitor
			)
		{
			if (i_greatHead != null)
			{
				i_greatHead.visitSubsequent(visitor);
			}
		}

		public virtual void visitMatch(com.db4o.inside.freespace.FreespaceVisitor visitor
			)
		{
			if (i_smallHead != null)
			{
				i_smallHead.visitMatch(visitor);
			}
		}
	}
}
