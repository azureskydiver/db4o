namespace com.db4o.ext
{
	/// <summary>the internal representation of a field on a stored class.</summary>
	/// <remarks>the internal representation of a field on a stored class.</remarks>
	public interface StoredField
	{
		/// <summary>returns the field value on the passed object.</summary>
		/// <remarks>
		/// returns the field value on the passed object.
		/// <br /><br />This method will also work, if the field is not present in the current
		/// version of the class.
		/// <br /><br />It is recommended to use this method for refactoring purposes, if fields
		/// are removed and the field values need to be copied to other fields.
		/// </remarks>
		object Get(object onObject);

		/// <summary>returns the name of the field.</summary>
		/// <remarks>returns the name of the field.</remarks>
		string GetName();

		/// <summary>returns the Class (Java) / Type (.NET) of the field.</summary>
		/// <remarks>
		/// returns the Class (Java) / Type (.NET) of the field.
		/// <br /><br />For array fields this method will return the type of the array.
		/// Use {link #isArray()} to detect arrays.
		/// </remarks>
		com.db4o.reflect.ReflectClass GetStoredType();

		/// <summary>returns true if the field is an array.</summary>
		/// <remarks>returns true if the field is an array.</remarks>
		bool IsArray();

		/// <summary>modifies the name of this stored field.</summary>
		/// <remarks>
		/// modifies the name of this stored field.
		/// <br /><br />After renaming one or multiple fields the ObjectContainer has
		/// to be closed and reopened to allow internal caches to be refreshed.<br /><br />
		/// </remarks>
		/// <param name="name">the new name</param>
		void Rename(string name);
	}
}
