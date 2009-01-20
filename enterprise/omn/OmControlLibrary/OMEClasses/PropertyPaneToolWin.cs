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
				string assemblypath = Assembly.GetExecutingAssembly().CodeBase.Remove(0, 8);
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
				
				//FIXME: Always get index 1? What happens if this addins is not OMN?
				AddIn addinobj = ViewBase.ApplicationObject.AddIns.Item(1);
				EnvDTE80.Windows2 wins2obj = (Windows2)ViewBase.ApplicationObject.Windows;

				object ctlobj = null;
				PropWindow = wins2obj.CreateToolWindow2(addinobj, assemblypath, className, caption, guidpos, ref ctlobj);
				SetTabPicture(PropWindow, Common.Constants.DB4OICON);

				if (PropWindow.AutoHides)
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
				object ctlobj = null;
				AddIn addinobj = ViewBase.ApplicationObject.AddIns.Item(1);
				EnvDTE80.Windows2 wins2obj = (Windows2)ViewBase.ApplicationObject.Windows;

				// Creates Tool Window and inserts the user control in it.
				objBrowserWindow = wins2obj.CreateToolWindow2(
										addinobj, 
										Assembly.GetExecutingAssembly().CodeBase.Remove(0, 8), 
										Common.Constants.CLASS_NAME_OBJECTBROWSER, 
										Helper.GetResourceString(Common.Constants.DB4O_BROWSER_CAPTION), 
										Common.Constants.GUID_OBJECTBROWSER, ref ctlobj);

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
