namespace com.db4o.drs.inside
{
	public class MapHandler : com.db4o.drs.inside.CollectionHandler
	{
		private readonly com.db4o.reflect.ReflectClass _reflectMapClass;

		private readonly com.db4o.reflect.Reflector _reflector;

		public MapHandler(com.db4o.reflect.Reflector reflector)
		{
			_reflector = reflector;
			_reflectMapClass = com.db4o.drs.inside.ReplicationPlatform.ForType(reflector, typeof(
				System.Collections.IDictionary));
		}

		public virtual bool CanHandle(com.db4o.reflect.ReflectClass claxx)
		{
			return _reflectMapClass.IsAssignableFrom(claxx);
		}

		public virtual bool CanHandle(object obj)
		{
			return CanHandle(_reflector.ForObject(obj));
		}

		public virtual bool CanHandle(System.Type c)
		{
			return CanHandle(com.db4o.drs.inside.ReplicationPlatform.ForType(_reflector, c));
		}

		public virtual com.db4o.foundation.Iterator4 IteratorFor(object collection)
		{
			System.Collections.IDictionary map = (System.Collections.IDictionary)collection;
			com.db4o.foundation.Collection4 result = new com.db4o.foundation.Collection4();
			System.Collections.IEnumerator it = map.GetEnumerator();
			while (it.MoveNext())
			{
				System.Collections.DictionaryEntry entry = (System.Collections.DictionaryEntry)it
					.Current;
				result.Add(entry.Key);
				result.Add(entry.Value);
			}
			return result.Iterator();
		}

		public virtual object EmptyClone(object original, com.db4o.reflect.ReflectClass originalCollectionClass
			)
		{
			return new System.Collections.Hashtable(((System.Collections.IDictionary)original
				).Count);
		}

		public virtual void CopyState(object original, object destination, com.db4o.drs.inside.CounterpartFinder
			 counterpartFinder)
		{
			System.Collections.IDictionary originalMap = (System.Collections.IDictionary)original;
			System.Collections.IDictionary destinationMap = (System.Collections.IDictionary)destination;
			destinationMap.Clear();
			System.Collections.IEnumerator it = originalMap.GetEnumerator();
			while (it.MoveNext())
			{
				System.Collections.DictionaryEntry entry = (System.Collections.DictionaryEntry)it
					.Current;
				object keyClone = counterpartFinder.FindCounterpart(entry.Key);
				object valueClone = counterpartFinder.FindCounterpart(entry.Value);
				destinationMap.Add(keyClone, valueClone);
			}
		}

		public virtual object CloneWithCounterparts(object originalMap, com.db4o.reflect.ReflectClass
			 claxx, com.db4o.drs.inside.CounterpartFinder elementCloner)
		{
			System.Collections.IDictionary original = (System.Collections.IDictionary)originalMap;
			System.Collections.IDictionary result = (System.Collections.IDictionary)EmptyClone
				(original, claxx);
			CopyState(original, result, elementCloner);
			return result;
		}
	}
}
