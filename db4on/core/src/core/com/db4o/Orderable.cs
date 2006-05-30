namespace com.db4o
{
	internal interface Orderable
	{
		int CompareTo(object obj);

		void HintOrder(int a_order, bool a_major);

		bool HasDuplicates();
	}
}
