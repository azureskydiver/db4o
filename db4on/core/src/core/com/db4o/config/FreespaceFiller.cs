namespace com.db4o.config
{
	/// <summary>Callback hook for overwriting freed space in the database file.</summary>
	/// <remarks>Callback hook for overwriting freed space in the database file.</remarks>
	public interface FreespaceFiller
	{
		/// <summary>Called to overwrite freed space in the database file.</summary>
		/// <remarks>Called to overwrite freed space in the database file.</remarks>
		/// <param name="io">Handle for the freed slot</param>
		void Fill(com.db4o.io.IoAdapterWindow io);
	}
}
