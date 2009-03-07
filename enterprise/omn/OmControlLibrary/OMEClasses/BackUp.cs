using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using OManager.BusinessLayer.UIHelper;
using OMControlLibrary.Common;
using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OMControlLibrary
{
	public class Backup
	{

		public void BackUpDataBase()
		{
			try
			{
				SaveFileDialog dialog = new SaveFileDialog();
				dialog.ShowDialog();

				string filepath = dialog.FileName;
				bool checkForException = dbInteraction.BackUpDatabase(filepath);
				if (checkForException == false)
				{
					MessageBox.Show("Backup Successful!", "ObjectManager Enterprise");
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

		}
	}
}