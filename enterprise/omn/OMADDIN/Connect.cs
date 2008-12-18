using System;
using System.Text.RegularExpressions;
using Extensibility;
using EnvDTE;
using EnvDTE80;
using Microsoft.VisualStudio.CommandBars;
using System.Windows.Forms;
using System.Collections;
using System.IO;
using System.Reflection;
using System.Drawing;
using System.ComponentModel;
using OManager.DataLayer.CommonDatalayer;
using OMControlLibrary;
using OMControlLibrary.Common;
using OMControlLibrary.LoginToSalesForce;
using OManager.BusinessLayer.QueryManager;
using OManager.BusinessLayer.Login;

using OME.Logging.Common;
using OME.Logging.ExceptionLogging;
using OME.Logging.Tracing;

namespace OMAddin
{
	/// <summary>The object for implementing an Add-in.</summary>
	/// <seealso class='IDTExtensibility2' />
	public class Connect : IDTExtensibility2, IDTCommandTarget
	{
		#region Private Variables

		private DTE2 _applicationObject;
		private AddIn _addInInstance;
		private Command cmd = null;
		private CommandBar omToolbar = null;
		private CommandBarPopup oPopup = null;
		//TODO: Never used ?
		private CommandBarPopup oPopupMaintainance = null;

		private CommandBarEvents loginControlHandler;
		private CommandBarEvents logoutControlHandler;
		private CommandBarEvents dbConnectControlHandler;
		private CommandBarEvents db4oControlHandler;
		private CommandBarEvents db4oDnldControlHandler;
		private CommandBarEvents salesForceControlHandler;
		private CommandBarEvents omHelpControlHandler;
		private CommandBarEvents reqConsultationControlHandler;
		private CommandBarEvents omDefragControlHandler;
		private CommandBarEvents omProxyConfigHandler;
		private CommandBarEvents omBackupControlHandler;
		private CommandBarEvents omObjectBrowserControlHandler;
		private CommandBarEvents db4oDeveloperControlHandler;
		private CommandBarEvents omAboutControlHandler;

		private CommandBarEvents dbCreateDemoDbControlHandler;
		private CommandBarEvents omQueryBuilderControlHandler;
		private CommandBarEvents omPropertiesControlHandler;



		private CommandBarControl dbCreateDemoDbControl = null;
		private CommandBarControl dbConnectControl = null;
		private CommandBarControl loginControl = null;
		private CommandBarControl logoutControl = null;
		private CommandBarControl omDefragControl = null;
		private CommandBarControl omProxyConfigControl = null;
		private CommandBarControl omBackupControl = null;

		private CommandBarControl omObjectBrowserControl = null;
		private CommandBarControl omQueryBuilderControl = null;
		private CommandBarControl omPropertiesControl = null;

		private CommandBarControl reqConsultationControl = null;
		private CommandBarControl db4oDeveloperControl = null;
		private CommandBarControl db4oDnldControl = null;
		private CommandBarControl omHelpControl = null;
		private CommandBarControl omAboutControl = null;

		private CommandBarButton loginButton = null;
		private CommandBarButton logoutButton = null;
		private CommandBarButton omButton = null;
		private CommandBarButton db4oHelpControlButton = null;
		private CommandBarButton salesForceControlButton = null;
		private CommandBarButton reqConsultationControlButton = null;
		private CommandBarControl salesForceControl = null;
		private CommandBarControl db4oControl = null;

		private Window windb4oHome = null;
		private Window windb4oDownloads = null;
		private Window winSupportdb4o = null;
		private Window winRequestConsultation = null;
		private Window winHelp = null;
		private Window windb4oDeveloper = null;




		private EnvDTE.WindowEvents _windowsEvents;


		#endregion


		#region Private Constants

		//private const string IMAGE_LOGGEDIN = "OMAddin.Images.Connected_1.gif";
		//private const string IMAGE_LOGGEDOUT = "OMAddin.Images.NotConnected_1.gif";
		private const string IMAGE_LOGIN = "OMAddin.Images.Login.gif";
		private const string IMAGE_LOGOUT = "OMAddin.Images.Logout.gif";
		private const string IMAGE_CONNECT = "OMAddin.Images.DBconnect.gif";
		//private const string IMAGE_DISCONNECT = "OMAddin.Images.DBdisconnect.gif";
		private const string IMAGE_DISCONNECT = "OMAddin.Images.DB_DISCONNECT2_a.GIF";
		private const string IMAGE_XTREMECONNECT = "OMAddin.Images.XtremeConnct_2.gif";
		private const string IMAGE_SUPPORTCASES = "OMAddin.Images.SupportCases.gif";
		private const string IMAGE_HELP = "OMAddin.Images.support1.gif";

		//Masked
		private const string IMAGE_LOGIN_MASKED = "OMAddin.Images.Login_Masked.bmp";
		private const string IMAGE_LOGOUT_MASKED = "OMAddin.Images.Logout_Masked.bmp";
		private const string IMAGE_CONNECT_MASKED = "OMAddin.Images.DBconnect_Masked.bmp";
		//private const string IMAGE_DISCONNECT_MASKED = "OMAddin.Images.DBdisconnect_Masked.bmp";
		private const string IMAGE_DISCONNECT_MASKED = "OMAddin.Images.DB_DISCONNECT2_b.BMP";
		private const string IMAGE_XTREMECONNECT_MASKED = "OMAddin.Images.XtremeConnct_2_Masked.bmp";
		private const string IMAGE_SUPPORTCASES_MASKED = "OMAddin.Images.SupportCases_Masked.bmp";//Mask.bmp";
		private const string IMAGE_HELP_MASKED = "OMAddin.Images.support1_Masked.bmp";
		//Masked

		private const string COMMAND_NAME = "OMAddin.Connect.ObjectManager Enterprise";
		//private const string DB4O_BROWSER = "db4o Browser";
		private const string DB4O_HOMEPAGE = "db4objects Homepage";
		private const string TOOLS = "Tools";
		private const string CONNECT = "Connect";
		private const string XTREME_CONNECT = "XtremeConnect";
		private const string SUPPORT_CASES = "Support Cases";
		private const string MAINTAINANCE = "Maintainance";
		private const string OPTIONS = "Options";
		private const string PROXYCONFIGURATIONS = "Proxy Configurations";

		private const string DEFRAG = "Defrag";
		private const string BACKUP = "Backup";
		private const string DB4O_DEVELOPER_COMMUNITY = "db4objects Developer Community";
		private const string DB4O_DOWNLOADS = "db4objects Downloads";
		private const string DB4O_HELP = "Help";
		private const string ABOUT_OME = "About ObjectManager Enterprise";
		private const string SUPPORT = "Support";
		private const string PAIRING = "Pairing";
		internal const string STATUS_FULLFUNCTIONALITYMODE = "Connected - All OME functionality available";
		internal const string STATUS_REDUCEDMODELOGGEDIN = "Connected -  OME Functionality is limited";

		internal const string STATUS_LOGGEDOUT = "Not Connected -All Functionality is limited";
		private const string CREATE_DEMO_DB = "Create Demo Database";

		private const char CHAR_FORWORD_SLASH = '/';
		private const string CONTACT_SALES = @"/ContactSales/ContactSales.htm";
		private const string FAQ = @"/FAQ/FAQ.htm";

		private const string URL_DB4O_DEVELOPER = "http://developer.db4o.com";
		private const string URL_DB4O_DOWNLOADS = "http://developer.db4o.com/files/default.aspx";
		private const string URL_DB4O_HOMEPAGE = "http://db4o.com";

		#endregion

		#region Connect Constructor
		/// <summary>Implements the constructor for the Add-in object. Place your initialization code within this method.</summary>
		public Connect()
		{
			try
			{
				OMETrace.Initialize();
			}
			catch (Exception ex)
			{
				ex.ToString();//ignore
			}

			try
			{
				ExceptionHandler.Initialize();
			}
			catch (Exception ex)
			{
				ex.ToString();//ignore
			}

			try
			{
				OMControlLibrary.ApplicationManager.CheckLocalAndSetLanguage();
			}
			catch (Exception ex)
			{
				ex.ToString();//ignore
			}
		}
		#endregion
		DTEEvents eve;
		#region Connect Event Handlers

		#region OnConnection
		/// <summary>Implements the OnConnection method of the IDTExtensibility2 interface. Receives notification that the Add-in is being                       loaded.</summary>
		/// <param term='application'>Root object of the host application.</param>
		/// <param term='connectMode'>Describes how the Add-in is being loaded.</param>
		/// <param term='addInInst'>Object representing this Add-in.</param>
		/// <seealso class='IDTExtensibility2' />
		public void OnConnection(object application, ext_ConnectMode connectMode, object addInInst, ref Array custom)
		{
			_applicationObject = (DTE2)application;
			_addInInstance = (AddIn)addInInst;

			try
			{
				if (connectMode == ext_ConnectMode.ext_cm_AfterStartup ||
					connectMode == ext_ConnectMode.ext_cm_Startup)
				{
					//This function creates menu
					CreateMenu();

					try
					{
						CommandBars toolBarCommandBars = ((CommandBars)_applicationObject.CommandBars);
						string toolbarName = Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION);
						try
						{
							omToolbar = toolBarCommandBars.Add(toolbarName, MsoBarPosition.msoBarTop, System.Type.Missing, false);
						}
						catch (ArgumentException)
						{
							//Try to find existing CommandBar
							omToolbar = toolBarCommandBars[toolbarName];
						}
						omToolbar.Visible = true;

						//This function creates Toolbar
						CreateToolBar();
					}
					catch (Exception oEx)
					{
						LoggingHelper.HandleException(oEx);
					}

					try
					{
						//Get the DTE Events object.
						EnvDTE.Events events = _applicationObject.Events;

						//Get the WindowEvents object.
						_windowsEvents = (EnvDTE.WindowEvents)events.get_WindowEvents(null);

						//Set the WindowActivated event delegate.
						_windowsEvents.WindowActivated += new _dispWindowEvents_WindowActivatedEventHandler(_windowsEvents_WindowActivated);

						eve = (DTEEvents)_applicationObject.Events.DTEEvents;

						eve.ModeChanged += new _dispDTEEvents_ModeChangedEventHandler(eve_ModeChanged);
					}
					catch (Exception oEx)
					{
						LoggingHelper.HandleException(oEx);
					}
					try
					{
						//This function checks whether user already logged in.

						ViewBase.ApplicationObject = _applicationObject;
						//enable disable connect button while checking cfredentials
						dbConnectControl.Enabled = false;
						omButton.Enabled = false;
						Cursor.Current = Cursors.WaitCursor;
						Helper.CheckIfAlreadyLoggedIn();
						Cursor.Current = Cursors.Default;
						dbConnectControl.Enabled = true;
						omButton.Enabled = true;
						StatusBarText();

					}
					catch (Exception oEx)
					{
						LoggingHelper.HandleException(oEx);
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}

		}

		void eve_ModeChanged(vsIDEMode LastMode)
		{
			try
			{
				StatusBarText();
				bool loginPresent = false, propertiesPane = false;

				foreach (Window win in _applicationObject.Windows)
				{
					if (win.Equals(Helper.LoginToolWindow))
					{
						loginPresent = true;
					}
					if (win.Caption.Equals("Properties") || win.Caption.Equals("DataBase Properties"))
					{
						propertiesPane = true;
					}

				}
				if (loginPresent == true && propertiesPane == false)
				{

					foreach (Window w in _applicationObject.ToolWindows.DTE.Windows)
					{
						if (w.Type == vsWindowType.vsWindowTypeToolWindow)
						{

							if (CheckIfWinISVSWin(w))
							{
								w.Visible = false;
							}
						}
					}
					if (Helper.LoginToolWindow != null)
						if (Helper.LoginToolWindow.Caption != "Closed")
							Helper.LoginToolWindow.Visible = true;
				}
				else if (!loginPresent)
				{

					//Helper.LoginToolWindow.Visible = false;
					foreach (Window w in _applicationObject.ToolWindows.DTE.Windows)
					{
						if (w.Type == vsWindowType.vsWindowTypeToolWindow)
						{
							if (CheckIfWinISVSWin(w))
							{
								w.Visible = false;

							}
						}
						if (Helper.LoginToolWindow != null)
							Helper.LoginToolWindow.Visible = false;
					}
				}
				else
				{
					foreach (Window w in _applicationObject.ToolWindows.DTE.Windows)
					{
						if (w.Type == vsWindowType.vsWindowTypeToolWindow)
						{

							if (CheckIfWinISVSWin(w))
							{
								if (w.Object == null || w.Caption != "Connect to db4o database" || w.Caption != "Closed"
									 || w.Caption != "Connect to db4o server")
									w.Visible = true;
								if (w.Caption == "Connect to db4o server" || w.Caption == "Connect to db4o database" || w.Caption == "Closed")
									w.Visible = false;

							}
						}
					}


				}
			}
			catch (System.Runtime.InteropServices.COMException)
			{
			}
			catch (Exception ex)
			{
				LoggingHelper.HandleException(ex);
			}


		}






		#endregion

		#region OnDisconnection
		/// <summary>Implements the OnDisconnection method of the IDTExtensibility2 interface. Receives notification that the Add-in is being                     unloaded.</summary>
		/// <param term='disconnectMode'>Describes how the Add-in is being unloaded.</param>
		/// <param term='custom'>Array of parameters that are host application specific.</param>
		/// <seealso class='IDTExtensibility2' />
		public void OnDisconnection(ext_DisconnectMode disconnectMode, ref Array custom)
		{
			try
			{

				//This function Aborts the current session
				Helper.AbortSession();
				Helper.ClearAllCachedAttributes();

			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}

			try
			{
				if (cmd != null)
					cmd.Delete();
			}
			catch (System.Runtime.InteropServices.InvalidComObjectException oEx)
			{
				oEx.ToString(); // Ignore
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
			try
			{
				if (oPopup != null)
					oPopup.Delete(null);
			}
			catch (System.Runtime.InteropServices.InvalidComObjectException oEx)
			{
				oEx.ToString(); // Ignore
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
			try
			{
				if (oPopupMaintainance != null)
					oPopupMaintainance.Delete(null);
			}
			catch (System.Runtime.InteropServices.InvalidComObjectException oEx)
			{
				oEx.ToString(); // Ignore
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
			try
			{
				if (omToolbar != null)
					omToolbar.Delete();
			}
			catch (System.Runtime.InteropServices.InvalidComObjectException oEx)
			{
				oEx.ToString(); // Ignore
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}
		#endregion

		#region OnAddInsUpdate
		/// <summary>Implements the OnAddInsUpdate method of the IDTExtensibility2 interface. Receives notification when the collection of                           Add-ins has changed.</summary>
		/// <param term='custom'>Array of parameters that are host application specific.</param>
		/// <seealso class='IDTExtensibility2' />		
		public void OnAddInsUpdate(ref Array custom)
		{
		}

		#endregion

		#region OnStartupComplete
		/// <summary>Implements the OnStartupComplete method of the IDTExtensibility2 interface. Receives notification that the host                             application has completed loading.</summary>
		/// <param term='custom'>Array of parameters that are host application specific.</param>
		/// <seealso class='IDTExtensibility2' />
		public void OnStartupComplete(ref Array custom)
		{
		}
		#endregion

		#region OnBeginShutdown
		/// <summary>Implements the OnBeginShutdown method of the IDTExtensibility2 interface. Receives notification that the host                                   application is being unloaded.</summary>
		/// <param term='custom'>Array of parameters that are host application specific.</param>
		/// <seealso class='IDTExtensibility2' />
		public void OnBeginShutdown(ref Array custom)
		{
			CloseAllToolWindows();
		}
		#endregion

		#region QueryStatus
		/// <summary>Implements the QueryStatus method of the IDTCommandTarget interface. This is called when the command's availability is                      updated</summary>
		/// <param term='commandName'>The name of the command to determine state for.</param>
		/// <param term='neededText'>Text that is needed for the command.</param>
		/// <param term='status'>The state of the command in the user interface.</param>
		/// <param term='commandText'>Text requested by the neededText parameter.</param>
		/// <seealso class='Exec' />
		public void QueryStatus(string commandName, vsCommandStatusTextWanted neededText, ref vsCommandStatus status, ref object commandText)
		{
			try
			{
				if (neededText == vsCommandStatusTextWanted.vsCommandStatusTextWantedNone)
				{
					if (commandName == COMMAND_NAME)
					{
						status = (vsCommandStatus)vsCommandStatus.vsCommandStatusSupported | vsCommandStatus.vsCommandStatusEnabled;
						return;
					}
					else
						status = vsCommandStatus.vsCommandStatusUnsupported;
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}

		}
		#endregion

		#region Exec
		/// <summary>Implements the Exec method of the IDTCommandTarget interface. This is called when the command is invoked.</summary>
		/// <param term='commandName'>The name of the command to execute.</param>
		/// <param term='executeOption'>Describes how the command should be run.</param>
		/// <param term='varIn'>Parameters passed from the caller to the command handler.</param>
		/// <param term='varOut'>Parameters passed from the command handler to the caller.</param>
		/// <param term='handled'>Informs the caller if the command was handled or not.</param>
		/// <seealso class='Exec' />
		public void Exec(string commandName, vsCommandExecOption executeOption, ref object varIn, ref object varOut, ref bool handled)
		{
			try
			{
				handled = false;
				if (executeOption == EnvDTE.vsCommandExecOption.vsCommandExecOptionDoDefault)
				{
					if (commandName == COMMAND_NAME)
					{
						// Add your command execution here 
						handled = true;
						return;
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}

		}
		#endregion

		#endregion

		#region Event Handlers

		#region _windowsEvents_WindowActivated
		/// <summary>
		/// This event handler gets the Activated event of tool window.
		/// When db4o Browser opens for first time, it creates menu option under ObjectManager Enterprise menu.
		/// Using this menu option, user can get back to db4o Browser if he has closed it before.
		/// </summary>
		/// <param name="GotFocus"></param>
		/// <param name="LostFocus"></param>
		void _windowsEvents_WindowActivated(Window GotFocus, Window LostFocus)
		{
			try
			{
				StatusBarText();
				Assembly ThisAssembly = Assembly.GetExecutingAssembly();

				//Object Browser Windows Menu
				if (GotFocus != null &&
					GotFocus.ObjectKind.Equals(OMControlLibrary.Common.Constants.GUID_OBJECTBROWSER.ToUpper())
					&& LostFocus.Caption != "Closed")
				{
					//db4o Browser Submenu
					if (omObjectBrowserControl == null)
					{
						omObjectBrowserControl = oPopup.Controls.Add(MsoControlType.msoControlButton,
													System.Reflection.Missing.Value,
													System.Reflection.Missing.Value,
													11, true);

						omObjectBrowserControl.BeginGroup = true;
						omObjectBrowserControl.Caption = Helper.GetResourceString(OMControlLibrary.Common.Constants.DB4O_BROWSER_CAPTION);

						omObjectBrowserControlHandler =
							(CommandBarEvents)_applicationObject.Events.get_CommandBarEvents(omObjectBrowserControl);
						omObjectBrowserControlHandler.Click +=
							new _dispCommandBarControlEvents_ClickEventHandler(omObjectBrowserControlHandler_Click);
					}
				}
				else if (GotFocus != null &&
					GotFocus.ObjectKind.Equals(OMControlLibrary.Common.Constants.GUID_QUERYBUILDER.ToUpper())
					 && LostFocus.Caption != "Closed")
				{
					if (omQueryBuilderControl == null)
					{
						omQueryBuilderControl = oPopup.Controls.Add(MsoControlType.msoControlButton,
													System.Reflection.Missing.Value,
													System.Reflection.Missing.Value,
													12, true);

						//omQueryBuilderControl.BeginGroup = true;
						omQueryBuilderControl.Caption = Helper.GetResourceString(OMControlLibrary.Common.Constants.QUERY_BUILDER_CAPTION);

						omQueryBuilderControlHandler =
							(CommandBarEvents)_applicationObject.Events.get_CommandBarEvents(omQueryBuilderControl);
						omQueryBuilderControlHandler.Click += new _dispCommandBarControlEvents_ClickEventHandler(omQueryBuilderControlHandler_Click);
					}
				}
				else if (GotFocus != null &&
				  GotFocus.ObjectKind.Equals(OMControlLibrary.Common.Constants.GUID_PROPERTIES.ToUpper())
			   && LostFocus.Caption != "Closed")
				{
					if (omPropertiesControl == null)
					{
						omPropertiesControl = oPopup.Controls.Add(MsoControlType.msoControlButton,
													System.Reflection.Missing.Value,
													System.Reflection.Missing.Value,
													13, true);

						//omPropertiesControl.BeginGroup = true;
						omPropertiesControl.Caption = Helper.GetResourceString(OMControlLibrary.Common.Constants.PROPERTIES_TAB_CAPTION).Trim();

						omPropertiesControlHandler =
							(CommandBarEvents)_applicationObject.Events.get_CommandBarEvents(omPropertiesControl);
						omPropertiesControlHandler.Click +=
							new _dispCommandBarControlEvents_ClickEventHandler(omPropertiesControlHandler_Click);
					}
				}
			}
			catch (System.Runtime.InteropServices.COMException)
			{
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		#endregion

		#region loginControlHandler_Click
		/// <summary>
		/// This event handler checks user status(Login/Logout).
		/// </summary>
		/// <param name="CommandBarControl"></param>
		/// <param name="Handled"></param>
		/// <param name="CancelDefault"></param>
		void loginControlHandler_Click(object CommandBarControl, ref bool Handled, ref bool CancelDefault)
		{
			Cursor.Current = Cursors.WaitCursor;

			LoginClick();
			Cursor.Current = Cursors.Default;

		}
		#endregion
		#region loginButton_Click
		/// <summary>
		/// This event handler checks user status(Login/Logout).
		/// </summary>
		/// <param name="Ctrl"></param>
		/// <param name="CancelDefault"></param>
		void loginButton_Click(CommandBarButton Ctrl, ref bool CancelDefault)
		{
			Cursor.Current = Cursors.WaitCursor;

			LoginClick();
			Cursor.Current = Cursors.Default;

		}
		#endregion

		#region dbConnectControlHandler_Click
		/// <summary>
		/// This event handler opens the Login tool window.
		/// </summary>
		/// <param name="CommandBarControl"></param>
		/// <param name="Handled"></param>
		/// <param name="CancelDefault"></param>
		void dbConnectControlHandler_Click(object CommandBarControl, ref bool Handled, ref bool CancelDefault)
		{
			Cursor.Current = Cursors.WaitCursor;

			ConnectToDatabaseOrServer((CommandBarControl)CommandBarControl);
			Cursor.Current = Cursors.Default;

		}
		#endregion
		#region omButton_Click
		void omButton_Click(CommandBarButton Ctrl, ref bool CancelDefault)
		{
			Cursor.Current = Cursors.WaitCursor;

			ConnectToDatabaseOrServer(Ctrl);
			Cursor.Current = Cursors.Default;

		}
		#endregion

		#region reqConsultationControlHandler_Click
		void reqConsultationControlHandler_Click(object CommandBarControl, ref bool Handled, ref bool CancelDefault)
		{
			Cursor.Current = Cursors.WaitCursor;
			XtremeConnect();
			Cursor.Current = Cursors.Default;

		}
		#endregion
		#region reqConsultationControlButton_Click
		void reqConsultationControlButton_Click(CommandBarButton Ctrl, ref bool CancelDefault)
		{
			Cursor.Current = Cursors.WaitCursor;
			XtremeConnect();
			Cursor.Current = Cursors.Default;
		}
		#endregion

		#region salesForceControlHandler_Click
		void salesForceControlHandler_Click(object CommandBarControl, ref bool Handled, ref bool CancelDefault)
		{
			Cursor.Current = Cursors.WaitCursor;
			SupportCases();
			Cursor.Current = Cursors.Default;
		}
		#endregion
		#region salesForceControlButton_Click
		void salesForceControlButton_Click(CommandBarButton Ctrl, ref bool CancelDefault)
		{
			Cursor.Current = Cursors.WaitCursor;
			SupportCases();
			Cursor.Current = Cursors.Default;
		}
		#endregion

		#region omBackupControlHandler_Click
		/// <summary>
		/// This event handler takes backup of currently connected db4o database.
		/// </summary>
		/// <param name="CommandBarControl"></param>
		/// <param name="Handled"></param>
		/// <param name="CancelDefault"></param>
		void omBackupControlHandler_Click(object CommandBarControl, ref bool Handled, ref bool CancelDefault)
		{
			BackupDatabase();
		}
		#endregion

		#region omDefragControlHandler_Click
		/// <summary>
		/// This event handler defrags currently connected db4o database.
		/// </summary>
		/// <param name="CommandBarControl"></param>
		/// <param name="Handled"></param>
		/// <param name="CancelDefault"></param>
		void omDefragControlHandler_Click(object CommandBarControl, ref bool Handled, ref bool CancelDefault)
		{
			DefragDatabase();
		}
		#endregion

		#region db4oControlHandler_Click
		void db4oControlHandler_Click(object CommandBarControl, ref bool Handled, ref bool CancelDefault)
		{
			Cursor.Current = Cursors.WaitCursor;
			Opendb4oHomepage();
			Cursor.Current = Cursors.Default;
		}
		#endregion
		#region db4oControlButton_Click
		void db4oControlButton_Click(CommandBarButton Ctrl, ref bool CancelDefault)
		{
			Cursor.Current = Cursors.WaitCursor;
			Opendb4oHomepage();
			Cursor.Current = Cursors.Default;
		}
		#endregion

		#region db4oDeveloperControlHandler_Click
		void db4oDeveloperControlHandler_Click(object CommandBarControl, ref bool Handled, ref bool CancelDefault)
		{
			Cursor.Current = Cursors.WaitCursor;
			Opendb4oDeveloper();
			Cursor.Current = Cursors.Default;
		}

		#endregion

		#region db4oDnldControlHandler_Click
		void db4oDnldControlHandler_Click(object CommandBarControl, ref bool Handled, ref bool CancelDefault)
		{
			Cursor.Current = Cursors.WaitCursor;
			Opendb4oDownloads();
			Cursor.Current = Cursors.Default;
		}
		#endregion
		#region db4oDnldControlButton_Click
		void db4oDnldControlButton_Click(CommandBarButton Ctrl, ref bool CancelDefault)
		{
			Cursor.Current = Cursors.WaitCursor;
			Opendb4oDownloads();
			Cursor.Current = Cursors.Default;
		}
		#endregion

		#region omHelpControlHandler_Click
		void omHelpControlHandler_Click(object CommandBarControl, ref bool Handled, ref bool CancelDefault)
		{
			Cursor.Current = Cursors.WaitCursor;
			OpenHelp();
			Cursor.Current = Cursors.Default;
		}
		#endregion
		#region db4oHelpControlButton_Click
		void db4oHelpControlButton_Click(CommandBarButton Ctrl, ref bool CancelDefault)
		{
			Cursor.Current = Cursors.WaitCursor;
			OpenHelp();
			Cursor.Current = Cursors.Default;
		}
		#endregion

		#region omAboutControlHandler_Click
		void omAboutControlHandler_Click(object CommandBarControl, ref bool Handled, ref bool CancelDefault)
		{
			Cursor.Current = Cursors.WaitCursor;
			OpenOMEAboutBox();
			Cursor.Current = Cursors.Default;

		}
		#endregion

		#region omObjectBrowserControlHandler_Click
		/// <summary>
		/// This event handler reopens db4o tool window. 
		/// </summary>
		/// <param name="CommandBarControl"></param>
		/// <param name="Handled"></param>
		/// <param name="CancelDefault"></param>
		void omObjectBrowserControlHandler_Click(object CommandBarControl, ref bool Handled, ref bool CancelDefault)
		{
			try
			{
				Cursor.Current = Cursors.WaitCursor;
				Helper.DbInteraction.ConnectoToDB(Helper.DbInteraction.GetCurrentRecentConnection());
				ObjectBrowserToolWin.CreateObjectBrowserToolWindow();
				Cursor.Current = Cursors.Default;
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		void omQueryBuilderControlHandler_Click(object CommandBarControl, ref bool Handled, ref bool CancelDefault)
		{
			try
			{
				Cursor.Current = Cursors.WaitCursor;
				Helper.DbInteraction.ConnectoToDB(Helper.DbInteraction.GetCurrentRecentConnection());
				Login.CreateQueryBuilderToolWindow();
				Cursor.Current = Cursors.Default;
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		void omPropertiesControlHandler_Click(object CommandBarControl, ref bool Handled, ref bool CancelDefault)
		{
			try
			{
				Cursor.Current = Cursors.WaitCursor;
				Helper.DbInteraction.ConnectoToDB(Helper.DbInteraction.GetCurrentRecentConnection());
				PropertyPaneToolWin.CreatePropertiesPaneToolWindow(true);
				Cursor.Current = Cursors.Default;
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		#endregion

		#endregion

		#region Private Methods

		#region AddSubMenu
		/// <summary>
		/// This functions adds submenu.
		/// </summary>
		/// <param name="Ctrl"></param>
		/// <param name="Popup"></param>
		/// <param name="ControlHandler"></param>
		/// <param name="Position"></param>
		/// <param name="Caption"></param>
		/// <param name="ImagePath"></param>
		private void AddSubMenu(ref CommandBarControl Ctrl, CommandBarPopup Popup, ref CommandBarEvents ControlHandler, int Position, string Caption, string ImagePath, string MaskedImagePath)
		{
			try
			{
				Assembly ThisAssembly = Assembly.GetExecutingAssembly();
				Ctrl = Popup.Controls.Add(MsoControlType.msoControlButton,
										  System.Reflection.Missing.Value,
										  System.Reflection.Missing.Value,
										  Position, true);
				Ctrl.Caption = Caption;

				ControlHandler = (CommandBarEvents)_applicationObject.Events.get_CommandBarEvents(Ctrl);

				if (!string.IsNullOrEmpty(ImagePath))
				{
					//Stream imgageStream = ThisAssembly.GetManifestResourceStream(ImagePath);
					//((CommandBarButton)(Ctrl.Control)).Picture = (stdole.StdPicture)MyHost.GetIPictureDispFromPicture(Image.FromStream(imgageStream));


					System.IO.Stream imgStreamPic = ThisAssembly.GetManifestResourceStream(ImagePath);
					System.IO.Stream imgStreamMask = ThisAssembly.GetManifestResourceStream(MaskedImagePath);
					//MyHost ax = new MyHost();               
					stdole.IPictureDisp Pic;
					stdole.IPictureDisp Mask;
					Pic = MyHost.IPictureDisp(Image.FromStream(imgStreamPic));
					Mask = MyHost.IPictureDisp(Image.FromStream(imgStreamMask));
					//Pic = (stdole.StdPicture)MyHost.GetIPictureDispFromPicture(Image.FromStream(imgStreamPic));
					//Mask = (stdole.StdPicture)MyHost.GetIPictureDispFromPicture(Image.FromStream(imgStreamMask));
					((CommandBarButton)(Ctrl.Control)).Picture = (stdole.StdPicture)Pic;
					((CommandBarButton)(Ctrl.Control)).Mask = (stdole.StdPicture)Mask;
				}
				if (string.Equals(Caption, Helper.GetResourceString("Login")) || string.Equals(Caption, "Logout"))
				{
					//  Helper.m_cmdBarCtrlLogin = Ctrl;
					Helper.m_AddIn_Assembly = Assembly.GetExecutingAssembly();
				}
				else if (string.Equals(Caption, DB4O_HOMEPAGE))
				{
					Ctrl.BeginGroup = true;
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}
		#endregion

		#region CreateMenu
		/// <summary>
		/// Creates Menu & Submenus under Tools menu.
		/// </summary>
		private void CreateMenu()
		{
			try
			{


				#region Creates ObjectManager Enterprise Menu item
				try
				{
					CommandBar oCommandBar = ((CommandBars)_applicationObject.CommandBars)[TOOLS];

					oPopup = (CommandBarPopup)oCommandBar.Controls.Add(MsoControlType.msoControlPopup,
											 System.Reflection.Missing.Value,
											 System.Reflection.Missing.Value,
											 1, true);
					oPopup.Caption = Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION);

				}
				catch (Exception oEx)
				{
					LoggingHelper.HandleException(oEx);
				}
				#endregion

				#region Creates submenu for Login/Logout
				this.AddSubMenu(ref loginControl, oPopup, ref loginControlHandler, 1, "Login", IMAGE_LOGIN, IMAGE_LOGIN_MASKED);
				loginControlHandler.Click += new _dispCommandBarControlEvents_ClickEventHandler(loginControlHandler_Click);
				Helper.m_cmdBarCtrlLogin = loginControl;

				this.AddSubMenu(ref logoutControl, oPopup, ref logoutControlHandler, 2, "Logout", IMAGE_LOGOUT, IMAGE_LOGOUT_MASKED);
				logoutControlHandler.Click += new _dispCommandBarControlEvents_ClickEventHandler(logoutControlHandler_Click);
				Helper.m_cmdBarCtrlLogout = logoutControl;

				#endregion

				#region Creates submenu for Connect/Disconnect
				this.AddSubMenu(ref dbConnectControl, oPopup, ref dbConnectControlHandler, 3, CONNECT, IMAGE_CONNECT, IMAGE_CONNECT_MASKED);
				dbConnectControlHandler.Click += new _dispCommandBarControlEvents_ClickEventHandler(dbConnectControlHandler_Click);
				#endregion

				#region Creates submenu for XtremeConnect
				this.AddSubMenu(ref reqConsultationControl, oPopup, ref reqConsultationControlHandler, 4, XTREME_CONNECT, IMAGE_XTREMECONNECT, IMAGE_XTREMECONNECT_MASKED);
				reqConsultationControlHandler.Click += new _dispCommandBarControlEvents_ClickEventHandler(reqConsultationControlHandler_Click);
				#endregion

				#region Creates submenu for Support Cases
				this.AddSubMenu(ref salesForceControl, oPopup, ref salesForceControlHandler, 5, SUPPORT_CASES, IMAGE_SUPPORTCASES, IMAGE_SUPPORTCASES_MASKED);
				salesForceControlHandler.Click += new _dispCommandBarControlEvents_ClickEventHandler(salesForceControlHandler_Click);
				#endregion

				#region Creates submenu for Maintainance
				CommandBarPopup oPopupMaintainance = (CommandBarPopup)oPopup.Controls.Add(MsoControlType.msoControlPopup,
												 System.Reflection.Missing.Value,
												 System.Reflection.Missing.Value,
												 6, true);
				oPopupMaintainance.Caption = MAINTAINANCE;
				#endregion

				#region Creates submenu for Defrag under Maintainance
				this.AddSubMenu(ref omDefragControl, oPopupMaintainance, ref omDefragControlHandler, 1, DEFRAG, string.Empty, string.Empty);
				omDefragControlHandler.Click += new _dispCommandBarControlEvents_ClickEventHandler(omDefragControlHandler_Click);
				omDefragControl.Enabled = false;
				#endregion

				#region Creates submenu for Backup under Maintainance
				this.AddSubMenu(ref omBackupControl, oPopupMaintainance, ref omBackupControlHandler, 2, BACKUP, string.Empty, string.Empty);
				omBackupControlHandler.Click += new _dispCommandBarControlEvents_ClickEventHandler(omBackupControlHandler_Click);
				omBackupControl.Enabled = false;
				#endregion

				#region Creates submenu for db4objects Homepage
				this.AddSubMenu(ref db4oControl, oPopup, ref db4oControlHandler, 6, DB4O_HOMEPAGE, string.Empty, string.Empty);
				db4oControlHandler.Click += new _dispCommandBarControlEvents_ClickEventHandler(db4oControlHandler_Click);
				#endregion

				#region Creates submenu for db4objects Developer Community
				this.AddSubMenu(ref db4oDeveloperControl, oPopup, ref db4oDeveloperControlHandler, 7, DB4O_DEVELOPER_COMMUNITY, string.Empty, string.Empty);
				db4oDeveloperControlHandler.Click += new _dispCommandBarControlEvents_ClickEventHandler(db4oDeveloperControlHandler_Click);
				#endregion

				#region Creates submenu for db4objects Downloads
				this.AddSubMenu(ref db4oDnldControl, oPopup, ref db4oDnldControlHandler, 8, DB4O_DOWNLOADS, string.Empty, string.Empty);
				db4oDnldControlHandler.Click += new _dispCommandBarControlEvents_ClickEventHandler(db4oDnldControlHandler_Click);
				#endregion

				#region Creates submenu for Help
				this.AddSubMenu(ref omHelpControl, oPopup, ref omHelpControlHandler, 9, DB4O_HELP, IMAGE_HELP, IMAGE_HELP_MASKED);
				omHelpControlHandler.Click += new _dispCommandBarControlEvents_ClickEventHandler(omHelpControlHandler_Click);
				#endregion

				#region Creates submenu for ObjectManager Enterprise About Box
				this.AddSubMenu(ref omAboutControl, oPopup, ref omAboutControlHandler, 10, ABOUT_OME, string.Empty, string.Empty);
				omAboutControlHandler.Click += new _dispCommandBarControlEvents_ClickEventHandler(omAboutControlHandler_Click);
				#endregion

				#region Creates submenu for Creating demo db
				this.AddSubMenu(ref dbCreateDemoDbControl, oPopup, ref dbCreateDemoDbControlHandler, 11, CREATE_DEMO_DB, string.Empty, string.Empty);
				dbCreateDemoDbControlHandler.Click += new _dispCommandBarControlEvents_ClickEventHandler(dbCreateDemoDbControlHandler_Click);
				dbCreateDemoDbControl.Enabled = true;
				#endregion

				#region Creates submenu for Options
				CommandBarPopup oPopupOptions = (CommandBarPopup)oPopup.Controls.Add(MsoControlType.msoControlPopup,
												   System.Reflection.Missing.Value,
												   System.Reflection.Missing.Value,
												   13, true);
				oPopupOptions.Caption = OPTIONS;
				#endregion

				#region Creates submenu for Proxy Configurations under Maintainance
				this.AddSubMenu(ref omProxyConfigControl, oPopupOptions, ref omProxyConfigHandler, 1, PROXYCONFIGURATIONS, string.Empty, string.Empty);
				omProxyConfigHandler.Click += new _dispCommandBarControlEvents_ClickEventHandler(omProxyConfigHandler_Click);
				omProxyConfigControl.Enabled = true;
				#endregion
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		void omProxyConfigHandler_Click(object CommandBarControl, ref bool Handled, ref bool CancelDefault)
		{
			try
			{
				ProxyLogin pLoginwin = new ProxyLogin();
				pLoginwin.Text = "Proxy Login Configurations";
				pLoginwin.buttonLogin.Text = "&Save";
				pLoginwin.ShowDialog();
				if (pLoginwin.DialogResult == DialogResult.OK)
				{
					dbInteraction dbint = new dbInteraction();
					ProxyAuthentication pAuth = new ProxyAuthentication();
					pAuth.Port = pLoginwin.textBoxPort.Text;
					pAuth.ProxyAddress = pLoginwin.textBoxProxy.Text;
					pAuth.UserName = pLoginwin.textBoxUserID.Text;
					pAuth.PassWord = Helper.EncryptPass(pLoginwin.textBoxPassword.Text);
					dbint.SetProxyInfo(pAuth);

				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}

		}

		#region Creating demo db handler click
		void dbCreateDemoDbControlHandler_Click(object CommandBarControl, ref bool Handled, ref bool CancelDefault)
		{
			Cursor.Current = Cursors.WaitCursor;
			//createdemo();
			CreateDemoDbMethod();

			Cursor.Current = Cursors.Default;
		}
		#endregion

		private void CreateDemoDbMethod()
		{
			try
			{
				bw = new BackgroundWorker();
				bw.WorkerSupportsCancellation = true;
				bw.WorkerReportsProgress = true;

				bw.ProgressChanged += new ProgressChangedEventHandler(bw_ProgressChanged);
				bw.DoWork += new DoWorkEventHandler(bw_DoWork);
				bw.RunWorkerCompleted += new RunWorkerCompletedEventHandler(bw_RunWorkerCompleted);

				isrunning = true;
				bw.RunWorkerAsync();

				// while(isrunning)
				for (double i = 1; i < 10000; i++)
				{
					i++;
					bw.ReportProgress((int)i * 100 / 1000);


					if (isrunning == false)
						break;

				}

			}
			catch (Exception oEx)
			{
				bw.CancelAsync();
				bw = null;
				LoggingHelper.HandleException(oEx);
			}


		}

		#region Function to create demo db
		void bw_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
		{
			OpenDemoDb();
			isrunning = false;
			_applicationObject.StatusBar.Clear();
			_applicationObject.StatusBar.Progress(false, "Creation successful", 0, 0);

			_applicationObject.StatusBar.Text = "Creation successful!";




		}
		void createdemo()
		{
			try
			{
				dbConnectControl.Enabled = false;
				omButton.Enabled = false;
				dbCreateDemoDbControl.Enabled = false;

				ViewBase.ApplicationObject = _applicationObject;

				CloseAllToolWindows();

				CreateDemoDb createDemoDbObj = new CreateDemoDb();
				dbConnectControl.Enabled = true;
				omButton.Enabled = true;
				dbCreateDemoDbControl.Enabled = true;


			}
			catch (Exception e1)
			{
				ViewBase.ApplicationObject.StatusBar.Clear();
				ViewBase.ApplicationObject.StatusBar.Animate(false, vsStatusAnimation.vsStatusAnimationBuild);
				LoggingHelper.HandleException(e1);
			}
		}
		#endregion
		void bw_DoWork(object sender, DoWorkEventArgs e)
		{
			try
			{
				_applicationObject.StatusBar.Animate(true, vsStatusAnimation.vsStatusAnimationBuild);
				createdemo();
				_applicationObject.StatusBar.Animate(false, vsStatusAnimation.vsStatusAnimationBuild);
			}
			catch (Exception oEx)
			{
				bw.CancelAsync();
				bw = null;
				LoggingHelper.HandleException(oEx);
			}

		}
		bool isrunning = true;
		BackgroundWorker bw;



		void bw_ProgressChanged(object sender, ProgressChangedEventArgs e)
		{
			try
			{
				_applicationObject.StatusBar.Progress(true, "Creating Demo database.... ", e.ProgressPercentage * 10, 10000);

			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}




		private void OpenDemoDb()
		{
			try
			{
				Assembly ThisAssembly = Assembly.GetExecutingAssembly();

				ObjectBrowserToolWin.CreateObjectBrowserToolWindow();
				ObjectBrowserToolWin.ObjBrowserWindow.Visible = true;
				Login.CreateQueryBuilderToolWindow();
				PropertyPaneToolWin.CreatePropertiesPaneToolWindow(true);
				PropertyPaneToolWin.PropWindow.Visible = true;
				dbCreateDemoDbControl.Enabled = false;
				dbConnectControl.Caption = OMControlLibrary.Common.Constants.TOOLBAR_DISCONNECT;
				((CommandBarButton)dbConnectControl).State = MsoButtonState.msoButtonDown;

				omButton.Caption = OMControlLibrary.Common.Constants.TOOLBAR_DISCONNECT;
				omButton.TooltipText = OMControlLibrary.Common.Constants.TOOLBAR_DISCONNECT;
				omButton.State = MsoButtonState.msoButtonDown;
				Stream imgageStream = ThisAssembly.GetManifestResourceStream(IMAGE_DISCONNECT);
				Stream imgageStreamMask = ThisAssembly.GetManifestResourceStream(IMAGE_DISCONNECT_MASKED);


				stdole.IPictureDisp Pic;
				stdole.IPictureDisp Mask;
				Pic = MyHost.IPictureDisp(Image.FromStream(imgageStream));
				Mask = MyHost.IPictureDisp(Image.FromStream(imgageStreamMask));
				//Pic = (stdole.StdPicture)MyHost.GetIPictureDispFromPicture(Image.FromStream(imgStreamPic));
				//Mask = (stdole.StdPicture)MyHost.GetIPictureDispFromPicture(Image.FromStream(imgStreamMask));
				omButton.Picture = (stdole.StdPicture)Pic;
				omButton.Mask = (stdole.StdPicture)Mask;


				//omButton.Picture = (stdole.StdPicture)MyHost.IPictureDisp(Image.FromStream(imgageStream));
				//omButton.Mask = (stdole.StdPicture)MyHost.IPictureDisp(Image.FromStream(imgageStreamMask));
				omBackupControl.Enabled = true;
				omDefragControl.Enabled = true;
				Helper.m_AddIn_Assembly = Assembly.GetExecutingAssembly();
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}

		}

		void logoutControlHandler_Click(object CommandBarControl, ref bool Handled, ref bool CancelDefault)
		{
			LogoutClick();
		}




		#endregion

		#region AddToolBarButton
		private void AddToolBarButton(ref CommandBarButton CommandBarButton, MsoButtonStyle Style, string Caption, string ToolTip, string ImagePath, string MaskImagePath)
		{
			try
			{
				//Assembly ThisAssembly = Assembly.GetExecutingAssembly();
				//CommandBarButton = (CommandBarButton)omToolbar.Controls.Add(MsoControlType.msoControlButton, 1, "", Type.Missing, true);
				//CommandBarButton.Caption = Caption;
				//CommandBarButton.TooltipText = ToolTip;
				//CommandBarButton.Style = Style;
				//CommandBarButton.Visible = true;
				//Stream imgageStream = ThisAssembly.GetManifestResourceStream(ImagePath);
				//CommandBarButton.Picture = (stdole.StdPicture)MyHost.GetIPictureDispFromPicture(Image.FromStream(imgageStream));

				Assembly ThisAssembly = Assembly.GetExecutingAssembly();
				CommandBarButton = (CommandBarButton)omToolbar.Controls.Add(MsoControlType.msoControlButton, 1, "", Type.Missing, true);
				CommandBarButton.Caption = Caption;
				CommandBarButton.TooltipText = ToolTip;
				CommandBarButton.Style = Style;
				CommandBarButton.Visible = true;
				//Stream imageStream = ThisAssembly.GetManifestResourceStream(ImagePath);
				//CommandBarButton.Picture = (stdole.StdPicture)MyHost.GetIPictureDispFromPicture(Image.FromStream(imgageStream));
				System.IO.Stream imgStreamPic = ThisAssembly.GetManifestResourceStream(ImagePath);
				System.IO.Stream imgStreamMask = ThisAssembly.GetManifestResourceStream(MaskImagePath);
				//MyHost ax = new MyHost();               
				stdole.IPictureDisp Pic;
				stdole.IPictureDisp Mask;
				Pic = MyHost.IPictureDisp(Image.FromStream(imgStreamPic));
				Mask = MyHost.IPictureDisp(Image.FromStream(imgStreamMask));
				//Pic = (stdole.StdPicture)MyHost.GetIPictureDispFromPicture(Image.FromStream(imgStreamPic));
				//Mask = (stdole.StdPicture)MyHost.GetIPictureDispFromPicture(Image.FromStream(imgStreamMask));
				CommandBarButton.Picture = (stdole.StdPicture)Pic;
				CommandBarButton.Mask = (stdole.StdPicture)Mask;
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		#endregion

		#region CreateToolBar
		private void CreateToolBar()
		{
			try
			{
				if (cmd == null)
				{
					object[] contextGUIDS = new object[] { };

					Commands2 cmds = (Commands2)_applicationObject.Commands;
					try
					{
						cmd = cmds.AddNamedCommand2(_addInInstance, Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION), Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION), Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION), true, 2073, ref contextGUIDS, (int)vsCommandStatus.vsCommandStatusEnabled | (int)vsCommandStatus.vsCommandStatusSupported, (int)vsCommandStyle.vsCommandStylePict, vsCommandControlType.vsCommandControlTypeButton);
					}
					catch (Exception oEx)
					{
						oEx.ToString();
					}

					this.AddToolBarButton(ref loginButton, MsoButtonStyle.msoButtonIconAndCaption, "Login", "Login", IMAGE_LOGIN, IMAGE_LOGIN_MASKED);
					loginButton.Click += new _CommandBarButtonEvents_ClickEventHandler(loginButton_Click);
					Helper.m_cmdBarBtnLogin = loginButton;
					loginButton.Enabled = false;

					this.AddToolBarButton(ref logoutButton, MsoButtonStyle.msoButtonIconAndCaption, "Logout", "Logout", IMAGE_LOGOUT, IMAGE_LOGOUT_MASKED);
					logoutButton.Click += new _CommandBarButtonEvents_ClickEventHandler(logoutButton_Click);
					Helper.m_cmdBarBtnLogout = logoutButton;
					logoutButton.Enabled = false;

					CommandBarButton labelButton = null;
					labelButton = (CommandBarButton)omToolbar.Controls.Add(MsoControlType.msoControlButton, 1, "", Type.Missing, true);

					//if (Helper.CheckFeaturePermission("QueryBuilder"))
					//    labelButton.Caption = "Status : " + STATUS_FULLFUNCTIONALITYMODE;
					//else
					//    labelButton.Caption = "Status : " + STATUS_REDUCEDMODE;

					labelButton.Style = MsoButtonStyle.msoButtonCaption;
					labelButton.Visible = true;
					labelButton.State = MsoButtonState.msoButtonUp;

					labelButton.BeginGroup = true;
					Helper.m_statusLabel = labelButton;

					this.AddToolBarButton(ref omButton, MsoButtonStyle.msoButtonIcon, CONNECT, CONNECT, IMAGE_CONNECT, IMAGE_CONNECT_MASKED);
					omButton.Click += new _CommandBarButtonEvents_ClickEventHandler(omButton_Click);
					omButton.BeginGroup = true;

					this.AddToolBarButton(ref reqConsultationControlButton, MsoButtonStyle.msoButtonIcon, XTREME_CONNECT, XTREME_CONNECT, IMAGE_XTREMECONNECT, IMAGE_XTREMECONNECT_MASKED);
					reqConsultationControlButton.Click += new _CommandBarButtonEvents_ClickEventHandler(reqConsultationControlButton_Click);
					reqConsultationControlButton.BeginGroup = true;

					this.AddToolBarButton(ref salesForceControlButton, MsoButtonStyle.msoButtonIcon, SUPPORT_CASES, SUPPORT_CASES, IMAGE_SUPPORTCASES, IMAGE_SUPPORTCASES_MASKED);
					salesForceControlButton.Click += new _CommandBarButtonEvents_ClickEventHandler(salesForceControlButton_Click);
					salesForceControlButton.BeginGroup = true;

					this.AddToolBarButton(ref db4oHelpControlButton, MsoButtonStyle.msoButtonIcon, DB4O_HELP, DB4O_HELP, IMAGE_HELP, IMAGE_HELP_MASKED);
					db4oHelpControlButton.Click += new _CommandBarButtonEvents_ClickEventHandler(db4oHelpControlButton_Click);
					db4oHelpControlButton.BeginGroup = true;

				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}

		}

		public void StatusBarText()
		{
			try
			{
				EnvDTE.StatusBar sBar = _applicationObject.StatusBar;


				if (!loginButton.Enabled)
				{
					if (Helper.CheckFeaturePermission("QueryBuilder"))
						sBar.Text = "Status : " + STATUS_FULLFUNCTIONALITYMODE;
					else
						sBar.Text = "Status : " + STATUS_REDUCEDMODELOGGEDIN;
				}
				else //logged out so no functionality
					sBar.Text = "Status : " + STATUS_LOGGEDOUT;

				//if (loginButton.Enabled)
				//    sBar.Text = "Status : " 
				//if (logoutButton.Enabled)
				//    sBar.Text = "Status : " 
			}
			catch (Exception)
			{
				// LoggingHelper.HandleException(oEx);
			}
		}
		void logoutButton_Click(CommandBarButton Ctrl, ref bool CancelDefault)
		{
			LogoutClick();
		}

		private void LogoutClick()
		{
			Assembly ThisAssembly = Assembly.GetExecutingAssembly();
			DialogResult res = MessageBox.Show(Helper.GetResourceString(OMControlLibrary.Common.Constants.CONFIRMATION_MSG_LOGOUT), Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION), MessageBoxButtons.YesNo, MessageBoxIcon.Question);
			if (res == DialogResult.Yes)
			{
				if (Helper.ReleaseSeat())
				{
					Helper.ChangeLogoutToLogin();
					Helper.ResponseTicket = null;
					CustomCookies cookies = new CustomCookies();
					cookies.SetCookies(string.Empty);
					Helper.CheckForIfAlreadyLoggedIn = false;
					StatusBarText();
				}

			}
		}
		#endregion

		#region ConnectToDatabaseOrServer
		private void ConnectToDatabaseOrServer(CommandBarControl Ctrl)
		{
			try
			{

				Assembly ThisAssembly = Assembly.GetExecutingAssembly();

				if (Ctrl.Caption.Equals(CONNECT))
				{
					ViewBase.ApplicationObject = _applicationObject;
					try
					{
						if (Helper.LoginToolWindow == null || Helper.LoginToolWindow.Visible == false)
						{
							Login.CreateLoginToolWindow(Ctrl, omButton, ThisAssembly, omDefragControl, omBackupControl, dbCreateDemoDbControl);
						}
					}
					catch (Exception) { }
				}
				else
				{
					SaveData();

					Ctrl.Caption = CONNECT;
					((CommandBarButton)Ctrl).State = MsoButtonState.msoButtonUp;
					omButton.Caption = CONNECT;
					omButton.TooltipText = CONNECT;
					omButton.State = MsoButtonState.msoButtonUp;
					try
					{
						Stream imgageStream = ThisAssembly.GetManifestResourceStream(IMAGE_CONNECT);
						Stream imgageStreamMask = ThisAssembly.GetManifestResourceStream(IMAGE_CONNECT_MASKED);
						((CommandBarButton)Ctrl.Control).Picture = (stdole.StdPicture)MyHost.IPictureDisp(Image.FromStream(imgageStream));
						((CommandBarButton)Ctrl.Control).Mask = (stdole.StdPicture)MyHost.IPictureDisp(Image.FromStream(imgageStreamMask));
					}
					catch (Exception) { }
					try
					{
						Stream imgageStream = ThisAssembly.GetManifestResourceStream(IMAGE_CONNECT);
						Stream imgageStreamMask = ThisAssembly.GetManifestResourceStream(IMAGE_CONNECT_MASKED);
						omButton.Picture = (stdole.StdPicture)MyHost.IPictureDisp(Image.FromStream(imgageStream));
						omButton.Mask = (stdole.StdPicture)MyHost.IPictureDisp(Image.FromStream(imgageStreamMask));
					}
					catch (Exception) { }

					dbCreateDemoDbControl.Enabled = true;
					omDefragControl.Enabled = false;
					omBackupControl.Enabled = false;

					CloseAllToolWindows();
					Helper.DbInteraction.SetCurrentRecentConnection(null);

				}

			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}
		#endregion

		#region ConnectToDatabaseOrServer
		private void ConnectToDatabaseOrServer(CommandBarButton Ctrl)
		{
			try
			{

				Assembly ThisAssembly = Assembly.GetExecutingAssembly();
				if (Ctrl.Caption.Equals(CONNECT))
				{
					ViewBase.ApplicationObject = _applicationObject;
					if (Helper.LoginToolWindow == null || Helper.LoginToolWindow.Visible == false)
					{
						Login.CreateLoginToolWindow(dbConnectControl, Ctrl, ThisAssembly, omDefragControl, omBackupControl, dbCreateDemoDbControl);

					}
				}
				else
				{
					SaveData();
					try
					{
						Stream imgageStream = ThisAssembly.GetManifestResourceStream(IMAGE_CONNECT);
						Stream imgageStreamMask = ThisAssembly.GetManifestResourceStream(IMAGE_CONNECT_MASKED);
						((CommandBarButton)dbConnectControl.Control).Picture = (stdole.StdPicture)MyHost.IPictureDisp(Image.FromStream(imgageStream));
						((CommandBarButton)dbConnectControl.Control).Mask = (stdole.StdPicture)MyHost.IPictureDisp(Image.FromStream(imgageStreamMask));
					}
					catch (Exception) { }
					try
					{
						Stream imgageStream = ThisAssembly.GetManifestResourceStream(IMAGE_CONNECT);
						Stream imgageStreamMask = ThisAssembly.GetManifestResourceStream(IMAGE_CONNECT_MASKED);
						Ctrl.Picture = (stdole.StdPicture)MyHost.IPictureDisp(Image.FromStream(imgageStream));
						Ctrl.Mask = (stdole.StdPicture)MyHost.IPictureDisp(Image.FromStream(imgageStreamMask));
					}
					catch (Exception) { }

					Ctrl.Caption = CONNECT;
					Ctrl.TooltipText = CONNECT;
					Ctrl.State = MsoButtonState.msoButtonUp;
					dbConnectControl.Caption = CONNECT;
					((CommandBarButton)dbConnectControl).State = MsoButtonState.msoButtonUp;
					dbCreateDemoDbControl.Enabled = true;
					omDefragControl.Enabled = false;
					omBackupControl.Enabled = false;

					CloseAllToolWindows();
					Helper.DbInteraction.SetCurrentRecentConnection(null);


				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}

		}

		private static void SaveData()
		{
			try
			{
				if (Helper.HashClassGUID != null)
				{
					// string winCaption = Window.Caption;
					IDictionaryEnumerator eNum = Helper.HashClassGUID.GetEnumerator();

					if (eNum != null)
					{
						while (eNum.MoveNext())
						{

							string enumwinCaption = eNum.Key.ToString();
							int index = enumwinCaption.LastIndexOf(',');
							string strClassName = enumwinCaption.Remove(0, index);

							string str = enumwinCaption.Remove(index);

							index = str.IndexOf('.');
							string caption = str.Remove(0, index + 1) + strClassName;


							dbDataGridView db = ListofModifiedObjects.Instance[enumwinCaption] as dbDataGridView;
							if (db != null)
							{
								bool check = false;
								DialogResult dialogRes = DialogResult.Ignore;
								bool checkforValueChanged = false;
								ListofModifiedObjects.SaveBeforeWindowHiding(ref check, ref dialogRes, ref checkforValueChanged, caption, db, -1);
								ListofModifiedObjects.Instance.Remove(enumwinCaption);
							}

						}
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}
		#endregion



		#region CloseAllToolWindows
		private void CloseAllToolWindows()
		{
			try
			{
				// Helper.ResponseTicket = null;
				//Helper.SetAppSettingForToolWindows(); // set 'reset toolwin seting' to false
				RecentQueries recQueries = Helper.DbInteraction.GetCurrentRecentConnection();
				if (recQueries != null)
				{
					Helper.DbInteraction.closedb(recQueries);
				}
				Helper.ClearAllCachedAttributes();


				if (windb4oHome != null)
					windb4oHome.Close(vsSaveChanges.vsSaveChangesNo);
				if (windb4oDownloads != null)
					windb4oDownloads.Close(vsSaveChanges.vsSaveChangesNo);
				if (winSupportdb4o != null)
					winSupportdb4o.Close(vsSaveChanges.vsSaveChangesNo);
				if (winRequestConsultation != null)
					winRequestConsultation.Close(vsSaveChanges.vsSaveChangesNo);
				if (winHelp != null)
					winHelp.Close(vsSaveChanges.vsSaveChangesNo);
				if (windb4oDeveloper != null)
					windb4oDeveloper.Close(vsSaveChanges.vsSaveChangesNo);
				if (Helper.winSalesPage != null)
					Helper.winSalesPage.Close(vsSaveChanges.vsSaveChangesNo);
				try
				{
					if (oPopup != null)
					{
						oPopup.Controls[Helper.GetResourceString(OMControlLibrary.Common.Constants.DB4O_BROWSER_CAPTION)].Delete(null);
						omObjectBrowserControl = null;
						oPopup.Controls[Helper.GetResourceString(OMControlLibrary.Common.Constants.QUERY_BUILDER_CAPTION)].Delete(null);
						omQueryBuilderControl = null;
						oPopup.Controls[Helper.GetResourceString(OMControlLibrary.Common.Constants.PROPERTIES_TAB_CAPTION).Trim()].Delete(null);
						omPropertiesControl = null;
					}
				}
				catch (System.ArgumentException)
				{
				}
				ToolWindows tw = _applicationObject.ToolWindows;

				foreach (Window w in tw.DTE.Windows)
				{
					if (w.Type == vsWindowType.vsWindowTypeToolWindow)
					{
						if (CheckIfWinISVSWin(w))
						{
							if (w.Object is QueryResult || w.Object == null)
							{
								w.Caption = "Closed";
								w.Close(vsSaveChanges.vsSaveChangesNo);
							}
						}
					}
				}

			}
			catch (System.Runtime.InteropServices.COMException)
			{
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}
		#endregion

		#region Method for checking if window is a VS window
		private bool CheckIfWinISVSWin(Window w)
		{
			if (!w.Caption.Contains("Bookmarks") && !w.Caption.Equals("Properties") && !w.Caption.Contains("Object Browser")
			&& !w.Caption.Contains("Document Outline") && !w.Caption.Contains("Code Definition Window")
			&& !w.Caption.Contains("Resource View") && !w.Caption.Contains("Object Test Bench") && !w.Caption.Contains("Output")
			&& !w.Caption.Contains("Find") && !w.Caption.Contains("Class View") && !w.Caption.Contains("Macro Explorer") && !w.Caption.Contains("Start Page")
			&& !w.Caption.Contains("Watch") && !w.Caption.Contains("Autos") && !w.Caption.Contains("Locals") && !w.Caption.Contains("Threads") && !w.Caption.Contains("Call Stack")
			&& !w.Caption.Contains("Immediate") && !w.Caption.Contains("Breakpoints") && !w.Caption.Contains("Script Explorer") && !w.Caption.Contains("Modules")
			&& !w.Caption.Contains("Processes") && !w.Caption.Contains("Memory") && !w.Caption.Contains("Disassembly") && !w.Caption.Contains("Registers") && !w.Caption.Contains("Server Explorer")
			&& !w.Caption.Contains("Solution Explorer") && !w.Caption.Contains("Error List") && !w.Caption.Contains("Task List") && !w.Caption.Contains("Toolbox") && !w.Caption.Contains("Command")
			&& !w.Caption.Contains("Property Manager") && !w.Caption.Contains("Web Browser") && !w.Caption.Contains("Data Sources"))

				return true;
			else
				return false;

		}
		#endregion




		#region DisplayInadequatePermissions

		private void DisplayInadequatePermissions()
		{
			//Helper.CheckForIfAlreadyLoggedIn = false;
			string filepath = string.Empty;
			try
			{
				filepath = Assembly.GetExecutingAssembly().CodeBase.Remove(0, 8);
				int index = filepath.LastIndexOf('/');
				filepath = filepath.Remove(index);
				filepath = filepath + CONTACT_SALES;

				if (Helper.winSalesPage == null || Helper.winSalesPage.Visible == false)
				{
					Helper.winSalesPage = _applicationObject.DTE.ItemOperations.Navigate(filepath, vsNavigateOptions.vsNavigateOptionsNewWindow);
				}
				else
					Helper.winSalesPage.Visible = true;
			}
			catch
			{
				Helper.winSalesPage = _applicationObject.DTE.ItemOperations.Navigate(filepath, vsNavigateOptions.vsNavigateOptionsNewWindow);
			}
		}
		#endregion

		#region LoginClick
		private void LoginClick()
		{
			try
			{
				Assembly ThisAssembly = Assembly.GetExecutingAssembly();
				bool check = Helper.InvokeDDNForm();
				if (!check)
				{
					string filepath = Assembly.GetExecutingAssembly().CodeBase.Remove(0, 8); ;


					int index = filepath.LastIndexOf(CHAR_FORWORD_SLASH);
					filepath = filepath.Remove(index);
					filepath = filepath + OMControlLibrary.Common.Constants.OBJECTMANAGER_CONTACT_US_FILE_PATH;
					try
					{
						if (Helper.winSalesPage == null || Helper.winSalesPage.Visible != true)
							Helper.winSalesPage = _applicationObject.DTE.ItemOperations.Navigate(filepath, vsNavigateOptions.vsNavigateOptionsNewWindow);
						else
							Helper.winSalesPage.Visible = true;
					}
					catch
					{
						Helper.winSalesPage = _applicationObject.DTE.ItemOperations.Navigate(filepath, vsNavigateOptions.vsNavigateOptionsNewWindow);
					}
				}
				else
				{
					StatusBarText();
				}
				//}
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		#endregion

		#region OpenOMEAboutBox
		private static void OpenOMEAboutBox()
		{
			using (AboutOME objectAbout = new AboutOME(Regex.Replace(DataLayerCommon.Db4oVersion, @"(.*?)\s(?<version>.*)", @"${version}")))
			{
				objectAbout.ShowDialog();
			}
		}
		#endregion

		#region Opendb4oDeveloper
		private void Opendb4oDeveloper()
		{
			try
			{
				if (windb4oDeveloper == null || windb4oDeveloper.Visible == false)
					windb4oDeveloper = _applicationObject.DTE.ItemOperations.Navigate(URL_DB4O_DEVELOPER, vsNavigateOptions.vsNavigateOptionsNewWindow);
				else
					windb4oDeveloper.Visible = true;

			}
			catch
			{
				windb4oDeveloper = _applicationObject.DTE.ItemOperations.Navigate(URL_DB4O_DEVELOPER, vsNavigateOptions.vsNavigateOptionsNewWindow);
			}
		}
		#endregion

		#region BackupDatabase
		private void BackupDatabase()
		{
			try
			{
				omDefragControl.Enabled = false;
				omBackupControl.Enabled = false;
				Backup backUp = new Backup();
				backUp.BackUpDataBase();
				omBackupControl.Enabled = true;
				omDefragControl.Enabled = true;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		#endregion

		#region Background process for Defrag
		bool isrunningDefrag = true;

		void bwForDefrag_RunWorkerCompleted(object sender, RunWorkerCompletedEventArgs e)
		{
			try
			{
				isrunningDefrag = false;
				ObjectBrowserToolWin.CreateObjectBrowserToolWindow();
				ObjectBrowserToolWin.ObjBrowserWindow.Visible = true;
				Login.CreateQueryBuilderToolWindow();
				PropertyPaneToolWin.CreatePropertiesPaneToolWindow(true);
				PropertyPaneToolWin.PropWindow.Visible = true;
				MessageBox.Show("Defragment successful!", Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION));
				_applicationObject.StatusBar.Progress(false, "Defragment successful", 0, 0);
				ObjectBrowser.Instance.Enabled = true;
				PropertiesTab.Instance.Enabled = true;
				QueryBuilder.Instance.Enabled = true;

			}
			catch (Exception ex)
			{
				LoggingHelper.HandleException(ex);
			}
		}


		void bwForDefrag_DoWork(object sender, DoWorkEventArgs e)
		{
			try
			{
				dbConnectControl.Enabled = false;
				omButton.Enabled = false;
				omDefragControl.Enabled = false;
				omBackupControl.Enabled = false;
				_applicationObject.StatusBar.Animate(true, vsStatusAnimation.vsStatusAnimationBuild);
				ThreadForDefrag();
				dbConnectControl.Enabled = true;
				omButton.Enabled = true;
				omDefragControl.Enabled = true;
				omBackupControl.Enabled = true;
				_applicationObject.StatusBar.Animate(false, vsStatusAnimation.vsStatusAnimationBuild);
			}
			catch (Exception ex)
			{
				bwForDefrag.CancelAsync();
				bwForDefrag = null;
				ViewBase.ApplicationObject.StatusBar.Clear();
				ViewBase.ApplicationObject.StatusBar.Animate(false, vsStatusAnimation.vsStatusAnimationBuild);
				LoggingHelper.HandleException(ex);
			}

		}



		void bwForDefrag_ProgressChanged(object sender, ProgressChangedEventArgs e)
		{
			_applicationObject.StatusBar.Progress(true, "Defragmenting database.... ", e.ProgressPercentage * 10, 10000);
		}

		#endregion

		#region DefragDatabase

		BackgroundWorker bwForDefrag;
		private void DefragDatabase()
		{
			try
			{
				string strShowMessage = Helper.GetResourceString(OMControlLibrary.Common.Constants.CONFIRMATION_MSG_DEFRAG1) + Environment.NewLine
							  + Helper.GetResourceString(OMControlLibrary.Common.Constants.CONFIRMATION_MSG_DEFRAG2);

				DialogResult dialogRes = MessageBox.Show(strShowMessage, Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION), MessageBoxButtons.YesNo, MessageBoxIcon.Question);

				if (dialogRes == DialogResult.Yes)
				{
					ObjectBrowser.Instance.Enabled = false;
					PropertiesTab.Instance.Enabled = false;
					QueryBuilder.Instance.Enabled = false;
					bwForDefrag = new BackgroundWorker();
					bwForDefrag.WorkerReportsProgress = true;
					bwForDefrag.WorkerSupportsCancellation = true;
					bwForDefrag.ProgressChanged += new ProgressChangedEventHandler(bwForDefrag_ProgressChanged);
					bwForDefrag.DoWork += new DoWorkEventHandler(bwForDefrag_DoWork);
					bwForDefrag.RunWorkerCompleted += new RunWorkerCompletedEventHandler(bwForDefrag_RunWorkerCompleted);


					isrunningDefrag = true;
					bwForDefrag.RunWorkerAsync();
					for (double i = 1; i < 10000; i++)
					{
						i++;
						bwForDefrag.ReportProgress((int)i * 100 / 1000);
						if (isrunningDefrag == false)
							break;

					}


				}
				else
				{
					return;
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
				return;
			}
		}


		void ThreadForDefrag()
		{
			RecentQueries recConn = Helper.DbInteraction.GetCurrentRecentConnection();
			string strDatabaseLocation = recConn.ConnParam.Connection;
			CloseAllToolWindows();
			Defragmentdb4oData d = new Defragmentdb4oData(strDatabaseLocation);
		}
		#endregion

		#region OpenHelp
		private void OpenHelp()
		{
			string filepath = string.Empty; ;
			try
			{
				//Attach Html file
				filepath = Assembly.GetExecutingAssembly().CodeBase.Remove(0, 8);
				int index = filepath.LastIndexOf(CHAR_FORWORD_SLASH);
				filepath = filepath.Remove(index);
				filepath = filepath + FAQ;
				if (winHelp == null || winHelp.Visible == false)
				{

					winHelp = _applicationObject.DTE.ItemOperations.Navigate(filepath, vsNavigateOptions.vsNavigateOptionsNewWindow);
				}
				else
					winHelp.Visible = true;

			}
			catch
			{
				winHelp = _applicationObject.DTE.ItemOperations.Navigate(filepath, vsNavigateOptions.vsNavigateOptionsNewWindow);
			}
		}
		#endregion

		#region SupportCases
		private void SupportCases()
		{
			try
			{
				ConnectToSupport();
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		private void ConnectToSupport()
		{
			try
			{
				if (winSupportdb4o == null || winSupportdb4o.Visible == false)
					winSupportdb4o = _applicationObject.DTE.ItemOperations.Navigate("https://customer.db4o.com/Support/Default.aspx", vsNavigateOptions.vsNavigateOptionsNewWindow);
				else
					winSupportdb4o.Visible = true;
			}
			catch
			{
				winSupportdb4o = _applicationObject.DTE.ItemOperations.Navigate("https://customer.db4o.com/Support/Default.aspx", vsNavigateOptions.vsNavigateOptionsNewWindow);
			}
		}

		#endregion

		#region XtremeConnect
		private void XtremeConnect()
		{
			try
			{
				ConnectToXtremeConnect();
			}
			catch (Exception oEx)
			{
				LoggingHelper.HandleException(oEx);
			}
		}

		private void ConnectToXtremeConnect()
		{
			try
			{
				if (winRequestConsultation == null || winRequestConsultation.Visible == false)
					winRequestConsultation = _applicationObject.DTE.ItemOperations.Navigate("https://customer.db4o.com/Peer/Default.aspx", vsNavigateOptions.vsNavigateOptionsNewWindow);
				else
					winRequestConsultation.Visible = true;
			}
			catch
			{
				winRequestConsultation = _applicationObject.DTE.ItemOperations.Navigate("https://customer.db4o.com/Peer/Default.aspx", vsNavigateOptions.vsNavigateOptionsNewWindow);
			}
		}
		#endregion

		#region Opendb4oDownloads
		private void Opendb4oDownloads()
		{
			try
			{
				if (windb4oDownloads == null || windb4oDownloads.Visible == false)
					windb4oDownloads = _applicationObject.DTE.ItemOperations.Navigate(URL_DB4O_DOWNLOADS, vsNavigateOptions.vsNavigateOptionsNewWindow);
				else
					windb4oDownloads.Visible = true;

			}
			catch
			{
				windb4oDownloads = _applicationObject.DTE.ItemOperations.Navigate(URL_DB4O_DOWNLOADS, vsNavigateOptions.vsNavigateOptionsNewWindow);
			}
		}
		#endregion

		#region Opendb4oHomepage
		private void Opendb4oHomepage()
		{
			try
			{
				if (windb4oHome == null || windb4oHome.Visible == false)
					windb4oHome = _applicationObject.DTE.ItemOperations.Navigate(URL_DB4O_HOMEPAGE, vsNavigateOptions.vsNavigateOptionsNewWindow);
				else
					windb4oHome.Visible = true;
			}
			catch
			{
				windb4oHome = _applicationObject.DTE.ItemOperations.Navigate(URL_DB4O_HOMEPAGE, vsNavigateOptions.vsNavigateOptionsNewWindow);
			}
		}

		#endregion

		#endregion
	}
}
