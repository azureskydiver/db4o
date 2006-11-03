namespace com.db4o
{
	/// <exclude></exclude>
	public class Unobfuscated
	{
		internal static object random;

		internal static bool CreateDb4oList(object a_stream)
		{
			((com.db4o.YapStream)a_stream).CheckClosed();
			return !((com.db4o.YapStream)a_stream).IsInstantiating();
		}

		public static byte[] GenerateSignature()
		{
			com.db4o.YapWriter writer = new com.db4o.YapWriter(null, 300);
			writer.WriteLong(j4o.lang.JavaSystem.CurrentTimeMillis());
			writer.WriteLong(RandomLong());
			writer.WriteLong(RandomLong() + 1);
			return writer.GetWrittenBytes();
		}

		internal static void LogErr(com.db4o.config.Configuration config, int code, string
			 msg, System.Exception t)
		{
			com.db4o.Messages.LogErr(config, code, msg, t);
		}

		internal static void PurgeUnsychronized(object a_stream, object a_object)
		{
			((com.db4o.YapStream)a_stream).Purge1(a_object);
		}

		public static long RandomLong()
		{
			return j4o.lang.JavaSystem.CurrentTimeMillis();
			if (random == null)
			{
				random = new j4o.util.Random();
			}
			return ((j4o.util.Random)random).NextLong();
		}

		internal static void ShutDownHookCallback(object a_stream)
		{
			((com.db4o.YapStream)a_stream).FailedToShutDown();
		}
	}
}
