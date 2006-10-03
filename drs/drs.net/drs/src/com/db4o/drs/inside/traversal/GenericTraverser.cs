namespace com.db4o.drs.inside.traversal
{
	public class GenericTraverser : com.db4o.drs.inside.traversal.Traverser
	{
		protected readonly com.db4o.reflect.Reflector _reflector;

		private readonly com.db4o.reflect.ReflectArray _arrayReflector;

		protected readonly com.db4o.drs.inside.traversal.CollectionFlattener _collectionFlattener;

		protected readonly com.db4o.foundation.Queue4 _queue = new com.db4o.foundation.Queue4
			();

		public GenericTraverser(com.db4o.reflect.Reflector reflector, com.db4o.drs.inside.traversal.CollectionFlattener
			 collectionFlattener)
		{
			_reflector = reflector;
			_arrayReflector = _reflector.Array();
			_collectionFlattener = collectionFlattener;
		}

		public virtual void TraverseGraph(object @object, com.db4o.drs.inside.traversal.Visitor
			 visitor)
		{
			QueueUpForTraversing(@object);
			while (true)
			{
				object next = _queue.Next();
				if (next == null)
				{
					return;
				}
				TraverseObject(next, visitor);
			}
		}

		protected virtual void TraverseObject(object @object, com.db4o.drs.inside.traversal.Visitor
			 visitor)
		{
			if (!visitor.Visit(@object))
			{
				return;
			}
			com.db4o.reflect.ReflectClass claxx = _reflector.ForObject(@object);
			TraverseFields(@object, claxx);
		}

		protected virtual void TraverseFields(object @object, com.db4o.reflect.ReflectClass
			 claxx)
		{
			com.db4o.reflect.ReflectField[] fields;
			fields = claxx.GetDeclaredFields();
			for (int i = 0; i < fields.Length; i++)
			{
				com.db4o.reflect.ReflectField field = fields[i];
				if (field.IsStatic())
				{
					continue;
				}
				if (field.IsTransient())
				{
					continue;
				}
				field.SetAccessible();
				object value = field.Get(@object);
				QueueUpForTraversing(value);
			}
			com.db4o.reflect.ReflectClass superclass = claxx.GetSuperclass();
			if (superclass == null)
			{
				return;
			}
			TraverseFields(@object, superclass);
		}

		protected virtual void TraverseCollection(object collection)
		{
			com.db4o.foundation.Iterator4 elements = _collectionFlattener.IteratorFor(collection
				);
			while (elements.MoveNext())
			{
				object element = elements.Current();
				if (element == null)
				{
					continue;
				}
				QueueUpForTraversing(element);
			}
		}

		protected virtual void TraverseArray(object array)
		{
			object[] contents = Contents(array);
			for (int i = 0; i < contents.Length; i++)
			{
				QueueUpForTraversing(contents[i]);
			}
		}

		protected virtual void QueueUpForTraversing(object @object)
		{
			if (@object == null)
			{
				return;
			}
			com.db4o.reflect.ReflectClass claxx = _reflector.ForObject(@object);
			if (IsSecondClass(claxx))
			{
				return;
			}
			if (_collectionFlattener.CanHandle(claxx))
			{
				TraverseCollection(@object);
				return;
			}
			if (claxx.IsArray())
			{
				TraverseArray(@object);
				return;
			}
			QueueAdd(@object);
		}

		protected virtual void QueueAdd(object @object)
		{
			_queue.Add(@object);
		}

		protected virtual bool IsSecondClass(com.db4o.reflect.ReflectClass claxx)
		{
			if (claxx.IsSecondClass())
			{
				return true;
			}
			return claxx.IsArray() && claxx.GetComponentType().IsSecondClass();
		}

		internal object[] Contents(object array)
		{
			int[] dim = _arrayReflector.Dimensions(array);
			object[] result = new object[Volume(dim)];
			_arrayReflector.Flatten(array, dim, 0, result, 0);
			return result;
		}

		private int Volume(int[] dim)
		{
			int result = dim[0];
			for (int i = 1; i < dim.Length; i++)
			{
				result = result * dim[i];
			}
			return result;
		}

		public virtual void ExtendTraversalTo(object disconnected)
		{
			QueueUpForTraversing(disconnected);
		}
	}
}
