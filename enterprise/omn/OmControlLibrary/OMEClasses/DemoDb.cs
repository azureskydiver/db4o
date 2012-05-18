using System;
using OMAddinDataTransferLayer;
using OManager.BusinessLayer.UIHelper;
using OManager.DataLayer.Connection;
using OMControlLibrary.Common;
using OManager.BusinessLayer.Login;
using OME.Logging.Common;
using System.IO;

namespace OMControlLibrary
{
	public class DemoDb
	{
	    private static string demoFilePath = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects"
			+ Path.DirectorySeparatorChar + "ObjectManagerEnterprise" + Path.DirectorySeparatorChar + "DemoDb.yap";

	    public static void Create()
		{
			try
			{
				dbInteraction.CreateDemoDb(demoFilePath);
			    ConnParams conparam = new ConnParams(demoFilePath,false);
				
				RecentQueries currRecentConnection = new RecentQueries(conparam);
				RecentQueries tempRc = currRecentConnection.ChkIfRecentConnIsInDb();
				if (tempRc != null)
					currRecentConnection = tempRc;
				currRecentConnection.Timestamp = DateTime.Now;
				currRecentConnection.ConnParam = conparam;
				AssemblyInspectorObject.Connection.ConnectToDatabase(currRecentConnection,false );
				OMEInteraction.SetCurrentRecentConnection(currRecentConnection);
                OMEInteraction.SaveRecentConnection(currRecentConnection);


			}
			catch (Exception e)
			{
				LoggingHelper.ShowMessage(e);
			}
		}


	}
}
