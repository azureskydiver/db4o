namespace Db4objects.Drs.Inside.Traversal
{
	public class GenericTraverser : Db4objects.Drs.Inside.Traversal.ITraverser
	{
		protected readonly Db4objects.Db4o.Reflect.IReflector _reflector;

		private readonly Db4objects.Db4o.Reflect.IReflectArray _arrayReflector;

		protected readonly Db4objects.Drs.Inside.Traversal.ICollectionFlattener _collectionFlattener;

		protected readonly Db4objects.Db4o.Foundation.Queue4 _queue = new Db4objects.Db4o.Foundation.Queue4
			();

		public GenericTraverser(Db4objects.Db4o.Reflect.IReflector reflector, Db4objects.Drs.Inside.Traversal.ICollectionFlattener
			 collectionFlattener)
		{
			_reflector = reflector;
			_arrayReflector = _reflector.Array();
			_collectionFlattener = collectionFlattener;
		}

		public virtual void TraverseGraph(object @object, Db4objects.Drs.Inside.Traversal.IVisitor
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

		protected virtual void TraverseObject(object @object, Db4objects.Drs.Inside.Traversal.IVisitor
			 visitor)
		{
			if (!visitor.Visit(@object))
			{
				return;
			}
			Db4objects.Db4o.Reflect.IReflectClass claxx = _reflector.ForObject(@object);
			TraverseFields(@object, claxx);
		}

		protected virtual void TraverseFields(object @object, Db4objects.Db4o.Reflect.IReflectClass
			 claxx)
		{
			Db4objects.Db4o.Reflect.IReflectField[] fields;
			fields = claxx.GetDeclaredFields();
			for (int i = 0; i < fields.Length; i++)
			{
				Db4objects.Db4o.Reflect.IReflectField field = fields[i];
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
			Db4objects.Db4o.Reflect.IReflectClass superclass = claxx.GetSuperclass();
			if (superclass == null)
			{
				return;
			}
			TraverseFields(@object, superclass);
		}

		protected virtual void TraverseCollection(object collection)
		{
			System.Collections.IEnumerator elements = _collectionFlattener.IteratorFor(collection
				);
			while (elements.MoveNext())
			{
				object element = elements.Current;
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
			Db4objects.Db4o.Reflect.IReflectClass claxx = _reflector.ForObject(@object);
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

		protected virtual bool IsSecondClass(Db4objects.Db4o.Reflect.IReflectClass claxx)
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
