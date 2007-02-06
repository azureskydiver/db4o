namespace com.db4o.nativequery.optimization
{
	public class ReflectUtil
	{
		public static j4o.lang.reflect.Method MethodFor(j4o.lang.Class clazz, string methodName
			, j4o.lang.Class[] paramTypes)
		{
			j4o.lang.Class curclazz = clazz;
			while (curclazz != null)
			{
				try
				{
					j4o.lang.reflect.Method method = curclazz.GetDeclaredMethod(methodName, paramTypes
						);
					com.db4o.@internal.Platform4.SetAccessible(method);
					return method;
				}
				catch
				{
				}
				curclazz = curclazz.GetSuperclass();
			}
			return null;
		}

		public static j4o.lang.reflect.Field FieldFor(j4o.lang.Class clazz, string name)
		{
			j4o.lang.Class curclazz = clazz;
			while (curclazz != null)
			{
				try
				{
					j4o.lang.reflect.Field field = curclazz.GetDeclaredField(name);
					com.db4o.@internal.Platform4.SetAccessible(field);
					return field;
				}
				catch
				{
				}
				curclazz = curclazz.GetSuperclass();
			}
			return null;
		}
	}
}
