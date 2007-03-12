namespace com.db4o.@internal
{
	/// <exclude></exclude>
	public class Unobfuscated
	{
		internal static object random;

		public static bool CreateDb4oList(object a_stream)
		{
			((com.db4o.@internal.ObjectContainerBase)a_stream).CheckClosed();
			return !((com.db4o.@internal.ObjectContainerBase)a_stream).IsInstantiating();
		}

		public static byte[] GenerateSignature()
		{
			com.db4o.@internal.StatefulBuffer writer = new com.db4o.@internal.StatefulBuffer(
				null, 300);
			writer.WriteLong(j4o.lang.JavaSystem.CurrentTimeMillis());
			writer.WriteLong(RandomLong());
			writer.WriteLong(RandomLong() + 1);
			return writer.GetWrittenBytes();
		}

		internal static void PurgeUnsychronized(object a_stream, object a_object)
		{
			((com.db4o.@internal.ObjectContainerBase)a_stream).Purge1(a_object);
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
			((com.db4o.@internal.ObjectContainerBase)a_stream).FailedToShutDown();
		}
	}
}
