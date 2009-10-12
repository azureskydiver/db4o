/* Copyright (C) 2004 - 2009  Versant Inc.  http://www.db4o.com */

using System;
using System.Collections.Generic;
using System.Reflection;
using System.Windows.Forms;
using EnvDTE;
using EnvDTE80;
using System.Runtime.InteropServices;
using OMControlLibrary.Common;
using Constants=EnvDTE.Constants;

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
			//ApplicationManager.CheckLocalAndSetLanguage();
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
		private static WindowVisibilityEvents events;
		private static readonly IDictionary<Window, bool> _omnWindows = new Dictionary<Window, bool>();

		public static Dictionary<Window, bool> PluginWindows
		{
			get
			{
				lock(_omnWindows)
				{
					return new Dictionary<Window, bool>(_omnWindows);
				}
			}
		}

		internal static Window CreateToolWindow<T>(string toolWindowClass, string caption, string guidpos, out T control)
		{
			AttachEventHandlerIfNecessary();

			string assemblypath = Assembly.GetExecutingAssembly().CodeBase.Remove(0, 8);
			Windows2 wins2obj = (Windows2)ApplicationObject.Windows;

			object ctlobj = null;
			Window window = wins2obj.CreateToolWindow2(
									FindAddin(ApplicationObject.AddIns), 
									assemblypath, 
									toolWindowClass, 
									caption, 
									guidpos, 
									ref ctlobj);

			control = (T) ctlobj;

			_omnWindows[window] = true;

			return window;
		}

       
		private static void AttachEventHandlerIfNecessary()
		{
			if (null == events)
			{
				Events2 eventsSource = (Events2) ApplicationObject.Events;
				events = eventsSource.get_WindowVisibilityEvents(null);
				events.WindowHiding += OnWindowHidding;
			}
		}

		private static void OnWindowHidding(Window window)
		{
			lock (_omnWindows)
			{
				if (_omnWindows.ContainsKey(window))
				{
					_omnWindows[window] = false;
				}
			}
		}

		public static bool IsOMNWindow(Window window)
		{
			return window != null ? _omnWindows.ContainsKey(window) : false;
		}

		private static AddIn FindAddin(AddIns addins)
		{
			foreach(AddIn addin in addins)
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
