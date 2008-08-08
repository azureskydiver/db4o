using System;
using System.Windows.Forms;
using OMControlLibrary.Common;
using OME.Logging.Common;

namespace OMControlLibrary
{
    public partial class AboutOME : Form
    {
        public AboutOME(string db4oVersion)
        {
            InitializeComponent();
        	labeldb4o.Text = "db4o (" + db4oVersion + ")";
        }

        private void AboutOME_Load(object sender, EventArgs e)
        {
            try
            {
				labelCStatus.Text = Helper.STATUS_LOGGEDOUT;
				if (!Helper.m_cmdBarBtnLogin.Enabled)
                {
					labelCStatus.Text = Helper.CheckFeaturePermission("QueryBuilder") ? 
													Helper.STATUS_FULLFUNCTIONALITYMODE : 
													Helper.STATUS_REDUCEDMODELOGGEDIN;
                }
            }
            catch(Exception oEx) 
            {
                LoggingHelper.HandleException(oEx);               
            }
        }

    	private void buttonOk_Click(object sender, EventArgs e)
        {
            Close();
        }
    }
}