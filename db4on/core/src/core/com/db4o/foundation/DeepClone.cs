
namespace com.db4o.foundation
{
	/// <summary>Deep clone</summary>
	/// <exclude></exclude>
	public interface DeepClone
	{
		/// <summary>
		/// The parameter allows passing one new object so parent
		/// references can be corrected on children.
		/// </summary>
		/// <remarks>
		/// The parameter allows passing one new object so parent
		/// references can be corrected on children.
		/// </remarks>
		object deepClone(object context);
	}
}
