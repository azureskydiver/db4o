using System;
using System.IO;  
using System.Collections; 
using System.Collections.Generic;
using System.ComponentModel;
using System.Configuration.Install;
using System.Windows.Forms;
using Microsoft.Win32; 
 
namespace OMAddin
{
    [RunInstaller(true)]
    public partial class OMInstaller : Installer
    {
      
        public OMInstaller()
        {
            InitializeComponent();
           
        }

        protected override void OnBeforeInstall(IDictionary savedState)
        {
            //base.OnBeforeInstall(savedState);
            //RegistryKey key = Registry.CurrentUser.OpenSubKey("HKEY_CLASSES_ROOT\\VisualStudio.DTE.8.0", true);
            //if (key == null)
            //{
            //    //MessageBox.Show("To install this product you require Visual Studio 2005.");
            //    InstallException ex = new InstallException("To install this product you require Visual Studio 2005.");
            //    throw ex;

            //}

        }

        //public override void Rollback(IDictionary savedState)
        //{
        //    base.Rollback(savedState);
        //    try
        //    {

        //        if (Directory.Exists(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects"))
        //        {
        //            string[] directory = Directory.GetDirectories(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects");
        //            foreach (string dir in directory)
        //            {
        //                string[] files = Directory.GetFiles(dir);
        //                foreach (string str in files)
        //                {
        //                    File.Delete(str);
        //                }
        //                Directory.Delete(dir);
        //            }
        //            string[] db4ofiles = Directory.GetFiles(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects");
        //            foreach (string file in db4ofiles)
        //            {
        //                File.Delete(file);
        //            }
        //            Directory.Delete(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects");
                    
        //        }

        //        if (Directory.Exists(Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments) + @"\Visual Studio 2005\Addins"))
        //        {
        //            string filePathforWindowsprfinAddinFolder = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments) + @"\Visual Studio 2005\Addins\windows.prf";
        //            if (File.Exists(filePathforWindowsprfinAddinFolder))

        //                File.Delete(filePathforWindowsprfinAddinFolder);
        //        }
        //    }
        //    catch (Exception oEx)
        //    {

        //    }
        //}

        protected override void OnAfterInstall(IDictionary savedState)
        {
            //Add steps to be done after the installation is over.
            base.OnAfterInstall(savedState);

            try
            {
                if (Directory.Exists(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects"))
                {
                    string[] directory = Directory.GetDirectories(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects");
                    foreach (string dir in directory)
                    {
                        string[] files = Directory.GetFiles(dir);
                        foreach (string str in files)
                        {
                            File.Delete(str);
                        }
                        Directory.Delete(dir);
                    }
                    string[] db4ofiles = Directory.GetFiles(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects");
                    foreach (string file in db4ofiles)
                    {
                        File.Delete(file);
                    }
                    Directory.Delete(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects");
                }
                if (!Directory.Exists(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects" + Path.DirectorySeparatorChar + "ObjectManagerEnterprise"))
                {

                    Directory.CreateDirectory(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects" + Path.DirectorySeparatorChar + "ObjectManagerEnterprise");
                }


                CopyWindowsPRFFile();
                InvokeReadMe();
            }

            catch (Exception oEx)
            {
                //if (oEx.Message.Contains("Error 1001"))
                //{
                //    MessageBox.Show("To install this product you require Visual Studio 2005.");
                //    base.Rollback(savedState);

                //}

            }
        }

        protected override void OnAfterUninstall(IDictionary savedState)
        {
            base.OnAfterUninstall(savedState);

            try
            {

                if (Directory.Exists(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects"))
                {
                    string[] directory = Directory.GetDirectories(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects");

                    foreach (string dir in directory)
                    {
                        string[] files = Directory.GetFiles(dir);
                        foreach (string str in files)
                        {
                            File.Delete(str);
                        }

                        Directory.Delete(dir);
                    }
                    string[] db4ofiles = Directory.GetFiles(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects");
                    foreach (string file in db4ofiles)
                    {
                        File.Delete(file);
                    }
                    Directory.Delete(Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects");
                }
            }
            catch (Exception oEx)
            {

            }
        }

        internal static void CopyWindowsPRFFile()
        {
            string filePathforWindowsprf = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar;
            filePathforWindowsprf = filePathforWindowsprf + @"Microsoft\VisualStudio\8.0\windows.prf";
            if (File.Exists(filePathforWindowsprf))
            {
                string filePathforWindowsprfinAddinFolder = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments) + @"\Visual Studio 2005\Addins\windows.prf";

                if (File.Exists(filePathforWindowsprfinAddinFolder))
                {
                    File.Copy(filePathforWindowsprfinAddinFolder, filePathforWindowsprf, true);

                    File.Delete(filePathforWindowsprfinAddinFolder);
                }
            }
        }

        internal void InvokeReadMe()
        {

            string filepath = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments);
            filepath = filepath + @"\Visual Studio 2005\Addins\ReadMe\ReadMe.htm";
            int Index = filepath.LastIndexOf('\\');
            string CheckFilePath = filepath.Remove(Index);

            if (Directory.Exists(CheckFilePath))
                System.Diagnostics.Process.Start(filepath);
        }
    }
}