using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using EnvDTE;
using EnvDTE80;
using System.Runtime.InteropServices;

namespace OMControlLibrary
{
    [ComVisibleAttribute(true)]
    public partial class ViewBase : UserControl, IView
    {

        #region Member Variables

        private static DTE2 m_applicationObject;

        #endregion

        #region Properties

        public static DTE2 ApplicationObject
        {
            get { return m_applicationObject; }
            set { m_applicationObject = value; }
        }

        #endregion

        #region Constructor
        public ViewBase()
        {
            InitializeComponent();
            //ApplicationManager.CheckLocalAndSetLanguage();
        }
        #endregion


        #region Virtual Method

        /// <summary>
        /// Set Literals
        /// </summary>
        public virtual void SetLiterals()
        {

        }

        #endregion

    }
}
