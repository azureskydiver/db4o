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
	internal class IxFileRangeReader : com.db4o.Readable
	{
		private int _baseAddress;

		private int _baseAddressOffset;

		private int _addressOffset;

		internal com.db4o.IxFileRange _fileRange;

		private readonly com.db4o.YapDataType _handler;

		private com.db4o.QCandidates _candidates;

		private int _lower;

		private int _upper;

		private int _cursor;

		private readonly com.db4o.YapReader _reader;

		private readonly int _slotLength;

		private readonly int _linkLegth;

		internal IxFileRangeReader(com.db4o.YapDataType handler)
		{
			_handler = handler;
			_linkLegth = handler.linkLength();
			_slotLength = _linkLegth + com.db4o.YapConst.YAPINT_LENGTH;
			_reader = new com.db4o.YapReader(_slotLength);
		}

		internal virtual com.db4o.Tree add(com.db4o.IxFileRange fileRange, com.db4o.Tree 
			newTree)
		{
			setFileRange(fileRange);
			com.db4o.YapFile yf = fileRange.stream();
			com.db4o.Transaction trans = fileRange.trans();
			while (true)
			{
				_reader.read(yf, _baseAddress, _baseAddressOffset + _addressOffset);
				_reader._offset = 0;
				int cmp = _handler.compareTo(_handler.indexObject(trans, _handler.readIndexEntry(
					_reader)));
				if (cmp == 0)
				{
					int parentID = _reader.readInt();
					cmp = parentID - ((com.db4o.IxPatch)newTree).i_parentID;
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
						if (newTree is com.db4o.IxRemove)
						{
							com.db4o.IxRemove ir = (com.db4o.IxRemove)newTree;
							if (_cursor == 0)
							{
								newTree.i_preceding = fileRange.i_preceding;
								if (fileRange._entries == 1)
								{
									newTree.i_subsequent = fileRange.i_subsequent;
									return newTree.balanceCheckNulls();
								}
								fileRange._entries--;
								fileRange.incrementAddress(_slotLength);
								fileRange.i_preceding = null;
								newTree.i_subsequent = fileRange;
							}
							else
							{
								if (_cursor + 1 == fileRange._entries)
								{
									newTree.i_preceding = fileRange;
									newTree.i_subsequent = fileRange.i_subsequent;
									fileRange.i_subsequent = null;
									fileRange._entries--;
								}
								else
								{
									return insert(newTree, _cursor, 0);
								}
							}
							fileRange.calculateSize();
							return newTree.balanceCheckNulls();
						}
						else
						{
							if (_cursor == 0)
							{
								newTree.i_subsequent = fileRange;
								return newTree.rotateLeft();
							}
							else
							{
								if (_cursor == fileRange._entries)
								{
									newTree.i_preceding = fileRange;
									return newTree.rotateRight();
								}
							}
							return insert(newTree, _cursor, cmp);
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
					return insert(newTree, _cursor, cmp);
				}
			}
		}

		public virtual com.db4o.Tree addToCandidatesTree(com.db4o.QCandidates candidates, 
			com.db4o.Tree a_tree, com.db4o.IxFileRange a_range, int[] a_LowerAndUpperMatch)
		{
			_candidates = candidates;
			if (a_LowerAndUpperMatch == null)
			{
				a_LowerAndUpperMatch = new int[] { 0, a_range._entries - 1 };
			}
			com.db4o.YapFile yf = _fileRange.stream();
			int baseAddress = a_range._address;
			int baseAddressOffset = a_range._addressOffset;
			bool sorted = false;
			int count = a_LowerAndUpperMatch[1] - a_LowerAndUpperMatch[0] + 1;
			if (count > 0)
			{
				com.db4o.YapReader reader = new com.db4o.YapReader(count * _slotLength);
				reader.read(yf, baseAddress, baseAddressOffset + (a_LowerAndUpperMatch[0] * _slotLength
					));
				com.db4o.Tree tree = new com.db4o.TreeReader(reader, this, false).read(count);
				if (tree != null)
				{
					a_tree = com.db4o.Tree.add(a_tree, tree);
				}
			}
			_candidates = null;
			return a_tree;
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

		internal virtual int compare(com.db4o.IxFileRange fileRange, com.db4o.Tree treeTo
			)
		{
			setFileRange(fileRange);
			com.db4o.YapFile yf = fileRange.stream();
			com.db4o.Transaction trans = fileRange.trans();
			while (true)
			{
				_reader.read(yf, _baseAddress, _baseAddressOffset + _addressOffset);
				_reader._offset = 0;
				int cmp = _handler.compareTo(_handler.indexObject(trans, _handler.readIndexEntry(
					_reader)));
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
						return 0;
					}
				}
				if (!adjustCursor())
				{
					return _cursor == 0 ? cmp : -1;
				}
			}
		}

		private com.db4o.Tree insert(com.db4o.Tree a_new, int a_cursor, int a_cmp)
		{
			int incStartNewAt = a_cmp <= 0 ? 1 : 0;
			int newAddressOffset = (a_cursor + incStartNewAt) * _slotLength;
			int newEntries = _fileRange._entries - a_cursor - incStartNewAt;
			_fileRange._entries = a_cmp < 0 ? a_cursor + 1 : a_cursor;
			com.db4o.IxFileRange ifr = new com.db4o.IxFileRange(_fileRange.i_fieldTransaction
				, _baseAddress, _baseAddressOffset + newAddressOffset, newEntries);
			ifr.i_subsequent = _fileRange.i_subsequent;
			_fileRange.i_subsequent = null;
			a_new.i_preceding = _fileRange.balanceCheckNulls();
			a_new.i_subsequent = ifr.balanceCheckNulls();
			return a_new.balance();
		}

		internal virtual int[] lowerAndUpperMatches()
		{
			int[] matches = new int[] { _lower, _upper };
			if (_lower > _upper)
			{
				return matches;
			}
			com.db4o.YapFile yf = _fileRange.stream();
			com.db4o.Transaction trans = _fileRange.trans();
			int tempCursor = _cursor;
			_upper = _cursor;
			adjustCursor();
			while (true)
			{
				_reader.read(yf, _baseAddress, _baseAddressOffset + _addressOffset);
				_reader._offset = 0;
				int cmp = _handler.compareTo(_handler.indexObject(trans, _handler.readIndexEntry(
					_reader)));
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
				int cmp = _handler.compareTo(_handler.indexObject(trans, _handler.readIndexEntry(
					_reader)));
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
			return matches;
		}

		private void setFileRange(com.db4o.IxFileRange a_fr)
		{
			_fileRange = a_fr;
			_lower = 0;
			_upper = a_fr._entries - 1;
			_baseAddress = a_fr._address;
			_baseAddressOffset = a_fr._addressOffset;
			adjustCursor();
		}

		public virtual object read(com.db4o.YapReader a_reader)
		{
			a_reader.incrementOffset(_linkLegth);
			return new com.db4o.QCandidate(_candidates, null, a_reader.readInt(), true);
		}

		public virtual int byteCount()
		{
			return _slotLength;
		}
	}
}
