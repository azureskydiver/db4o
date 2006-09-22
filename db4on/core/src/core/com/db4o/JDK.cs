namespace com.db4o
{
	/// <exclude></exclude>
	public class JDK
	{
		internal virtual j4o.lang.Thread AddShutdownHook(j4o.lang.Runnable a_runnable)
		{
			return null;
		}

		internal virtual com.db4o.types.Db4oCollections Collections(com.db4o.YapStream a_stream
			)
		{
			return null;
		}

		internal virtual j4o.lang.Class ConstructorClass()
		{
			return null;
		}

		internal virtual object CreateReferenceQueue()
		{
			return null;
		}

		public virtual object CreateWeakReference(object obj)
		{
			return obj;
		}

		internal virtual object CreateYapRef(object a_queue, com.db4o.YapObject a_yapObject
			, object a_object)
		{
			return null;
		}

		internal virtual object Deserialize(byte[] bytes)
		{
			throw new com.db4o.ext.Db4oException(com.db4o.Messages.NOT_IMPLEMENTED);
		}

		public virtual com.db4o.Config4Class ExtendConfiguration(com.db4o.reflect.ReflectClass
			 clazz, com.db4o.config.Configuration config, com.db4o.Config4Class classConfig)
		{
			return classConfig;
		}

		internal virtual void ForEachCollectionElement(object a_object, com.db4o.foundation.Visitor4
			 a_visitor)
		{
		}

		internal virtual string Format(j4o.util.Date date, bool showTime)
		{
			return date.ToString();
		}

		internal virtual object GetContextClassLoader()
		{
			return null;
		}

		internal virtual object GetYapRefObject(object a_object)
		{
			return null;
		}

		internal virtual bool IsCollectionTranslator(com.db4o.Config4Class a_config)
		{
			return false;
		}

		public virtual bool IsConnected(j4o.net.Socket socket)
		{
			return socket != null;
		}

		public virtual int Ver()
		{
			return 1;
		}

		internal virtual void KillYapRef(object obj)
		{
		}

		internal virtual void LockFile(object file)
		{
			lock (this)
			{
			}
		}

		/// <summary>
		/// use for system classes only, since not ClassLoader
		/// or Reflector-aware
		/// </summary>
		internal virtual bool MethodIsAvailable(string className, string methodName, j4o.lang.Class[]
			 @params)
		{
			return false;
		}

		internal virtual void PollReferenceQueue(com.db4o.YapStream a_stream, object a_referenceQueue
			)
		{
		}

		public virtual void RegisterCollections(com.db4o.reflect.generic.GenericReflector
			 reflector)
		{
		}

		internal virtual void RemoveShutdownHook(j4o.lang.Thread a_thread)
		{
		}

		public virtual j4o.lang.reflect.Constructor SerializableConstructor(j4o.lang.Class
			 clazz)
		{
			return null;
		}

		internal virtual byte[] Serialize(object obj)
		{
			throw new com.db4o.ext.Db4oException(com.db4o.Messages.NOT_IMPLEMENTED);
		}

		internal virtual void SetAccessible(object a_accessible)
		{
		}

		internal virtual bool IsEnum(com.db4o.reflect.Reflector reflector, com.db4o.reflect.ReflectClass
			 clazz)
		{
			return false;
		}

		internal virtual void UnlockFile(object file)
		{
			lock (this)
			{
			}
		}

		public virtual object WeakReferenceTarget(object weakRef)
		{
			return weakRef;
		}

		public virtual com.db4o.reflect.Reflector CreateReflector(object classLoader)
		{
			return null;
		}
	}
}
