using System;
using System.Collections.Generic;

namespace com.db4o.test.net2
{
	public class Net2GenericOtherCollections
	{
		public void store()
		{
			CHolder ch = new CHolder();
			ch.CreateCollections();
			Tester.store(ch);
		}

		public void test()
		{
			CHolder ch = QueryHolder();
			ch.TestBeforeUpdate();
			ch.Update();
			Tester.reOpen();
			ch = QueryHolder();
			ch.TestAfterUpdate();
		}

		private CHolder QueryHolder()
		{
			return Tester.objectContainer().query<CHolder>(typeof(CHolder))[0];
		}
	}

	public class CHolder
	{
		private LinkedList<CItem> linkedList;
		private Queue<CItem> queue;
		private SortedDictionary<CItem, string> sortedDictionary;
		private SortedList<CItem, string> sortedList;
		private Stack<CItem> stack;

		public void CreateCollections()
		{
			linkedList = new LinkedList<CItem>();
			for (int i = 0; i < 10; i++)
			{
				linkedList.AddLast(new CItem("ll" + i));
			}

			queue = new Queue<CItem>();
			for (int i = 0; i < 10; i++)
			{
				queue.Enqueue(new CItem("q" + i));
			}

			sortedDictionary = new SortedDictionary<CItem, string>();
			for (int i = 0; i < 10; i++)
			{
				sortedDictionary.Add(new CItem("sd" + i), "sd" + i);
			}

			sortedList = new SortedList<CItem, string>();
			for (int i = 0; i < 10; i++)
			{
				sortedList.Add(new CItem("sl" + i), "sl" + i);
			}

			stack = new Stack<CItem>();
			for (int i = 0; i < 10; i++)
			{
				stack.Push(new CItem("st" + i));
			}
		}

		public void TestBeforeUpdate()
		{
			CheckLinkedList();
			CheckQueue();

			Tester.objectContainer().activate(sortedDictionary, int.MaxValue);
			CheckSortedDictionary();
		}

		private void CheckLinkedList()
		{
			Tester.ensure(linkedList.Last.Value.Equals(new CItem("ll9")));
		}

		private void CheckQueue()
		{
			for (int i = 0; i < 10; i++)
			{
				Tester.ensure(queue.Dequeue().Equals(new CItem("q" + i)));
			}
		}

		private void CheckSortedDictionary()
		{
			foreach (CItem cItem in sortedDictionary.Keys)
			{
				Console.WriteLine(cItem.Name);
			}
			for (int i = 0; i < 10; i++)
			{
				Object obj = sortedDictionary[new CItem("sd" + i)];
				Tester.ensure(obj.Equals("sd" + i));
			}
		}

		public void Update()
		{
			ObjectContainer oc = Tester.objectContainer();

			linkedList.AddLast(new CItem("update"));
			oc.set(linkedList);
		}

		public void TestAfterUpdate()
		{
			// Tester.ensure(linkedList.Last.Value.Equals(new CItem("update")));
		}
	}

	public class CItem : IComparable
	{
		private string _name;

		public CItem()
		{
		}

		public CItem(string name)
		{
			_name = name;
		}

		public override int GetHashCode()
		{
			return _name.GetHashCode();
		}

		public override bool Equals(object obj)
		{
			CItem other = obj as CItem;
			if (other == null)
			{
				return false;
			}
			return _name == other._name;
		}

		public string Name
		{
			get { return _name; }
		}

		public int CompareTo(Object other)
		{
			CItem otherCItem = other as CItem;
			return _name.CompareTo(otherCItem._name);
		}
	}
}