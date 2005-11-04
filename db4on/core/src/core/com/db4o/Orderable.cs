namespace com.db4o
{
	internal interface Orderable
	{
		int compareTo(object obj);

		void hintOrder(int a_order, bool a_major);

		bool hasDuplicates();
	}
}
