namespace com.db4o.reflect.generic
{
	/// <exclude></exclude>
	public class GenericField : com.db4o.reflect.ReflectField, com.db4o.foundation.DeepClone
	{
		private readonly string _name;

		private readonly com.db4o.reflect.generic.GenericClass _type;

		private readonly bool _primitive;

		private readonly bool _array;

		private readonly bool _nDimensionalArray;

		private int _index = -1;

		public GenericField(string name, com.db4o.reflect.ReflectClass clazz, bool primitive
			, bool array, bool nDimensionalArray)
		{
			_name = name;
			_type = (com.db4o.reflect.generic.GenericClass)clazz;
			_primitive = primitive;
			_array = array;
			_nDimensionalArray = nDimensionalArray;
		}

		public virtual object DeepClone(object obj)
		{
			com.db4o.reflect.Reflector reflector = (com.db4o.reflect.Reflector)obj;
			com.db4o.reflect.ReflectClass newReflectClass = null;
			if (_type != null)
			{
				newReflectClass = reflector.ForName(_type.GetName());
			}
			return new com.db4o.reflect.generic.GenericField(_name, newReflectClass, _primitive
				, _array, _nDimensionalArray);
		}

		public virtual object Get(object onObject)
		{
			return ((com.db4o.reflect.generic.GenericObject)onObject).Get(_index);
		}

		public virtual string GetName()
		{
			return _name;
		}

		public virtual com.db4o.reflect.ReflectClass GetFieldType()
		{
			if (_array)
			{
				return _type.ArrayClass();
			}
			return _type;
		}

		public virtual bool IsPublic()
		{
			return true;
		}

		public virtual bool IsPrimitive()
		{
			return _primitive;
		}

		public virtual bool IsStatic()
		{
			return false;
		}

		public virtual bool IsTransient()
		{
			return false;
		}

		public virtual void Set(object onObject, object value)
		{
			((com.db4o.reflect.generic.GenericObject)onObject).Set(_index, value);
		}

		public virtual void SetAccessible()
		{
		}

		internal virtual void SetIndex(int index)
		{
			_index = index;
		}

		public virtual object IndexEntry(object orig)
		{
			return orig;
		}

		public virtual com.db4o.reflect.ReflectClass IndexType()
		{
			return GetFieldType();
		}
	}
}
