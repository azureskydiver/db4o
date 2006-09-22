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
			a_size = NewSize(a_size);
			i_tableSize = 1;
			while (i_tableSize < a_size)
			{
				i_tableSize = i_tableSize << 1;
			}
			i_mask = i_tableSize - 1;
			i_maximumSize = (int)(i_tableSize * FILL);
			i_table = new com.db4o.foundation.HashtableIntEntry[i_tableSize];
		}

		public Hashtable4() : this(1)
		{
		}

		protected Hashtable4(com.db4o.foundation.DeepClone cloneOnlyCtor)
		{
		}

		public virtual int Size()
		{
			return i_size;
		}

		public virtual object DeepClone(object obj)
		{
			return DeepCloneInternal(new com.db4o.foundation.Hashtable4((com.db4o.foundation.DeepClone
				)null), obj);
		}

		public virtual void ForEachKey(com.db4o.foundation.Visitor4 visitor)
		{
			for (int i = 0; i < i_table.Length; i++)
			{
				com.db4o.foundation.HashtableIntEntry entry = i_table[i];
				while (entry != null)
				{
					entry.AcceptKeyVisitor(visitor);
					entry = entry.i_next;
				}
			}
		}

		public virtual void ForEachKeyForIdentity(com.db4o.foundation.Visitor4 visitor, object
			 a_identity)
		{
			for (int i = 0; i < i_table.Length; i++)
			{
				com.db4o.foundation.HashtableIntEntry entry = i_table[i];
				while (entry != null)
				{
					if (entry.i_object == a_identity)
					{
						entry.AcceptKeyVisitor(visitor);
					}
					entry = entry.i_next;
				}
			}
		}

		public virtual void ForEachValue(com.db4o.foundation.Visitor4 visitor)
		{
			for (int i = 0; i < i_table.Length; i++)
			{
				com.db4o.foundation.HashtableIntEntry entry = i_table[i];
				while (entry != null)
				{
					visitor.Visit(entry.i_object);
					entry = entry.i_next;
				}
			}
		}

		public virtual object Get(byte[] key)
		{
			int intKey = com.db4o.foundation.HashtableByteArrayEntry.Hash(key);
			return GetFromObjectEntry(intKey, key);
		}

		public virtual object Get(int key)
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

		public virtual object Get(object key)
		{
			if (key == null)
			{
				return null;
			}
			return GetFromObjectEntry(key.GetHashCode(), key);
		}

		public virtual bool ContainsKey(object key)
		{
			if (null == key)
			{
				return false;
			}
			return null != GetObjectEntry(key.GetHashCode(), key);
		}

		public virtual void Put(byte[] key, object value)
		{
			PutEntry(new com.db4o.foundation.HashtableByteArrayEntry(key, value));
		}

		public virtual void Put(int key, object value)
		{
			PutEntry(new com.db4o.foundation.HashtableIntEntry(key, value));
		}

		public virtual void Put(object key, object value)
		{
			PutEntry(new com.db4o.foundation.HashtableObjectEntry(key, value));
		}

		public virtual object Remove(byte[] key)
		{
			int intKey = com.db4o.foundation.HashtableByteArrayEntry.Hash(key);
			return RemoveObjectEntry(intKey, key);
		}

		public virtual void Remove(int a_key)
		{
			com.db4o.foundation.HashtableIntEntry entry = i_table[a_key & i_mask];
			com.db4o.foundation.HashtableIntEntry predecessor = null;
			while (entry != null)
			{
				if (entry.i_key == a_key)
				{
					RemoveEntry(predecessor, entry);
					return;
				}
				predecessor = entry;
				entry = entry.i_next;
			}
		}

		public virtual void Remove(object objectKey)
		{
			int intKey = objectKey.GetHashCode();
			RemoveObjectEntry(intKey, objectKey);
		}

		protected virtual com.db4o.foundation.Hashtable4 DeepCloneInternal(com.db4o.foundation.Hashtable4
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
					ret.i_table[i] = (com.db4o.foundation.HashtableIntEntry)i_table[i].DeepClone(obj);
				}
			}
			return ret;
		}

		private int EntryIndex(com.db4o.foundation.HashtableIntEntry entry)
		{
			return entry.i_key & i_mask;
		}

		private com.db4o.foundation.HashtableIntEntry FindWithSameKey(com.db4o.foundation.HashtableIntEntry
			 newEntry)
		{
			com.db4o.foundation.HashtableIntEntry existing = i_table[EntryIndex(newEntry)];
			while (null != existing)
			{
				if (existing.SameKeyAs(newEntry))
				{
					return existing;
				}
				existing = existing.i_next;
			}
			return null;
		}

		private object GetFromObjectEntry(int intKey, object objectKey)
		{
			com.db4o.foundation.HashtableObjectEntry entry = GetObjectEntry(intKey, objectKey
				);
			return entry == null ? null : entry.i_object;
		}

		private com.db4o.foundation.HashtableObjectEntry GetObjectEntry(int intKey, object
			 objectKey)
		{
			com.db4o.foundation.HashtableObjectEntry entry = (com.db4o.foundation.HashtableObjectEntry
				)i_table[intKey & i_mask];
			while (entry != null)
			{
				if (entry.i_key == intKey && entry.HasKey(objectKey))
				{
					return entry;
				}
				entry = (com.db4o.foundation.HashtableObjectEntry)entry.i_next;
			}
			return null;
		}

		private void IncreaseSize()
		{
			i_tableSize = i_tableSize << 1;
			i_maximumSize = i_maximumSize << 1;
			i_mask = i_tableSize - 1;
			com.db4o.foundation.HashtableIntEntry[] temp = i_table;
			i_table = new com.db4o.foundation.HashtableIntEntry[i_tableSize];
			for (int i = 0; i < temp.Length; i++)
			{
				Reposition(temp[i]);
			}
		}

		private void Insert(com.db4o.foundation.HashtableIntEntry newEntry)
		{
			i_size++;
			if (i_size > i_maximumSize)
			{
				IncreaseSize();
			}
			int index = EntryIndex(newEntry);
			newEntry.i_next = i_table[index];
			i_table[index] = newEntry;
		}

		private int NewSize(int a_size)
		{
			return (int)(a_size / FILL);
		}

		private void PutEntry(com.db4o.foundation.HashtableIntEntry newEntry)
		{
			com.db4o.foundation.HashtableIntEntry existing = FindWithSameKey(newEntry);
			if (null != existing)
			{
				Replace(existing, newEntry);
			}
			else
			{
				Insert(newEntry);
			}
		}

		private void RemoveEntry(com.db4o.foundation.HashtableIntEntry predecessor, com.db4o.foundation.HashtableIntEntry
			 entry)
		{
			if (predecessor != null)
			{
				predecessor.i_next = entry.i_next;
			}
			else
			{
				i_table[EntryIndex(entry)] = entry.i_next;
			}
			i_size--;
		}

		private object RemoveObjectEntry(int intKey, object objectKey)
		{
			com.db4o.foundation.HashtableObjectEntry entry = (com.db4o.foundation.HashtableObjectEntry
				)i_table[intKey & i_mask];
			com.db4o.foundation.HashtableObjectEntry predecessor = null;
			while (entry != null)
			{
				if (entry.i_key == intKey && entry.HasKey(objectKey))
				{
					RemoveEntry(predecessor, entry);
					return entry.i_object;
				}
				predecessor = entry;
				entry = (com.db4o.foundation.HashtableObjectEntry)entry.i_next;
			}
			return null;
		}

		private void Replace(com.db4o.foundation.HashtableIntEntry existing, com.db4o.foundation.HashtableIntEntry
			 newEntry)
		{
			newEntry.i_next = existing.i_next;
			com.db4o.foundation.HashtableIntEntry entry = i_table[EntryIndex(existing)];
			if (entry == existing)
			{
				i_table[EntryIndex(existing)] = newEntry;
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

		private void Reposition(com.db4o.foundation.HashtableIntEntry a_entry)
		{
			if (a_entry != null)
			{
				Reposition(a_entry.i_next);
				a_entry.i_next = i_table[EntryIndex(a_entry)];
				i_table[EntryIndex(a_entry)] = a_entry;
			}
		}
	}
}
