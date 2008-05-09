namespace Contacts
{
	public static class Console
	{
		public static string Prompt(string message)
		{
			System.Console.WriteLine(message);
			return System.Console.ReadLine();
		}
	}
}