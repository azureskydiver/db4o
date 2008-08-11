using System;
using System.Threading;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using OManager.DataLayer.Connection;
using OMControlLibrary.Common;
using OManager.BusinessLayer.Login;
using OME.Logging.Common;

namespace OMControlLibrary
{
	public class Defragmentdb4oData
	{
		string strLocation;
		delegate void DefragData();
		ProgressBar p = new ProgressBar();

		public Defragmentdb4oData(string strLocation)
		{
			try
			{
				this.strLocation = strLocation;
				DefragFile();
				//DefragData UpdateProgress = new DefragData(DefragFile);
				//UpdateProgress.Invoke();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		//private void ShowDialogforProgressBar()
		//{
		//    try
		//    {
		//        p.ShowDialog();
		//    }
		//    catch (ThreadAbortException)
		//    {
		//        System.Threading.Thread.ResetAbort();

		//    }
		//    catch (Exception oEx)
		//    {
		//        LoggingHelper.ShowMessage(oEx);
		//    }
		//}

		public void DefragFile()
		{
			// System.Threading.Thread t = null;
			try
			{
				//  t = new System.Threading.Thread(new ThreadStart(ShowDialogforProgressBar));
				//  t.Start();
				//Helper.DbInteraction.closedb(Helper.DbInteraction.GetCurrentRecentConnection());
				bool checkExecption = Helper.DbInteraction.DefragDatabase(strLocation);
				ConnParams conparam = new ConnParams(strLocation, null, null, null, 0);
				RecentQueries currRecentConnection = new RecentQueries(conparam);
				RecentQueries tempRc = currRecentConnection.ChkIfRecentConnIsInDb();
				if (tempRc != null)
					currRecentConnection = tempRc;
				currRecentConnection.Timestamp = DateTime.Now;
				Helper.DbInteraction.ConnectoToDB(currRecentConnection);
				Helper.DbInteraction.SetCurrentRecentConnection(currRecentConnection);
				//    t.Abort();

				if (checkExecption == true)
				{
					MessageBox.Show(Helper.GetResourceString(Common.Constants.ERROR_MSG_DEFRAGMENT),
						Helper.GetResourceString(Common.Constants.PRODUCT_CAPTION),
						MessageBoxButtons.OK,
						MessageBoxIcon.Error);
				}
			}
			catch (Exception e)
			{
				//    t.Abort();
				LoggingHelper.ShowMessage(e);
			}
		}
	}
}
