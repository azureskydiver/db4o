namespace com.db4o.foundation
{
	internal class HashtableObjectEntry : com.db4o.foundation.HashtableIntEntry
	{
		internal object i_objectKey;

		internal HashtableObjectEntry(object a_key, object a_object) : base(a_key.GetHashCode
			(), a_object)
		{
			i_objectKey = a_key;
		}

		internal HashtableObjectEntry(int a_hash, object a_key, object a_object) : base(a_hash
			, a_object)
		{
			i_objectKey = a_key;
		}
	}
}
