
namespace com.db4o.foundation
{
	/// <summary>A collection of cool static methods that should be part of the runtime environment but are not.
	/// 	</summary>
	/// <remarks>A collection of cool static methods that should be part of the runtime environment but are not.
	/// 	</remarks>
	/// <exclude></exclude>
	public class Cool
	{
		public static void sleepIgnoringInterruption(long millis)
		{
			try
			{
				j4o.lang.Thread.sleep(millis);
			}
			catch (System.Exception ignored)
			{
			}
		}
	}
}
