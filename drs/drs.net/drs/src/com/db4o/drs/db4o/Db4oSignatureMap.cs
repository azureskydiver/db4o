namespace com.db4o.drs.db4o
{
	internal class Db4oSignatureMap
	{
		private readonly com.db4o.YapStream _stream;

		private readonly com.db4o.foundation.Hashtable4 _identities;

		internal Db4oSignatureMap(com.db4o.YapStream stream)
		{
			_stream = stream;
			_identities = new com.db4o.foundation.Hashtable4();
		}

		internal virtual com.db4o.ext.Db4oDatabase Produce(byte[] signature, long creationTime
			)
		{
			com.db4o.ext.Db4oDatabase db = (com.db4o.ext.Db4oDatabase)_identities.Get(signature
				);
			if (db != null)
			{
				return db;
			}
			db = new com.db4o.ext.Db4oDatabase(signature, creationTime);
			db.Bind(_stream.GetTransaction());
			_identities.Put(signature, db);
			return db;
		}

		public virtual void Put(com.db4o.ext.Db4oDatabase db)
		{
			com.db4o.ext.Db4oDatabase existing = (com.db4o.ext.Db4oDatabase)_identities.Get(db
				.GetSignature());
			if (existing == null)
			{
				_identities.Put(db.GetSignature(), db);
			}
		}
	}
}
