using System;
using System.Collections;
using OManager.BusinessLayer.UIHelper;
using OMControlLibrary.Common;
using System.Windows.Forms;
using OME.Logging.Common;

namespace OMControlLibrary
{
	public class ListofModifiedObjects
	{

		private static Hashtable hash;

		public static Hashtable Instance
		{
			get
			{
				if (hash == null)
				{
					hash = new Hashtable();
				}
				return hash;
			}
		}

		public static void AddDatagrid(string strClassName, dbDataGridView dbDataGridViewQueryResult)
		{
			if (Instance.ContainsKey(strClassName))
			{
				Instance.Remove(strClassName);
				Instance.Add(strClassName, dbDataGridViewQueryResult);
			}
			else
			{
				Instance.Add(strClassName, dbDataGridViewQueryResult);
			}
		}
		
		public static DialogResult SaveBeforeWindowHiding(ref bool check, ref bool checkforValueChanged, string Caption, dbDataGridView db, int hierarchyLevel)
		{
			DialogResult result = DialogResult.Cancel;
			try
			{
				foreach (DataGridViewRow row in db.Rows)
				{
					if (Convert.ToBoolean(row.Cells[Constants.QUERY_GRID_ISEDITED_HIDDEN].Value))
					{
						checkforValueChanged = true;
						break;
					}
				}

				if (!checkforValueChanged)
					return result;
				
				result = MessageBox.Show("'" + Caption + "' contains some modified objects. Do you want to save changes?",
				                         Helper.GetResourceString(Constants.PRODUCT_CAPTION), MessageBoxButtons.YesNo,
				                         MessageBoxIcon.Question);

				if (result == DialogResult.Yes)
				{
					foreach (DataGridViewRow row in db.Rows)
					{
						if (Convert.ToBoolean(row.Cells[Constants.QUERY_GRID_ISEDITED_HIDDEN].Value))
						{
							if (hierarchyLevel == -1)
								hierarchyLevel = dbInteraction.GetDepth(row.Tag);

							Helper.DbInteraction.SaveCollection(row.Tag, hierarchyLevel);
						}
					}
				}
				else
				{
					foreach (DataGridViewRow row in db.Rows)
					{
						if (Convert.ToBoolean(row.Cells[Constants.QUERY_GRID_ISEDITED_HIDDEN].Value))
						{
							dbInteraction.RefreshObject(row.Tag, 1);
						}
					}
				}
			}
			catch (Exception ex)
			{
				LoggingHelper.ShowMessage(ex);
			}

			return result;
		}

		public static int CalculateLevel(object obj)
		{
			return dbInteraction.GetDepth(obj);
		}
	}
}