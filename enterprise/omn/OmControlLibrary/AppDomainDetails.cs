using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Reflection;
using System.Text;
using System.Windows.Forms;
using System.Xml;
using OMAddinDataTransferLayer;
using OMAddinDataTransferLayer.AssemblyInfo;
using OMAddinDataTransferLayer.Connection;
using OMAddinDataTransferLayer.DataBaseDetails;
using OMAddinDataTransferLayer.DataEditing;

using OMAddinDataTransferLayer.DataPopulation;
using OMAddinDataTransferLayer.TypeMauplation;
using OMControlLibrary.Common;



namespace OMControlLibrary
{
	public class AppDomainDetails
	{
		public AppDomain workerAppDomain;
		byte[] assemblyBuffer;
        public bool LoadAppDomain(string path)
        {

            try
            {


                AppDomainSetup setup = new AppDomainSetup();
                setup.ApplicationBase = GetPath() + "\\";
#if DEBUG
                setup.ApplicationBase = @"E:\db4object\db4o\Trunk\omn\OMADDIN\bin\";

#else 
                setup.ShadowCopyDirectories = Path.GetTempPath();
#endif
                setup.ShadowCopyFiles = "true";


                workerAppDomain = AppDomain.CreateDomain("WorkerAppDomain", null, setup);
                AppDomain.CurrentDomain.AssemblyResolve += CurrentDomain_AssemblyResolve;

                if (path != string.Empty && path != (Helper.GetResourceString(Constants.COMBOBOX_DEFAULT_TEXT)))
                {
                    if (!File.Exists(path))
                    {
                        MessageBox.Show(path + Properties.Resources.AppDomainDetails_LoadAppDomain__does_not_exists,
                                        Helper.GetResourceString(Constants.PRODUCT_CAPTION), MessageBoxButtons.OK,
                                        MessageBoxIcon.Information);
                        return false;
                    }

                    assemblyBuffer = File.ReadAllBytes(path);
                    object anObject = CreateGlobalObjects("OMAddinDataTransferLayer",
                                                          "OMAddinDataTransferLayer.AssemblyInfo.AssemblyInspector");

                    IAssemblyInspector assemblyInspector = anObject as IAssemblyInspector;
                    assemblyInspector.LoadAssembly(assemblyBuffer);
                    AssemblyInspectorObject.AssemblyInspector = assemblyInspector;

                }

                object anObject1 = CreateGlobalObjects("OMAddinDataTransferLayer",
                                                       "OMAddinDataTransferLayer.Connection.Connection");
                IConnection conn = anObject1 as IConnection;
                AssemblyInspectorObject.Connection = conn;

                object anObject2 = CreateGlobalObjects("OMAddinDataTransferLayer",
                                                       "OMAddinDataTransferLayer.DataPopulation.PopulateData");
                IPopulateData populate = anObject2 as IPopulateData;
                AssemblyInspectorObject.DataPopulation = populate;

                object anObject3 = CreateGlobalObjects("OMAddinDataTransferLayer",
                                                       "OMAddinDataTransferLayer.TypeMauplation.DataType");
                IDataType dataType = anObject3 as IDataType;
                AssemblyInspectorObject.DataType = dataType;

                object anObject4 = CreateGlobalObjects("OMAddinDataTransferLayer",
                                                       "OMAddinDataTransferLayer.DataEditing.SaveData");
                ISaveData saveData = anObject4 as ISaveData;
                AssemblyInspectorObject.DataSave = saveData;

                object anObject5 = CreateGlobalObjects("OMAddinDataTransferLayer",
                                                       "OMAddinDataTransferLayer.DataBaseDetails.ObjectProperties");
                IObjectProperties objectProp = anObject5 as IObjectProperties;
                AssemblyInspectorObject.ObjectProperties = objectProp;

                object anObject6 = CreateGlobalObjects("OMAddinDataTransferLayer",
                                                       "OMAddinDataTransferLayer.DataBaseDetails.ClassProperties");
                IClassProperties classProp = anObject6 as IClassProperties;
                AssemblyInspectorObject.ClassProperties = classProp;
                AppDomain.CurrentDomain.AssemblyResolve -= CurrentDomain_AssemblyResolve;
            }
            catch (Exception)
            {
                return false;
            }
            return true;
        }

	    public string GetPath()
        {
            string path = Path.Combine(Environment.GetFolderPath(Environment.SpecialFolder.CommonApplicationData), @"Microsoft\MSEnvShared\Addins\OMAddin.addin");
            XmlDocument addinDoc = new XmlDocument();
            string nodePath = "/ns:Extensibility/ns:Addin/ns:Assembly";
            addinDoc.Load(path);
            XmlNode nodePath1 = addinDoc.SelectSingleNode(nodePath, NameSpaceManagerFor(addinDoc, ""));
            return Path.GetDirectoryName(nodePath1.FirstChild.Value);
        }

	    private static XmlNamespaceManager NameSpaceManagerFor(XmlDocument addinDoc, string prefix)
        {
            XmlNamespaceManager nsmgr = new XmlNamespaceManager(addinDoc.NameTable);
            nsmgr.AddNamespace("ns", addinDoc.DocumentElement.GetNamespaceOfPrefix(prefix));

            return nsmgr;
        }

		private object  CreateGlobalObjects(string assemblyName, string typeName)
		{
			return workerAppDomain.CreateInstanceAndUnwrap(assemblyName, typeName);
		}

		Assembly CurrentDomain_AssemblyResolve(object sender, ResolveEventArgs args)
		{
			return (typeof(AssemblyInspector).Assembly.FullName == args.Name) ? (typeof(AssemblyInspector).Assembly) : null;
		}
	}
}
