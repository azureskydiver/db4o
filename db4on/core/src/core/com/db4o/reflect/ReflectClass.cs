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
		com.db4o.reflect.ReflectClass GetComponentType();

		com.db4o.reflect.ReflectConstructor[] GetDeclaredConstructors();

		com.db4o.reflect.ReflectField[] GetDeclaredFields();

		com.db4o.reflect.ReflectField GetDeclaredField(string name);

		com.db4o.reflect.ReflectClass GetDelegate();

		com.db4o.reflect.ReflectMethod GetMethod(string methodName, com.db4o.reflect.ReflectClass[]
			 paramClasses);

		string GetName();

		com.db4o.reflect.ReflectClass GetSuperclass();

		bool IsAbstract();

		bool IsArray();

		bool IsAssignableFrom(com.db4o.reflect.ReflectClass type);

		bool IsCollection();

		bool IsInstance(object obj);

		bool IsInterface();

		bool IsPrimitive();

		bool IsSecondClass();

		object NewInstance();

		com.db4o.reflect.Reflector Reflector();

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
		bool SkipConstructor(bool flag);

		void UseConstructor(com.db4o.reflect.ReflectConstructor constructor, object[] @params
			);

		object[] ToArray(object obj);
	}
}
