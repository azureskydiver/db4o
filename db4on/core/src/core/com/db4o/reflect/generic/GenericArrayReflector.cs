namespace com.db4o.reflect.generic
{
	/// <exclude></exclude>
	public class GenericArrayReflector : com.db4o.reflect.ReflectArray
	{
		private readonly com.db4o.reflect.ReflectArray _delegate;

		public GenericArrayReflector(com.db4o.reflect.generic.GenericReflector reflector)
		{
			_delegate = reflector.GetDelegate().Array();
		}

		public virtual int[] Dimensions(object arr)
		{
			return _delegate.Dimensions(arr);
		}

		public virtual int Flatten(object a_shaped, int[] a_dimensions, int a_currentDimension
			, object[] a_flat, int a_flatElement)
		{
			return _delegate.Flatten(a_shaped, a_dimensions, a_currentDimension, a_flat, a_flatElement
				);
		}

		public virtual object Get(object onArray, int index)
		{
			if (onArray is com.db4o.reflect.generic.GenericArray)
			{
				return ((com.db4o.reflect.generic.GenericArray)onArray)._data[index];
			}
			return _delegate.Get(onArray, index);
		}

		public virtual com.db4o.reflect.ReflectClass GetComponentType(com.db4o.reflect.ReflectClass
			 claxx)
		{
			claxx = claxx.GetDelegate();
			if (claxx is com.db4o.reflect.generic.GenericClass)
			{
				return claxx;
			}
			return _delegate.GetComponentType(claxx);
		}

		public virtual int GetLength(object array)
		{
			if (array is com.db4o.reflect.generic.GenericArray)
			{
				return ((com.db4o.reflect.generic.GenericArray)array).GetLength();
			}
			return _delegate.GetLength(array);
		}

		public virtual bool IsNDimensional(com.db4o.reflect.ReflectClass a_class)
		{
			if (a_class is com.db4o.reflect.generic.GenericArrayClass)
			{
				return false;
			}
			return _delegate.IsNDimensional(a_class.GetDelegate());
		}

		public virtual object NewInstance(com.db4o.reflect.ReflectClass componentType, int
			 length)
		{
			componentType = componentType.GetDelegate();
			if (componentType is com.db4o.reflect.generic.GenericClass)
			{
				return new com.db4o.reflect.generic.GenericArray(((com.db4o.reflect.generic.GenericClass
					)componentType).ArrayClass(), length);
			}
			return _delegate.NewInstance(componentType, length);
		}

		public virtual object NewInstance(com.db4o.reflect.ReflectClass componentType, int[]
			 dimensions)
		{
			return _delegate.NewInstance(componentType.GetDelegate(), dimensions);
		}

		public virtual void Set(object onArray, int index, object element)
		{
			if (onArray is com.db4o.reflect.generic.GenericArray)
			{
				((com.db4o.reflect.generic.GenericArray)onArray)._data[index] = element;
				return;
			}
			_delegate.Set(onArray, index, element);
		}

		public virtual int Shape(object[] a_flat, int a_flatElement, object a_shaped, int[]
			 a_dimensions, int a_currentDimension)
		{
			return _delegate.Shape(a_flat, a_flatElement, a_shaped, a_dimensions, a_currentDimension
				);
		}
	}
}
