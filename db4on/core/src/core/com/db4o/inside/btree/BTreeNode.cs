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
		private const int COUNT_LEAF_AND_3_LINK_LENGTH = (com.db4o.YapConst.YAPINT_LENGTH
			 * 4) + 1;

		private const int SLOT_LEADING_LENGTH = com.db4o.YapConst.LEADING_LENGTH + COUNT_LEAF_AND_3_LINK_LENGTH;

		internal readonly com.db4o.inside.btree.BTree _btree;

		private int _count;

		private bool _isLeaf;

		private object[] _keys;

		/// <summary>Can contain BTreeNode or Integer for ID of BTreeNode</summary>
		private object[] _children;

		/// <summary>Only used for leafs</summary>
		private object[] _values;

		private int _parentID;

		private int _previousID;

		private int _nextID;

		private bool _cached;

		private bool _dead;

		public BTreeNode(com.db4o.inside.btree.BTree btree, int count, bool isLeaf, int parentID
			, int previousID, int nextID)
		{
			_btree = btree;
			_parentID = parentID;
			_previousID = previousID;
			_nextID = nextID;
			_count = count;
			_isLeaf = isLeaf;
			PrepareArrays();
			SetStateDirty();
		}

		public BTreeNode(int id, com.db4o.inside.btree.BTree btree)
		{
			_btree = btree;
			SetID(id);
			SetStateDeactivated();
		}

		public BTreeNode(com.db4o.Transaction trans, com.db4o.inside.btree.BTreeNode firstChild
			, com.db4o.inside.btree.BTreeNode secondChild) : this(firstChild._btree, 2, false
			, 0, 0, 0)
		{
			_keys[0] = firstChild._keys[0];
			_children[0] = firstChild;
			_keys[1] = secondChild._keys[0];
			_children[1] = secondChild;
			Write(trans.SystemTransaction());
			firstChild._parentID = GetID();
			secondChild._parentID = GetID();
		}

		/// <returns>
		/// the split node if this node is split
		/// or this if the first key has changed
		/// </returns>
		public virtual com.db4o.inside.btree.BTreeNode Add(com.db4o.Transaction trans)
		{
			com.db4o.YapReader reader = PrepareRead(trans);
			com.db4o.inside.btree.Searcher s = Search(trans, reader);
			if (_isLeaf)
			{
				PrepareWrite(trans);
				if (s._cmp < 0)
				{
					s._cursor++;
				}
				Insert(s._cursor);
				_keys[s._cursor] = new com.db4o.inside.btree.BTreeAdd(trans, KeyHandler().Current
					());
				if (HandlesValues())
				{
					_values[s._cursor] = ValueHandler().Current();
				}
			}
			else
			{
				com.db4o.inside.btree.BTreeNode childNode = Child(reader, s._cursor);
				com.db4o.inside.btree.BTreeNode childNodeOrSplit = childNode.Add(trans);
				if (childNodeOrSplit == null)
				{
					return null;
				}
				PrepareWrite(trans);
				_keys[s._cursor] = childNode._keys[0];
				if (childNode != childNodeOrSplit)
				{
					int splitCursor = s._cursor + 1;
					Insert(splitCursor);
					_keys[splitCursor] = childNodeOrSplit._keys[0];
					_children[splitCursor] = childNodeOrSplit;
				}
			}
			if (_count >= _btree._nodeSize)
			{
				return Split(trans);
			}
			if (s._cursor == 0)
			{
				return this;
			}
			return null;
		}

		private bool CanWrite()
		{
			return _keys != null;
		}

		private com.db4o.inside.btree.BTreeNode Child(int index)
		{
			if (_children[index] is com.db4o.inside.btree.BTreeNode)
			{
				return (com.db4o.inside.btree.BTreeNode)_children[index];
			}
			return _btree.ProduceNode(((int)_children[index]));
		}

		private com.db4o.inside.btree.BTreeNode Child(com.db4o.YapReader reader, int index
			)
		{
			if (ChildLoaded(index))
			{
				return (com.db4o.inside.btree.BTreeNode)_children[index];
			}
			com.db4o.inside.btree.BTreeNode child = _btree.ProduceNode(ChildID(reader, index)
				);
			if (_children != null)
			{
				if (_cached || child.CanWrite())
				{
					_children[index] = child;
				}
			}
			return child;
		}

		private int ChildID(com.db4o.YapReader reader, int index)
		{
			if (_children == null)
			{
				SeekChild(reader, index);
				return reader.ReadInt();
			}
			return ChildID(index);
		}

		private int ChildID(int index)
		{
			if (ChildLoaded(index))
			{
				return ((com.db4o.inside.btree.BTreeNode)_children[index]).GetID();
			}
			return ((int)_children[index]);
		}

		private bool ChildLoaded(int index)
		{
			if (_children == null)
			{
				return false;
			}
			return _children[index] is com.db4o.inside.btree.BTreeNode;
		}

		private bool ChildCanSupplyFirstKey(int index)
		{
			if (!ChildLoaded(index))
			{
				return false;
			}
			return ((com.db4o.inside.btree.BTreeNode)_children[index])._keys != null;
		}

		internal virtual void Commit(com.db4o.Transaction trans)
		{
			if (_dead)
			{
				return;
			}
			_cached = false;
			if (!CanWrite())
			{
				return;
			}
			if (!IsDirty(trans))
			{
				return;
			}
			object keyZero = _keys[0];
			if (_isLeaf)
			{
				bool vals = HandlesValues();
				object[] tempKeys = new object[_btree._nodeSize];
				object[] tempValues = vals ? new object[_btree._nodeSize] : null;
				int count = 0;
				for (int i = 0; i < _count; i++)
				{
					object key = _keys[i];
					com.db4o.inside.btree.BTreePatch patch = KeyPatch(i);
					if (patch != null)
					{
						key = patch.Commit(trans, _btree);
					}
					if (key != com.db4o.foundation.No4.INSTANCE)
					{
						tempKeys[count] = key;
						if (vals)
						{
							tempValues[count] = _values[i];
						}
						count++;
					}
				}
				_keys = tempKeys;
				_values = tempValues;
				_count = count;
				if (_count == 0)
				{
					if (_parentID != 0)
					{
						Free(trans);
						return;
					}
				}
			}
			if (_keys[0] != keyZero)
			{
				TellParentAboutChangedKey(trans);
			}
		}

		private void Free(com.db4o.Transaction trans)
		{
			_dead = true;
			if (_parentID != 0)
			{
				com.db4o.inside.btree.BTreeNode parent = _btree.ProduceNode(_parentID);
				parent.RemoveChild(trans, this);
			}
			PointPreviousTo(trans, _nextID);
			PointNextTo(trans, _previousID);
			trans.SystemTransaction().SlotFreePointerOnCommit(GetID());
			_btree.RemoveNode(this);
		}

		internal virtual void HoldChildrenAsIDs()
		{
			if (_children == null)
			{
				return;
			}
			for (int i = 0; i < _count; i++)
			{
				if (_children[i] is com.db4o.inside.btree.BTreeNode)
				{
					_children[i] = ((com.db4o.inside.btree.BTreeNode)_children[i]).GetID();
				}
			}
		}

		private void RemoveChild(com.db4o.Transaction trans, com.db4o.inside.btree.BTreeNode
			 child)
		{
			PrepareWrite(trans);
			int id = child.GetID();
			for (int i = 0; i < _count; i++)
			{
				if (ChildID(i) == id)
				{
					if (_count == 1)
					{
						if (_parentID != 0)
						{
							Free(trans);
							return;
						}
					}
					Remove(i);
					if (i <= 1)
					{
						if (_parentID != 0)
						{
							com.db4o.inside.btree.BTreeNode parent = _btree.ProduceNode(_parentID);
							parent.KeyChanged(trans, this);
							return;
						}
					}
					if (_count == 0)
					{
						_isLeaf = true;
					}
				}
			}
		}

		private void KeyChanged(com.db4o.Transaction trans, com.db4o.inside.btree.BTreeNode
			 child)
		{
			PrepareWrite(trans);
			int id = child.GetID();
			for (int i = 0; i < _count; i++)
			{
				if (ChildID(i) == id)
				{
					_keys[i] = child._keys[0];
					_children[i] = child;
					if (i == 0)
					{
						TellParentAboutChangedKey(trans);
					}
					return;
				}
			}
		}

		private void TellParentAboutChangedKey(com.db4o.Transaction trans)
		{
			if (_parentID != 0)
			{
				com.db4o.inside.btree.BTreeNode parent = _btree.ProduceNode(_parentID);
				parent.KeyChanged(trans, this);
			}
		}

		private bool IsDirty(com.db4o.Transaction trans)
		{
			if (!CanWrite())
			{
				return false;
			}
			for (int i = 0; i < _count; i++)
			{
				com.db4o.inside.btree.BTreePatch patch = KeyPatch(i);
				if (patch != null)
				{
					if (patch.ForTransaction(trans) != null)
					{
						return true;
					}
				}
			}
			return false;
		}

		private void Compare(com.db4o.inside.btree.Searcher s, com.db4o.YapReader reader)
		{
			com.db4o.inside.ix.Indexable4 handler = KeyHandler();
			if (_keys != null)
			{
				s.ResultIs(handler.CompareTo(Key(s._cursor)));
			}
			else
			{
				SeekKey(reader, s._cursor);
				s.ResultIs(handler.CompareTo(handler.ReadIndexEntry(reader)));
			}
		}

		private int EntryLength()
		{
			int len = KeyHandler().LinkLength();
			if (_isLeaf)
			{
				if (HandlesValues())
				{
					len += ValueHandler().LinkLength();
				}
			}
			else
			{
				len += com.db4o.YapConst.YAPID_LENGTH;
			}
			return len;
		}

		private object FirstKey(com.db4o.Transaction trans)
		{
			for (int ix = 0; ix < _count; ix++)
			{
				com.db4o.inside.btree.BTreePatch patch = KeyPatch(ix);
				if (patch == null)
				{
					return _keys[ix];
				}
				object obj = patch.GetObject(trans);
				if (obj != com.db4o.foundation.No4.INSTANCE)
				{
					return obj;
				}
			}
			return com.db4o.foundation.No4.INSTANCE;
		}

		public override byte GetIdentifier()
		{
			return com.db4o.YapConst.BTREE_NODE;
		}

		private bool HandlesValues()
		{
			return _btree._valueHandler != com.db4o.Null.INSTANCE;
		}

		private void Insert(int pos)
		{
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
			System.Array.Copy(_keys, pos, _keys, pos + 1, len);
			if (_values != null)
			{
				System.Array.Copy(_values, pos, _values, pos + 1, len);
			}
			if (_children != null)
			{
				System.Array.Copy(_children, pos, _children, pos + 1, len);
			}
			_count++;
		}

		private void Remove(int pos)
		{
			int len = _count - pos;
			_count--;
			System.Array.Copy(_keys, pos + 1, _keys, pos, len);
			_keys[_count] = null;
			if (_values != null)
			{
				System.Array.Copy(_values, pos + 1, _values, pos, len);
				_values[_count] = null;
			}
			if (_children != null)
			{
				System.Array.Copy(_children, pos + 1, _children, pos, len);
				_children[_count] = null;
			}
		}

		private object Key(int index)
		{
			com.db4o.inside.btree.BTreePatch patch = KeyPatch(index);
			if (patch == null)
			{
				return _keys[index];
			}
			return patch._object;
		}

		private object Key(com.db4o.Transaction trans, com.db4o.YapReader reader, int index
			)
		{
			if (_keys != null)
			{
				return Key(trans, index);
			}
			SeekKey(reader, index);
			return KeyHandler().ReadIndexEntry(reader);
		}

		private object Key(com.db4o.Transaction trans, int index)
		{
			com.db4o.inside.btree.BTreePatch patch = KeyPatch(index);
			if (patch == null)
			{
				return _keys[index];
			}
			return patch.GetObject(trans);
		}

		private com.db4o.inside.btree.BTreePatch KeyPatch(int index)
		{
			if (_keys[index] is com.db4o.inside.btree.BTreePatch)
			{
				return (com.db4o.inside.btree.BTreePatch)_keys[index];
			}
			return null;
		}

		private com.db4o.inside.ix.Indexable4 KeyHandler()
		{
			return _btree._keyHandler;
		}

		internal virtual void MarkAsCached(int height)
		{
			_cached = true;
			_btree.AddNode(this);
			if (_isLeaf || (_children == null))
			{
				return;
			}
			height--;
			if (height < 1)
			{
				HoldChildrenAsIDs();
				return;
			}
			for (int i = 0; i < _count; i++)
			{
				if (_children[i] is com.db4o.inside.btree.BTreeNode)
				{
					((com.db4o.inside.btree.BTreeNode)_children[i]).MarkAsCached(height);
				}
			}
		}

		public override int OwnLength()
		{
			return SLOT_LEADING_LENGTH + (_count * EntryLength()) + com.db4o.YapConst.BRACKETS_BYTES;
		}

		private com.db4o.YapReader PrepareRead(com.db4o.Transaction trans)
		{
			if (CanWrite())
			{
				return null;
			}
			if (IsNew())
			{
				return null;
			}
			if (_cached)
			{
				Read(trans.SystemTransaction());
				_btree.AddToProcessing(this);
				return null;
			}
			com.db4o.YapReader reader = trans.i_file.ReadReaderByID(trans.SystemTransaction()
				, GetID());
			ReadSlotHeader(reader);
			return reader;
		}

		internal virtual void PrepareWrite(com.db4o.Transaction trans)
		{
			if (_dead)
			{
				return;
			}
			if (CanWrite())
			{
				SetStateDirty();
				return;
			}
			if (_keys == null)
			{
				Read(trans.SystemTransaction());
				SetStateDirty();
				_btree.AddToProcessing(this);
			}
		}

		internal virtual void PrepareArrays()
		{
			if (_keys != null)
			{
				return;
			}
			_keys = new object[_btree._nodeSize];
			if (_isLeaf)
			{
				if (HandlesValues())
				{
					_values = new object[_btree._nodeSize];
				}
			}
			else
			{
				_children = new object[_btree._nodeSize];
			}
		}

		private void ReadSlotHeader(com.db4o.YapReader reader)
		{
			_count = reader.ReadInt();
			byte leafByte = reader.ReadByte();
			_isLeaf = (leafByte == 1);
			_parentID = reader.ReadInt();
			_previousID = reader.ReadInt();
			_nextID = reader.ReadInt();
		}

		public override void ReadThis(com.db4o.Transaction trans, com.db4o.YapReader reader
			)
		{
			ReadSlotHeader(reader);
			PrepareArrays();
			bool isInner = !_isLeaf;
			bool vals = HandlesValues() && _isLeaf;
			for (int i = 0; i < _count; i++)
			{
				_keys[i] = KeyHandler().ReadIndexEntry(reader);
				if (vals)
				{
					_values[i] = ValueHandler().ReadIndexEntry(reader);
				}
				else
				{
					if (isInner)
					{
						_children[i] = reader.ReadInt();
					}
				}
			}
		}

		public virtual void Remove(com.db4o.Transaction trans)
		{
			com.db4o.YapReader reader = PrepareRead(trans);
			com.db4o.inside.btree.Searcher s = Search(trans, reader);
			if (s._cursor < 0)
			{
				return;
			}
			if (_isLeaf)
			{
				if (s._cmp != 0)
				{
					return;
				}
				PrepareWrite(trans);
				com.db4o.inside.btree.BTreeRemove btr = new com.db4o.inside.btree.BTreeRemove(trans
					, KeyHandler().Current());
				com.db4o.inside.btree.BTreePatch patch = KeyPatch(s._cursor);
				if (patch != null)
				{
					_keys[s._cursor] = patch.Append(btr);
				}
				else
				{
					_keys[s._cursor] = btr;
				}
				if (s._cursor == 0)
				{
					TellParentAboutChangedKey(trans);
				}
				return;
			}
			Child(reader, s._cursor).Remove(trans);
		}

		internal virtual void Rollback(com.db4o.Transaction trans)
		{
			if (!CanWrite())
			{
				return;
			}
			if (_isLeaf)
			{
				object keyZero = _keys[0];
				bool vals = HandlesValues();
				object[] tempKeys = new object[_btree._nodeSize];
				object[] tempValues = vals ? new object[_btree._nodeSize] : null;
				int count = 0;
				for (int i = 0; i < _count; i++)
				{
					object key = _keys[i];
					com.db4o.inside.btree.BTreePatch patch = KeyPatch(i);
					if (patch != null)
					{
						key = patch.Rollback(trans, _btree);
					}
					if (key != com.db4o.foundation.No4.INSTANCE)
					{
						tempKeys[count] = key;
						if (vals)
						{
							tempValues[count] = _values[i];
						}
						count++;
					}
				}
				_keys = tempKeys;
				_values = tempValues;
				_count = count;
				if (keyZero != _keys[0])
				{
					TellParentAboutChangedKey(trans);
				}
			}
		}

		private com.db4o.inside.btree.Searcher Search(com.db4o.Transaction trans, com.db4o.YapReader
			 reader)
		{
			com.db4o.inside.btree.Searcher s = new com.db4o.inside.btree.Searcher(_count);
			while (s.Incomplete())
			{
				Compare(s, reader);
			}
			if (s._cursor < 0)
			{
				s._cursor = 0;
			}
			else
			{
				if (!_isLeaf)
				{
					if (s._cmp > 0 && s._cursor > 0)
					{
						s._cursor--;
					}
				}
			}
			return s;
		}

		private void SeekAfterKey(com.db4o.YapReader reader, int ix)
		{
			SeekKey(reader, ix);
			reader._offset += KeyHandler().LinkLength();
		}

		private void SeekChild(com.db4o.YapReader reader, int ix)
		{
			SeekAfterKey(reader, ix);
		}

		private void SeekKey(com.db4o.YapReader reader, int ix)
		{
			reader._offset = SLOT_LEADING_LENGTH + (EntryLength() * ix);
		}

		private void SeekValue(com.db4o.YapReader reader, int ix)
		{
			if (HandlesValues())
			{
				SeekAfterKey(reader, ix);
			}
			else
			{
				SeekKey(reader, ix);
			}
		}

		private com.db4o.inside.btree.BTreeNode Split(com.db4o.Transaction trans)
		{
			com.db4o.inside.btree.BTreeNode res = new com.db4o.inside.btree.BTreeNode(_btree, 
				_btree._halfNodeSize, _isLeaf, _parentID, GetID(), _nextID);
			System.Array.Copy(_keys, _btree._halfNodeSize, res._keys, 0, _btree._halfNodeSize
				);
			for (int i = _btree._halfNodeSize; i < _keys.Length; i++)
			{
				_keys[i] = null;
			}
			if (_values != null)
			{
				res._values = new object[_btree._nodeSize];
				System.Array.Copy(_values, _btree._halfNodeSize, res._values, 0, _btree._halfNodeSize
					);
				for (int i = _btree._halfNodeSize; i < _values.Length; i++)
				{
					_values[i] = null;
				}
			}
			if (_children != null)
			{
				res._children = new object[_btree._nodeSize];
				System.Array.Copy(_children, _btree._halfNodeSize, res._children, 0, _btree._halfNodeSize
					);
				for (int i = _btree._halfNodeSize; i < _children.Length; i++)
				{
					_children[i] = null;
				}
			}
			_count = _btree._halfNodeSize;
			res.Write(trans.SystemTransaction());
			_btree.AddNode(res);
			int splitID = res.GetID();
			PointNextTo(trans, splitID);
			SetNextID(trans, splitID);
			if (_children != null)
			{
				for (int i = 0; i < _btree._halfNodeSize; i++)
				{
					if (res._children[i] == null)
					{
						break;
					}
					res.Child(i).SetParentID(trans, splitID);
				}
			}
			return res;
		}

		private void PointNextTo(com.db4o.Transaction trans, int id)
		{
			if (_nextID > 0)
			{
				_btree.ProduceNode(_nextID).SetPreviousID(trans, id);
			}
		}

		private void PointPreviousTo(com.db4o.Transaction trans, int id)
		{
			if (_previousID != 0)
			{
				_btree.ProduceNode(_previousID).SetNextID(trans, id);
			}
		}

		internal virtual void Purge()
		{
			if (_dead)
			{
				_keys = null;
				_values = null;
				_children = null;
				return;
			}
			if (_cached)
			{
				return;
			}
			if (_keys == null)
			{
				return;
			}
			for (int i = 0; i < _count; i++)
			{
				if (_keys[i] is com.db4o.inside.btree.BTreePatch)
				{
					HoldChildrenAsIDs();
					_btree.AddNode(this);
					return;
				}
			}
		}

		private void SetParentID(com.db4o.Transaction trans, int id)
		{
			PrepareWrite(trans);
			_parentID = id;
		}

		private void SetPreviousID(com.db4o.Transaction trans, int id)
		{
			PrepareWrite(trans);
			_previousID = id;
		}

		private void SetNextID(com.db4o.Transaction trans, int id)
		{
			PrepareWrite(trans);
			_nextID = id;
		}

		public virtual void TraverseKeys(com.db4o.Transaction trans, com.db4o.foundation.Visitor4
			 visitor)
		{
			com.db4o.YapReader reader = PrepareRead(trans);
			if (_isLeaf)
			{
				for (int i = 0; i < _count; i++)
				{
					object obj = Key(trans, reader, i);
					if (obj != com.db4o.foundation.No4.INSTANCE)
					{
						visitor.Visit(obj);
					}
				}
			}
			else
			{
				for (int i = 0; i < _count; i++)
				{
					Child(reader, i).TraverseKeys(trans, visitor);
				}
			}
		}

		public virtual void TraverseValues(com.db4o.Transaction trans, com.db4o.foundation.Visitor4
			 visitor)
		{
			if (!HandlesValues())
			{
				TraverseKeys(trans, visitor);
				return;
			}
			com.db4o.YapReader reader = PrepareRead(trans);
			if (_isLeaf)
			{
				for (int i = 0; i < _count; i++)
				{
					if (Key(trans, reader, i) != com.db4o.foundation.No4.INSTANCE)
					{
						visitor.Visit(Value(reader, i));
					}
				}
			}
			else
			{
				for (int i = 0; i < _count; i++)
				{
					Child(reader, i).TraverseValues(trans, visitor);
				}
			}
		}

		private object Value(com.db4o.YapReader reader, int index)
		{
			if (_values != null)
			{
				return _values[index];
			}
			SeekValue(reader, index);
			return ValueHandler().ReadIndexEntry(reader);
		}

		private com.db4o.inside.ix.Indexable4 ValueHandler()
		{
			return _btree._valueHandler;
		}

		public override bool WriteObjectBegin()
		{
			if (_dead)
			{
				return false;
			}
			if (_keys == null)
			{
				return false;
			}
			return base.WriteObjectBegin();
		}

		public override void WriteThis(com.db4o.Transaction trans, com.db4o.YapReader a_writer
			)
		{
			int count = 0;
			int startOffset = a_writer._offset;
			a_writer.IncrementOffset(COUNT_LEAF_AND_3_LINK_LENGTH);
			if (_isLeaf)
			{
				bool vals = HandlesValues();
				for (int i = 0; i < _count; i++)
				{
					object obj = Key(trans, i);
					if (obj != com.db4o.foundation.No4.INSTANCE)
					{
						count++;
						KeyHandler().WriteIndexEntry(a_writer, obj);
						if (vals)
						{
							ValueHandler().WriteIndexEntry(a_writer, _values[i]);
						}
					}
				}
			}
			else
			{
				for (int i = 0; i < _count; i++)
				{
					if (ChildCanSupplyFirstKey(i))
					{
						com.db4o.inside.btree.BTreeNode child = (com.db4o.inside.btree.BTreeNode)_children
							[i];
						object childKey = child.FirstKey(trans);
						if (childKey != com.db4o.foundation.No4.INSTANCE)
						{
							count++;
							KeyHandler().WriteIndexEntry(a_writer, childKey);
							a_writer.WriteIDOf(trans, child);
						}
					}
					else
					{
						count++;
						KeyHandler().WriteIndexEntry(a_writer, Key(i));
						a_writer.WriteIDOf(trans, _children[i]);
					}
				}
			}
			int endOffset = a_writer._offset;
			a_writer._offset = startOffset;
			a_writer.WriteInt(count);
			a_writer.Append(_isLeaf ? (byte)1 : (byte)0);
			a_writer.WriteInt(_parentID);
			a_writer.WriteInt(_previousID);
			a_writer.WriteInt(_nextID);
			a_writer._offset = endOffset;
		}

		public override string ToString()
		{
			if (_count == 0)
			{
				return "Node not loaded";
			}
			string str = "\nBTreeNode";
			str += "\nid: " + GetID();
			str += "\nparent: " + _parentID;
			str += "\nprevious: " + _previousID;
			str += "\nnext: " + _nextID;
			str += "\ncount:" + _count;
			str += "\nleaf:" + _isLeaf + "\n";
			if (_keys != null)
			{
				str += " { ";
				bool first = true;
				for (int i = 0; i < _count; i++)
				{
					if (_keys[i] != null)
					{
						if (!first)
						{
							str += ", ";
						}
						str += _keys[i].ToString();
						first = false;
					}
				}
				str += " }";
			}
			return str;
		}
	}
}
