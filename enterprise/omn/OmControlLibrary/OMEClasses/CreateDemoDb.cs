using System;
using System.Collections.Generic;
using System.Text;
using OMControlLibrary.Common;
using OManager.BusinessLayer.Login;
using OME.Logging.Common;
using System.IO;
using System.Threading;
using System.ComponentModel;

namespace OMControlLibrary
{
	public class CreateDemoDb
	{


		delegate void CreateDemoDataBase();
		ProgressBar p = new ProgressBar();


		string demoFilePath = Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData) + Path.DirectorySeparatorChar + "db4objects"
			+ Path.DirectorySeparatorChar + "ObjectManagerEnterprise" + Path.DirectorySeparatorChar + "DemoDb.yap";
		bool isrunning = true;


		public CreateDemoDb()
		{
			try
			{



				CreateDemoDbMethod();

			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}






		public void CreateDemoDbMethod()
		{
			//System.Threading.Thread t = null;
			try
			{


				bool checkExecption = Helper.DbInteraction.CreateDemoDb(demoFilePath);

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
