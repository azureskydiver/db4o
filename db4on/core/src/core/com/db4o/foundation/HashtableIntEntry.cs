namespace com.db4o.foundation
{
	internal class HashtableIntEntry : com.db4o.foundation.DeepClone
	{
		internal int i_key;

		internal object i_object;

		internal com.db4o.foundation.HashtableIntEntry i_next;

		internal HashtableIntEntry(int a_hash, object a_object)
		{
			i_key = a_hash;
			i_object = a_object;
		}

		protected HashtableIntEntry()
		{
		}

		public virtual void AcceptKeyVisitor(com.db4o.foundation.Visitor4 visitor)
		{
			visitor.Visit(i_key);
		}

		public virtual object DeepClone(object obj)
		{
			return DeepCloneInternal(new com.db4o.foundation.HashtableIntEntry(), obj);
		}

		public virtual bool SameKeyAs(com.db4o.foundation.HashtableIntEntry other)
		{
			return i_key == other.i_key;
		}

		protected virtual com.db4o.foundation.HashtableIntEntry DeepCloneInternal(com.db4o.foundation.HashtableIntEntry
			 entry, object obj)
		{
			entry.i_key = i_key;
			entry.i_next = i_next;
			if (i_object is com.db4o.foundation.DeepClone)
			{
				entry.i_object = ((com.db4o.foundation.DeepClone)i_object).DeepClone(obj);
			}
			else
			{
				entry.i_object = i_object;
			}
			if (i_next != null)
			{
				entry.i_next = (com.db4o.foundation.HashtableIntEntry)i_next.DeepClone(obj);
			}
			return entry;
		}
	}
}
