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

		private HashtableObjectEntry() : base()
		{
		}

		public override void acceptKeyVisitor(com.db4o.foundation.Visitor4 visitor)
		{
			visitor.visit(i_objectKey);
		}

		public override object deepClone(object obj)
		{
			com.db4o.foundation.HashtableObjectEntry ret = new com.db4o.foundation.HashtableObjectEntry
				();
			deepCloneInternal(ret, obj);
			ret.i_objectKey = i_objectKey;
			return ret;
		}

		public virtual bool hasKey(object key)
		{
			return i_objectKey.Equals(key);
		}

		public override bool sameKeyAs(com.db4o.foundation.HashtableIntEntry other)
		{
			return other is com.db4o.foundation.HashtableObjectEntry ? hasKey(((com.db4o.foundation.HashtableObjectEntry
				)other).i_objectKey) : false;
		}
	}
}
