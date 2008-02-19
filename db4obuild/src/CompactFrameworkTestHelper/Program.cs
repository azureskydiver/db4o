using System;
using System.Diagnostics;
using System.Globalization;
using System.IO;
using System.Reflection;
using System.Threading;
using Microsoft.SmartDevice.Connectivity;
using Microsoft.DeviceEmulatorManager.Interop;
using Microsoft.Win32;

namespace CompactFrameworkTestHelper
{
	/**
	 * This program starts the device emulator, deploys Db4objects.Db4o.Tests,
	 * starts the tests and finally waits these tests to complete.
	 */
	class Program
	{
		private const int DoNotSaveState = 0;
		private static readonly string DeviceTestPath = "/Temp/";

		private static readonly int ERROR_BASE = 0;
		private static readonly int EXCEPTION_RUNNING_TESTS = ERROR_BASE - 1;
		private static readonly int FAILED_LAUNCHING_TESTS = ERROR_BASE - 2;
		private static readonly int INVALID_PROGRAM_PARAMETERS = ERROR_BASE - 3;

		static int Main(string[] args)
		{
			int targetFrameworkVersion;
			if (!ValidateArguments(args, out targetFrameworkVersion))
			{
				return INVALID_PROGRAM_PARAMETERS;
			}

			int ret;
			try
			{
				Console.WriteLine("CompactFrameworkTestHelper - Copyright (C) 2004-2008  db4objects Inc.\r\n");

				StartEmulator(targetFrameworkVersion);

				Device device = EmulatorHelper.GetDevice();
				device.Connect();

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

		private static bool ValidateArguments(string[] args, out int version)
		{
			version = 0;
			if (args.Length != 1)
			{
				Console.WriteLine("Invalid program parameter count.\r\n" +
				                  "Use: {0} <version> (2.0 | 3.5)", Assembly.GetExecutingAssembly().GetName().Name);

				return false;
			}

			float tmpVersion;
			if (Single.TryParse(args[0], NumberStyles.AllowDecimalPoint, NumberFormatInfo.InvariantInfo, out tmpVersion) && (tmpVersion == 2.0 || tmpVersion == 3.5)) 
			{
				version = (int) (tmpVersion*10);
				return true;
			}

			Console.WriteLine("Version must be either 2.0 or 3.5");
			return false;
		}

		private static void StartEmulator(int targetFrameworkVersion)
		{
			string deviceEmulatorPath = BuildDeviceEmulatorPath();
			string imageFilePath = BuildSourceImageFile(targetFrameworkVersion);

			if (!File.Exists(imageFilePath))
			{
				throw new FileNotFoundException("Device Emulator image.", imageFilePath);
			}

			Process.Start(deviceEmulatorPath, String.Format("/s {0} /nosecurityprompt", imageFilePath));
			Thread.Sleep(8000); // let's give the process some time to finish its startup...

			/*string targetImageFile = BuildTargetImageFile();

			if (File.Exists(targetImageFile))
			{
				File.Delete(targetImageFile);	
			}

			if (IsSameVolume(sourceImageFile, targetImageFile))
			{
				CreateHardLink(targetImageFile, sourceImageFile, IntPtr.Zero);
			}
			else
			{
				File.Copy(sourceImageFile, targetImageFile);
			}*/
		}

		private static string BuildDeviceEmulatorPath()
		{
			string deviceEmulatorCLSID = (string) Registry.ClassesRoot.OpenSubKey(@"DEMComInterface.DeviceEmulatorManager\CLSID").GetValue("");
			string deviceEmulatorManagerPath = (string) Registry.ClassesRoot.OpenSubKey(String.Format(@"CLSID\{0}\LocalServer32", deviceEmulatorCLSID)).GetValue("");

			deviceEmulatorManagerPath = deviceEmulatorManagerPath.Replace("\"", "");

			return Path.Combine(Path.GetDirectoryName(deviceEmulatorManagerPath), "DeviceEmulator.exe");
		}

		/*private static bool IsSameVolume(string file1, string file2)
		{
			return Path.GetPathRoot(file1) == Path.GetPathRoot(file2);
		}

		private static string BuildTargetImageFile()
		{
			string imageFilesFolder = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData);
			imageFilesFolder = Path.Combine(imageFilesFolder, "Microsoft/Device Emulator");

			return Path.Combine(imageFilesFolder, EmulatorHelper.DEVICE_ID + ".dess");
		}*/

		private static string BuildSourceImageFile(int targetFrameworkVersion)
		{
			String partialPath = String.Format(@"Images\{0}.{1}.dess", EmulatorHelper.DEVICE_ID, targetFrameworkVersion);
			return Path.Combine(Path.GetDirectoryName(Assembly.GetExecutingAssembly().Location), partialPath);
		}
	}
}