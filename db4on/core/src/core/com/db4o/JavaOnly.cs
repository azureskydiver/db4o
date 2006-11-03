namespace com.db4o
{
	internal class JavaOnly
	{
		internal static int CollectionUpdateDepth(j4o.lang.Class a_class)
		{
			return 0;
		}

		internal static bool IsCollection(j4o.lang.Class a_class)
		{
			return false;
		}

		internal static bool IsCollectionTranslator(com.db4o.Config4Class a_config)
		{
			return false;
		}

		public static com.db4o.JDK Jdk()
		{
			return new com.db4o.JDK();
		}

		public static void Link()
		{
		}

		public static void RunFinalizersOnExit()
		{
		}

		internal static readonly j4o.lang.Class[] SIMPLE_CLASSES = null;
	}
}
