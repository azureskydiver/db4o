using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Configuration.Install;

namespace OMAddin
{
    [RunInstaller(true)]
    public partial class ObjectManagerInstaller : Installer
    {
        public ObjectManagerInstaller()
        {
            InitializeComponent();
        }
    }
}