namespace com.db4o.reflect.generic
{
	public class GenericClassBuilder : com.db4o.reflect.generic.ReflectClassBuilder
	{
		private com.db4o.reflect.generic.GenericReflector _reflector;

		private com.db4o.reflect.Reflector _delegate;

		public GenericClassBuilder(com.db4o.reflect.generic.GenericReflector reflector, com.db4o.reflect.Reflector
			 delegate_) : base()
		{
			_reflector = reflector;
			_delegate = delegate_;
		}

		public virtual com.db4o.reflect.ReflectClass CreateClass(string name, com.db4o.reflect.ReflectClass
			 superClass, int fieldCount)
		{
			com.db4o.reflect.ReflectClass nativeClass = _delegate.ForName(name);
			com.db4o.reflect.generic.GenericClass clazz = new com.db4o.reflect.generic.GenericClass
				(_reflector, nativeClass, name, (com.db4o.reflect.generic.GenericClass)superClass
				);
			clazz.SetDeclaredFieldCount(fieldCount);
			return clazz;
		}

		public virtual com.db4o.reflect.ReflectField CreateField(com.db4o.reflect.ReflectClass
			 parentType, string fieldName, com.db4o.reflect.ReflectClass fieldType, bool isVirtual
			, bool isPrimitive, bool isArray, bool isNArray)
		{
			if (isVirtual)
			{
				return new com.db4o.reflect.generic.GenericVirtualField(fieldName);
			}
			return new com.db4o.reflect.generic.GenericField(fieldName, fieldType, isPrimitive
				, isArray, isNArray);
		}

		public virtual void InitFields(com.db4o.reflect.ReflectClass clazz, com.db4o.reflect.ReflectField[]
			 fields)
		{
			((com.db4o.reflect.generic.GenericClass)clazz).InitFields((com.db4o.reflect.generic.GenericField[]
				)fields);
		}

		public virtual com.db4o.reflect.ReflectClass ArrayClass(com.db4o.reflect.ReflectClass
			 clazz)
		{
			return ((com.db4o.reflect.generic.GenericClass)clazz).ArrayClass();
		}

		public virtual com.db4o.reflect.ReflectField[] FieldArray(int length)
		{
			return new com.db4o.reflect.generic.GenericField[length];
		}
	}
}
