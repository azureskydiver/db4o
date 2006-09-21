namespace com.db4o.drs.inside
{
	public class ReplicationReflector
	{
		private static com.db4o.drs.inside.ReplicationReflector instance = new com.db4o.drs.inside.ReplicationReflector
			();

		private readonly com.db4o.reflect.Reflector _reflector;

		private readonly com.db4o.reflect.ReflectArray _arrayReflector;

		private ReplicationReflector()
		{
			com.db4o.ext.ExtObjectContainer tempOcToGetReflector = com.db4o.ext.ExtDb4o.OpenMemoryFile
				(new com.db4o.ext.MemoryFile()).Ext();
			_reflector = tempOcToGetReflector.Reflector();
			_arrayReflector = _reflector.Array();
			tempOcToGetReflector.Close();
		}

		public static com.db4o.drs.inside.ReplicationReflector GetInstance()
		{
			return instance;
		}

		public virtual object[] ArrayContents(object array)
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

		internal virtual com.db4o.reflect.ReflectClass ForObject(object obj)
		{
			return _reflector.ForObject(obj);
		}

		internal virtual com.db4o.reflect.ReflectClass GetComponentType(com.db4o.reflect.ReflectClass
			 claxx)
		{
			return _arrayReflector.GetComponentType(claxx);
		}

		internal virtual int[] ArrayDimensions(object obj)
		{
			return _arrayReflector.Dimensions(obj);
		}

		public virtual object NewArrayInstance(com.db4o.reflect.ReflectClass componentType
			, int[] dimensions)
		{
			return _arrayReflector.NewInstance(componentType, dimensions);
		}

		public virtual int ArrayShape(object[] a_flat, int a_flatElement, object a_shaped
			, int[] a_dimensions, int a_currentDimension)
		{
			return _arrayReflector.Shape(a_flat, a_flatElement, a_shaped, a_dimensions, a_currentDimension
				);
		}

		public virtual com.db4o.reflect.Reflector Reflector()
		{
			return _reflector;
		}
	}
}
