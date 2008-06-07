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
            filePathforWindowsprf = filePathforWindowsprf + @"Microsoft\VisualStudio\9.0\windows.prf";
            if (File.Exists(filePathforWindowsprf))
            {
                string filePathforWindowsprfinAddinFolder = Environment.GetFolderPath(Environment.SpecialFolder.MyDocuments) + @"\Visual Studio 2008\Addins\windows.prf";

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
            filepath = filepath + @"\Visual Studio 2008\Addins\ReadMe_vs2008\ReadMe_vs2008.htm";
            int Index = filepath.LastIndexOf('\\');
            string CheckFilePath = filepath.Remove(Index);

            if (Directory.Exists(CheckFilePath))
                System.Diagnostics.Process.Start(filepath);
        }
    }
}