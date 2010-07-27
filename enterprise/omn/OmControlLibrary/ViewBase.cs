/* Copyright (C) 2004 - 2009  Versant Inc.  http://www.db4o.com */

using System;
using System.Collections.Generic;
using System.Reflection;
using System.Windows.Forms;
using EnvDTE;
using EnvDTE80;
using System.Runtime.InteropServices;
using OMControlLibrary.Common;
using Constants = OMControlLibrary.Common.Constants;

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
			_currentMode = m_applicationObject.DTE.Debugger.CurrentMode;
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

		//TODO: Use the window caption as soon as we fix the dependency on Caption being equal
		//      to "Closed".
		private static WindowVisibilityEvents _events;
		private static readonly IDictionary<Window, bool> _omnWindows = new Dictionary<Window, bool>();
		private static dbgDebugMode _currentMode;
		public static Dictionary<Window, bool> PluginWindows
		{

			get
			{
				lock (_omnWindows)
				{
					return new Dictionary<Window, bool>(_omnWindows);
				}
			}

		}

		public static void ResetToolWindowList()
		{
			lock (_omnWindows)
			{
				_omnWindows.Clear();
			}
		}

		internal static Window CreateToolWindow(string toolWindowClass, string caption, string guidpos)
		{
			AttachEventHandlerIfNecessary();

			Window found = GetWindow(caption);
			if (found != null)
			{
				found.Activate();
				LoadDataForAppropriateClass(found);
				_omnWindows[found] = true;
				return found;
			}

			string assemblypath = Assembly.GetExecutingAssembly().CodeBase.Remove(0, 8);
			object ctlobj = null;

			Windows2 wins2obj = (Windows2)ApplicationObject.Windows;
			Window window = wins2obj.CreateToolWindow2(
									FindAddin(ApplicationObject.AddIns),
									assemblypath,
									toolWindowClass,
									caption,
									guidpos,
									ref ctlobj);

			_omnWindows[window] = true;
			window.Linkable = true;

			return window;
		}

		public static Window GetWindow(string caption)
		{
			foreach (KeyValuePair<Window, bool> entry in PluginWindows)
			{
				if (caption == entry.Key.Caption)
				{
					_omnWindows[entry.Key] = true;
					return entry.Key;
				}
			}
			return null;
		}

		private static void AttachEventHandlerIfNecessary()
		{
			if (null == _events)
			{
				Events2 eventsSource = (Events2)ApplicationObject.Events;
				_events = eventsSource.get_WindowVisibilityEvents(null);
				_events.WindowHiding += OnWindowHidding;

			}
		}



		private static void LoadDataForAppropriateClass(Window win)
		{
			ILoadData loaddata = null;
			switch (win.Caption)
			{
				case Constants.LOGIN:
					loaddata = win.Object as Login;
					break;
				case Constants.DB4OBROWSER:
					loaddata = win.Object as ObjectBrowser;
					break;
				case Constants.QUERYBUILDER:
					loaddata = win.Object as QueryBuilder;
					break;
				default:
					break;
			}
			if (loaddata != null)
				loaddata.LoadAppropriatedata();
		}

		private static void OnWindowHidding(Window window)
		{
			lock (_omnWindows)
			{

				if (_currentMode == window.DTE.Debugger.CurrentMode)
				{
					if (_omnWindows.ContainsKey(window))
					{
						_omnWindows[window] = false;
						switch (window.Caption)
						{
							case Constants.LOGIN:
							case Constants.QUERYBUILDER:
							case Constants.DB4OPROPERTIES:
							case Constants.DB4OBROWSER:
								break;
							default:
								Helper.SaveData();
								break;
						}
					}
				}
				_currentMode = m_applicationObject.DTE.Debugger.CurrentMode;
			}
		}

		public static bool IsOMNWindow(Window window)
		{
			return window != null ? _omnWindows.ContainsKey(window) : false;
		}

		private static AddIn FindAddin(AddIns addins)
		{
			foreach (AddIn addin in addins)
			{
				if (!string.IsNullOrEmpty(addin.Name) && addin.Name.Contains("OMAddin"))
				{
					return addin;
				}
			}

			throw new InvalidOperationException();
		}
	}
}
