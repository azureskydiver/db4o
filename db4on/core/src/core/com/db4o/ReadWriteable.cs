namespace com.db4o
{
	/// <exclude></exclude>
	public interface ReadWriteable : com.db4o.Readable
	{
		void write(com.db4o.YapWriter a_writer);
	}
}
