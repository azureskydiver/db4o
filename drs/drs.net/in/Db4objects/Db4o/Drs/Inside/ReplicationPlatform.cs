using System;
using Db4objects.Db4o.Reflect;

namespace Db4objects.Db4o.Drs.Inside
{
	/// <summary>
	/// Platform dependent code goes here to minimize manually
	/// converted code.
	/// </summary>
	public class ReplicationPlatform
	{
        public static void CopyCollectionState(object original, object destination, Db4objects.Db4o.Drs.Inside.ICounterpartFinder
			 counterpartFinder)
		{
			System.Collections.IList originalCollection = (System.Collections.IList
				)original;
			System.Collections.IList destinationCollection = (System.Collections.IList
				)destination;
			destinationCollection.Clear();
			System.Collections.IEnumerator it = originalCollection.GetEnumerator();
			while (it.MoveNext())
			{
				object element = it.Current;
				object counterpart = counterpartFinder.FindCounterpart(element);
				destinationCollection.Add(counterpart);
			}
		}

		public static System.Collections.ICollection EmptyCollectionClone(System.Collections.ICollection
			 original)
		{
			if (original is System.Collections.IList)
			{
				return new System.Collections.ArrayList(original.Count);
			}
			return null;
		}
	}
}

