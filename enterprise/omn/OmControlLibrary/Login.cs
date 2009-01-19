#region Namespaces
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Windows.Forms;
using EnvDTE;
using EnvDTE80;
using System.Reflection;
using OMControlLibrary.Common;
using OManager.BusinessLayer.Login;
using Microsoft.VisualStudio.CommandBars;
using System.IO;
using stdole;
using OME.Logging.Common;

#endregion

namespace OMControlLibrary
{
	#region public partial class Login : ViewBase
	/// <summary>
	/// Using this user control, user can login to ObjectManager Enterprise.
	/// </summary>
	public partial class Login
	{
		#region Member Variables

		//Private static variables
		private static CommandBarControl m_cmdBarCtrlCreateDemoDb;
		internal static CommandBarControl m_cmdBarCtrlConnect;
		internal static CommandBarControl m_cmdBarCtrlDefrag;
		internal static CommandBarControl m_cmdBarCtrlBackup;
		internal static CommandBarButton m_cmdBarBtnConnect;
		private static Assembly m_AddIn_Assembly;
		private static WindowVisibilityEvents windowsVisEvents;
		//Private variables
		private IList<RecentQueries> m_recentConnections;

		//Constants

		private const string IMAGE_DISCONNECT = "OMAddin.Images.DB_DISCONNECT2_a.GIF";//DBdisconnect.gif";
		private const string IMAGE_DISCONNECT_MASKED = "OMAddin.Images.DB_DISCONNECT2_b.BMP";//DBdisconnect_Masked.bmp";

		private const string OPEN_FILE_DIALOG_FILTER = "db4o Database Files(*.yap, *.db4o)|*.yap;*.db4o|All Files(*.*)|*.*";
		private const string STRING_SERVER = "server:";
		private const string STRING_COLON = ":";
		private const char CHAR_COLON = ':';

		static Window queryBuilderToolWindow;


		#endregion

		#region Constructor

		/// <summary>
		/// Constructor for Login
		/// </summary>
		public Login()
		{
			InitializeComponent();
			try
			{
				m_recentConnections = GetAllRecentConnections();
				if (m_recentConnections != null)
				{
					foreach (RecentQueries recentQuery in m_recentConnections)
					{
						if (recentQuery.ConnParam.Host != null)
							radioButtonRemote.Checked = true;
						else
							radioButtonLocal.Checked = true;
						break;
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		#endregion

		#region Methods

		#region Public

		#region SetLiterals()
		/// <summary>
		/// A method from ViewBase class overriden for setting the text to all the labels.
		/// </summary>
		public override void SetLiterals()
		{
			try
			{
				labelFile.Text = Helper.GetResourceString(Common.Constants.LOGIN_RECENTCONNECTION_TEXT);
				labelHost.Text = Helper.GetResourceString(Common.Constants.LOGIN_HOST_TEXT);
				labelPort.Text = Helper.GetResourceString(Common.Constants.LOGIN_PORT_TEXT);
				labelUserName.Text = Helper.GetResourceString(Common.Constants.LOGIN_USERNAME_TEXT);
				labelPassword.Text = Helper.GetResourceString(Common.Constants.LOGIN_PASSWORD_TEXT);
				labelType.Text = Helper.GetResourceString(Common.Constants.LOGIN_TYPE_TEXT);
				labelNewConnection.Text = Helper.GetResourceString(Common.Constants.LOGIN_NEWCONNECTION_TEXT);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
		#endregion

		#region CreateLoginToolWindow()
		/// <summary>
		/// Creates the login tool window.
		/// </summary>
		public static void CreateLoginToolWindow(CommandBarControl cmdBarCtrl,
			CommandBarButton cmdBarBtn, Assembly addIn_Assembly, CommandBarControl cmdBarCtrlDefrag,
			CommandBarControl cmdBarCtrlBackup, CommandBarControl dbCreateDemoDbControl)
		{
			try
			{
				m_AddIn_Assembly = addIn_Assembly;
				m_cmdBarCtrlConnect = cmdBarCtrl;
				m_cmdBarBtnConnect = cmdBarBtn;
				m_cmdBarCtrlDefrag = cmdBarCtrlDefrag;
				m_cmdBarCtrlBackup = cmdBarCtrlBackup;
				m_cmdBarCtrlCreateDemoDb = dbCreateDemoDbControl;

				string assemblyPath = Assembly.GetExecutingAssembly().CodeBase.Remove(0, 8);
			    string guidPos = Guid.NewGuid().ToString(Helper.GetResourceString(Common.Constants.GUID_FORMATTER_STRING));//"{426E8D27-3D33-4fc8-B3E9-9883AADC679F}";
				string caption = string.Empty;
				object ctlObj = null;
				AddIn addinObj = ApplicationObject.AddIns.Item(1);
				EnvDTE80.Windows2 wins2Obj = (Windows2)ApplicationObject.Windows;

				if (Helper.LoginToolWindow != null)
					Helper.LoginToolWindow.Close(vsSaveChanges.vsSaveChangesNo);

				// Creates Tool Window and inserts the user control in it.
				Helper.LoginToolWindow = wins2Obj.CreateToolWindow2(addinObj, assemblyPath, Common.Constants.CLASS_NAME_LOGIN, caption, guidPos, ref ctlObj);
				if (Helper.LoginToolWindow.AutoHides)
				{
					Helper.LoginToolWindow.AutoHides = false;
				}
				Helper.LoginToolWindow.Visible = true;
				Helper.LoginToolWindow.Width = 425;
				Helper.LoginToolWindow.Height = 170;
				EnvDTE80.Events2 event_1 = (Events2)ApplicationObject.Events;
				windowsVisEvents = event_1.get_WindowVisibilityEvents(Helper.QueryResultToolWindow);
				windowsVisEvents.WindowHiding += windowsVisEvents_WindowHiding;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		static void windowsVisEvents_WindowHiding(Window Window)
		{
			//if (Helper.LoginToolWindow != null)
			//    if(Window==Helper.LoginToolWindow )
			//    Helper.LoginToolWindow.Caption = "Closed";
		}
		#endregion



		public static void CreateQueryBuilderToolWindow()
		{
			try
			{
				string assemblypath = Assembly.GetExecutingAssembly().CodeBase.Remove(0, 8);
			    string caption = Helper.GetResourceString(Common.Constants.QUERY_BUILDER_CAPTION);
				object ctlobj = null;
				AddIn addinobj = ApplicationObject.AddIns.Item(1);
				EnvDTE80.Windows2 wins2obj = (Windows2)ApplicationObject.Windows;

				// Creates Tool Window and inserts the user control in it.
				queryBuilderToolWindow = wins2obj.CreateToolWindow2(addinobj, assemblypath, Common.Constants.CLASS_NAME_QUERYBUILDER, caption, Common.Constants.GUID_QUERYBUILDER, ref ctlobj);
				if (queryBuilderToolWindow.AutoHides)
				{
					queryBuilderToolWindow.AutoHides = false;
				}
				queryBuilderToolWindow.Visible = true;
				queryBuilderToolWindow.IsFloating = false;
				queryBuilderToolWindow.Linkable = false;

			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}




		#endregion



		#region Private

		#region AfterSuccessfullyConnected()
		private void AfterSuccessfullyConnected()
		{
			try
			{
				m_cmdBarCtrlConnect.Caption = Common.Constants.TOOLBAR_DISCONNECT;
				((CommandBarButton)m_cmdBarCtrlConnect).State = MsoButtonState.msoButtonDown;

				m_cmdBarBtnConnect.Caption = Common.Constants.TOOLBAR_DISCONNECT;
				m_cmdBarBtnConnect.TooltipText = Common.Constants.TOOLBAR_DISCONNECT;
				m_cmdBarBtnConnect.State = MsoButtonState.msoButtonDown;

				if (radioButtonLocal.Checked)
				{
					m_cmdBarCtrlDefrag.Enabled = true;
					m_cmdBarCtrlBackup.Enabled = true;
					m_cmdBarCtrlCreateDemoDb.Enabled = false;
				}
				Stream imgageStream = m_AddIn_Assembly.GetManifestResourceStream(IMAGE_DISCONNECT);
				Stream imageStreamMask = m_AddIn_Assembly.GetManifestResourceStream(IMAGE_DISCONNECT_MASKED);
				((CommandBarButton)m_cmdBarCtrlConnect.Control).Picture = (StdPicture)PictureHost.IPictureDisp(Image.FromStream(imgageStream));
				((CommandBarButton)m_cmdBarCtrlConnect.Control).Mask = (StdPicture)PictureHost.IPictureDisp(Image.FromStream(imageStreamMask));


				((CommandBarButton)m_cmdBarBtnConnect.Control).Picture = (StdPicture)PictureHost.IPictureDisp(Image.FromStream(imgageStream));
				((CommandBarButton)m_cmdBarBtnConnect.Control).Mask = (StdPicture)PictureHost.IPictureDisp(Image.FromStream(imageStreamMask));

				//Stream imgStreamPic = m_AddIn_Assembly.GetManifestResourceStream(IMAGE_DISCONNECT);
				//Stream imgStreamMask = m_AddIn_Assembly.GetManifestResourceStream(IMAGE_DISCONNECT_MASKED);
				////Stream imgageStream = m_AddIn_Assembly.GetManifestResourceStream(IMAGE_DISCONNECT);
				//stdole.IPictureDisp Pic = PictureHost.IPictureDisp(Image.FromStream(imgStreamPic)); ;
				//stdole.IPictureDisp Mask=PictureHost.IPictureDisp(Image.FromStream(imgStreamMask));
				//((CommandBarButton)m_cmdBarCtrlConnect.Control).Picture = (stdole.StdPicture)Pic;
				//((CommandBarButton)m_cmdBarCtrlConnect.Control).Mask = (stdole.StdPicture)Mask;
				//((CommandBarButton)m_cmdBarCtrlConnect.Control).Mask  = (stdole.StdPicture)Common.PictureHost.IPictureDisp(Image.FromStream(imgageStream));

				// imgageStream = m_AddIn_Assembly.GetManifestResourceStream(IMAGE_DISCONNECT);
				// m_cmdBarBtnConnect.Picture = (stdole.StdPicture)Common.PictureHost.IPictureDisp(Image.FromStream(imgageStream));
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
		#endregion

		#region PopulateLocalRecentConnections()
		private void PopulateLocalRecentConnections()
		{
			try
			{
				if (m_recentConnections == null)
					m_recentConnections = GetAllRecentConnections();

				if (m_recentConnections.Count > 0)
				{
					comboBoxFilePath.Items.Clear();
					comboBoxFilePath.Items.Add(Helper.GetResourceString(Common.Constants.COMBOBOX_DEFAULT_TEXT));
					foreach (RecentQueries recentQuery in m_recentConnections)
					{
						if (recentQuery.ConnParam.Host == null)
							comboBoxFilePath.Items.Add(recentQuery.ConnParam.Connection);
					}
					comboBoxFilePath.SelectedIndex = 0;
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
		#endregion

		#region PopulateRemoteRecentConnections()
		private void PopulateRemoteRecentConnections()
		{
			try
			{
				if (m_recentConnections == null)
					m_recentConnections = GetAllRecentConnections();

				if (m_recentConnections.Count > 0)
				{
					comboBoxFilePath.Items.Clear();
					comboBoxFilePath.Items.Add(Helper.GetResourceString(Common.Constants.COMBOBOX_DEFAULT_TEXT));
					foreach (RecentQueries recentQuery in m_recentConnections)
					{
						if (recentQuery.ConnParam.Host != null)
							comboBoxFilePath.Items.Add(recentQuery.ConnParam.Connection);
					}
					comboBoxFilePath.SelectedIndex = 0;
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
		#endregion

		#region GetAllRecentConnections()
		private static List<RecentQueries> GetAllRecentConnections()
		{
			List<RecentQueries> recentConnections = new List<RecentQueries>();
			try
			{
				recentConnections = Helper.DbInteraction.FetchRecentQueries();
				if (recentConnections != null)
				{
					CompareTimestamps comparator = new CompareTimestamps();
					recentConnections.Sort(comparator);
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
			return recentConnections;
		}
		#endregion

		#endregion

		#endregion

		#region Event Handlers

		#region Login_Load
		/// <summary>
		/// Sets the label text.
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void Login_Load(object sender, EventArgs e)
		{
			try
			{
				SetLiterals();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
		#endregion

		#region radioButton_Click
		/// <summary>
		/// Event handler for toggling between Local Connection & Remote Connection.
		/// </summary>
		/// <param name="sender">The event can be invoked by either Local or remote radio button</param>
		/// <param name="e"></param>
		private void radioButton_Click(object sender, EventArgs e)
		{
			try
			{
				if (comboBoxFilePath.Items.Count > 0)
					comboBoxFilePath.SelectedIndex = 0;

				if (((RadioButton)sender).Text.Equals(Helper.GetResourceString(Common.Constants.LOGIN_CAPTION_LOCAL)))
				{
					textBoxConnection.Clear();
					panelLocal.Visible = true;
					panelRemote.Visible = false;
					buttonConnect.Text = Helper.GetResourceString(Common.Constants.LOGIN_CAPTION_OPEN);
					Helper.LoginToolWindow.Caption =
						Helper.GetResourceString(Common.Constants.LOGIN_WINDOW_LOCAL_CAPTION);
					PopulateLocalRecentConnections();
				}
				else
				{
					textBoxHost.Clear();
					textBoxPort.Clear();
					textBoxUserName.Clear();
					textBoxPassword.Clear();

					panelLocal.Visible = false;
					panelRemote.Visible = true;
					buttonConnect.Text = Helper.GetResourceString(Common.Constants.LOGIN_CAPTION_CONNECT);
					Helper.LoginToolWindow.Caption =
						Helper.GetResourceString(Common.Constants.LOGIN_WINDOW_REMOTE_CAPTION);
					PopulateRemoteRecentConnections();
					m_cmdBarCtrlBackup.Enabled = false;
					m_cmdBarCtrlDefrag.Enabled = false;
					m_cmdBarCtrlCreateDemoDb.Enabled = true;
				}
				if (comboBoxFilePath.Items.Count > 1)
				{
					comboBoxFilePath.SelectedIndex = 1;
				}
				else if (comboBoxFilePath.Items.Count == 1)
				{
					comboBoxFilePath.Items.Clear();
					toolTipForTextBox.RemoveAll();
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
		#endregion

		#region buttonBrowse_Click
		/// <summary>
		/// Event handler for browsing. This is used for Local Connection to select the database file. 
		/// </summary>
		/// <param name="sender"></param>
		/// <param name="e"></param>
		private void buttonBrowse_Click(object sender, EventArgs e)
		{
			try
			{
				if (comboBoxFilePath.Items.Count > 0)
				{
					comboBoxFilePath.SelectedIndex = 0;
					textBoxConnection.Clear();
				}
				openFileDialog.Filter = OPEN_FILE_DIALOG_FILTER;
				openFileDialog.Title = Helper.GetResourceString(Common.Constants.LOGIN_OPEN_FILE_DIALOG_CAPTION);  //OPEN_FILE_DIALOG_TITLE;
				if (openFileDialog.ShowDialog() != DialogResult.Cancel)
				{
					textBoxConnection.Text = openFileDialog.FileName;
					toolTipForTextBox.SetToolTip(textBoxConnection, textBoxConnection.Text);
					if (comboBoxFilePath.Items.Contains(textBoxConnection.Text))
						comboBoxFilePath.SelectedItem = textBoxConnection.Text;
					buttonConnect.Focus();
				}
				else
				{
					if (comboBoxFilePath.Items.Count > 0)
					{
						comboBoxFilePath.SelectedIndex = 1;
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
		#endregion

		#region buttonConnect_Click
		private void buttonConnect_Click(object sender, EventArgs e)
		{
			ConnParams conparam = null;
			RecentQueries currRecentQueries = null;
			string exceptionString = string.Empty;
			try
			{
				if (((Button)sender).Text.Equals(Helper.GetResourceString(Common.Constants.LOGIN_CAPTION_OPEN))) // Local Connection
				{

					if (!(Validations.ValidateLocalLoginParams(ref comboBoxFilePath, ref textBoxConnection)))
						return;
					try
					{
						conparam = new ConnParams(textBoxConnection.Text.Trim(), null, null, null, 0);
					}
					catch (Exception oEx)
					{
						LoggingHelper.ShowMessage(oEx);
					}
				}
				else // Remote Connection
				{
					if (!(Validations.ValidateRemoteLoginParams(ref comboBoxFilePath, ref textBoxHost, ref textBoxPort, ref textBoxUserName, ref textBoxPassword)))
						return;
					try
					{
						string connection = STRING_SERVER + textBoxHost.Text.Trim() + STRING_COLON + textBoxPort.Text.Trim() + STRING_COLON + textBoxUserName.Text.Trim();
						conparam = new ConnParams(connection, textBoxHost.Text.Trim(), textBoxUserName.Text.Trim(), textBoxPassword.Text.Trim(), Convert.ToInt32(textBoxPort.Text.Trim()));
					}
					catch (Exception oEx)
					{
						LoggingHelper.ShowMessage(oEx);
					}
				}
				try
				{
					buttonConnect.Enabled = false;
					currRecentQueries = new RecentQueries(conparam);
					RecentQueries tempRecentQueries = currRecentQueries.ChkIfRecentConnIsInDb();
					if (tempRecentQueries != null)
						currRecentQueries = tempRecentQueries;
					exceptionString = Helper.DbInteraction.ConnectoToDB(currRecentQueries);
				}
				catch (Exception oEx)
				{
					LoggingHelper.ShowMessage(oEx);
				}
				if (exceptionString == string.Empty)
				{
					Helper.DbInteraction.SetCurrentRecentConnection(currRecentQueries);
					Helper.DbInteraction.SaveRecentConnection(currRecentQueries);
					AfterSuccessfullyConnected();
					ObjectBrowserToolWin.CreateObjectBrowserToolWindow();
					ObjectBrowserToolWin.ObjBrowserWindow.Visible = true;
					CreateQueryBuilderToolWindow();

					PropertyPaneToolWin.CreatePropertiesPaneToolWindow(true);
					PropertyPaneToolWin.PropWindow.Visible = true;
					if (Helper.LoginToolWindow.AutoHides)
					{
						Helper.LoginToolWindow.AutoHides = false;
					}
					Helper.LoginToolWindow.Visible = false;
				}
				else
				{
					buttonConnect.Enabled = true;
					textBoxConnection.Clear();
					MessageBox.Show(exceptionString,
						Helper.GetResourceString(Common.Constants.PRODUCT_CAPTION),
						MessageBoxButtons.OK,
						MessageBoxIcon.Error);
					return;
				}

			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
		#endregion

		#region comboBoxFilePath_SelectedIndexChanged
		private void comboBoxFilePath_SelectedIndexChanged(object sender, EventArgs e)
		{
			try
			{
				if (radioButtonRemote.Checked)
				{
					if (!comboBoxFilePath.Text.Equals(Helper.GetResourceString(Common.Constants.COMBOBOX_DEFAULT_TEXT)))
					{
						string[] strRemote = comboBoxFilePath.Text.Split(CHAR_COLON);
						textBoxHost.Text = strRemote[1];
						textBoxPort.Text = strRemote[2];
						textBoxUserName.Text = strRemote[3];
						textBoxPassword.Focus();
						toolTipForTextBox.SetToolTip(comboBoxFilePath, comboBoxFilePath.SelectedItem.ToString());
					}
					else
					{
						textBoxHost.Clear();
						textBoxPort.Clear();
						textBoxUserName.Clear();
						textBoxPassword.Clear();
					}
				}
				else
				{
					if (!comboBoxFilePath.Text.Equals(Helper.GetResourceString(Common.Constants.COMBOBOX_DEFAULT_TEXT)))
					{
						textBoxConnection.Text = comboBoxFilePath.Text.Trim();
						toolTipForTextBox.SetToolTip(textBoxConnection, textBoxConnection.Text);
						toolTipForTextBox.SetToolTip(comboBoxFilePath, comboBoxFilePath.SelectedItem.ToString());
					}
					else
					{
						textBoxConnection.Clear();
					}

				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
		#endregion

		#region comboBoxFilePath_DropdownItemSelected
		private void comboBoxFilePath_DropdownItemSelected(object sender, ToolTipComboBox.DropdownItemSelectedEventArgs e)
		{
			try
			{
				if (e.SelectedItem < 0 || e.Scrolled) toolTipForTextBox.Hide(comboBoxFilePath);
				else
					toolTipForTextBox.Show(comboBoxFilePath.Items[e.SelectedItem].ToString(), comboBoxFilePath, e.Bounds.Location.X + Cursor.Size.Width, e.Bounds.Location.Y + Cursor.Size.Height);
			}
			catch (Exception ex)
			{
				LoggingHelper.HandleException(ex);
			}
		}
		#endregion

		#region buttonCancel_Click
		private void buttonCancel_Click(object sender, EventArgs e)
		{
			try
			{
				textBoxPort.Clear();
				textBoxHost.Clear();
				textBoxConnection.Clear();
				textBoxPassword.Clear();
				textBoxUserName.Clear();
				Helper.LoginToolWindow.Caption = "Closed";
				Helper.LoginToolWindow.Close(vsSaveChanges.vsSaveChangesNo);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
		#endregion

		private void textBoxPort_KeyPress(object sender, KeyPressEventArgs e)
		{
			try
			{
				char c = e.KeyChar;

				//Allow only numeric charaters in filter textbox.
				if (!Helper.IsNumeric(c.ToString()))
					e.Handled = true;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		#endregion

		private void textBoxPort_TextChanged(object sender, EventArgs e)
		{
			int result;
			if (!Int32.TryParse(textBoxPort.Text.Trim(), out result))
			{
				textBoxPort.Text = string.Empty;
			}
		}

		private void textBoxPort_KeyDown(object sender, KeyEventArgs e)
		{
			if (e.Modifiers == Keys.Control)
				e.Handled = true;
		}
	}
	#endregion
}
