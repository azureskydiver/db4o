namespace com.db4o.ext
{
	/// <summary>extended factory class with static methods to open special db4o sessions.
	/// 	</summary>
	/// <remarks>extended factory class with static methods to open special db4o sessions.
	/// 	</remarks>
	public class ExtDb4oFactory : com.db4o.Db4o
	{
		/// <summary>
		/// Operates just like
		/// <see cref="com.db4o.ext.ExtDb4o.OpenMemoryFile">com.db4o.ext.ExtDb4o.OpenMemoryFile
		/// 	</see>
		/// , but uses
		/// the global db4o
		/// <see cref="com.db4o.config.Configuration">Configuration</see>
		/// context.
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
		/// <param name="config">
		/// a custom
		/// <see cref="com.db4o.config.Configuration">Configuration</see>
		/// instance to be obtained via
		/// <see cref="com.db4o.Db4o.NewConfiguration">com.db4o.Db4o.NewConfiguration</see>
		/// </param>
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
		public static com.db4o.ObjectContainer OpenMemoryFile(com.db4o.config.Configuration
			 config, com.db4o.ext.MemoryFile memoryFile)
		{
			return OpenMemoryFile1(config, memoryFile);
		}
	}
}
