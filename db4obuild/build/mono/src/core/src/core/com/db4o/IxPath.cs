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
	/// <summary>
	/// Index Path to represent a list of traversed index tree entries,
	/// used by IxTraverser
	/// </summary>
	internal class IxPath : j4o.lang.Cloneable, com.db4o.Visitor4
	{
		private com.db4o.QCandidates i_candidates;

		private com.db4o.Tree i_candidatesTree;

		internal int i_comparisonResult;

		internal int[] i_lowerAndUpperMatch;

		internal int i_upperNull = -1;

		internal com.db4o.IxPath i_next;

		internal com.db4o.IxTraverser i_traverser;

		internal com.db4o.IxTree i_tree;

		internal IxPath(com.db4o.IxTraverser a_traverser, com.db4o.IxPath a_next, com.db4o.IxTree
			 a_tree, int a_comparisonResult)
		{
			i_traverser = a_traverser;
			i_next = a_next;
			i_tree = a_tree;
			i_comparisonResult = a_comparisonResult;
			if (a_tree is com.db4o.IxFileRange)
			{
				i_lowerAndUpperMatch = a_tree.i_fieldTransaction.i_index.fileRangeReader().lowerAndUpperMatches
					();
			}
		}

		internal virtual com.db4o.Tree addPrecedingToCandidatesTree(com.db4o.Tree a_tree, 
			com.db4o.QCandidates a_candidates)
		{
			i_candidatesTree = a_tree;
			i_candidates = a_candidates;
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
				i_candidatesTree = i_tree.addToCandidatesTree(i_candidatesTree, i_candidates, lowerAndUpperMatch
					);
			}
			else
			{
				if (i_comparisonResult < 0)
				{
					visit(i_tree);
				}
			}
			return i_candidatesTree;
		}

		internal virtual com.db4o.Tree addSubsequentToCandidatesTree(com.db4o.Tree a_tree
			, com.db4o.QCandidates a_candidates)
		{
			i_candidatesTree = a_tree;
			i_candidates = a_candidates;
			if (i_tree.i_subsequent != null)
			{
				if (i_next == null || i_next.i_tree != i_tree.i_subsequent)
				{
					i_tree.i_subsequent.traverse(this);
				}
			}
			if (i_lowerAndUpperMatch != null)
			{
				int[] lowerAndUpperMatch = new int[] { i_lowerAndUpperMatch[1] + 1, ((com.db4o.IxFileRange
					)i_tree)._entries - 1 };
				i_candidatesTree = i_tree.addToCandidatesTree(i_candidatesTree, i_candidates, lowerAndUpperMatch
					);
			}
			else
			{
				if (i_comparisonResult > 0)
				{
					visit(i_tree);
				}
			}
			return i_candidatesTree;
		}

		internal virtual com.db4o.Tree addToCandidatesTree(com.db4o.Tree a_tree, com.db4o.QCandidates
			 a_candidates)
		{
			if (i_comparisonResult == 0 && i_traverser.i_take[1])
			{
				a_tree = i_tree.addToCandidatesTree(a_tree, a_candidates, i_lowerAndUpperMatch);
			}
			return a_tree;
		}

		internal virtual com.db4o.IxPath append(com.db4o.IxPath a_head, com.db4o.IxPath a_tail
			)
		{
			if (a_head == null)
			{
				return this;
			}
			i_next = a_head;
			return a_tail;
		}

		internal virtual com.db4o.IxPath append(com.db4o.IxTree a_tree, int a_comparisonResult
			)
		{
			i_next = new com.db4o.IxPath(i_traverser, null, a_tree, a_comparisonResult);
			i_next.i_tree = a_tree;
			return i_next;
		}

		internal virtual bool carriesTheSame(com.db4o.IxPath a_path)
		{
			return i_tree == a_path.i_tree;
		}

		private void checkUpperNull()
		{
			if (i_upperNull == -1)
			{
				i_traverser.i_handler.prepareComparison(null);
				i_tree.compare(null);
				int[] nullMatches = i_tree.i_fieldTransaction.i_index.fileRangeReader().lowerAndUpperMatches
					();
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

		internal virtual int countMatching()
		{
			if (i_comparisonResult == 0)
			{
				if (i_lowerAndUpperMatch == null)
				{
					if (i_tree is com.db4o.IxRemove)
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
					preceding += i_tree.i_preceding.i_size;
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
				if (i_comparisonResult < 0 && !(i_tree is com.db4o.IxRemove))
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
					subsequent += i_tree.i_subsequent.i_size;
				}
			}
			if (i_lowerAndUpperMatch != null)
			{
				subsequent += ((com.db4o.IxFileRange)i_tree)._entries - i_lowerAndUpperMatch[1] -
					 1;
			}
			else
			{
				if (i_comparisonResult > 0 && !(i_tree is com.db4o.IxRemove))
				{
					subsequent++;
				}
			}
			return subsequent;
		}

		internal virtual com.db4o.IxPath shallowClone()
		{
			try
			{
				return (com.db4o.IxPath)j4o.lang.JavaSystem.clone(this);
			}
			catch (j4o.lang.CloneNotSupportedException e)
			{
			}
			return null;
		}

		public override string ToString()
		{
			return i_tree.ToString();
		}

		public virtual void visit(object a_object)
		{
			i_candidatesTree = ((com.db4o.IxTree)a_object).addToCandidatesTree(i_candidatesTree
				, i_candidates, null);
		}
	}
}
