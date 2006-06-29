namespace com.db4o.foundation
{
	internal class HashtableObjectEntry : com.db4o.foundation.HashtableIntEntry
	{
		internal object i_objectKey;

		internal HashtableObjectEntry(int a_hash, object a_key, object a_object) : base(a_hash
			, a_object)
		{
			i_objectKey = a_key;
		}

		internal HashtableObjectEntry(object a_key, object a_object) : base(a_key.GetHashCode
			(), a_object)
		{
			i_objectKey = a_key;
		}

		protected HashtableObjectEntry() : base()
		{
		}

		public override void AcceptKeyVisitor(com.db4o.foundation.Visitor4 visitor)
		{
			visitor.Visit(i_objectKey);
		}

		public override object DeepClone(object obj)
		{
			return DeepCloneInternal(new com.db4o.foundation.HashtableObjectEntry(), obj);
		}

		protected override com.db4o.foundation.HashtableIntEntry DeepCloneInternal(com.db4o.foundation.HashtableIntEntry
			 entry, object obj)
		{
			((com.db4o.foundation.HashtableObjectEntry)entry).i_objectKey = i_objectKey;
			return base.DeepCloneInternal(entry, obj);
		}

		public virtual bool HasKey(object key)
		{
			return i_objectKey.Equals(key);
		}

		public override bool SameKeyAs(com.db4o.foundation.HashtableIntEntry other)
		{
			return other is com.db4o.foundation.HashtableObjectEntry ? HasKey(((com.db4o.foundation.HashtableObjectEntry
				)other).i_objectKey) : false;
		}
	}
}
