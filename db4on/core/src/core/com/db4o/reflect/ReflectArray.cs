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
		int[] Dimensions(object arr);

		int Flatten(object a_shaped, int[] a_dimensions, int a_currentDimension, object[]
			 a_flat, int a_flatElement);

		object Get(object onArray, int index);

		com.db4o.reflect.ReflectClass GetComponentType(com.db4o.reflect.ReflectClass a_class
			);

		int GetLength(object array);

		bool IsNDimensional(com.db4o.reflect.ReflectClass a_class);

		object NewInstance(com.db4o.reflect.ReflectClass componentType, int length);

		object NewInstance(com.db4o.reflect.ReflectClass componentType, int[] dimensions);

		void Set(object onArray, int index, object element);

		int Shape(object[] a_flat, int a_flatElement, object a_shaped, int[] a_dimensions
			, int a_currentDimension);
	}
}
