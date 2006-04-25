namespace com.db4o.inside.btree
{
	/// <summary>
	/// We work with BTreeNode in two states:
	/// - deactivated: never read, no valid members, ID correct or 0 if new
	/// - write: real representation of keys, values and children in arrays
	/// The write state can be detected with canWrite().
	/// </summary>
	/// <remarks>
	/// We work with BTreeNode in two states:
	/// - deactivated: never read, no valid members, ID correct or 0 if new
	/// - write: real representation of keys, values and children in arrays
	/// The write state can be detected with canWrite(). States can be changed
	/// as needed with prepareRead() and prepareWrite().
	/// </remarks>
	/// <exclude></exclude>
	public class BTreeNode : com.db4o.YapMeta
	{
		private const int MAX_ENTRIES = 4;

		private const int HALF_ENTRIES = MAX_ENTRIES / 2;

		private const int SLOT_LEADING_LENGTH = com.db4o.YapConst.OBJECT_LENGTH + com.db4o.YapConst
			.YAPINT_LENGTH * 2;

		internal readonly com.db4o.inside.btree.BTree _btree;

		private int _count;

		private int _height;

		private object[] _keys;

		/// <summary>Can contain BTreeNode or Integer for ID of BTreeNode</summary>
		private object[] _children;

		/// <summary>Only used for leafs where _height == 0</summary>
		private object[] _values;

		public BTreeNode(com.db4o.inside.btree.BTree btree)
		{
			_btree = btree;
			setStateClean();
		}

		public BTreeNode(com.db4o.inside.btree.BTree btree, int id)
		{
			_btree = btree;
			setID(id);
			setStateDeactivated();
		}

		/// <returns>
		/// a split node if the node is split
		/// or the first key, if the first key has changed
		/// </returns>
		public virtual object add(com.db4o.Transaction trans)
		{
			com.db4o.YapReader reader = prepareRead(trans);
			com.db4o.inside.btree.Searcher s = search(trans, reader);
			if (s._cursor < 0)
			{
				s._cursor = 0;
			}
			if (isLeaf())
			{
				prepareWrite(trans);
				if (s._cmp < 0)
				{
					s._cursor++;
				}
				insert(trans, s._cursor);
				_keys[s._cursor] = new com.db4o.inside.btree.BTreeAdd(trans, keyHandler().current
					());
				if (handlesValues())
				{
					_values[s._cursor] = valueHandler().current();
				}
			}
			else
			{
				object addResult = child(reader, s._cursor).add(trans);
				if (addResult == null)
				{
					return null;
				}
				prepareWrite(trans);
				if (addResult is com.db4o.inside.btree.BTreeNode)
				{
					com.db4o.inside.btree.BTreeNode splitChild = (com.db4o.inside.btree.BTreeNode)addResult;
					s._cursor++;
					insert(trans, s._cursor);
					_keys[s._cursor] = splitChild._keys[0];
					_children[s._cursor] = splitChild;
				}
				else
				{
					_keys[s._cursor] = addResult;
				}
			}
			setStateDirty();
			if (_count == MAX_ENTRIES)
			{
				return split(trans);
			}
			if (s._cursor == 0)
			{
				return _keys[0];
			}
			return null;
		}

		private bool canWrite()
		{
			return _keys != null;
		}

		private com.db4o.inside.btree.BTreeNode child(com.db4o.YapReader reader, int index
			)
		{
			if (childLoaded(index))
			{
				return (com.db4o.inside.btree.BTreeNode)_children[index];
			}
			com.db4o.inside.btree.BTreeNode child = _btree.produceNode(childID(reader, index)
				);
			if (_children != null)
			{
				_children[index] = child;
			}
			return child;
		}

		private int childID(com.db4o.YapReader reader, int index)
		{
			if (_children == null)
			{
				seekChild(reader, index);
				return reader.readInt();
			}
			if (childLoaded(index))
			{
				return ((com.db4o.inside.btree.BTreeNode)_children[index]).getID();
			}
			return ((int)_children[index]);
		}

		private bool childLoaded(int index)
		{
			if (_children == null)
			{
				return false;
			}
			return _children[index] is com.db4o.inside.btree.BTreeNode;
		}

		internal virtual void commit(com.db4o.Transaction trans)
		{
		}

		private void compare(com.db4o.inside.btree.Searcher s, com.db4o.YapReader reader)
		{
			com.db4o.inside.ix.Indexable4 handler = keyHandler();
			if (_keys != null)
			{
				s.resultIs(handler.compareTo(key(s._cursor)));
			}
			else
			{
				seekKey(reader, s._cursor);
				s.resultIs(handler.compareTo(handler.readIndexEntry(reader)));
			}
		}

		private int entryLength()
		{
			int len = keyHandler().linkLength();
			if (isLeaf())
			{
				if (handlesValues())
				{
					len += valueHandler().linkLength();
				}
			}
			else
			{
				len += com.db4o.YapConst.YAPID_LENGTH;
			}
			return len;
		}

		private object firstKey(com.db4o.Transaction trans)
		{
			for (int ix = 0; ix < _count; ix++)
			{
				com.db4o.inside.btree.BTreePatch patch = keyPatch(ix);
				if (patch == null)
				{
					return _keys[ix];
				}
				object obj = patch.getObject(trans);
				if (obj != com.db4o.Null.INSTANCE)
				{
					return obj;
				}
			}
			return com.db4o.Null.INSTANCE;
		}

		public override byte getIdentifier()
		{
			return com.db4o.YapConst.BTREE_NODE;
		}

		private bool handlesValues()
		{
			return _btree._valueHandler != com.db4o.Null.INSTANCE;
		}

		private void insert(com.db4o.Transaction trans, int pos)
		{
			prepareWrite(trans);
			if (pos < 0)
			{
				pos = 0;
			}
			if (pos > _count - 1)
			{
				_count++;
				return;
			}
			int len = _count - pos;
			j4o.lang.JavaSystem.arraycopy(_keys, pos, _keys, pos + 1, len);
			if (_values != null)
			{
				j4o.lang.JavaSystem.arraycopy(_values, pos, _values, pos + 1, len);
			}
			if (_children != null)
			{
				j4o.lang.JavaSystem.arraycopy(_children, pos, _children, pos + 1, len);
			}
			_count++;
		}

		private bool isLeaf()
		{
			return _height == 0;
		}

		private object key(int index)
		{
			com.db4o.inside.btree.BTreePatch patch = keyPatch(index);
			if (patch == null)
			{
				return _keys[index];
			}
			return patch._object;
		}

		private object key(com.db4o.Transaction trans, com.db4o.YapReader reader, int index
			)
		{
			if (_keys != null)
			{
				return key(trans, index);
			}
			seekKey(reader, index);
			return keyHandler().readIndexEntry(reader);
		}

		private object key(com.db4o.Transaction trans, int index)
		{
			com.db4o.inside.btree.BTreePatch patch = keyPatch(index);
			if (patch == null)
			{
				return _keys[index];
			}
			return patch.getObject(trans);
		}

		private com.db4o.inside.btree.BTreePatch keyPatch(int index)
		{
			if (_keys[index] is com.db4o.inside.btree.BTreePatch)
			{
				return (com.db4o.inside.btree.BTreePatch)_keys[index];
			}
			return null;
		}

		private com.db4o.inside.ix.Indexable4 keyHandler()
		{
			return _btree._keyHandler;
		}

		internal virtual com.db4o.inside.btree.BTreeNode newRoot(com.db4o.Transaction trans
			, com.db4o.inside.btree.BTreeNode peer)
		{
			com.db4o.inside.btree.BTreeNode res = new com.db4o.inside.btree.BTreeNode(_btree);
			res._height = _height + 1;
			res._count = 2;
			res.prepareWrite(trans);
			res._keys[0] = _keys[0];
			res._children[0] = this;
			res._keys[1] = peer._keys[0];
			res._children[1] = peer;
			return res;
		}

		public override int ownLength()
		{
			return com.db4o.YapConst.OBJECT_LENGTH + com.db4o.YapConst.YAPINT_LENGTH * 2 + _count
				 * entryLength();
		}

		private com.db4o.YapReader prepareRead(com.db4o.Transaction trans)
		{
			if (canWrite())
			{
				return null;
			}
			if (isNew())
			{
				return null;
			}
			com.db4o.YapReader reader = trans.i_file.readReaderByID(trans, getID());
			_count = reader.readInt();
			_height = reader.readInt();
			return reader;
		}

		private void prepareWrite(com.db4o.Transaction trans)
		{
			if (canWrite())
			{
				return;
			}
			if (isNew())
			{
				prepareArrays();
				return;
			}
			if (!isActive())
			{
				prepareArrays();
				read(trans);
			}
		}

		private void prepareArrays()
		{
			_keys = new object[MAX_ENTRIES];
			if (isLeaf())
			{
				if (handlesValues())
				{
					_values = new object[MAX_ENTRIES];
				}
			}
			else
			{
				_children = new object[MAX_ENTRIES];
			}
		}

		public override void readThis(com.db4o.Transaction a_trans, com.db4o.YapReader a_reader
			)
		{
			_count = a_reader.readInt();
			_height = a_reader.readInt();
			bool isInner = !isLeaf();
			bool vals = handlesValues() && isLeaf();
			for (int i = 0; i < _count; i++)
			{
				_keys[i] = keyHandler().readIndexEntry(a_reader);
				if (vals)
				{
					_values[i] = valueHandler().readIndexEntry(a_reader);
				}
				else
				{
					if (isInner)
					{
						_children[i] = a_reader.readInt();
					}
				}
			}
		}

		public virtual com.db4o.inside.btree.BTreeNode remove(com.db4o.Transaction trans)
		{
			com.db4o.YapReader reader = prepareRead(trans);
			com.db4o.inside.btree.Searcher s = search(trans, reader);
			if (s._cursor < 0)
			{
				return this;
			}
			if (isLeaf())
			{
				if (s._cmp == 0)
				{
					prepareWrite(trans);
					object obj = _keys[s._cursor];
					if (obj is com.db4o.inside.btree.BTreePatch)
					{
					}
				}
				else
				{
					if (s._cmp < 0)
					{
						s._cursor++;
					}
					insert(trans, s._cursor);
					_keys[s._cursor] = new com.db4o.inside.btree.BTreeAdd(trans, keyHandler().current
						());
					if (handlesValues())
					{
						_values[s._cursor] = valueHandler().current();
					}
				}
			}
			else
			{
				child(reader, s._cursor).remove(trans);
			}
			return this;
		}

		internal virtual void rollback(com.db4o.Transaction trans)
		{
			int xxx = 1;
		}

		private com.db4o.inside.btree.Searcher search(com.db4o.Transaction trans, com.db4o.YapReader
			 reader)
		{
			com.db4o.inside.btree.Searcher s = new com.db4o.inside.btree.Searcher(_count);
			while (s.incomplete())
			{
				compare(s, reader);
			}
			return s;
		}

		private void seekAfterKey(com.db4o.YapReader reader, int ix)
		{
			seekKey(reader, ix);
			reader._offset += keyHandler().linkLength();
		}

		private void seekChild(com.db4o.YapReader reader, int ix)
		{
			seekAfterKey(reader, ix);
		}

		private void seekKey(com.db4o.YapReader reader, int ix)
		{
			reader._offset = SLOT_LEADING_LENGTH + (entryLength() * ix);
		}

		private void seekValue(com.db4o.YapReader reader, int ix)
		{
			if (handlesValues())
			{
				seekAfterKey(reader, ix);
			}
			else
			{
				seekKey(reader, ix);
			}
		}

		public override void setID(int a_id)
		{
			if (getID() == 0)
			{
				_btree.addNode(a_id, this);
			}
			base.setID(a_id);
		}

		private com.db4o.inside.btree.BTreeNode split(com.db4o.Transaction trans)
		{
			com.db4o.inside.btree.BTreeNode res = new com.db4o.inside.btree.BTreeNode(_btree);
			res.prepareWrite(trans);
			j4o.lang.JavaSystem.arraycopy(_keys, HALF_ENTRIES, res._keys, 0, HALF_ENTRIES);
			if (_values != null)
			{
				res._values = new object[MAX_ENTRIES];
				j4o.lang.JavaSystem.arraycopy(_values, HALF_ENTRIES, res._values, 0, HALF_ENTRIES
					);
			}
			if (_children != null)
			{
				res._children = new object[MAX_ENTRIES];
				j4o.lang.JavaSystem.arraycopy(_children, HALF_ENTRIES, res._children, 0, HALF_ENTRIES
					);
			}
			res._count = HALF_ENTRIES;
			_count = HALF_ENTRIES;
			return res;
		}

		public virtual void traverseKeys(com.db4o.Transaction trans, com.db4o.foundation.Visitor4
			 visitor)
		{
			com.db4o.YapReader reader = prepareRead(trans);
			if (isLeaf())
			{
				for (int i = 0; i < _count; i++)
				{
					object obj = key(trans, reader, i);
					if (obj != com.db4o.Null.INSTANCE)
					{
						visitor.visit(obj);
					}
				}
			}
			else
			{
				for (int i = 0; i < _count; i++)
				{
					child(reader, i).traverseKeys(trans, visitor);
				}
			}
		}

		public virtual void traverseValues(com.db4o.Transaction trans, com.db4o.foundation.Visitor4
			 visitor)
		{
			if (!handlesValues())
			{
				traverseKeys(trans, visitor);
				return;
			}
			com.db4o.YapReader reader = prepareRead(trans);
			if (isLeaf())
			{
				for (int i = 0; i < _count; i++)
				{
					if (key(trans, reader, i) != com.db4o.Null.INSTANCE)
					{
						visitor.visit(value(reader, i));
					}
				}
			}
			else
			{
				for (int i = 0; i < _count; i++)
				{
					child(reader, i).traverseValues(trans, visitor);
				}
			}
		}

		private object value(com.db4o.YapReader reader, int index)
		{
			if (_values != null)
			{
				return _values[index];
			}
			seekValue(reader, index);
			return valueHandler().readIndexEntry(reader);
		}

		private com.db4o.inside.ix.Indexable4 valueHandler()
		{
			return _btree._valueHandler;
		}

		public override void writeThis(com.db4o.Transaction trans, com.db4o.YapReader a_writer
			)
		{
			int count = 0;
			int startOffset = a_writer._offset;
			a_writer.incrementOffset(com.db4o.YapConst.YAPINT_LENGTH * 2);
			if (isLeaf())
			{
				bool vals = handlesValues();
				for (int i = 0; i < _count; i++)
				{
					object obj = key(trans, i);
					if (obj != com.db4o.Null.INSTANCE)
					{
						count++;
						keyHandler().writeIndexEntry(a_writer, obj);
						if (vals)
						{
							valueHandler().writeIndexEntry(a_writer, _values[i]);
						}
					}
				}
			}
			else
			{
				for (int i = 0; i < _count; i++)
				{
					if (childLoaded(i))
					{
						com.db4o.inside.btree.BTreeNode child = (com.db4o.inside.btree.BTreeNode)_children
							[i];
						object childKey = child.firstKey(trans);
						if (childKey != com.db4o.Null.INSTANCE)
						{
							count++;
							keyHandler().writeIndexEntry(a_writer, childKey);
							a_writer.writeIDOf(trans, child);
						}
					}
					else
					{
						count++;
						keyHandler().writeIndexEntry(a_writer, _keys[i]);
						a_writer.writeIDOf(trans, _children[i]);
					}
				}
			}
			int endOffset = a_writer._offset;
			a_writer._offset = startOffset;
			a_writer.writeInt(count);
			a_writer.writeInt(_height);
			a_writer._offset = endOffset;
		}
	}
}
