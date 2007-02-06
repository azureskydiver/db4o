namespace com.db4o.ext
{
	/// <summary>Class to identify a database by it's signature.</summary>
	/// <remarks>
	/// Class to identify a database by it's signature.
	/// <br /><br />db4o UUID handling uses a reference to the Db4oDatabase object, that
	/// represents the database an object was created on.
	/// </remarks>
	/// <persistent></persistent>
	/// <exclude></exclude>
	public class Db4oDatabase : com.db4o.types.Db4oType, com.db4o.Internal4
	{
		/// <summary>Field is public for implementation reasons, DO NOT TOUCH!</summary>
		public byte[] i_signature;

		/// <summary>
		/// Field is public for implementation reasons, DO NOT TOUCH!
		/// This field is badly named, it really is the creation time.
		/// </summary>
		/// <remarks>
		/// Field is public for implementation reasons, DO NOT TOUCH!
		/// This field is badly named, it really is the creation time.
		/// </remarks>
		public long i_uuid;

		private static readonly string CREATIONTIME_FIELD = "i_uuid";

		/// <summary>cached ObjectContainer for getting the own ID.</summary>
		/// <remarks>cached ObjectContainer for getting the own ID.</remarks>
		[System.NonSerialized]
		private com.db4o.@internal.ObjectContainerBase i_stream;

		/// <summary>cached ID, only valid in combination with i_objectContainer</summary>
		[System.NonSerialized]
		private int i_id;

		public Db4oDatabase()
		{
		}

		public Db4oDatabase(byte[] signature, long creationTime)
		{
			i_signature = signature;
			i_uuid = creationTime;
		}

		/// <summary>generates a new Db4oDatabase object with a unique signature.</summary>
		/// <remarks>generates a new Db4oDatabase object with a unique signature.</remarks>
		public static com.db4o.ext.Db4oDatabase Generate()
		{
			return new com.db4o.ext.Db4oDatabase(com.db4o.@internal.Unobfuscated.GenerateSignature
				(), j4o.lang.JavaSystem.CurrentTimeMillis());
		}

		/// <summary>comparison by signature.</summary>
		/// <remarks>comparison by signature.</remarks>
		public override bool Equals(object obj)
		{
			if (obj == this)
			{
				return true;
			}
			if (obj == null || j4o.lang.JavaSystem.GetClassForObject(this) != j4o.lang.JavaSystem.GetClassForObject
				(obj))
			{
				return false;
			}
			com.db4o.ext.Db4oDatabase other = (com.db4o.ext.Db4oDatabase)obj;
			if (null == other.i_signature || null == this.i_signature)
			{
				return false;
			}
			return com.db4o.foundation.Arrays4.AreEqual(other.i_signature, this.i_signature);
		}

		public override int GetHashCode()
		{
			return i_signature.GetHashCode();
		}

		/// <summary>gets the db4o ID, and may cache it for performance reasons.</summary>
		/// <remarks>gets the db4o ID, and may cache it for performance reasons.</remarks>
		/// <returns>the db4o ID for the ObjectContainer</returns>
		public virtual int GetID(com.db4o.@internal.Transaction trans)
		{
			com.db4o.@internal.ObjectContainerBase stream = trans.Stream();
			if (stream != i_stream)
			{
				i_stream = stream;
				i_id = Bind(trans);
			}
			return i_id;
		}

		public virtual long GetCreationTime()
		{
			return i_uuid;
		}

		/// <summary>returns the unique signature</summary>
		public virtual byte[] GetSignature()
		{
			return i_signature;
		}

		public override string ToString()
		{
			return "db " + i_signature;
		}

		public virtual bool IsOlderThan(com.db4o.ext.Db4oDatabase peer)
		{
			if (peer == this)
			{
				throw new System.ArgumentException();
			}
			if (i_uuid != peer.i_uuid)
			{
				return i_uuid < peer.i_uuid;
			}
			if (i_signature.Length != peer.i_signature.Length)
			{
				return i_signature.Length < peer.i_signature.Length;
			}
			for (int i = 0; i < i_signature.Length; i++)
			{
				if (i_signature[i] != peer.i_signature[i])
				{
					return i_signature[i] < peer.i_signature[i];
				}
			}
			throw new System.Exception();
		}

		/// <summary>make sure this Db4oDatabase is stored.</summary>
		/// <remarks>make sure this Db4oDatabase is stored. Return the ID.</remarks>
		public virtual int Bind(com.db4o.@internal.Transaction trans)
		{
			com.db4o.@internal.ObjectContainerBase stream = trans.Stream();
			com.db4o.ext.Db4oDatabase stored = (com.db4o.ext.Db4oDatabase)stream.Db4oTypeStored
				(trans, this);
			if (stored == null)
			{
				stream.ShowInternalClasses(true);
				stream.Set3(trans, this, 2, false);
				int newID = stream.GetID1(this);
				stream.ShowInternalClasses(false);
				return newID;
			}
			if (stored == this)
			{
				return stream.GetID1(this);
			}
			if (i_uuid == 0)
			{
				i_uuid = stored.i_uuid;
			}
			stream.ShowInternalClasses(true);
			int id = stream.GetID1(stored);
			stream.Bind(this, id);
			stream.ShowInternalClasses(false);
			return id;
		}

		/// <summary>find a Db4oDatabase with the same signature as this one</summary>
		public virtual com.db4o.ext.Db4oDatabase Query(com.db4o.@internal.Transaction trans
			)
		{
			if (i_uuid > 0)
			{
				com.db4o.ext.Db4oDatabase res = Query(trans, true);
				if (res != null)
				{
					return res;
				}
			}
			return Query(trans, false);
		}

		private com.db4o.ext.Db4oDatabase Query(com.db4o.@internal.Transaction trans, bool
			 constrainByUUID)
		{
			com.db4o.@internal.ObjectContainerBase stream = trans.Stream();
			com.db4o.query.Query q = stream.Query(trans);
			q.Constrain(j4o.lang.JavaSystem.GetClassForObject(this));
			if (constrainByUUID)
			{
				q.Descend(CREATIONTIME_FIELD).Constrain(i_uuid);
			}
			com.db4o.ObjectSet objectSet = q.Execute();
			while (objectSet.HasNext())
			{
				com.db4o.ext.Db4oDatabase storedDatabase = (com.db4o.ext.Db4oDatabase)objectSet.Next
					();
				stream.Activate1(null, storedDatabase, 4);
				if (storedDatabase.Equals(this))
				{
					return storedDatabase;
				}
			}
			return null;
		}
	}
}
