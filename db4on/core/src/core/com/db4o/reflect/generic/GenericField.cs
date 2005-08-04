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

		public virtual object deepClone(object obj)
		{
			com.db4o.reflect.Reflector reflector = (com.db4o.reflect.Reflector)obj;
			com.db4o.reflect.ReflectClass newReflectClass = null;
			if (_type != null)
			{
				newReflectClass = reflector.forName(_type.getName());
			}
			return new com.db4o.reflect.generic.GenericField(_name, newReflectClass, _primitive
				, _array, _nDimensionalArray);
		}

		public virtual object get(object onObject)
		{
			return ((com.db4o.reflect.generic.GenericObject)onObject).get(_index);
		}

		public virtual string getName()
		{
			return _name;
		}

		public virtual com.db4o.reflect.ReflectClass getType()
		{
			if (_array)
			{
				return _type.arrayClass();
			}
			return _type;
		}

		public virtual bool isPublic()
		{
			return true;
		}

		public virtual bool isPrimitive()
		{
			return _primitive;
		}

		public virtual bool isStatic()
		{
			return false;
		}

		public virtual bool isTransient()
		{
			return false;
		}

		public virtual void set(object onObject, object value)
		{
			((com.db4o.reflect.generic.GenericObject)onObject).set(_index, value);
		}

		public virtual void setAccessible()
		{
		}

		internal virtual void setIndex(int index)
		{
			_index = index;
		}
	}
}
