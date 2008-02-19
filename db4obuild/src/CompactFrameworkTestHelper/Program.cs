using System;
using System.Collections.Generic;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Threading;
using Microsoft.DeviceEmulatorManager.Interop;
using Microsoft.SmartDevice.Connectivity;

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

		private static readonly Dictionary<int, FrameWorkInfo> _deployment;

		private const int DoNotSaveState = 0;
		private static readonly string DeviceTestPath = "/Temp/";

		private static readonly int ERROR_BASE = 0;
		private static readonly int EXCEPTION_RUNNING_TESTS = ERROR_BASE - 1;
		private static readonly int FAILED_LAUNCHING_TESTS = ERROR_BASE - 2;
		private static readonly int INVALID_PROGRAM_PARAMETERS = ERROR_BASE - 3;

		static Program()
		{
			_deployment = new Dictionary<int, FrameWorkInfo>();
			_deployment.Add(20, new FrameWorkInfo(new ObjectId(new Guid("ABD785F0-CDA7-41c5-8375-2451A7CBFF26")), "NETCFv2.ppc.armv4.cab"));
			_deployment.Add(35, new FrameWorkInfo(new ObjectId(new Guid("ABD785F0-CDA7-41c5-8375-2451A7CBFF37")), "NETCFv35.ppc.armv4.cab"));
		}

		static int Main(string[] args)
		{
			int targetFrameworkVersion;
			if (!ProcessArguments(args, out targetFrameworkVersion))
			{
				return INVALID_PROGRAM_PARAMETERS;
			}

			int ret;
			try
			{
				Console.WriteLine("CompactFrameworkTestHelper - Copyright (C) 2004-2008  db4objects Inc.\r\n");

				Device device = EmulatorHelper.GetDevice();
				device.Connect();

				DeployDotNetFramework(device, targetFrameworkVersion);

				IDeviceEmulatorManagerVMID virtualDevice = EmulatorHelper.GetVirtualDevice();

				string storageCardPath = Path.Combine(Environment.GetEnvironmentVariable("TEMP"), "StorageCard");
				EmulatorHelper.SetStorageCardPath(virtualDevice, storageCardPath);

				try
				{
					string db4oTests = "Db4objects.Db4o.Tests.exe";

					EmulatorHelper.CopyFiles(device.GetFileDeployer(), Path.Combine(storageCardPath, "*"), DeviceTestPath);

					RemoteProcess process = device.GetRemoteProcess();
					if (process.Start(DeviceTestPath + db4oTests, "run"))
					{
						while (!process.HasExited())
						{
							Thread.Sleep(2000);
						}

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

		private static bool ProcessArguments(string[] args, out int version)
		{
			version = 0;
			if (args.Length != 1)
			{
				Console.WriteLine("Invalid program parameter count.\r\n" +
				                  "Use: {0} <version> <emulator images path>\r\n\r\n" +
								  "Version must be either 2.0 or 3.5", Assembly.GetExecutingAssembly().GetName().Name);

				return false;
			}

			float tmpVersion;
			if (Single.TryParse(args[0], NumberStyles.AllowDecimalPoint, NumberFormatInfo.InvariantInfo, out tmpVersion) && (tmpVersion == 2.0 || tmpVersion == 3.5)) 
			{
				version = (int)(tmpVersion * 10);
				return true;
			}

			return false;
		}

		private static void DeployDotNetFramework(Device device, int version)
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