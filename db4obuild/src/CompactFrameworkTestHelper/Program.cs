using System;
using System.Collections.Generic;
using System.IO;
using System.Reflection;
using System.Threading;
using Microsoft.DeviceEmulatorManager.Interop;
using Microsoft.SmartDevice.Connectivity;
using CommandLine.Utility;

namespace CompactFrameworkTestHelper
{
	/**
	 * This program starts the device emulator, deploys Db4objects.Db4o.Tests,
	 * starts the tests and finally waits these tests to complete.
	 */
	class Program
	{
		private struct FrameWorkInfo
		{
			public readonly ObjectId PackageId;
			public readonly string packageFullPath;

			public FrameWorkInfo(ObjectId id, string packageFullPath)
			{
				PackageId = id;
				this.packageFullPath = packageFullPath;
			}
		}

		private static readonly Dictionary<string, FrameWorkInfo> _deployment;

		private const int DoNotSaveState = 0;
		private static readonly string DeviceTestPath = "/Temp/";

		private static readonly int ERROR_BASE = 0;
		private static readonly int EXCEPTION_RUNNING_TESTS = ERROR_BASE - 1;
		private static readonly int FAILED_LAUNCHING_TESTS = ERROR_BASE - 2;
		private static readonly int INVALID_PROGRAM_PARAMETERS = ERROR_BASE - 3;

		static Program()
		{
			_deployment = new Dictionary<string, FrameWorkInfo>();
			_deployment.Add("2.0", new FrameWorkInfo(new ObjectId(new Guid("ABD785F0-CDA7-41c5-8375-2451A7CBFF26")), "NETCFv2.ppc.armv4.cab"));
			_deployment.Add("3.5", new FrameWorkInfo(new ObjectId(new Guid("ABD785F0-CDA7-41c5-8375-2451A7CBFF37")), "NETCFv35.ppc.armv4.cab"));
		}

		static int Main(string[] args)
		{
            Arguments arguments = new Arguments(args);

            string targetFrameworkVersion = arguments["version"];
            if (targetFrameworkVersion == null)
            {
                targetFrameworkVersion = "2.0";
            }

            string db4oDistPath = arguments["dir.dll.compact"];
            if (db4oDistPath == null)
            {
                Help();
                return INVALID_PROGRAM_PARAMETERS;
            }
            
			int ret;
			try
			{
				Console.WriteLine("CompactFrameworkTestHelper - Copyright (C) 2004-2008  db4objects Inc.\r\n");

				Device device = EmulatorHelper.GetDevice();
				device.Connect();

				DeployDotNetFramework(device, targetFrameworkVersion);

				try
				{
					string db4oTests = "Db4objects.Db4o.Tests.exe";

                    EmulatorHelper.CopyFiles(device.GetFileDeployer(), Path.Combine(db4oDistPath, "*"), DeviceTestPath);

					RemoteProcess process = device.GetRemoteProcess();
					if (process.Start(DeviceTestPath + db4oTests, "run"))
					{
						while (!process.HasExited())
						{
							Thread.Sleep(2000);
						}

                        EmulatorHelper.PublishTestLog(device.GetFileDeployer(), db4oDistPath);

						ret = process.GetExitCode();
						if (ret != 0)
						{
							Console.WriteLine("{0} returned: {1}", db4oTests, ret);
						}
					}
					else
					{
						ret = FAILED_LAUNCHING_TESTS;
					}
				}
				finally
				{
					device.Disconnect();
                    IDeviceEmulatorManagerVMID virtualDevice = EmulatorHelper.GetVirtualDevice();
					virtualDevice.Shutdown(DoNotSaveState);
				}
			}
			catch(Exception ex)
			{
				Console.WriteLine("Error running Db4objects.Db4o.Tests.exe\r\n{0}", ex);
				ret = EXCEPTION_RUNNING_TESTS;
			}

			return ret;
		}

		private static void Help()
		{
		    Console.WriteLine("Invalid program parameter count.\r\n" +
		                        "Use: {0} [-version]=[2.0 | 3.5] <-dir.dll.compact>=<path to db4o .NET Compact Framework distribution> \r\n\r\n", 
                                Assembly.GetExecutingAssembly().GetName().Name);
		}

		private static void DeployDotNetFramework(Device device, string version)
		{
			FileDeployer fd = device.GetFileDeployer();

			FrameWorkInfo info = _deployment[version];
			fd.DownloadPackage(info.PackageId);

			RemoteProcess installer = device.GetRemoteProcess();
			installer.Start("wceload.exe", String.Format(@"/noui \windows\{0}", info.packageFullPath));
			while (installer.HasExited() != true)
			{
				Thread.Sleep(1000);
			}
		}
	}
}