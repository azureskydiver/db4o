namespace com.db4o
{
	internal interface Readable
	{
		object read(com.db4o.YapReader a_reader);

		int byteCount();
	}
}
