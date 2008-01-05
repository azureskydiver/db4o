using System;

namespace UGMan6000.Transient
{
	class Application
	{
		public static void Run()
		{
			new ConsoleView(new ApplicationController()).Interact();
		}
	}
}
