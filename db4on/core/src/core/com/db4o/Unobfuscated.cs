
namespace com.db4o
{
	/// <exclude></exclude>
	public class Unobfuscated
	{
		internal static object random;

		internal static bool createDb4oList(object a_stream)
		{
			((com.db4o.YapStream)a_stream).checkClosed();
			return !((com.db4o.YapStream)a_stream).isInstantiating();
		}

		public static byte[] generateSignature()
		{
			com.db4o.YapWriter writer = new com.db4o.YapWriter(null, 300);
			com.db4o.YLong.writeLong(j4o.lang.JavaSystem.currentTimeMillis(), writer);
			com.db4o.YLong.writeLong(randomLong(), writer);
			com.db4o.YLong.writeLong(randomLong() + 1, writer);
			return writer.getWrittenBytes();
		}

		internal static void logErr(com.db4o.config.Configuration config, int code, string
			 msg, System.Exception t)
		{
			com.db4o.Messages.logErr(config, code, msg, t);
		}

		internal static void purgeUnsychronized(object a_stream, object a_object)
		{
			((com.db4o.YapStream)a_stream).purge1(a_object);
		}

		public static long randomLong()
		{
			return j4o.lang.JavaSystem.currentTimeMillis();
		}

		internal static void shutDownHookCallback(object a_stream)
		{
			((com.db4o.YapStream)a_stream).failedToShutDown();
		}
	}
}
