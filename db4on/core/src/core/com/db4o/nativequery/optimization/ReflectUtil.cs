namespace com.db4o.nativequery.optimization
{
	public class ReflectUtil
	{
		public static j4o.lang.reflect.Method methodFor(j4o.lang.Class clazz, string methodName
			, j4o.lang.Class[] paramTypes)
		{
			j4o.lang.Class curclazz = clazz;
			while (curclazz != null)
			{
				try
				{
					j4o.lang.reflect.Method method = curclazz.getDeclaredMethod(methodName, paramTypes
						);
					com.db4o.Platform4.setAccessible(method);
					return method;
				}
				catch (System.Exception e)
				{
				}
				curclazz = curclazz.getSuperclass();
			}
			return null;
		}

		public static j4o.lang.reflect.Field fieldFor(j4o.lang.Class clazz, string name)
		{
			j4o.lang.Class curclazz = clazz;
			while (curclazz != null)
			{
				try
				{
					j4o.lang.reflect.Field field = curclazz.getDeclaredField(name);
					com.db4o.Platform4.setAccessible(field);
					return field;
				}
				catch (System.Exception e)
				{
				}
				curclazz = curclazz.getSuperclass();
			}
			return null;
		}
	}
}
