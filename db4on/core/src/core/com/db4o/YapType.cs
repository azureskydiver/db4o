namespace com.db4o
{
	internal interface YapType
	{
		object defaultValue();

		int typeID();


		void write(object obj, byte[] bytes, int offset);

		object read(byte[] bytes, int offset);

		int compare(object compare, object with);

		bool isEqual(object compare, object with);
	}
}
