namespace com.db4o.ext
{
	/// <summary>
	/// interface to the internal reference that an ObjectContainer
	/// holds for a stored object.
	/// </summary>
	/// <remarks>
	/// interface to the internal reference that an ObjectContainer
	/// holds for a stored object.
	/// </remarks>
	public interface ObjectInfo
	{
		/// <summary>returns the object that is referenced.</summary>
		/// <remarks>
		/// returns the object that is referenced.
		/// <br /><br />This method may return null, if the object has
		/// been garbage collected.
		/// </remarks>
		/// <returns>
		/// the referenced object or null, if the object has
		/// been garbage collected.
		/// </returns>
		object GetObject();

		/// <summary>returns a UUID representation of the referenced object.</summary>
		/// <remarks>
		/// returns a UUID representation of the referenced object.
		/// UUID generation has to be turned on, in order to be able
		/// to use this feature:
		/// <see cref="com.db4o.config.Configuration.GenerateUUIDs">com.db4o.config.Configuration.GenerateUUIDs
		/// 	</see>
		/// </remarks>
		/// <returns>the UUID of the referenced object.</returns>
		com.db4o.ext.Db4oUUID GetUUID();

		/// <summary>
		/// returns the transaction serial number ("version") the
		/// referenced object was stored with last.
		/// </summary>
		/// <remarks>
		/// returns the transaction serial number ("version") the
		/// referenced object was stored with last.
		/// Version number generation has to be turned on, in order to
		/// be able to use this feature:
		/// <see cref="com.db4o.config.Configuration.GenerateVersionNumbers">com.db4o.config.Configuration.GenerateVersionNumbers
		/// 	</see>
		/// </remarks>
		/// <returns>the version number.</returns>
		long GetVersion();
	}
}
