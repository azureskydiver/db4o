namespace Db4oUnit
{
	using System.Reflection;

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

		public static MethodInfo[] GetAllMethods(System.Type type)
		{
			return type.GetMethods(
					BindingFlags.Public
					|BindingFlags.NonPublic
					|BindingFlags.Instance
					|BindingFlags.Static);
		}	

		public static bool IsStatic(MethodInfo method)
		{
			return method.IsStatic;
		}

		public static bool IsPublic(MethodInfo method)
		{
			return method.IsPublic;
		}

		public static bool HasParameters(MethodInfo method)
		{
			return method.GetParameters().Length > 0;
		}
	}
}
