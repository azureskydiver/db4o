namespace com.db4o
{
	internal interface ReadWriteable : com.db4o.Readable
	{
		void write(com.db4o.YapWriter a_writer);
	}
}
