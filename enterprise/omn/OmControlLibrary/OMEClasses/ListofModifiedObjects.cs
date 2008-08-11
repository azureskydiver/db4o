using System;
using System.Collections.Generic;
using System.Text;
using System.Collections;
using OMControlLibrary.Common;
using System.Windows.Forms;
using OME.Logging.Common;
using OME.Logging.Tracing;
using OManager.BusinessLayer.QueryManager;

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
			if (ListofModifiedObjects.Instance.ContainsKey(strClassName))
			{
				ListofModifiedObjects.Instance.Remove(strClassName);
				ListofModifiedObjects.Instance.Add(strClassName, dbDataGridViewQueryResult);
			}
			else
			{
				ListofModifiedObjects.Instance.Add(strClassName, dbDataGridViewQueryResult);
			}
		}
		public static void SaveBeforeWindowHiding(ref bool check, ref DialogResult dialogRes, ref bool checkforValueChanged, string Caption, dbDataGridView db, int hierarchyLevel)
		{
			try
			{
				foreach (DataGridViewRow row in db.Rows)
				{


					if (Convert.ToBoolean(row.Cells[OMControlLibrary.Common.Constants.QUERY_GRID_ISEDITED_HIDDEN].Value) == true)
					{
						checkforValueChanged = true;
						break;
					}
				}
				if (checkforValueChanged == true)
				{
					dialogRes = MessageBox.Show("'" + Caption + "' contains some modified objects, Do you want to save changes?", Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION), MessageBoxButtons.YesNo, MessageBoxIcon.Question);
					if (dialogRes == DialogResult.Yes)
					{
						foreach (DataGridViewRow row in db.Rows)
						{


							if (Convert.ToBoolean(row.Cells[OMControlLibrary.Common.Constants.QUERY_GRID_ISEDITED_HIDDEN].Value) == true)
							{
								dbInteraction dbInt = new dbInteraction();
								if (hierarchyLevel == -1)
									hierarchyLevel = dbInt.GetDepth(row.Tag);

								Helper.DbInteraction.SaveCollection(row.Tag, hierarchyLevel);
								//}
								//else
								//{
								//    int calLevel = CalculateLevel(row.Tag);
								//    Helper.DbInteraction.SaveCollection(row.Tag, calLevel);
								//}

							}
						}
					}
					else
					{
						foreach (DataGridViewRow row in db.Rows)
						{
							if (Convert.ToBoolean(row.Cells[OMControlLibrary.Common.Constants.QUERY_GRID_ISEDITED_HIDDEN].Value) == true)
							{
								Helper.DbInteraction.RefreshObject(row.Tag, 1);
							}
						}

					}
				}

			}
			catch (Exception ex)
			{
				LoggingHelper.ShowMessage(ex);
			}

		}
		public static int CalculateLevel(object obj)
		{
			dbInteraction dbI = new dbInteraction();
			return dbI.GetDepth(obj);

		}
	}


}
