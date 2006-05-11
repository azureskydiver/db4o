namespace com.db4o.inside.replication
{
	public class CollectionHandlerImpl : com.db4o.inside.replication.CollectionHandler
	{
		private readonly com.db4o.inside.replication.CollectionHandler _mapHandler;

		private readonly com.db4o.reflect.ReflectClass _reflectCollectionClass;

		private readonly com.db4o.reflect.Reflector _reflector;

		public CollectionHandlerImpl() : this(com.db4o.inside.replication.ReplicationReflector
			.GetInstance().Reflector())
		{
		}

		public CollectionHandlerImpl(com.db4o.reflect.Reflector reflector)
		{
			_mapHandler = new com.db4o.inside.replication.MapHandler(reflector);
			_reflector = reflector;
//			_reflectCollectionClass = reflector.ForClass(j4o.lang.Class.GetClassForType(typeof(
//				j4o.util.Collection)));
		}

		public virtual bool CanHandle(com.db4o.reflect.ReflectClass claxx)
		{
			if (_mapHandler.CanHandle(claxx))
			{
				return true;
			}
			return _reflectCollectionClass.IsAssignableFrom(claxx);
		}

		public virtual bool CanHandle(object obj)
		{
			return CanHandle(_reflector.ForObject(obj));
		}

		public virtual bool CanHandle(j4o.lang.Class c)
		{
			return CanHandle(_reflector.ForClass(c));
		}

		public virtual object EmptyClone(object originalCollection, com.db4o.reflect.ReflectClass
			 originalCollectionClass)
		{
			if (_mapHandler.CanHandle(originalCollectionClass))
			{
				return _mapHandler.EmptyClone(originalCollection, originalCollectionClass);
			}
//			j4o.util.Collection original = (j4o.util.Collection)originalCollection;
//			if (original is j4o.util.List)
//			{
//				return new j4o.util.ArrayList(original.Size());
//			}
//			if (original is j4o.util.Set)
//			{
//				return new j4o.util.HashSet(original.Size());
//			}
//			return _reflector.ForClass(j4o.lang.Class.GetClassForObject(original)).NewInstance
//				();
			return null;
		}

		public virtual com.db4o.foundation.Iterator4 IteratorFor(object collection)
		{
			if (_mapHandler.CanHandle(_reflector.ForObject(collection)))
			{
				return _mapHandler.IteratorFor(collection);
			}
//			j4o.util.Collection subject = (j4o.util.Collection)collection;
//			com.db4o.foundation.Collection4 result = new com.db4o.foundation.Collection4();
//			j4o.util.Iterator it = subject.Iterator();
//			while (it.HasNext())
//			{
//				result.Add(it.Next());
//			}
//			return result.Iterator();
			return null;
		}

		public virtual void CopyState(object original, object destination, com.db4o.inside.replication.CounterpartFinder
			 counterpartFinder)
		{
			if (_mapHandler.CanHandle(original))
			{
				_mapHandler.CopyState(original, destination, counterpartFinder);
			}
			else
			{
				DoCopyState(original, destination, counterpartFinder);
			}
		}

		private void DoCopyState(object original, object destination, com.db4o.inside.replication.CounterpartFinder
			 counterpartFinder)
		{
//			j4o.util.Collection originalCollection = (j4o.util.Collection)original;
//			j4o.util.Collection destinationCollection = (j4o.util.Collection)destination;
//			destinationCollection.Clear();
//			j4o.util.Iterator it = originalCollection.Iterator();
//			while (it.HasNext())
//			{
//				object element = it.Next();
//				object counterpart = counterpartFinder.FindCounterpart(element);
//				destinationCollection.Add(counterpart);
//			}
		}
	}
}
