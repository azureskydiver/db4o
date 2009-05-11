﻿using System;
using System.Collections.Generic;
using System.Reflection;
using System.Windows;
using Db4objects.Db4o;
using Db4objects.Db4o.Config;
using Db4objects.Db4o.IO;
using Sharpen.Lang;
using SimpleCRUD.Model;

namespace SimpleCRUD
{
	public partial class App
	{

		#region Db4o specific code

		#region Assembly.Load() issue workaround

		private static readonly IDictionary<string, Assembly> _assemblyCache = new Dictionary<string, Assembly>();
		static App()
		{
			//_assemblyCache[AssemblyNameFor(typeof(Queue<>))] = typeof(Queue<>).Assembly;
			//_assemblyCache[AssemblyNameFor(typeof(List<>))] = typeof(List<>).Assembly;
			_assemblyCache[AssemblyNameFor(typeof(Person))] = typeof(Person).Assembly;
			_assemblyCache[AssemblyNameFor(typeof(Db4oFactory))] = typeof(Db4oFactory).Assembly;
			
			TypeReference.AssemblyResolve += (sender, args) => args.Assembly = _assemblyCache[args.Name];
		}

		#endregion

		private void InitializeDatabase()
		{
			_db = Db4oEmbedded.OpenFile(Config(), DatabaseFileName);
		}

		private static IEmbeddedConfiguration Config()
		{
			IEmbeddedConfiguration config = Db4oEmbedded.NewConfiguration();
			config.File.Storage = new IsolatedStorageStorage();
			return config;
		}

		private IObjectContainer _db;
		private const string DatabaseFileName = "SimpleCRUD.odb";

		#endregion

		private static string AssemblyNameFor(Type t)
		{
			return new AssemblyName(t.Assembly.FullName).Name;
		}

		public App()
		{
			Startup += Application_Startup;
			Exit += Application_Exit;
			UnhandledException += Application_UnhandledException;

			InitializeComponent();
		}

		private void Application_Startup(object sender, StartupEventArgs e)
		{
			InitializeDatabase();
			RootVisual = new MainPage(_db);
		}

		private void Application_Exit(object sender, EventArgs e)
		{
			if (_db != null)
			{
				_db.Close();
			}
		}

		private void Application_UnhandledException(object sender, ApplicationUnhandledExceptionEventArgs e)
		{
			// If the app is running outside of the debugger then report the exception using
			// the browser's exception mechanism. On IE this will display it a yellow alert 
			// icon in the status bar and Firefox will display a script error.
			if (!System.Diagnostics.Debugger.IsAttached)
			{

				// NOTE: This will allow the application to continue running after an exception has been thrown
				// but not handled. 
				// For production applications this error handling should be replaced with something that will 
				// report the error to the website and stop the application.
				e.Handled = true;
				Deployment.Current.Dispatcher.BeginInvoke(delegate { ReportErrorToDOM(e); });
			}
		}
		
		private void ReportErrorToDOM(ApplicationUnhandledExceptionEventArgs e)
		{
			try
			{
				string errorMsg = e.ExceptionObject.Message + e.ExceptionObject.StackTrace;
				errorMsg = errorMsg.Replace('"', '\'').Replace("\r\n", @"\n");

				System.Windows.Browser.HtmlPage.Window.Eval("throw new Error(\"Unhandled Error in Silverlight Application " + errorMsg + "\");");
			}
			catch (Exception)
			{
			}
		}
	}
}
