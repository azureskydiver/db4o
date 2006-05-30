namespace com.db4o.ext
{
	/// <summary>generic callback interface.</summary>
	/// <remarks>generic callback interface.</remarks>
	public interface Db4oCallback
	{
		/// <summary>the callback method</summary>
		/// <param name="obj">the object passed to the callback method</param>
		void Callback(object obj);
	}
}
