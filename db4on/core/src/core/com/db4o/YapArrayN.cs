namespace com.db4o
{
	/// <summary>n-dimensional array</summary>
	internal sealed class YapArrayN : com.db4o.YapArray
	{
		internal YapArrayN(com.db4o.YapStream stream, com.db4o.YapDataType a_handler, bool
			 a_isPrimitive) : base(stream, a_handler, a_isPrimitive)
		{
		}

		internal sealed override object[] allElements(object a_array)
		{
			int[] dim = _reflectArray.dimensions(a_array);
			object[] flat = new object[elementCount(dim)];
			_reflectArray.flatten(a_array, dim, 0, flat, 0);
			return flat;
		}

		internal sealed override int elementCount(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_bytes)
		{
			return elementCount(readDimensions(a_trans, a_bytes, new com.db4o.reflect.ReflectClass
				[1]));
		}

		private int elementCount(int[] a_dim)
		{
			int elements = a_dim[0];
			for (int i = 1; i < a_dim.Length; i++)
			{
				elements = elements * a_dim[i];
			}
			return elements;
		}

		internal sealed override byte identifier()
		{
			return com.db4o.YapConst.YAPARRAYN;
		}

		internal sealed override int objectLength(object a_object)
		{
			int[] dim = _reflectArray.dimensions(a_object);
			return com.db4o.YapConst.OBJECT_LENGTH + (com.db4o.YapConst.YAPINT_LENGTH * ((com.db4o.Debug
				.arrayTypes ? 2 : 1) + dim.Length)) + (elementCount(dim) * i_handler.linkLength(
				));
		}

		internal sealed override object read1(com.db4o.YapWriter a_bytes)
		{
			object[] ret = new object[1];
			int[] dim = read1Create(a_bytes.getTransaction(), a_bytes, ret);
			if (ret[0] != null)
			{
				object[] objects = new object[elementCount(dim)];
				for (int i = 0; i < objects.Length; i++)
				{
					objects[i] = i_handler.read(a_bytes);
				}
				_reflectArray.shape(objects, 0, ret[0], dim, 0);
			}
			return ret[0];
		}

		internal sealed override object read1Query(com.db4o.Transaction a_trans, com.db4o.YapReader
			 a_bytes)
		{
			object[] ret = new object[1];
			int[] dim = read1Create(a_trans, a_bytes, ret);
			if (ret[0] != null)
			{
				object[] objects = new object[elementCount(dim)];
				for (int i = 0; i < objects.Length; i++)
				{
					objects[i] = i_handler.readQuery(a_trans, a_bytes, true);
				}
				_reflectArray.shape(objects, 0, ret[0], dim, 0);
			}
			return ret[0];
		}

		private int[] read1Create(com.db4o.Transaction a_trans, com.db4o.YapReader a_bytes
			, object[] obj)
		{
			com.db4o.reflect.ReflectClass[] clazz = new com.db4o.reflect.ReflectClass[1];
			int[] dim = readDimensions(a_trans, a_bytes, clazz);
			if (i_isPrimitive)
			{
				obj[0] = a_trans.reflector().array().newInstance(i_handler.primitiveClassReflector
					(), dim);
			}
			else
			{
				if (clazz[0] != null)
				{
					obj[0] = a_trans.reflector().array().newInstance(clazz[0], dim);
				}
			}
			return dim;
		}

		private int[] readDimensions(com.db4o.Transaction a_trans, com.db4o.YapReader a_bytes
			, com.db4o.reflect.ReflectClass[] clazz)
		{
			int[] dim = new int[readElementsAndClass(a_trans, a_bytes, clazz)];
			for (int i = 0; i < dim.Length; i++)
			{
				dim[i] = a_bytes.readInt();
			}
			return dim;
		}

		internal sealed override void writeNew1(object a_object, com.db4o.YapWriter a_bytes
			)
		{
			int[] dim = _reflectArray.dimensions(a_object);
			writeClass(a_object, a_bytes);
			a_bytes.writeInt(dim.Length);
			for (int i = 0; i < dim.Length; i++)
			{
				a_bytes.writeInt(dim[i]);
			}
			object[] objects = allElements(a_object);
			for (int i = 0; i < objects.Length; i++)
			{
				i_handler.writeNew(element(objects, i), a_bytes);
			}
		}

		private object element(object a_array, int a_position)
		{
			try
			{
				return _reflectArray.get(a_array, a_position);
			}
			catch (System.Exception e)
			{
				return null;
			}
		}
	}
}
