using System;
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
				Helper.DbInteraction.CreateDemoDb(demoFilePath);
			    ConnParams conparam = new ConnParams(demoFilePath, null, null, null, 0);

				RecentQueries currRecentConnection = new RecentQueries(conparam);
				RecentQueries tempRc = currRecentConnection.ChkIfRecentConnIsInDb();
				if (tempRc != null)
					currRecentConnection = tempRc;
				currRecentConnection.Timestamp = DateTime.Now;
				Helper.DbInteraction.ConnectoToDB(currRecentConnection);
				Helper.DbInteraction.SetCurrentRecentConnection(currRecentConnection);


			}
			catch (Exception e)
			{
				LoggingHelper.ShowMessage(e);
			}
		}


	}
}
