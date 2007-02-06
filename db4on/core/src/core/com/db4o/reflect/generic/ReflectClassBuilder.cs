namespace com.db4o.reflect.generic
{
	public interface ReflectClassBuilder
	{
		com.db4o.reflect.ReflectClass CreateClass(string name, com.db4o.reflect.ReflectClass
			 superClass, int fieldCount);

		com.db4o.reflect.ReflectField CreateField(com.db4o.reflect.ReflectClass parentType
			, string fieldName, com.db4o.reflect.ReflectClass fieldType, bool isVirtual, bool
			 isPrimitive, bool isArray, bool isNArray);

		void InitFields(com.db4o.reflect.ReflectClass clazz, com.db4o.reflect.ReflectField[]
			 fields);

		com.db4o.reflect.ReflectClass ArrayClass(com.db4o.reflect.ReflectClass clazz);

		com.db4o.reflect.ReflectField[] FieldArray(int length);
	}
}
