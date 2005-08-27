
namespace com.db4o.ext
{
	/// <summary>a unique universal identify for an object.</summary>
	/// <remarks>
	/// a unique universal identify for an object.
	/// <br /><br />The db4o UUID consists of two parts:<br />
	/// - an indexed long for fast access,<br />
	/// - the signature of the
	/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
	/// the object
	/// was created with.
	/// <br /><br />Db4oUUIDs are valid representations of objects
	/// over multiple ObjectContainers
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
		/// returns the long part of this UUID.
		/// <br /><br />To uniquely identify an object universally, db4o
		/// uses an indexed long and a reference to the
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
		/// returns the signature part of this UUID.
		/// <br /><br />
		/// <br /><br />To uniquely identify an object universally, db4o
		/// uses an indexed long and a reference to the
		/// <see cref="com.db4o.ext.Db4oDatabase">com.db4o.ext.Db4oDatabase</see>
		/// singleton object of the
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// it was created on.
		/// This method returns the signature of the Db4oDatabase object of
		/// the ObjectContainer: the signature of the origin ObjectContainer.
		/// </remarks>
		/// <returns>the signature of the Db4oDatabase for this UUID.</returns>
		public virtual byte[] getSignaturePart()
		{
			return signaturePart;
		}
	}
}
