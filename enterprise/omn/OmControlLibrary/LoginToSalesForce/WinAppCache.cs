using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using OMControlLibrary.Common;
using System.Web.Caching;
using EnvDTE;
using System.Reflection;
using EnvDTE80;

using OME.Logging.Exceptions;
using OME.Logging.ExceptionLogging;
using OME.Logging.Common;



namespace OMControlLibrary.LoginToSalesForce
{
    public partial class WinAppCache : Form
    {
        DTE2 ApplicationObject;
        const string CACHE_KEY = "LOGININFOCACHEKEY";
        CustomCookies customCookies = null;
        public static bool isPasswordEmpty = false;
        public static bool isUserNameEmpty = false;
        public WinAppCache(DTE2 AppObj)
        {
            ApplicationObject = AppObj;
            customCookies = new CustomCookies();

            InitializeComponent();
        }

        public WinAppCache()
        {
            customCookies = new CustomCookies();
            this.SetStyle(ControlStyles.CacheText |
               ControlStyles.AllPaintingInWmPaint |
               ControlStyles.UserPaint |
               ControlStyles.OptimizedDoubleBuffer |
               ControlStyles.Opaque, true);
            InitializeComponent();
        }
        private void buttonLogin_Click(object sender, EventArgs e)
        {
            try
            {
                if (string.IsNullOrEmpty(textBoxUserID.Text.Trim())
                           || string.IsNullOrEmpty(textBoxPassword.Text.Trim()))
                {
                    MessageBox.Show(Helper.GetResourceString(Common.Constants.VALIDATION_MSG_MANDATORY_FIELDS),
                        Helper.GetResourceString(Common.Constants.PRODUCT_CAPTION), MessageBoxButtons.OK, MessageBoxIcon.Warning);

                    if (string.IsNullOrEmpty(textBoxUserID.Text.Trim()))
                        textBoxUserID.Focus();
                    else if (string.IsNullOrEmpty(textBoxPassword.Text.Trim()))
                        textBoxPassword.Focus();

                    this.DialogResult = DialogResult.None;

                    return;
                }

                if (checkBoxRememberMe.Checked)
                {
                    string logininfo = textBoxUserID.Text + "~" + textBoxPassword.Text;
                    customCookies.SetCookies(logininfo);
                }
                else
                {
                    customCookies.SetCookies(string.Empty);
                }
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }

        private void OnLoginClick()
        {
            if (textBoxUserID.Text.Equals(string.Empty))
            {
                isUserNameEmpty = true;
                lblUserName.Visible = true;
                lblUserName.Text = "*";
                lblUserName.ForeColor = Color.Red;
                return;
            }
            else
            {
                isUserNameEmpty = false;
                lblUserName.Visible = false;

            }
            if (textBoxPassword.Text.Equals(string.Empty))
            {
                isPasswordEmpty = true;
                lblPassword.Visible = true;
                lblPassword.Text = "*";
                lblPassword.ForeColor = Color.Red;
                return;
            }
            else
            {
                lblPassword.Visible = false;
                isPasswordEmpty = false;
            }

            
        }



        private void buttonCancel_Click(object sender, EventArgs e)
        {
           
        }

        private void linkLabelForgotPassword_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            System.Diagnostics.Process.Start("https://www.db4o.com/users/retrievePassword.aspx");
            //ApplicationObject.DTE.ItemOperations.Navigate("http://www.db4o.com/users/retrievePassword.aspx", vsNavigateOptions.vsNavigateOptionsNewWindow);
            // this.Close();
        }

        private void linkLabelPurchase_LinkClicked(object sender, LinkLabelLinkClickedEventArgs e)
        {
            string filepath = Assembly.GetExecutingAssembly().CodeBase.Remove(0, 8); ;

            filepath = filepath.Remove(filepath.Length - 21, 21);
            filepath = filepath + @"/ContactSales/ContactSales.htm";
            System.Diagnostics.Process.Start(filepath);

        }

        private void textBoxUserID_TextChanged(object sender, EventArgs e)
        {
            try
            {
                if (!string.IsNullOrEmpty(textBoxUserID.Text.Trim()))
                    lblUserName.Visible = false;
                else
                    lblUserName.Visible = true;
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx); 
            }
        }

        private void textBoxPassword_TextChanged(object sender, EventArgs e)
        {
            try
            {
                if (!string.IsNullOrEmpty(textBoxPassword.Text.Trim()))
                    lblPassword.Visible = false;
                else
                    lblPassword.Visible = true;
            }
            catch (Exception oEx)
            {
                LoggingHelper.ShowMessage(oEx);
            }
        }
    }
}