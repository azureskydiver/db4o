using System;
using EnvDTE;
using EnvDTE80;
using System.Reflection;
using OMControlLibrary.Common;
using OME.Logging.Common;

namespace OMControlLibrary
{
	public class PropertyPaneToolWin
	{
		private static Window propWindow;

		public static Window PropWindow
		{
			get
			{
				if (propWindow == null)
				{
					CreatePropertiesPaneToolWindow(false);
					PropertyPaneToolWin.propWindow.Visible = false;
				}
				return propWindow;
			}
			set
			{
				propWindow = value;
			}
		}

		public static void CreatePropertiesPaneToolWindow(bool DbDetails)
		{
			try
			{
				const string className = Common.Constants.CLASS_NAME_PROPERTIES;
				string caption;
				const string guidpos = Common.Constants.GUID_PROPERTIES;

				if (DbDetails)
				{
					caption = Helper.GetResourceString(Common.Constants.PROPERTIES_TAB_DATABASE_CAPTION);
				}
				else
				{
					caption = Helper.ClassName + Helper.GetResourceString(Common.Constants.PROPERTIES_TAB_CAPTION);
				}
				
				object ctlobj;
				PropWindow = ViewBase.CreateToolWindow(className, caption, guidpos, out ctlobj);
				SetTabPicture(PropWindow, Common.Constants.DB4OICON);

				//if (PropWindow.AutoHides)
				{
					PropWindow.AutoHides = false;
				}
				PropWindow.Visible = true;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private static void SetTabPicture(Window window, string iconResource)
		{
			window.SetTabPicture(Helper.GetResourceImage(iconResource));
		}
	}

	public class ObjectBrowserToolWin
	{
		private static Window objBrowserWindow;

		public static Window ObjBrowserWindow
		{
			get
			{
				if (objBrowserWindow == null)
				{
					CreateObjectBrowserToolWindow();
					ObjBrowserWindow.Visible = false;
				}
				return objBrowserWindow;
			}
			set
			{
				objBrowserWindow = value;
			}
		}
		public static void CreateObjectBrowserToolWindow()
		{
			try
			{
				object ctlobj;
				objBrowserWindow = ViewBase.CreateToolWindow(
										Common.Constants.CLASS_NAME_OBJECTBROWSER, 
										Helper.GetResourceString(Common.Constants.DB4O_BROWSER_CAPTION), 
										Common.Constants.GUID_OBJECTBROWSER, out ctlobj);

				objBrowserWindow.SetTabPicture(Helper.GetResourceImage(Common.Constants.DB4OICON));
				if (objBrowserWindow.AutoHides)
				{
					objBrowserWindow.AutoHides = false;
				}
				objBrowserWindow.Visible = true;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
	}
}
