using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using OMControlLibrary.Common;
using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OMControlLibrary
{
    public partial class AboutOME : Form
    {
        public AboutOME()
        {
            InitializeComponent();
            
        }

        private void AboutOME_Load(object sender, EventArgs e)
        {
            try
            {
                if (!Helper.m_cmdBarBtnLogin.Enabled)
                {
                    if (Helper.CheckFeaturePermission("QueryBuilder"))
                        labelCStatus.Text = Helper.STATUS_FULLFUNCTIONALITYMODE;
                    else
                        labelCStatus.Text = Helper.STATUS_REDUCEDMODELOGGEDIN;
                }
                else //logged out so no functionality
                    labelCStatus.Text = Helper.STATUS_LOGGEDOUT;
               

                //if (Helper.CheckFeaturePermission("QueryBuilder"))
                //    labelCStatus.Text = Helper.GetResourceString(Common.Constants.TOOLBAR_MODE_FULL);
                //else
                //    labelCStatus.Text = Helper.GetResourceString(Common.Constants.TOOLBAR_MODE_REDUCED);
            }
            catch(Exception oEx) 
            {
                LoggingHelper.HandleException(oEx);               
            }
        }

        private void buttonOk_Click(object sender, EventArgs e)
        {
            this.Close();
        }

        private void labelOME_Click(object sender, EventArgs e)
        {

        }

    }
}