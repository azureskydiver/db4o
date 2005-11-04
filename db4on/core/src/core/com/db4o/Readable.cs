namespace com.db4o
{
	/// <exclude></exclude>
	public interface Readable
	{
		object read(com.db4o.YapReader a_reader);

		int byteCount();
	}
}
