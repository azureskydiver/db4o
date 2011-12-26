using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Windows.Forms;
using OME.Logging.Common;
using OManager.BusinessLayer.Config;

namespace OMAddinDataTransferLayer.AssemblyInfo
{
	public class AssemblyInspector : MarshalByRefObject, IAssemblyInspector
	{
        private IDictionary<string  , Assembly> assemblies = new Dictionary<string , Assembly>();

		public bool LoadAssembly(ISearchPath searchPath )
		{
		  
            AppDomain.CurrentDomain.AssemblyResolve+=(CurrentDomain_AssemblyResolve); 
            try
			{

                foreach (string path in searchPath.Paths)
                {
                    if (!File.Exists(path))
                    {
                        MessageBox.Show(path + " does not exist. Please remove it using 'Add Assemblies' button.","Object Manager Enterprise",
						MessageBoxButtons.OK, MessageBoxIcon.Information);
                        return false;
                       
                    }
                    

                    string[] str = path.Split('\\');

                    string name = str[str.Length - 1];
                    name = name.Remove(name.LastIndexOf('.'));
                    bool check = AppDomain.CurrentDomain.GetAssemblies().Any(a => a.GetName().Name == name);

                    if (check == false)
                    {
                        byte[] assemblyBuffer = File.ReadAllBytes(path);
                        string pdbPath = path.Remove(path.LastIndexOf('\\'));
                        pdbPath = pdbPath + "\\"+ name + ".pdb";
                        Assembly assembly;
                        if (File.Exists(pdbPath))
                        {
                            byte[] symbolAssemblyBuffer = File.ReadAllBytes(pdbPath);
                            assembly = AppDomain.CurrentDomain.Load(assemblyBuffer, symbolAssemblyBuffer);
                        }
                        else
                        {
                            assembly = AppDomain.CurrentDomain.Load(assemblyBuffer);
                        }

                        assemblies[assembly.FullName] = assembly;
                    }
                }
			    return true;
			}
			catch (Exception exception)
			{
                LoggingHelper.ShowMessage(exception);
			    return false;
			}


		}

        Assembly CurrentDomain_AssemblyResolve(object sender, ResolveEventArgs args)
        {
            if (assemblies.ContainsKey(args.Name ))
            {
                var toBeReturned = assemblies[args.Name];
                return toBeReturned;
            }
            return null;
        }

	   

	    public override object InitializeLifetimeService() 
	{     
	
		return null; 
	} 


		
	}
}
