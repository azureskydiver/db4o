using System;
using System.IO;
using System.Collections;
using System.ComponentModel;
using System.Configuration.Install;

namespace OMAddin
{
    [RunInstaller(true)]
    public partial class OMInstaller : Installer
    {
        public OMInstaller()
        {
            InitializeComponent();
        }
     
        protected override void OnAfterInstall(IDictionary savedState)
        {
        	base.OnAfterInstall(savedState);
        	DeleteApplicationDataFolder();

			try
			{
				string version = Context.Parameters["version"];

				CopyWindowsPRFFile(version);
				InvokeReadMe(version);
			}
			catch (Exception)
			{
			}
        }

    	protected override void OnAfterUninstall(IDictionary savedState)
        {
            base.OnAfterUninstall(savedState);
			DeleteApplicationDataFolder();
        }

        internal static void CopyWindowsPRFFile(string VSVersion)
        {
        	string currentVSConfigFile = Path.Combine(VSProfilePathFor(VSVersion), "windows.prf");
            if (File.Exists(currentVSConfigFile))
            {
                string addinWindowConfigFile = Path.Combine(VSUserHomeFor(VSVersion), @"Addins\windows.prf");
                if (File.Exists(addinWindowConfigFile))
                {
                    File.Copy(addinWindowConfigFile, currentVSConfigFile, true);
                    File.Delete(addinWindowConfigFile);
                }
            }
        }

    	internal static void InvokeReadMe(string VSVersion)
        {
			String readmeFilePath = Path.Combine(VSUserHomeFor(VSVersion), @"Addins\ReadMe\ReadMe.htm");

            if (File.Exists(readmeFilePath))
			{
                System.Diagnostics.Process.Start(readmeFilePath);
			}
        }

		private static void DeleteApplicationDataFolder()
		{
			try
			{
				string path = Folder.DB4OHome;
				Folder.Delete(path);
				if (!Directory.Exists(Folder.OMNHome))
				{
					Directory.CreateDirectory(Folder.OMNHome);
				}
			}
			catch (Exception oEx)
			{

			}
		}

		private static string VSUserHomeFor(string VSVersion)
    	{
			return Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments), @"Visual Studio " + VSVersion);
    	}

		private static string VSProfilePathFor(string version)
		{
			return Path.Combine(
						Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData),
						string.Format(@"Microsoft\VisualStudio\{0}", version == "2005" ? "8.0" : "9.0"));
		}
	}
}