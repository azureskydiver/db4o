using System;
using System.Collections;
using com.db4o;
using com.db4o.query;
using com.db4o.test;

namespace com.db4o.test
{
	internal class Sortable
	{
		public String a;
		
		public Sortable(String a)
		{
			this.a=a;
		}
	}
	
	internal class SortableComparator : QueryComparator
	{
		public int Compare(Object first, Object second) {
			return ((Sortable)first).a.CompareTo(((Sortable)second).a);
		}
	}

	public class SortedSameOrder
	{
		public void Store()
		{
			Tester.Store(new Sortable("a"));
			Tester.Store(new Sortable("c"));
			Tester.Store(new Sortable("b"));
		}
		
		public void Test()
		{
			Query query=Tester.Query();
			query.Constrain(typeof(Sortable));
			SortableComparator cmp = new SortableComparator();
			query.SortBy(cmp);
			ObjectSet result=query.Execute();
			
			Object last=null;
			while(result.HasNext()) {
				Object cur=result.Next();
				assertAscending(last,cur,cmp);
				last=cur;
			}
			last=null;
			result.Reset();

			for(int idx=0;idx<result.Count;idx++) {
				Object cur=result[idx];
				assertAscending(last,cur,cmp);
				last=cur;
			}
			last=null;
			result.Reset();

			foreach(Object cur in result) {
				assertAscending(last,cur,cmp);
				last=cur;
			}
		}
		
		private void assertAscending(Object a,Object b,QueryComparator cmp)
		{
			Tester.Ensure(a==null||cmp.Compare(a,b)<=0);
		}
	}
}