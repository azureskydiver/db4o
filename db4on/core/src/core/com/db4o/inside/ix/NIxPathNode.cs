namespace com.db4o.inside.ix
{
	/// <exclude></exclude>
	public class NIxPathNode
	{
		internal com.db4o.inside.ix.IxTree _tree;

		internal int _comparisonResult;

		internal int[] _lowerAndUpperMatch;

		internal com.db4o.inside.ix.NIxPathNode _next;

		/// <summary>
		/// returns 0, if keys are equal
		/// uses this - other
		/// returns positive if this is greater than a_to
		/// returns negative if this is smaller than a_to
		/// </summary>
		internal virtual int compare(com.db4o.inside.ix.NIxPathNode other, int myType, int
			 otherType)
		{
			if (_next == null)
			{
				if (other._next != null)
				{
					return other.ascending() ? -1 : 1;
				}
				if (_lowerAndUpperMatch == null)
				{
					return myType - otherType;
				}
				if (_lowerAndUpperMatch[0] != other._lowerAndUpperMatch[0])
				{
					int res0 = _lowerAndUpperMatch[0] - other._lowerAndUpperMatch[0];
					if (res0 == 0)
					{
						return myType - otherType;
					}
					return res0;
				}
				if (_lowerAndUpperMatch[1] != other._lowerAndUpperMatch[1])
				{
					int res1 = _lowerAndUpperMatch[1] - other._lowerAndUpperMatch[1];
					if (res1 == 0)
					{
						return myType - otherType;
					}
					return res1;
				}
				return myType - otherType;
			}
			if (other._next == null)
			{
				return ascending() ? 1 : -1;
			}
			com.db4o.inside.ix.IxTree otherNext = other._next._tree;
			if (otherNext == _next._tree)
			{
				return _next.compare(other._next, myType, otherType);
			}
			if (_tree.i_subsequent == otherNext)
			{
				return -1;
			}
			return 1;
		}

		internal virtual bool ascending()
		{
			return _tree.i_subsequent == _next._tree;
		}

		internal virtual bool carriesTheSame(com.db4o.inside.ix.NIxPathNode node)
		{
			if (node == null)
			{
				return false;
			}
			return _tree == node._tree;
		}

		internal virtual int countPreceding()
		{
			int preceding = 0;
			if (_tree.i_preceding != null)
			{
				if (_next == null || _next._tree != _tree.i_preceding)
				{
					preceding += _tree.i_preceding.size();
				}
			}
			if (_lowerAndUpperMatch != null)
			{
				preceding += _lowerAndUpperMatch[0];
			}
			else
			{
				if (_comparisonResult < 0 && !(_tree is com.db4o.inside.ix.IxRemove))
				{
					preceding++;
				}
			}
			return preceding;
		}

		internal virtual int countMatching()
		{
			if (_comparisonResult == 0)
			{
				if (_lowerAndUpperMatch == null)
				{
					if (_tree is com.db4o.inside.ix.IxRemove)
					{
						return 0;
					}
					return 1;
				}
				return _lowerAndUpperMatch[1] - _lowerAndUpperMatch[0] + 1;
			}
			return 0;
		}

		internal virtual int countSubsequent()
		{
			int subsequent = 0;
			if (_tree.i_subsequent != null)
			{
				if (_next == null || _next._tree != _tree.i_subsequent)
				{
					subsequent += _tree.i_subsequent.size();
				}
			}
			if (_lowerAndUpperMatch != null)
			{
				subsequent += ((com.db4o.inside.ix.IxFileRange)_tree)._entries - _lowerAndUpperMatch
					[1] - 1;
			}
			else
			{
				if (_comparisonResult > 0 && !(_tree is com.db4o.inside.ix.IxRemove))
				{
					subsequent++;
				}
			}
			return subsequent;
		}

		internal virtual int countSpan(com.db4o.inside.ix.NIxPath greatPath, com.db4o.inside.ix.NIxPath
			 smallPath, com.db4o.inside.ix.NIxPathNode small)
		{
			if (_comparisonResult != 0)
			{
				return 0;
			}
			if (_lowerAndUpperMatch == null)
			{
				if (_tree is com.db4o.inside.ix.IxRemove)
				{
					return 0;
				}
				if (greatPath._takeMatches || smallPath._takeMatches)
				{
					return 1;
				}
				return 0;
			}
			if (_lowerAndUpperMatch[0] == small._lowerAndUpperMatch[0])
			{
				if (greatPath._takeMatches || smallPath._takeMatches)
				{
					return _lowerAndUpperMatch[1] - _lowerAndUpperMatch[0] + 1;
				}
				return 0;
			}
			int upper = _lowerAndUpperMatch[0] - 1;
			int lower = 0;
			if (!smallPath._takePreceding)
			{
				lower = small._lowerAndUpperMatch[0];
			}
			if (!smallPath._takeMatches)
			{
				lower = small._lowerAndUpperMatch[1] + 1;
			}
			return upper - lower + 1;
		}

		internal virtual void traversePreceding(com.db4o.foundation.Visitor4Dispatch dispatcher
			)
		{
			if (_tree.i_preceding != null)
			{
				if (_next == null || _next._tree != _tree.i_preceding)
				{
					_tree.i_preceding.traverse(dispatcher);
				}
			}
			if (_lowerAndUpperMatch != null)
			{
				int[] lowerAndUpperMatch = new int[] { 0, _lowerAndUpperMatch[0] - 1 };
				_tree.visit(dispatcher._target, lowerAndUpperMatch);
				return;
			}
			if (_comparisonResult < 0 && !(_tree is com.db4o.inside.ix.IxRemove))
			{
				_tree.visit(dispatcher._target);
			}
		}

		internal virtual void traverseMatching(com.db4o.foundation.Visitor4Dispatch dispatcher
			)
		{
			if (_comparisonResult == 0)
			{
				_tree.visit(dispatcher._target, _lowerAndUpperMatch);
			}
		}

		internal virtual void traverseSubsequent(com.db4o.foundation.Visitor4Dispatch dispatcher
			)
		{
			if (_tree.i_subsequent != null)
			{
				if (_next == null || _next._tree != _tree.i_subsequent)
				{
					_tree.i_subsequent.traverse(dispatcher);
				}
			}
			if (_lowerAndUpperMatch != null)
			{
				int[] lowerAndUpperMatch = new int[] { _lowerAndUpperMatch[1] + 1, ((com.db4o.inside.ix.IxFileRange
					)_tree)._entries - 1 };
				_tree.visit(dispatcher._target, lowerAndUpperMatch);
				return;
			}
			if (_comparisonResult > 0)
			{
				_tree.visit(dispatcher._target);
			}
		}

		internal virtual void traverseSpan(com.db4o.inside.ix.NIxPath greatPath, com.db4o.inside.ix.NIxPath
			 smallPath, com.db4o.inside.ix.NIxPathNode small, com.db4o.foundation.Visitor4Dispatch
			 dispatcher)
		{
			if (_comparisonResult != 0)
			{
				return;
			}
			if (_lowerAndUpperMatch == null)
			{
				if (greatPath._takeMatches || smallPath._takeMatches)
				{
					_tree.visit(dispatcher._target);
					return;
				}
			}
			if (_lowerAndUpperMatch[0] == small._lowerAndUpperMatch[0])
			{
				if (greatPath._takeMatches || smallPath._takeMatches)
				{
					_tree.visit(dispatcher._target, _lowerAndUpperMatch);
				}
				return;
			}
			int upper = _lowerAndUpperMatch[0] - 1;
			int lower = 0;
			if (!smallPath._takePreceding)
			{
				lower = small._lowerAndUpperMatch[0];
			}
			if (!smallPath._takeMatches)
			{
				lower = small._lowerAndUpperMatch[1] + 1;
			}
			_tree.visit(dispatcher._target, new int[] { lower, upper });
		}

		public override string ToString()
		{
			return base.ToString();
			return _tree.ToString() + "\n cmp: " + _comparisonResult;
		}
	}
}
