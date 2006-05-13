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
			_reflectCollectionClass = reflector.ForClass(j4o.lang.Class.GetClassForType(typeof(
				System.Collections.ICollection)));
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
			System.Collections.ICollection original = (System.Collections.ICollection)originalCollection;
			if (original is System.Collections.IList)
			{
				return new System.Collections.ArrayList(original.Count);
			}
			return _reflector.ForClass(j4o.lang.Class.GetClassForObject(original)).NewInstance
				();
		}

		public virtual com.db4o.foundation.Iterator4 IteratorFor(object collection)
		{
			if (_mapHandler.CanHandle(_reflector.ForObject(collection)))
			{
				return _mapHandler.IteratorFor(collection);
			}
			System.Collections.ICollection subject = (System.Collections.ICollection)collection;
			com.db4o.foundation.Collection4 result = new com.db4o.foundation.Collection4();
			System.Collections.IEnumerator it = subject.GetEnumerator();
			while (it.MoveNext())
			{
				result.Add(it.Current);
			}
			return result.Iterator();
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

		public virtual object CloneWithCounterparts(object originalCollection, com.db4o.reflect.ReflectClass
			 claxx, com.db4o.inside.replication.CounterpartFinder counterpartFinder)
		{
			if (_mapHandler.CanHandle(claxx))
			{
				return _mapHandler.CloneWithCounterparts(originalCollection, claxx, counterpartFinder
					);
			}
			System.Collections.ICollection original = (System.Collections.ICollection)originalCollection;
			System.Collections.ICollection result = (System.Collections.ICollection)EmptyClone
				(originalCollection, claxx);
			CopyState(original, result, counterpartFinder);
			return result;
		}

		private void DoCopyState(object original, object destination, com.db4o.inside.replication.CounterpartFinder
			 counterpartFinder)
		{
			System.Collections.ICollection originalCollection = (System.Collections.ICollection
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
	}
}
