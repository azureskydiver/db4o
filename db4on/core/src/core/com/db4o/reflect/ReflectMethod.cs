namespace com.db4o.reflect
{
	/// <summary>representation for java.lang.reflect.Method.</summary>
	/// <remarks>
	/// representation for java.lang.reflect.Method.
	/// <br /><br />See the respective documentation in the JDK API.
	/// </remarks>
	/// <seealso cref="com.db4o.reflect.Reflector">com.db4o.reflect.Reflector</seealso>
	public interface ReflectMethod
	{
		object invoke(object onObject, object[] parameters);

		com.db4o.reflect.ReflectClass getReturnType();
	}
}
