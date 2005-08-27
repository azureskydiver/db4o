
namespace com.db4o
{
	/// <summary>
	/// TODO: Do we need this class? Possibly it's initialized by reflection
	/// during a license check to bypass hacks.
	/// </summary>
	/// <remarks>
	/// TODO: Do we need this class? Possibly it's initialized by reflection
	/// during a license check to bypass hacks.
	/// </remarks>
	internal class UserException : j4o.lang.RuntimeException
	{
		internal readonly int errCode;

		internal readonly string errMsg;

		internal UserException(int a_code, string a_msg, int a)
		{
			errCode = a_code;
			errMsg = a_msg;
		}
	}
}
