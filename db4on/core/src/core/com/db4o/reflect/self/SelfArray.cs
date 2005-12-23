namespace com.db4o.reflect.self
{
	public class SelfArray : com.db4o.reflect.ReflectArray
	{
		private readonly com.db4o.reflect.Reflector _reflector;

		private readonly com.db4o.reflect.self.SelfReflectionRegistry _registry;

		internal SelfArray(com.db4o.reflect.Reflector reflector, com.db4o.reflect.self.SelfReflectionRegistry
			 registry)
		{
			_reflector = reflector;
			_registry = registry;
		}

		public virtual int[] dimensions(object arr)
		{
			return new int[] { getLength(arr) };
		}

		public virtual int flatten(object a_shaped, int[] a_dimensions, int a_currentDimension
			, object[] a_flat, int a_flatElement)
		{
			if (a_shaped is object[])
			{
				object[] shaped = (object[])a_shaped;
				j4o.lang.JavaSystem.arraycopy(shaped, 0, a_flat, 0, shaped.Length);
				return shaped.Length;
			}
			return _registry.flattenArray(a_shaped, a_flat);
		}

		public virtual object get(object onArray, int index)
		{
			if (onArray is object[])
			{
				return ((object[])onArray)[index];
			}
			return _registry.getArray(onArray, index);
		}

		public virtual com.db4o.reflect.ReflectClass getComponentType(com.db4o.reflect.ReflectClass
			 a_class)
		{
			return ((com.db4o.reflect.self.SelfClass)a_class).getComponentType();
		}

		public virtual int getLength(object array)
		{
			if (array is object[])
			{
				return ((object[])array).Length;
			}
			return _registry.arrayLength(array);
		}

		public virtual bool isNDimensional(com.db4o.reflect.ReflectClass a_class)
		{
			return false;
		}

		public virtual object newInstance(com.db4o.reflect.ReflectClass componentType, int
			 length)
		{
			return _registry.arrayFor(((com.db4o.reflect.self.SelfClass)componentType).getJavaClass
				(), length);
		}

		public virtual object newInstance(com.db4o.reflect.ReflectClass componentType, int[]
			 dimensions)
		{
			return newInstance(componentType, dimensions[0]);
		}

		public virtual void set(object onArray, int index, object element)
		{
			if (onArray is object[])
			{
				((object[])onArray)[index] = element;
				return;
			}
			_registry.setArray(onArray, index, element);
		}

		public virtual int shape(object[] a_flat, int a_flatElement, object a_shaped, int[]
			 a_dimensions, int a_currentDimension)
		{
			if (a_shaped is object[])
			{
				object[] shaped = (object[])a_shaped;
				j4o.lang.JavaSystem.arraycopy(a_flat, 0, shaped, 0, a_flat.Length);
				return a_flat.Length;
			}
			return _registry.shapeArray(a_flat, a_shaped);
		}
	}
}
