namespace com.db4o.reflect
{
	/// <summary>representation for java.lang.Class.</summary>
	/// <remarks>
	/// representation for java.lang.Class.
	/// <br /><br />See the respective documentation in the JDK API.
	/// </remarks>
	/// <seealso cref="com.db4o.reflect.Reflector">com.db4o.reflect.Reflector</seealso>
	public interface ReflectClass
	{
		com.db4o.reflect.ReflectClass getComponentType();

		com.db4o.reflect.ReflectConstructor[] getDeclaredConstructors();

		com.db4o.reflect.ReflectField[] getDeclaredFields();

		com.db4o.reflect.ReflectField getDeclaredField(string name);

		com.db4o.reflect.ReflectClass getDelegate();

		com.db4o.reflect.ReflectMethod getMethod(string methodName, com.db4o.reflect.ReflectClass[]
			 paramClasses);

		string getName();

		com.db4o.reflect.ReflectClass getSuperclass();

		bool isAbstract();

		bool isArray();

		bool isAssignableFrom(com.db4o.reflect.ReflectClass type);

		bool isCollection();

		bool isInstance(object obj);

		bool isInterface();

		bool isPrimitive();

		bool isSecondClass();

		object newInstance();

		com.db4o.reflect.Reflector reflector();

		/// <summary>
		/// instructs to install or uninstall a special constructor for the
		/// respective platform that avoids calling the constructor for the
		/// respective class
		/// </summary>
		/// <param name="flag">
		/// true to try to install a special constructor, false if
		/// such a constructor is to be removed if present
		/// </param>
		/// <returns>true if the special constructor is in place after the call</returns>
		bool skipConstructor(bool flag);

		object[] toArray(object obj);

		void useConstructor(com.db4o.reflect.ReflectConstructor constructor, object[] _params
			);
	}
}
