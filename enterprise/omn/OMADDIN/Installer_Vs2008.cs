using System;
using System.IO;
using System.Collections;
using System.ComponentModel;
using System.Configuration.Install;
using System.Windows.Forms;
using System.Xml;

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
				string yearVersion = Context.Parameters["version"];
				string addinPath = Context.Parameters["assemblypath"];

				UpdateAddinFile(yearVersion, addinPath);
				CopyWindowsPRFFile(yearVersion);
				InvokeReadMe(Path.GetDirectoryName(addinPath));
			}
			catch (Exception ex)
			{
				MessageBox.Show(ex.ToString());
			}
        }

    	private static void UpdateAddinFile(string yearVersion, string addinAssemblyPath)
    	{
			string addinFilePath = AddinFileFor(yearVersion);

			XmlDocument addinFile = new XmlDocument();
			addinFile.Load(addinFilePath);

    		XmlNamespaceManager nsmgr = NameSpaceManagerFor(addinFile, "");

    		UpdateNode(addinFile, nsmgr,  "/ns:Extensibility/ns:Addin/ns:Assembly", addinAssemblyPath);
			UpdateNode(addinFile, nsmgr, "/ns:Extensibility/ns:HostApplication/ns:Version", VSVersionNumberFor(yearVersion));

			addinFile.Save(addinFilePath);
    	}

    	private static XmlNamespaceManager NameSpaceManagerFor(XmlDocument addinFile, string prefix)
    	{
    		XmlNamespaceManager nsmgr = new XmlNamespaceManager(addinFile.NameTable);
    		nsmgr.AddNamespace("ns", addinFile.DocumentElement.GetNamespaceOfPrefix(prefix));
    		
			return nsmgr;
    	}

    	private static void UpdateNode(XmlDocument addinFile, XmlNamespaceManager nsmgr, string nodePath, string value)
    	{
			XmlNode node = addinFile.SelectSingleNode(nodePath, nsmgr);
    		node.FirstChild.Value = value;
    	}

    	protected override void OnAfterUninstall(IDictionary savedState)
        {
            base.OnAfterUninstall(savedState);
			DeleteApplicationDataFolder();
        }

        internal static void CopyWindowsPRFFile(string yearVersion)
        {
        	string currentVSConfigFile = Path.Combine(VSProfilePathFor(yearVersion), "windows.prf");
            if (File.Exists(currentVSConfigFile))
            {
                string addinWindowConfigFile = Path.Combine(VSUserHomeFor(yearVersion), @"Addins\windows.prf");
                if (File.Exists(addinWindowConfigFile))
                {
                    File.Copy(addinWindowConfigFile, currentVSConfigFile, true);
                    File.Delete(addinWindowConfigFile);
                }
            }
        }

    	internal static void InvokeReadMe(string basePath)
        {
			String readmeFilePath = Path.Combine(basePath, @"ReadMe\ReadMe.htm");

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

		private static string VSUserHomeFor(string yearVersion)
    	{
			return Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments), @"Visual Studio " + yearVersion);
    	}

		private static string VSProfilePathFor(string version)
		{
			return Path.Combine(
						Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData),
						string.Format(@"Microsoft\VisualStudio\{0}", VSVersionNumberFor(version)));
		}

		private static string VSVersionNumberFor(string yearVersion)
		{
			if (yearVersion == "2005") return "8.0";
			if (yearVersion == "2008") return "9.0";

			throw new ArgumentException("Unsuported Visual Studio version: " + yearVersion, "yearVersion");
		}

		private static string AddinFileFor(string yearVersion)
		{
			return Path.Combine(VSUserHomeFor(yearVersion), @"Addins\OMAddin.addin");
		}
	}
}