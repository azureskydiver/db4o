namespace com.db4o
{
	internal sealed class EventDispatcher
	{
		private static readonly string[] events = { "objectCanDelete", "objectOnDelete", 
			"objectOnActivate", "objectOnDeactivate", "objectOnNew", "objectOnUpdate", "objectCanActivate"
			, "objectCanDeactivate", "objectCanNew", "objectCanUpdate" };

		internal const int CAN_DELETE = 0;

		internal const int DELETE = 1;

		internal const int SERVER_COUNT = 2;

		internal const int ACTIVATE = 2;

		internal const int DEACTIVATE = 3;

		internal const int NEW = 4;

		internal const int UPDATE = 5;

		internal const int CAN_ACTIVATE = 6;

		internal const int CAN_DEACTIVATE = 7;

		internal const int CAN_NEW = 8;

		internal const int CAN_UPDATE = 9;

		internal const int COUNT = 10;

		private readonly com.db4o.reflect.ReflectMethod[] methods;

		private EventDispatcher(com.db4o.reflect.ReflectMethod[] methods)
		{
			this.methods = methods;
		}

		internal bool dispatch(com.db4o.YapStream stream, object obj, int eventID)
		{
			if (methods[eventID] != null)
			{
				object[] parameters = new object[] { stream };
				try
				{
					object res = methods[eventID].invoke(obj, parameters);
					if (res != null && res is bool)
					{
						return ((bool)res);
					}
				}
				catch (System.Exception t)
				{
				}
			}
			return true;
		}

		internal static com.db4o.EventDispatcher forClass(com.db4o.YapStream a_stream, com.db4o.reflect.ReflectClass
			 classReflector)
		{
			if (a_stream == null || classReflector == null)
			{
				return null;
			}
			com.db4o.EventDispatcher dispatcher = null;
			int count = 0;
			if (a_stream.i_config.callbacks())
			{
				count = COUNT;
			}
			else
			{
				if (a_stream.i_config.isServer())
				{
					count = SERVER_COUNT;
				}
			}
			if (count > 0)
			{
				com.db4o.reflect.ReflectClass[] parameterClasses = { a_stream.i_handlers.ICLASS_OBJECTCONTAINER
					 };
				com.db4o.reflect.ReflectMethod[] methods = new com.db4o.reflect.ReflectMethod[COUNT
					];
				for (int i = COUNT - 1; i >= 0; i--)
				{
					try
					{
						com.db4o.reflect.ReflectMethod method = classReflector.getMethod(events[i], parameterClasses
							);
						if (null == method)
						{
							method = classReflector.getMethod(toPascalCase(events[i]), parameterClasses);
						}
						methods[i] = method;
						if (dispatcher == null)
						{
							dispatcher = new com.db4o.EventDispatcher(methods);
						}
					}
					catch (System.Exception t)
					{
					}
				}
			}
			return dispatcher;
		}

		private static string toPascalCase(string name)
		{
			return j4o.lang.JavaSystem.substring(name, 0, 1).ToUpper() + j4o.lang.JavaSystem.substring
				(name, 1);
		}
	}
}
