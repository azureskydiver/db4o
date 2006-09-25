using System;
using System.Reflection;
using com.db4o.reflect;
using com.db4o.reflect.generic;

namespace com.db4o.db4ounit
{
	class Db4oUnitPlatform
	{
		public static ReflectClass GetReflectClass(GenericReflector reflector, Type clazz)
		{
			return reflector.ForClass(j4o.lang.Class.GetClassForType(clazz));
		}

		public static FieldInfo[] GetDeclaredFields(Type clazz)
		{
			return clazz.GetFields(BindingFlags.Instance|BindingFlags.Public|BindingFlags.NonPublic|BindingFlags.DeclaredOnly);
		}
	}
}
