namespace com.db4o
{
	/// <exclude></exclude>
	public class MigrationConnection
	{
		private readonly com.db4o.foundation.Hashtable4 _referenceMap;

		internal MigrationConnection()
		{
			_referenceMap = new com.db4o.foundation.Hashtable4(1);
		}

		public virtual void mapReference(object obj, com.db4o.YapObject _ref)
		{
			_referenceMap.put(j4o.lang.JavaSystem.identityHashCode(obj), _ref);
		}

		public virtual com.db4o.YapObject referenceFor(object obj)
		{
			int hcode = j4o.lang.JavaSystem.identityHashCode(obj);
			com.db4o.YapObject _ref = (com.db4o.YapObject)_referenceMap.get(hcode);
			_referenceMap.remove(hcode);
			return _ref;
		}
	}
}
