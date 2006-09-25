namespace com.db4o.ext
{
	/// <summary>extended factory class with static methods to open special db4o sessions.
	/// 	</summary>
	/// <remarks>extended factory class with static methods to open special db4o sessions.
	/// 	</remarks>
	public sealed class ExtDb4o : com.db4o.Db4o
	{
		/// <summary>
		/// opens an
		/// <see cref="com.db4o.ObjectContainer">com.db4o.ObjectContainer</see>
		/// for in-memory use .
		/// <br /><br />In-memory ObjectContainers are useful for maximum performance
		/// on small databases, for swapping objects or for storing db4o format data
		/// to other media or other databases.<br /><br />Be aware of the danger of running
		/// into OutOfMemory problems or complete loss of all data, in case of hardware
		/// or JVM failures.<br /><br />
		/// </summary>
		/// <param name="memoryFile">
		/// a
		/// <see cref="com.db4o.ext.MemoryFile">MemoryFile</see>
		/// 
		/// to store the raw byte data.
		/// </param>
		/// <returns>
		/// an open
		/// <see cref="com.db4o.ObjectContainer">ObjectContainer</see>
		/// </returns>
		/// <seealso cref="com.db4o.ext.MemoryFile">com.db4o.ext.MemoryFile</seealso>
		public static com.db4o.ObjectContainer OpenMemoryFile(com.db4o.ext.MemoryFile memoryFile
			)
		{
			return OpenMemoryFile1(com.db4o.Db4o.NewConfiguration(), memoryFile);
		}

		public static com.db4o.ObjectContainer OpenMemoryFile(com.db4o.config.Configuration
			 config, com.db4o.ext.MemoryFile memoryFile)
		{
			return OpenMemoryFile1(config, memoryFile);
		}
	}
}
