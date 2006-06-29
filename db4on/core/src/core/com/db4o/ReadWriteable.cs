namespace com.db4o
{
	/// <exclude></exclude>
	public interface ReadWriteable : com.db4o.Readable
	{
		void Write(com.db4o.YapReader a_writer);
	}
}
