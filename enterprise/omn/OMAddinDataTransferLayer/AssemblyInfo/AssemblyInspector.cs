using System;
using System.Reflection;

namespace OMAddinDataTransferLayer.AssemblyInfo
{
	public class AssemblyInspector : MarshalByRefObject, IAssemblyInspector
	{
		

		public void LoadAssembly(byte[] assemblyBuffer)
		{

			try
			{
				Assembly assembly = AppDomain.CurrentDomain.Load(assemblyBuffer);
				AppDomain.CurrentDomain.AssemblyResolve += delegate { return assembly; };

			}
			catch (Exception exception)
			{
				Console.WriteLine("Unexpected exception occurred:\r\n{0}\r\n{1}", exception.Message, exception.StackTrace);
			}


		}
	


	public override object InitializeLifetimeService() 
	{     
	
		return null; 
	} 


		
	}
}
