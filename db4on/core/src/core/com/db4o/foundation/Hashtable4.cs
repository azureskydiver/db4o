namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class Hashtable4 : com.db4o.foundation.DeepClone
	{
		private const float FILL = 0.5F;

		private int i_tableSize;

		private int i_mask;

		private int i_maximumSize;

		private int i_size;

		private com.db4o.foundation.HashtableIntEntry[] i_table;

		public Hashtable4(int a_size)
		{
			a_size = newSize(a_size);
			i_tableSize = 1;
			while (i_tableSize < a_size)
			{
				i_tableSize = i_tableSize << 1;
			}
			i_mask = i_tableSize - 1;
			i_maximumSize = (int)(i_tableSize * FILL);
			i_table = new com.db4o.foundation.HashtableIntEntry[i_tableSize];
		}

		protected Hashtable4()
		{
		}

		public virtual object deepClone(object obj)
		{
			return deepCloneInternal(new com.db4o.foundation.Hashtable4(), obj);
		}

		public virtual void forEachKey(com.db4o.foundation.Visitor4 visitor)
		{
			for (int i = 0; i < i_table.Length; i++)
			{
				com.db4o.foundation.HashtableIntEntry entry = i_table[i];
				while (entry != null)
				{
					entry.acceptKeyVisitor(visitor);
					entry = entry.i_next;
				}
			}
		}

		public virtual void forEachKeyForIdentity(com.db4o.foundation.Visitor4 visitor, object
			 a_identity)
		{
			for (int i = 0; i < i_table.Length; i++)
			{
				com.db4o.foundation.HashtableIntEntry entry = i_table[i];
				while (entry != null)
				{
					if (entry.i_object == a_identity)
					{
						entry.acceptKeyVisitor(visitor);
					}
					entry = entry.i_next;
				}
			}
		}

		public virtual void forEachValue(com.db4o.foundation.Visitor4 visitor)
		{
			for (int i = 0; i < i_table.Length; i++)
			{
				com.db4o.foundation.HashtableIntEntry entry = i_table[i];
				while (entry != null)
				{
					visitor.visit(entry.i_object);
					entry = entry.i_next;
				}
			}
		}

		public virtual object get(byte[] key)
		{
			int intKey = com.db4o.foundation.HashtableByteArrayEntry.hash(key);
			return getFromObjectEntry(intKey, key);
		}

		public virtual object get(int key)
		{
			com.db4o.foundation.HashtableIntEntry entry = i_table[key & i_mask];
			while (entry != null)
			{
				if (entry.i_key == key)
				{
					return entry.i_object;
				}
				entry = entry.i_next;
			}
			return null;
		}

		public virtual object get(object key)
		{
			if (key == null)
			{
				return null;
			}
			int intKey = key.GetHashCode();
			return getFromObjectEntry(intKey, key);
		}

		public virtual void put(byte[] key, object value)
		{
			putEntry(new com.db4o.foundation.HashtableByteArrayEntry(key, value));
		}

		public virtual void put(int key, object value)
		{
			putEntry(new com.db4o.foundation.HashtableIntEntry(key, value));
		}

		public virtual void put(object key, object value)
		{
			putEntry(new com.db4o.foundation.HashtableObjectEntry(key, value));
		}

		public virtual object remove(byte[] key)
		{
			int intKey = com.db4o.foundation.HashtableByteArrayEntry.hash(key);
			return removeObjectEntry(intKey, key);
		}

		public virtual void remove(int a_key)
		{
			com.db4o.foundation.HashtableIntEntry entry = i_table[a_key & i_mask];
			com.db4o.foundation.HashtableIntEntry predecessor = null;
			while (entry != null)
			{
				if (entry.i_key == a_key)
				{
					removeEntry(predecessor, entry);
					return;
				}
				predecessor = entry;
				entry = entry.i_next;
			}
		}

		public virtual void remove(object objectKey)
		{
			int intKey = objectKey.GetHashCode();
			removeObjectEntry(intKey, objectKey);
		}

		protected virtual com.db4o.foundation.Hashtable4 deepCloneInternal(com.db4o.foundation.Hashtable4
			 ret, object obj)
		{
			ret.i_mask = i_mask;
			ret.i_maximumSize = i_maximumSize;
			ret.i_size = i_size;
			ret.i_tableSize = i_tableSize;
			ret.i_table = new com.db4o.foundation.HashtableIntEntry[i_tableSize];
			for (int i = 0; i < i_tableSize; i++)
			{
				if (i_table[i] != null)
				{
					ret.i_table[i] = (com.db4o.foundation.HashtableIntEntry)i_table[i].deepClone(obj);
				}
			}
			return ret;
		}

		private int entryIndex(com.db4o.foundation.HashtableIntEntry entry)
		{
			return entry.i_key & i_mask;
		}

		private com.db4o.foundation.HashtableIntEntry findWithSameKey(com.db4o.foundation.HashtableIntEntry
			 newEntry)
		{
			com.db4o.foundation.HashtableIntEntry existing = i_table[entryIndex(newEntry)];
			while (null != existing)
			{
				if (existing.sameKeyAs(newEntry))
				{
					return existing;
				}
				existing = existing.i_next;
			}
			return null;
		}

		private object getFromObjectEntry(int intKey, object objectKey)
		{
			com.db4o.foundation.HashtableObjectEntry entry = (com.db4o.foundation.HashtableObjectEntry
				)i_table[intKey & i_mask];
			while (entry != null)
			{
				if (entry.i_key == intKey && entry.hasKey(objectKey))
				{
					return entry.i_object;
				}
				entry = (com.db4o.foundation.HashtableObjectEntry)entry.i_next;
			}
			return null;
		}

		private void increaseSize()
		{
			i_tableSize = i_tableSize << 1;
			i_maximumSize = i_maximumSize << 1;
			i_mask = i_tableSize - 1;
			com.db4o.foundation.HashtableIntEntry[] temp = i_table;
			i_table = new com.db4o.foundation.HashtableIntEntry[i_tableSize];
			for (int i = 0; i < temp.Length; i++)
			{
				reposition(temp[i]);
			}
		}

		private void insert(com.db4o.foundation.HashtableIntEntry newEntry)
		{
			i_size++;
			if (i_size > i_maximumSize)
			{
				increaseSize();
			}
			int index = entryIndex(newEntry);
			newEntry.i_next = i_table[index];
			i_table[index] = newEntry;
		}

		private int newSize(int a_size)
		{
			return (int)(a_size / FILL);
		}

		private void putEntry(com.db4o.foundation.HashtableIntEntry newEntry)
		{
			com.db4o.foundation.HashtableIntEntry existing = findWithSameKey(newEntry);
			if (null != existing)
			{
				replace(existing, newEntry);
			}
			else
			{
				insert(newEntry);
			}
		}

		private void removeEntry(com.db4o.foundation.HashtableIntEntry predecessor, com.db4o.foundation.HashtableIntEntry
			 entry)
		{
			if (predecessor != null)
			{
				predecessor.i_next = entry.i_next;
			}
			else
			{
				i_table[entryIndex(entry)] = entry.i_next;
			}
			i_size--;
		}

		private object removeObjectEntry(int intKey, object objectKey)
		{
			com.db4o.foundation.HashtableObjectEntry entry = (com.db4o.foundation.HashtableObjectEntry
				)i_table[intKey & i_mask];
			com.db4o.foundation.HashtableObjectEntry predecessor = null;
			while (entry != null)
			{
				if (entry.i_key == intKey && entry.hasKey(objectKey))
				{
					removeEntry(predecessor, entry);
					return entry.i_object;
				}
				predecessor = entry;
				entry = (com.db4o.foundation.HashtableObjectEntry)entry.i_next;
			}
			return null;
		}

		private void replace(com.db4o.foundation.HashtableIntEntry existing, com.db4o.foundation.HashtableIntEntry
			 newEntry)
		{
			newEntry.i_next = existing.i_next;
			com.db4o.foundation.HashtableIntEntry entry = i_table[entryIndex(existing)];
			if (entry == existing)
			{
				i_table[entryIndex(existing)] = newEntry;
			}
			else
			{
				while (entry.i_next != existing)
				{
					entry = entry.i_next;
				}
				entry.i_next = newEntry;
			}
		}

		private void reposition(com.db4o.foundation.HashtableIntEntry a_entry)
		{
			if (a_entry != null)
			{
				reposition(a_entry.i_next);
				a_entry.i_next = i_table[entryIndex(a_entry)];
				i_table[entryIndex(a_entry)] = a_entry;
			}
		}
	}
}
