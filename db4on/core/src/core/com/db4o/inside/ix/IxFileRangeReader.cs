namespace com.db4o.inside.ix
{
	/// <exclude></exclude>
	internal class IxFileRangeReader
	{
		private int _baseAddress;

		private int _baseAddressOffset;

		private int _addressOffset;

		private readonly com.db4o.inside.ix.Indexable4 _handler;

		private int _lower;

		private int _upper;

		private int _cursor;

		private readonly com.db4o.YapReader _reader;

		internal readonly int _slotLength;

		internal readonly int _linkLegth;

		internal IxFileRangeReader(com.db4o.inside.ix.Indexable4 handler)
		{
			_handler = handler;
			_linkLegth = handler.linkLength();
			_slotLength = _linkLegth + com.db4o.YapConst.YAPINT_LENGTH;
			_reader = new com.db4o.YapReader(_slotLength);
		}

		internal virtual com.db4o.Tree add(com.db4o.inside.ix.IxFileRange fileRange, com.db4o.Tree
			 newTree)
		{
			setFileRange(fileRange);
			com.db4o.YapFile yf = fileRange.stream();
			com.db4o.Transaction trans = fileRange.trans();
			while (true)
			{
				_reader.read(yf, _baseAddress, _baseAddressOffset + _addressOffset);
				_reader._offset = 0;
				int cmp = compare(trans);
				if (cmp == 0)
				{
					int parentID = _reader.readInt();
					cmp = parentID - ((com.db4o.inside.ix.IxPatch)newTree)._parentID;
				}
				if (cmp > 0)
				{
					_upper = _cursor - 1;
					if (_upper < _lower)
					{
						_upper = _lower;
					}
				}
				else
				{
					if (cmp < 0)
					{
						_lower = _cursor + 1;
						if (_lower > _upper)
						{
							_lower = _upper;
						}
					}
					else
					{
						if (newTree is com.db4o.inside.ix.IxRemove)
						{
							com.db4o.inside.ix.IxRemove ir = (com.db4o.inside.ix.IxRemove)newTree;
							if (_cursor == 0)
							{
								newTree._preceding = fileRange._preceding;
								if (fileRange._entries == 1)
								{
									newTree._subsequent = fileRange._subsequent;
									return newTree.balanceCheckNulls();
								}
								fileRange._entries--;
								fileRange.incrementAddress(_slotLength);
								fileRange._preceding = null;
								newTree._subsequent = fileRange;
							}
							else
							{
								if (_cursor + 1 == fileRange._entries)
								{
									newTree._preceding = fileRange;
									newTree._subsequent = fileRange._subsequent;
									fileRange._subsequent = null;
									fileRange._entries--;
								}
								else
								{
									return insert(fileRange, newTree, _cursor, 0);
								}
							}
							fileRange.calculateSize();
							return newTree.balanceCheckNulls();
						}
						else
						{
							if (_cursor == 0)
							{
								newTree._subsequent = fileRange;
								return newTree.rotateLeft();
							}
							else
							{
								if (_cursor == fileRange._entries)
								{
									newTree._preceding = fileRange;
									return newTree.rotateRight();
								}
							}
							return insert(fileRange, newTree, _cursor, cmp);
						}
					}
				}
				if (!adjustCursor())
				{
					if (_cursor == 0 && cmp > 0)
					{
						return fileRange.add(newTree, 1);
					}
					if (_cursor == fileRange._entries - 1 && cmp < 0)
					{
						return fileRange.add(newTree, -1);
					}
					return insert(fileRange, newTree, _cursor, cmp);
				}
			}
		}

		private bool adjustCursor()
		{
			if (_upper < _lower)
			{
				return false;
			}
			int oldCursor = _cursor;
			_cursor = _lower + ((_upper - _lower) / 2);
			if (_cursor == oldCursor && _cursor == _lower && _lower < _upper)
			{
				_cursor++;
			}
			_addressOffset = _cursor * _slotLength;
			return _cursor != oldCursor;
		}

		internal virtual int compare(com.db4o.inside.ix.IxFileRange fileRange, int[] matches
			)
		{
			setFileRange(fileRange);
			com.db4o.YapFile yf = fileRange.stream();
			com.db4o.Transaction trans = fileRange.trans();
			int res = 0;
			while (true)
			{
				_reader.read(yf, _baseAddress, _baseAddressOffset + _addressOffset);
				_reader._offset = 0;
				int cmp = compare(trans);
				if (cmp > 0)
				{
					_upper = _cursor - 1;
				}
				else
				{
					if (cmp < 0)
					{
						_lower = _cursor + 1;
					}
					else
					{
						break;
					}
				}
				if (!adjustCursor())
				{
					if (_cursor <= 0)
					{
						if (!(cmp < 0 && fileRange._entries > 1))
						{
							res = cmp;
						}
					}
					else
					{
						if (_cursor >= (fileRange._entries - 1))
						{
							if (cmp < 0)
							{
								res = cmp;
							}
						}
					}
					break;
				}
			}
			matches[0] = _lower;
			matches[1] = _upper;
			if (_lower > _upper)
			{
				return res;
			}
			int tempCursor = _cursor;
			_upper = _cursor;
			adjustCursor();
			while (true)
			{
				_reader.read(yf, _baseAddress, _baseAddressOffset + _addressOffset);
				_reader._offset = 0;
				int cmp = compare(trans);
				if (cmp == 0)
				{
					_upper = _cursor;
				}
				else
				{
					_lower = _cursor + 1;
					if (_lower > _upper)
					{
						matches[0] = _upper;
						break;
					}
				}
				if (!adjustCursor())
				{
					matches[0] = _upper;
					break;
				}
			}
			_upper = matches[1];
			_lower = tempCursor;
			if (_lower > _upper)
			{
				_lower = _upper;
			}
			adjustCursor();
			while (true)
			{
				_reader.read(yf, _baseAddress, _baseAddressOffset + _addressOffset);
				_reader._offset = 0;
				int cmp = compare(trans);
				if (cmp == 0)
				{
					_lower = _cursor;
				}
				else
				{
					_upper = _cursor - 1;
					if (_upper < _lower)
					{
						matches[1] = _lower;
						break;
					}
				}
				if (!adjustCursor())
				{
					matches[1] = _lower;
					break;
				}
			}
			return res;
		}

		private int compare(com.db4o.Transaction trans)
		{
			return _handler.compareTo(_handler.comparableObject(trans, _handler.readIndexEntry
				(_reader)));
		}

		private com.db4o.Tree insert(com.db4o.inside.ix.IxFileRange fileRange, com.db4o.Tree
			 a_new, int a_cursor, int a_cmp)
		{
			int incStartNewAt = a_cmp <= 0 ? 1 : 0;
			int newAddressOffset = (a_cursor + incStartNewAt) * _slotLength;
			int newEntries = fileRange._entries - a_cursor - incStartNewAt;
			fileRange._entries = a_cmp < 0 ? a_cursor + 1 : a_cursor;
			com.db4o.inside.ix.IxFileRange ifr = new com.db4o.inside.ix.IxFileRange(fileRange
				._fieldTransaction, _baseAddress, _baseAddressOffset + newAddressOffset, newEntries
				);
			ifr._subsequent = fileRange._subsequent;
			fileRange._subsequent = null;
			a_new._preceding = fileRange.balanceCheckNulls();
			a_new._subsequent = ifr.balanceCheckNulls();
			return a_new.balance();
		}

		private void setFileRange(com.db4o.inside.ix.IxFileRange a_fr)
		{
			_lower = 0;
			_upper = a_fr._entries - 1;
			_baseAddress = a_fr._address;
			_baseAddressOffset = a_fr._addressOffset;
			adjustCursor();
		}
	}
}
