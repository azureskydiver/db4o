namespace com.db4o.db4ounit.common.assorted
{
	public class LongLinkedListTestCase : Db4oUnit.Extensions.AbstractDb4oTestCase
	{
		private const int COUNT = 1000;

		public class LinkedList
		{
			public com.db4o.db4ounit.common.assorted.LongLinkedListTestCase.LinkedList _next;

			public int _depth;
		}

		private static com.db4o.db4ounit.common.assorted.LongLinkedListTestCase.LinkedList
			 NewLongCircularList()
		{
			com.db4o.db4ounit.common.assorted.LongLinkedListTestCase.LinkedList head = new com.db4o.db4ounit.common.assorted.LongLinkedListTestCase.LinkedList
				();
			com.db4o.db4ounit.common.assorted.LongLinkedListTestCase.LinkedList tail = head;
			for (int i = 1; i < COUNT; i++)
			{
				tail._next = new com.db4o.db4ounit.common.assorted.LongLinkedListTestCase.LinkedList
					();
				tail = tail._next;
				tail._depth = i;
			}
			tail._next = head;
			return head;
		}

		public static void Main(string[] args)
		{
			new com.db4o.db4ounit.common.assorted.LongLinkedListTestCase().RunSolo();
		}

		protected override void Store()
		{
			Store(NewLongCircularList());
		}

		public virtual void Test()
		{
			com.db4o.query.Query q = NewQuery(typeof(com.db4o.db4ounit.common.assorted.LongLinkedListTestCase.LinkedList)
				);
			q.Descend("_depth").Constrain(0);
			com.db4o.ObjectSet objectSet = q.Execute();
			Db4oUnit.Assert.AreEqual(1, objectSet.Size());
			com.db4o.db4ounit.common.assorted.LongLinkedListTestCase.LinkedList head = (com.db4o.db4ounit.common.assorted.LongLinkedListTestCase.LinkedList
				)objectSet.Next();
			Db().Activate(head, int.MaxValue);
			AssertListIsComplete(head);
			Db().Deactivate(head, int.MaxValue);
			Db().Activate(head, int.MaxValue);
			AssertListIsComplete(head);
			Db().Deactivate(head, int.MaxValue);
			Db().Refresh(head, int.MaxValue);
			AssertListIsComplete(head);
		}

		private void AssertListIsComplete(com.db4o.db4ounit.common.assorted.LongLinkedListTestCase.LinkedList
			 head)
		{
			int count = 1;
			com.db4o.db4ounit.common.assorted.LongLinkedListTestCase.LinkedList tail = head._next;
			while (tail != head)
			{
				count++;
				tail = tail._next;
			}
			Db4oUnit.Assert.AreEqual(COUNT, count);
		}
	}
}
