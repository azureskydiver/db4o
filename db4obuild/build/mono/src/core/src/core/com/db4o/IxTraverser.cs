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
	/// <summary>Index traverser</summary>
	internal class IxTraverser
	{
		private com.db4o.IxPath i_appendHead;

		private com.db4o.IxPath i_appendTail;

		internal com.db4o.QCandidates i_candidates;

		private com.db4o.IxPath i_greatHead;

		private com.db4o.IxPath i_greatTail;

		internal com.db4o.YapDataType i_handler;

		private com.db4o.IxPath i_smallHead;

		private com.db4o.IxPath i_smallTail;

		internal com.db4o.Tree i_tree;

		internal bool[] i_take;

		private void add(com.db4o.IxPath a_previousPath, com.db4o.IxPath a_great, com.db4o.IxPath
			 a_small)
		{
			addPathTree(a_previousPath);
			if (a_great != null && a_small != null && a_great.carriesTheSame(a_small))
			{
				add(a_great, a_great.i_next, a_small.i_next);
				return;
			}
			addGreater(a_small);
			addSmaller(a_great);
		}

		private void addGreater(com.db4o.IxPath a_path)
		{
			if (a_path != null)
			{
				if (a_path.i_next == null)
				{
					addSubsequent(a_path);
				}
				else
				{
					if (a_path.i_next.i_tree == a_path.i_tree.i_preceding)
					{
						addSubsequent(a_path);
					}
					else
					{
						addPathTree(a_path);
					}
					addGreater(a_path.i_next);
				}
			}
		}

		private void addPathTree(com.db4o.IxPath a_path)
		{
			if (a_path != null)
			{
				i_tree = a_path.addToCandidatesTree(i_tree, i_candidates);
			}
		}

		private void addAll(com.db4o.Tree a_tree)
		{
			if (a_tree != null)
			{
				i_tree = ((com.db4o.IxTree)a_tree).addToCandidatesTree(i_tree, i_candidates, null
					);
				addAll(a_tree.i_preceding);
				addAll(a_tree.i_subsequent);
			}
		}

		private void addPreceding(com.db4o.IxPath a_path)
		{
			addPathTree(a_path);
			addAll(a_path.i_tree.i_preceding);
		}

		private void addSmaller(com.db4o.IxPath a_path)
		{
			if (a_path != null)
			{
				if (a_path.i_next == null)
				{
					addPreceding(a_path);
				}
				else
				{
					if (a_path.i_next.i_tree == a_path.i_tree.i_subsequent)
					{
						addPreceding(a_path);
					}
					else
					{
						addPathTree(a_path);
					}
					addSmaller(a_path.i_next);
				}
			}
		}

		private void addSubsequent(com.db4o.IxPath a_path)
		{
			addPathTree(a_path);
			addAll(a_path.i_tree.i_subsequent);
		}

		private int countGreater(com.db4o.IxPath a_path, int a_sum)
		{
			if (a_path.i_next == null)
			{
				return a_sum + countSubsequent(a_path);
			}
			else
			{
				if (a_path.i_next.i_tree == a_path.i_tree.i_preceding)
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

		private int countPreceding(com.db4o.IxPath a_path)
		{
			return com.db4o.Tree.size(a_path.i_tree.i_preceding) + a_path.countMatching();
		}

		private int countSmaller(com.db4o.IxPath a_path, int a_sum)
		{
			if (a_path.i_next == null)
			{
				return a_sum + countPreceding(a_path);
			}
			else
			{
				if (a_path.i_next.i_tree == a_path.i_tree.i_subsequent)
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

		private int countSpan(com.db4o.IxPath a_previousPath, com.db4o.IxPath a_great, com.db4o.IxPath
			 a_small)
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

		private int countSubsequent(com.db4o.IxPath a_path)
		{
			return com.db4o.Tree.size(a_path.i_tree.i_subsequent) + a_path.countMatching();
		}

		private void delayedAppend(com.db4o.IxTree a_tree, int a_comparisonResult)
		{
			if (i_appendHead == null)
			{
				i_appendHead = new com.db4o.IxPath(this, null, a_tree, a_comparisonResult);
				i_appendTail = i_appendHead;
			}
			else
			{
				i_appendTail = i_appendTail.append(a_tree, a_comparisonResult);
			}
		}

		private void findBoth()
		{
			if (i_greatTail.i_comparisonResult == 0)
			{
				findSmallestEqualFromEqual((com.db4o.IxTree)i_greatTail.i_tree.i_preceding);
				resetDelayedAppend();
				findGreatestEqualFromEqual((com.db4o.IxTree)i_greatTail.i_tree.i_subsequent);
			}
			else
			{
				if (i_greatTail.i_comparisonResult < 0)
				{
					findBoth1((com.db4o.IxTree)i_greatTail.i_tree.i_subsequent);
				}
				else
				{
					findBoth1((com.db4o.IxTree)i_greatTail.i_tree.i_preceding);
				}
			}
		}

		private void findBoth1(com.db4o.IxTree a_tree)
		{
			if (a_tree != null)
			{
				int res = a_tree.compare(null);
				i_greatTail = i_greatTail.append(a_tree, res);
				i_smallTail = i_smallTail.append(a_tree, res);
				findBoth();
			}
		}

		internal virtual int findBoundsQuery(com.db4o.QConObject a_qcon, com.db4o.IxTree 
			a_tree)
		{
			if (!a_qcon.i_evaluator.supportsIndex())
			{
				return -1;
			}
			i_take = new bool[] { false, false, false, false };
			a_qcon.i_evaluator.indexBitMap(i_take);
			return findBounds1(a_qcon.i_object, a_tree);
		}

		internal virtual int findBoundsExactMatch(object a_constraint, com.db4o.IxTree a_tree
			)
		{
			i_take = new bool[] { false, true, false, false };
			return findBounds1(a_constraint, a_tree);
		}

		private int findBounds1(object a_constraint, com.db4o.IxTree a_tree)
		{
			if (a_tree != null)
			{
				i_handler = a_tree.handler();
				i_handler.prepareComparison(a_constraint);
				int res = a_tree.compare(null);
				i_greatHead = new com.db4o.IxPath(this, null, a_tree, res);
				i_greatTail = i_greatHead;
				i_smallHead = i_greatHead.shallowClone();
				i_smallTail = i_smallHead;
				findBoth();
				int span = 0;
				if (i_take[1])
				{
					span += countSpan(i_greatHead, i_greatHead.i_next, i_smallHead.i_next);
				}
				if (i_take[0])
				{
					com.db4o.IxPath head = i_smallHead;
					while (head != null)
					{
						span += head.countPreceding(i_take[3]);
						head = head.i_next;
					}
				}
				if (i_take[2])
				{
					com.db4o.IxPath head = i_greatHead;
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

		private void findGreatestEqual(com.db4o.IxTree a_tree)
		{
			int res = a_tree.compare(null);
			i_greatTail = i_greatTail.append(a_tree, res);
			if (res == 0)
			{
				findGreatestEqualFromEqual(a_tree);
			}
			else
			{
				if (res < 0)
				{
					if (a_tree.i_subsequent != null)
					{
						findGreatestEqual((com.db4o.IxTree)a_tree.i_subsequent);
					}
				}
				else
				{
					if (a_tree.i_preceding != null)
					{
						findGreatestEqual((com.db4o.IxTree)a_tree.i_preceding);
					}
				}
			}
		}

		private void findGreatestEqualFromEqual(com.db4o.IxTree a_tree)
		{
			if (a_tree != null)
			{
				int res = a_tree.compare(null);
				delayedAppend(a_tree, res);
				if (res == 0)
				{
					i_greatTail = i_greatTail.append(i_appendHead, i_appendTail);
					resetDelayedAppend();
				}
				if (res > 0)
				{
					findGreatestEqualFromEqual((com.db4o.IxTree)a_tree.i_preceding);
				}
				else
				{
					findGreatestEqualFromEqual((com.db4o.IxTree)a_tree.i_subsequent);
				}
			}
		}

		private void findSmallestEqual(com.db4o.IxTree a_tree)
		{
			int res = a_tree.compare(null);
			i_smallTail = i_smallTail.append(a_tree, res);
			if (res == 0)
			{
				findSmallestEqualFromEqual(a_tree);
			}
			else
			{
				if (res < 0)
				{
					if (a_tree.i_subsequent != null)
					{
						findSmallestEqual((com.db4o.IxTree)a_tree.i_subsequent);
					}
				}
				else
				{
					if (a_tree.i_preceding != null)
					{
						findSmallestEqual((com.db4o.IxTree)a_tree.i_preceding);
					}
				}
			}
		}

		private void findSmallestEqualFromEqual(com.db4o.IxTree a_tree)
		{
			if (a_tree != null)
			{
				int res = a_tree.compare(null);
				delayedAppend(a_tree, res);
				if (res == 0)
				{
					i_smallTail = i_smallTail.append(i_appendHead, i_appendTail);
					resetDelayedAppend();
				}
				if (res < 0)
				{
					findSmallestEqualFromEqual((com.db4o.IxTree)a_tree.i_subsequent);
				}
				else
				{
					findSmallestEqualFromEqual((com.db4o.IxTree)a_tree.i_preceding);
				}
			}
		}

		internal virtual com.db4o.Tree getMatches(com.db4o.QCandidates a_candidates)
		{
			i_candidates = a_candidates;
			i_tree = null;
			if (i_take[1])
			{
				if (i_greatHead != null)
				{
					add(i_greatHead, i_greatHead.i_next, i_smallHead.i_next);
				}
			}
			if (i_take[0])
			{
				com.db4o.IxPath head = i_smallHead;
				while (head != null)
				{
					i_tree = head.addPrecedingToCandidatesTree(i_tree, a_candidates);
					head = head.i_next;
				}
			}
			if (i_take[2])
			{
				com.db4o.IxPath head = i_greatHead;
				while (head != null)
				{
					i_tree = head.addSubsequentToCandidatesTree(i_tree, a_candidates);
					head = head.i_next;
				}
			}
			return i_tree;
		}

		private void resetDelayedAppend()
		{
			i_appendHead = null;
			i_appendTail = null;
		}
	}
}
