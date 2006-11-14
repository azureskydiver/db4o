using System;
using System.Reflection;
using com.db4o.reflect;
using com.db4o.reflect.generic;

namespace com.db4o.db4ounit
{
	class Db4oUnitPlatform
	{
		public static ReflectClass GetReflectClass(Reflector reflector, Type clazz)
		{
			return reflector.ForClass(j4o.lang.Class.GetClassForType(clazz));
		}

	    public static bool IsStoreableField(FieldInfo a_field)
	    {
	        if (a_field.IsStatic) return false;
            if (j4o.lang.reflect.Field.IsTransient(a_field)) return false;
	        if (a_field.Name.IndexOf("$") != -1) return false;
	        return true;
	    }
	}
}
