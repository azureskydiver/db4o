using System;

namespace UGMan6000.Extensible
{
	class Application
	{
		public static void Run()
		{
			using (ApplicationController controller = new ApplicationController())
			{
				new ConsoleView(controller).Interact();
			}
		}
	}
}
