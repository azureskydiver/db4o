namespace com.db4o.inside.replication
{
	public class MapHandler : com.db4o.inside.replication.CollectionHandler
	{
		private readonly com.db4o.reflect.ReflectClass _reflectMapClass;

		private readonly com.db4o.reflect.Reflector _reflector;

		public MapHandler(com.db4o.reflect.Reflector reflector)
		{
			_reflector = reflector;
//			_reflectMapClass = reflector.ForClass(j4o.lang.Class.GetClassForType(typeof(j4o.util.Map
//				)));
		}

		public virtual bool CanHandle(com.db4o.reflect.ReflectClass claxx)
		{
			return _reflectMapClass.IsAssignableFrom(claxx);
		}

		public virtual bool CanHandle(object obj)
		{
			return CanHandle(_reflector.ForObject(obj));
		}

		public virtual bool CanHandle(j4o.lang.Class c)
		{
			return CanHandle(_reflector.ForClass(c));
		}

		public virtual com.db4o.foundation.Iterator4 IteratorFor(object collection)
		{
//			j4o.util.Map map = (j4o.util.Map)collection;
//			com.db4o.foundation.Collection4 result = new com.db4o.foundation.Collection4();
//			j4o.util.Iterator it = map.EntrySet().Iterator();
//			while (it.HasNext())
//			{
//				j4o.util.Map.Entry entry = (j4o.util.Map.Entry)it.Next();
//				result.Add(entry.GetKey());
//				result.Add(entry.GetValue());
//			}
//			return result.Iterator();
			return null;
		}

		public virtual object EmptyClone(object original, com.db4o.reflect.ReflectClass originalCollectionClass
			)
		{
//			return new j4o.util.HashMap(((j4o.util.Map)original).Size());
			return null;
		}

		public virtual void CopyState(object original, object destination, com.db4o.inside.replication.CounterpartFinder
			 counterpartFinder)
		{
//			j4o.util.Map originalMap = (j4o.util.Map)original;
//			j4o.util.Map destinationMap = (j4o.util.Map)destination;
//			destinationMap.Clear();
//			j4o.util.Iterator it = originalMap.EntrySet().Iterator();
//			while (it.HasNext())
//			{
//				j4o.util.Map.Entry entry = (j4o.util.Map.Entry)it.Next();
//				object keyClone = counterpartFinder.FindCounterpart(entry.GetKey());
//				object valueClone = counterpartFinder.FindCounterpart(entry.GetValue());
//				destinationMap.Put(keyClone, valueClone);
//			}
		}
	}
}
