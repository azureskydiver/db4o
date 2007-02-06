using System;
using System.Reflection;
using com.db4o.foundation;
using com.db4o.reflect;
using j4o.lang;

namespace Db4oUnit.Extensions
{
	public class Db4oUnitPlatform
	{
		public static ReflectClass GetReflectClass(Reflector reflector, Type clazz)
		{
			return reflector.ForClass(GetClassForType(clazz));
		}

	    public static bool ArrayContainsInstanceOf(object[] array, System.Type type)
        {
            return Arrays4.ContainsInstanceOf(array, GetClassForType(type));
        }

	    public static bool IsStoreableField(FieldInfo a_field)
	    {
	        if (a_field.IsStatic) return false;
            if (j4o.lang.reflect.Field.IsTransient(a_field)) return false;
	        if (a_field.Name.IndexOf("$") != -1) return false;
	        return true;
	    }

        private static Class GetClassForType(Type clazz)
        {
            return j4o.lang.Class.GetClassForType(clazz);
        }
	}
}
