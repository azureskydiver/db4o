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
		[com.db4o.Transient]
		private com.db4o.YapStream i_stream;

		/// <summary>cached ID, only valid in combination with i_objectContainer</summary>
		[com.db4o.Transient]
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
		public static com.db4o.ext.Db4oDatabase generate()
		{
			com.db4o.ext.Db4oDatabase db = new com.db4o.ext.Db4oDatabase();
			db.i_signature = com.db4o.Unobfuscated.generateSignature();
			db.i_uuid = j4o.lang.JavaSystem.currentTimeMillis();
			return db;
		}

		/// <summary>comparison by signature.</summary>
		/// <remarks>comparison by signature.</remarks>
		public override bool Equals(object obj)
		{
			if (obj == this)
			{
				return true;
			}
			if (obj == null || j4o.lang.Class.getClassForObject(this) != j4o.lang.Class.getClassForObject
				(obj))
			{
				return false;
			}
			com.db4o.ext.Db4oDatabase other = (com.db4o.ext.Db4oDatabase)obj;
			if (other.i_signature == null || this.i_signature == null)
			{
				return false;
			}
			if (other.i_signature.Length != i_signature.Length)
			{
				return false;
			}
			for (int i = 0; i < i_signature.Length; i++)
			{
				if (i_signature[i] != other.i_signature[i])
				{
					return false;
				}
			}
			return true;
		}

		/// <summary>gets the db4o ID, and may cache it for performance reasons.</summary>
		/// <remarks>gets the db4o ID, and may cache it for performance reasons.</remarks>
		/// <returns>the db4o ID for the ObjectContainer</returns>
		public virtual int getID(com.db4o.Transaction trans)
		{
			com.db4o.YapStream stream = trans.i_stream;
			if (stream != i_stream)
			{
				i_stream = stream;
				i_id = bind(trans);
			}
			return i_id;
		}

		public virtual long getCreationTime()
		{
			return i_uuid;
		}

		/// <summary>returns the unique signature</summary>
		public virtual byte[] getSignature()
		{
			return i_signature;
		}

		public override string ToString()
		{
			return "db " + i_signature;
		}

		public virtual bool isOlderThan(com.db4o.ext.Db4oDatabase peer)
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
			throw new j4o.lang.RuntimeException();
		}

		/// <summary>make sure this Db4oDatabase is stored.</summary>
		/// <remarks>make sure this Db4oDatabase is stored. Return the ID.</remarks>
		public virtual int bind(com.db4o.Transaction trans)
		{
			com.db4o.YapStream stream = trans.i_stream;
			com.db4o.ext.Db4oDatabase stored = (com.db4o.ext.Db4oDatabase)stream.db4oTypeStored
				(trans, this);
			if (stored == null)
			{
				stream.showInternalClasses(true);
				stream.set3(trans, this, 2, false);
				int newID = stream.getID1(trans, this);
				stream.showInternalClasses(false);
				return newID;
			}
			if (stored == this)
			{
				return stream.getID1(trans, this);
			}
			if (i_uuid == 0)
			{
				i_uuid = stored.i_uuid;
			}
			stream.showInternalClasses(true);
			int id = stream.getID1(trans, stored);
			stream.bind(this, id);
			stream.showInternalClasses(false);
			return id;
		}

		/// <summary>find a Db4oDatabase with the same signature as this one</summary>
		public virtual com.db4o.ext.Db4oDatabase query(com.db4o.Transaction trans)
		{
			if (i_uuid > 0)
			{
				com.db4o.ext.Db4oDatabase res = query(trans, true);
				if (res != null)
				{
					return res;
				}
			}
			return query(trans, false);
		}

		private com.db4o.ext.Db4oDatabase query(com.db4o.Transaction trans, bool constrainByUUID
			)
		{
			com.db4o.YapStream stream = trans.i_stream;
			com.db4o.query.Query q = stream.querySharpenBug(trans);
			q.constrain(j4o.lang.Class.getClassForObject(this));
			if (constrainByUUID)
			{
				q.descend(CREATIONTIME_FIELD).constrain(i_uuid);
			}
			com.db4o.ObjectSet objectSet = q.execute();
			while (objectSet.hasNext())
			{
				com.db4o.ext.Db4oDatabase storedDatabase = (com.db4o.ext.Db4oDatabase)objectSet.next
					();
				stream.activate1(null, storedDatabase, 4);
				if (storedDatabase.Equals(this))
				{
					return storedDatabase;
				}
			}
			return null;
		}
	}
}
