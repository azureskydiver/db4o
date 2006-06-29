namespace com.db4o
{
	/// <exclude></exclude>
	public interface Readable
	{
		object Read(com.db4o.YapReader a_reader);

		int ByteCount();
	}
}
