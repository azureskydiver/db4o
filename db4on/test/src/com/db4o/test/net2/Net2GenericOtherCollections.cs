namespace com.db4o.test.net2
{
#if NET_2_0 || CF_2_0

    using System;
	using System.Collections.Generic;
	using com.db4o.ext;

    public class Net2GenericOtherCollections
    {
        public void Store()
        {
            CHolder ch = new CHolder();
            ch.CreateCollections();
            Tester.Store(ch);
        }

        public void Test()
        {
            CHolder ch = QueryHolder();
            ch.TestBeforeUpdate();
            ch.Update();
            Tester.ReOpen();
            ch = QueryHolder();
            ch.TestAfterUpdate();
        }

        private CHolder QueryHolder()
        {
            return Tester.ObjectContainer().Query<CHolder>(typeof(CHolder))[0];
        }

    }

    public class CHolder
    {
        LinkedList <CItem> linkedList;
        Queue<CItem> queue;
#if !CF_2_0
        SortedDictionary<CItem,string> sortedDictionary;
#endif
        SortedList<CItem,string> sortedList;
        Stack<CItem> stack;

        public void CreateCollections()
        {
            linkedList = new LinkedList<CItem>();
            for(int i = 0; i < 10; i++)
            {
                linkedList.AddLast(new CItem("ll" + i));
            }

            queue = new Queue<CItem>();
            for (int i = 0; i < 10; i++)
            {
                queue.Enqueue(new CItem("q" + i));
            }
#if !CF_2_0
            sortedDictionary = new SortedDictionary<CItem, string>();
            for (int i = 0; i < 10; i++)
            {
                sortedDictionary.Add(new CItem("sd" + i), "sd" + i);
            }
#endif

            sortedList = new SortedList<CItem,string>();
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
            ExtObjectContainer oc = Tester.ObjectContainer();

            Tester.Ensure(linkedList.Last.Value.Equals(new CItem("ll9")));

            for (int i = 0; i < 10; i++)
            {
                Tester.Ensure(queue.Dequeue().Equals(new CItem("q" + i)));
            }
#if !CF_2_0
            // Sorted dictionary needs explicit activation since it uses a TreeSet underneath.
            oc.Activate(sortedDictionary, int.MaxValue);
            for (int i = 0; i < 10; i++)
            {
                Object obj = sortedDictionary[new CItem("sd" + i)];
                Tester.Ensure(obj.Equals("sd" + i));
            }
#endif

            for (int i = 0; i < 10; i++)
            {
                Tester.Ensure(sortedList[new CItem("sl" + i)].Equals("sl" + i));
            }

            for (int i = 9; i >= 0; i--)
            {
                Tester.Ensure(stack.Pop().Equals(new CItem("st" + i)));
            }

        }

        public void Update()
        {
            ObjectContainer oc = Tester.ObjectContainer();
            
            linkedList.AddLast(new CItem("update"));
            oc.Set(linkedList);

            queue.Enqueue(new CItem("update"));
            oc.Set(queue);
#if !CF_2_0
            sortedDictionary.Add(new CItem("update"), "update");
            oc.Set(sortedDictionary);
#endif
            sortedList.Add(new CItem("update"), "update");
            oc.Set(sortedList);

            stack.Push(new CItem("update"));
            oc.Set(stack);
        }

        public void TestAfterUpdate()
        {
            Tester.Ensure(linkedList.Last.Value.Equals(new CItem("update")));

            Tester.Ensure(queue.Dequeue().Equals(new CItem("update")));

            Tester.Ensure(sortedList[new CItem("update")].Equals("update"));

            Tester.Ensure(stack.Pop().Equals(new CItem("update")));

        }

    }

    public class CItem : IComparable
    {
        string _name;

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
            if (obj == null)
            {
                return false;
            }

            CItem other = obj as CItem;

            if (other == null)
            {
                return false;
            }

            return _name.Equals(other._name);
        }

        public string Name()
        {
            return _name;
        }

        public int CompareTo(Object other)
        {
            CItem otherCItem = other as CItem;
            return _name.CompareTo(otherCItem._name);
        }
    }
#endif
}
