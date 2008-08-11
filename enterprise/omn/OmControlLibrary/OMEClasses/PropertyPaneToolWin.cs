using System;
using System.Collections.Generic;
using System.Text;
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
					//propWindow
					PropertyPaneToolWin.CreatePropertiesPaneToolWindow(false);
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
				string className = Common.Constants.CLASS_NAME_PROPERTIES;
				string caption = string.Empty;
				string guidpos = Common.Constants.GUID_PROPERTIES;

				if (DbDetails)
				{
					caption = Helper.GetResourceString(Common.Constants.PROPERTIES_TAB_DATABASE_CAPTION);
				}
				else
				{
					caption = Helper.ClassName + Helper.GetResourceString(Common.Constants.PROPERTIES_TAB_CAPTION);
				}
				object ctlobj = null;
				AddIn addinobj = ViewBase.ApplicationObject.AddIns.Item(1);
				EnvDTE80.Windows2 wins2obj = (Windows2)ViewBase.ApplicationObject.Windows;

				PropertyPaneToolWin.PropWindow = wins2obj.CreateToolWindow2(addinobj, assemblypath,
									className, caption, guidpos, ref ctlobj);
				if (PropertyPaneToolWin.PropWindow.AutoHides == true)
				{
					PropertyPaneToolWin.PropWindow.AutoHides = false;
				}
				PropertyPaneToolWin.PropWindow.Visible = true;

				//PropertyPaneToolWin.PropWindow.IsFloating = false;
				//PropertyPaneToolWin.PropWindow.Linkable = false;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
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
					ObjectBrowserToolWin.CreateObjectBrowserToolWindow();
					ObjectBrowserToolWin.ObjBrowserWindow.Visible = false;
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
				// Helper.m_AddIn_Assembly = Assembly.GetExecutingAssembly().CodeBase.Remove(0, 8); 

				string assemblypath = Assembly.GetExecutingAssembly().CodeBase.Remove(0, 8);
				string className = Common.Constants.CLASS_NAME_OBJECTBROWSER;
				// string guidpos = Helper.GetClassGUID(Helper.BaseClass);
				string guidpos = Common.Constants.GUID_OBJECTBROWSER;
				string caption = Helper.GetResourceString(Common.Constants.DB4O_BROWSER_CAPTION);
				object ctlobj = null;
				AddIn addinobj = ViewBase.ApplicationObject.AddIns.Item(1);
				EnvDTE80.Windows2 wins2obj = (Windows2)ViewBase.ApplicationObject.Windows;

				// Creates Tool Window and inserts the user control in it.
				ObjectBrowserToolWin.objBrowserWindow = wins2obj.CreateToolWindow2(addinobj, assemblypath,
									className, caption, guidpos, ref ctlobj);

				//objectBrowserToolWindow.IsFloating = false;
				//objectBrowserToolWindow.Linkable = false;
				if (ObjectBrowserToolWin.objBrowserWindow.AutoHides == true)
				{
					ObjectBrowserToolWin.objBrowserWindow.AutoHides = false;
				}
				ObjectBrowserToolWin.objBrowserWindow.Visible = true;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
	}
}
