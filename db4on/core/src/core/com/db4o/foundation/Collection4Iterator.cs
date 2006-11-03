namespace com.db4o.foundation
{
	/// <exclude></exclude>
	public class Collection4Iterator : com.db4o.foundation.Iterator4Impl
	{
		private readonly com.db4o.foundation.Collection4 _collection;

		private readonly int _initialVersion;

		public Collection4Iterator(com.db4o.foundation.Collection4 collection, com.db4o.foundation.List4
			 first) : base(first)
		{
			_collection = collection;
			_initialVersion = CurrentVersion();
		}

		public override bool MoveNext()
		{
			Validate();
			return base.MoveNext();
		}

		public override object Current
		{
			get
			{
				Validate();
				return base.Current;
			}
		}

		private void Validate()
		{
			if (_initialVersion != CurrentVersion())
			{
				throw new com.db4o.foundation.InvalidIteratorException();
			}
		}

		private int CurrentVersion()
		{
			return _collection.Version();
		}
	}
}
