namespace com.db4o.ext
{
	/// <summary>a unique universal identify for an object.</summary>
	/// <remarks>
	/// a unique universal identify for an object. <br /><br />The db4o UUID consists of
	/// two parts:<br /> - an indexed long for fast access,<br /> - the signature of the
	/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
	/// the object was created with.
	/// <br /><br />Db4oUUIDs are valid representations of objects over multiple
	/// ObjectContainers
	/// </remarks>
	public class Db4oUUID
	{
		private readonly long longPart;

		private readonly byte[] signaturePart;

		public Db4oUUID(long longPart, byte[] signaturePart)
		{
			this.longPart = longPart;
			this.signaturePart = signaturePart;
		}

		/// <summary>returns the long part of this UUID.</summary>
		/// <remarks>
		/// returns the long part of this UUID. <br /><br />To uniquely identify an object
		/// universally, db4o uses an indexed long and a reference to the
		/// <see cref="com.db4o.ext.Db4oDatabase">com.db4o.ext.Db4oDatabase</see>
		/// object it was created on.
		/// </remarks>
		/// <returns>the long part of this UUID.</returns>
		public virtual long getLongPart()
		{
			return longPart;
		}

		/// <summary>returns the signature part of this UUID.</summary>
		/// <remarks>
		/// returns the signature part of this UUID. <br /><br /> <br /><br />To uniquely
		/// identify an object universally, db4o uses an indexed long and a reference to
		/// the
		/// <see cref="com.db4o.ext.Db4oDatabase">com.db4o.ext.Db4oDatabase</see>
		/// singleton object of the
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// it was created on. This method
		/// returns the signature of the Db4oDatabase object of the ObjectContainer: the
		/// signature of the origin ObjectContainer.
		/// </remarks>
		/// <returns>the signature of the Db4oDatabase for this UUID.</returns>
		public virtual byte[] getSignaturePart()
		{
			return signaturePart;
		}

		public override bool Equals(object o)
		{
			if (this == o)
			{
				return true;
			}
			if (o == null || j4o.lang.Class.getClassForObject(this) != j4o.lang.Class.getClassForObject
				(o))
			{
				return false;
			}
			com.db4o.ext.Db4oUUID db4oUUID = (com.db4o.ext.Db4oUUID)o;
			if (longPart != db4oUUID.longPart)
			{
				return false;
			}
			if (signaturePart == null)
			{
				return db4oUUID.signaturePart == null;
			}
			if (signaturePart.Length != db4oUUID.signaturePart.Length)
			{
				return false;
			}
			for (int i = 0; i < signaturePart.Length; i++)
			{
				if (signaturePart[i] != db4oUUID.signaturePart[i])
				{
					return false;
				}
			}
			return true;
		}

		public override int GetHashCode()
		{
			return (int)(longPart ^ (unchecked((int)(unchecked((uint)(longPart)) >> 32))));
		}

		public override string ToString()
		{
			j4o.lang.JavaSystem._out.println("toString");
			return "long part = " + longPart + ", sign = " + flattenSign();
		}

		protected virtual string flattenSign()
		{
			string _out = "";
			for (int i = 0; i < signaturePart.Length; i++)
			{
				if (i != 0)
				{
					_out += ", ";
				}
				_out += signaturePart[i];
			}
			return _out;
		}
	}
}
