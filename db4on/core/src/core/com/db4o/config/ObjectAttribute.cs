namespace com.db4o.config
{
	/// <summary>generic interface to allow returning an attribute of an object.</summary>
	/// <remarks>generic interface to allow returning an attribute of an object.</remarks>
	public interface ObjectAttribute
	{
		/// <summary>generic method to return an attribute of a parent object.</summary>
		/// <remarks>generic method to return an attribute of a parent object.</remarks>
		/// <param name="parent">the parent object</param>
		/// <returns>Object - the attribute</returns>
		object attribute(object parent);
	}
}
