using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using OManager.BusinessLayer.QueryManager;
using OManager.BusinessLayer.Login;
using System.Net;
using OMControlLibrary.Common;
using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OMControlLibrary.LoginToSalesForce
{
	public partial class ProxyLogin : Form
	{
		private const string CONST_BACKSLASH = "\\";
		public ProxyLogin()
		{
			InitializeComponent();
			try
			{
				dbInteraction dbInt = new dbInteraction();
				ProxyAuthentication proxy = dbInt.RetrieveProxyInfo();
				if (proxy != null)
				{
					this.textBoxUserID.Text = proxy.UserName;
					this.textBoxPassword.Focus();
					this.textBoxPort.Text = proxy.Port;
					this.textBoxProxy.Text = proxy.ProxyAddress;
					this.textBoxPassword.Text = Helper.DecryptPass(proxy.PassWord);
				}
				else
				{
					string domain = Environment.UserDomainName;
					string username = Environment.UserName;
					this.textBoxUserID.Text = domain + CONST_BACKSLASH + username;
					if (((WebProxy)GlobalProxySelection.Select).Address != null)
					{
						int colonIndex = ((WebProxy)GlobalProxySelection.Select).Address.ToString().LastIndexOf(':');
						string proxystr = ((WebProxy)GlobalProxySelection.Select).Address.ToString().Substring(0, colonIndex);
						string port = ((WebProxy)GlobalProxySelection.Select).Address.ToString().Substring(colonIndex + 1, ((WebProxy)GlobalProxySelection.Select).Address.ToString().Length - colonIndex - 1);
						port.TrimEnd('/');
						this.textBoxPassword.Text = string.Empty;
						this.textBoxProxy.Text = proxystr;
						this.textBoxPort.Text = port.Substring(0, 4);
					}
				}
			}
			catch (Exception e)
			{
				LoggingHelper.ShowMessage(e);
			}
		}




	}
}