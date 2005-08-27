
namespace com.db4o.reflect
{
	/// <summary>representation for java.lang.reflect.Array.</summary>
	/// <remarks>
	/// representation for java.lang.reflect.Array.
	/// <br /><br />See the respective documentation in the JDK API.
	/// </remarks>
	/// <seealso cref="com.db4o.reflect.Reflector">com.db4o.reflect.Reflector</seealso>
	public interface ReflectArray
	{
		int[] dimensions(object arr);

		int flatten(object a_shaped, int[] a_dimensions, int a_currentDimension, object[]
			 a_flat, int a_flatElement);

		object get(object onArray, int index);

		com.db4o.reflect.ReflectClass getComponentType(com.db4o.reflect.ReflectClass a_class
			);

		int getLength(object array);

		bool isNDimensional(com.db4o.reflect.ReflectClass a_class);

		object newInstance(com.db4o.reflect.ReflectClass componentType, int length);

		object newInstance(com.db4o.reflect.ReflectClass componentType, int[] dimensions);

		void set(object onArray, int index, object element);

		int shape(object[] a_flat, int a_flatElement, object a_shaped, int[] a_dimensions
			, int a_currentDimension);
	}
}
