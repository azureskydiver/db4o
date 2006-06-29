namespace com.db4o.reflect
{
	/// <summary>representation for java.lang.reflect.Constructor.</summary>
	/// <remarks>
	/// representation for java.lang.reflect.Constructor.
	/// <br /><br />See the respective documentation in the JDK API.
	/// </remarks>
	/// <seealso cref="com.db4o.reflect.Reflector">com.db4o.reflect.Reflector</seealso>
	public interface ReflectConstructor
	{
		void SetAccessible();

		com.db4o.reflect.ReflectClass[] GetParameterTypes();

		object NewInstance(object[] parameters);
	}
}
