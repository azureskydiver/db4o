namespace Db4oUnit
{
	public class TestPlatform
	{
		public static void PrintStackTrace(System.IO.TextWriter writer, System.Exception 
			t)
		{
			writer.Write(t);
		}

		public static System.IO.TextWriter GetStdOut()
		{
			return System.Console.Out;
		}

		public static bool IsTestMethod(System.Reflection.MethodInfo method)
		{
			return method.Name.StartsWith("Test")
				&& method.IsPublic 
				&& !method.IsStatic
				&& method.GetParameters().Length == 0;
		}
	}
}
