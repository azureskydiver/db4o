using System;
using System.IO;
using System.Runtime.InteropServices;
using Microsoft.DeviceEmulatorManager.Interop;
using Microsoft.SmartDevice.Connectivity;

namespace CompactFrameworkTestHelper
{
	class EmulatorHelper
	{
		private static readonly int ITERATION_FINISHED_HRESULT = -2147024637;
		public static readonly string DEVICE_ID = "{DE425A95-FBB8-46CB-8DFD-89867130F732}";
		public static readonly string POCKET_PC_PLATFORM_ID = "3C41C503-53EF-4c2a-8DD4-A8217CAD115E";

		public static void CopyFiles(FileDeployer deployer, string desktoPath, string devicePath)
		{
			foreach (string file in Directory.GetFiles(Path.GetDirectoryName(desktoPath), Path.GetFileName(desktoPath)))
			{
				deployer.SendFile(file, devicePath + Path.GetFileName(file));
			}
		}

        public static void publishTestLog(FileDeployer deployer, string path)
        {
            string logFile = Path.Combine(path, "db4ounit.log");
            deployer.ReceiveFile(@"\db4ounit.log", logFile);
            try
            {
                StreamReader reader = File.OpenText(logFile);
                string line = "";
                while ((line = reader.ReadLine()) != null)
                {
                    Console.Error.WriteLine(line);
                }
            }
            catch (FileNotFoundException)
            {
                Console.Error.WriteLine("Db4ounit.log has not been generated.");
            }
        }

		public static IDeviceEmulatorManagerVMID FindDevice(IDeviceEmulatorManager manager, string id)
		{
			try
			{
				while (true)
				{
					IDeviceEmulatorManagerVMID device = FindDevice(manager.EnumerateSDKs(), id);
					if (device != null) return device;
					manager.MoveNext();
				}
			}
			catch (COMException)
			{
			}

			return null;
		}

		private static IDeviceEmulatorManagerVMID FindDevice(IEnumManagerSDKs enumSDKs, string id)
		{
			try
			{
				while (true)
				{
					IDeviceEmulatorManagerVMID device = FindDevice(enumSDKs.EnumerateVMIDs(), id);
					if (device != null)
					{
						return device;
					}
					enumSDKs.MoveNext();
				}
			}
			catch (COMException)
			{
			}

			return null;
		}

		private static IDeviceEmulatorManagerVMID FindDevice(IEnumVMIDs deviceEnumerator, string id)
		{
			try
			{
				while (true)
				{
					IDeviceEmulatorManagerVMID virtualDevice = deviceEnumerator.GetVMID();
					if (virtualDevice.get_VMID() == id)
					{
						return virtualDevice;
					}
					deviceEnumerator.MoveNext();
				}
			}
			catch (COMException)
			{
			}

			return null;
		}

		public static bool IsIteratonFinishedException(ExternalException comException)
		{
			return comException.ErrorCode == ITERATION_FINISHED_HRESULT;
		}

		public static IDeviceEmulatorManagerVMID GetVirtualDevice()
		{
			IDeviceEmulatorManager manager = new DeviceEmulatorManagerClass();
			return FindDevice(manager, DEVICE_ID);
		}

		public static Device GetDevice()
		{
			DatastoreManager dsm = new DatastoreManager(1033);
			ObjectId pocketPCId = new ObjectId(POCKET_PC_PLATFORM_ID);
			Platform platform = dsm.GetPlatform(pocketPCId);

			ObjectId pocketPC2003VGA = new ObjectId("E282E6BE-C7C3-4ece-916A-88FB1CF8AF3C");
			return platform.GetDevice(pocketPC2003VGA);
		}
	}
}
