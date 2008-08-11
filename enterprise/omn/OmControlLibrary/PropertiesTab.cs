using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Data;
using System.Text;
using System.Windows.Forms;
using OMControlLibrary.Common;
using System.Collections;
using OManager.DataLayer.PropertyTable;
using OManager.DataLayer.Connection;
using OManager.BusinessLayer.Login;
using OME.Logging.Common;
using OME.Logging.Tracing;
using EnvDTE;

namespace OMControlLibrary
{
	public partial class PropertiesTab : ViewBase
	{
		#region Member Variables

		dbDataGridView dbGridViewProperties;

		private bool m_showObjectPropertiesTab;
		private bool m_showClassProperties;

		private static PropertiesTab instance;
		private EnvDTE.WindowEvents _windowsEvents;
		object m_selectedObject = null;

		#endregion

		#region Properties

		public void SelectDefaultTab()
		{
			tabStripProperties.SelectedItem = (OMETabStripItem)tabStripProperties.Items[0];
		}

		public static PropertiesTab Instance
		{
			get
			{
				if (instance == null)
				{
					instance = new PropertiesTab();
				}
				return instance;
			}
		}

		public bool ShowObjectPropertiesTab
		{
			get { return m_showObjectPropertiesTab; }
			set
			{
				m_showObjectPropertiesTab = value;
				tabItemObjectProperties.Visible =
						m_showObjectPropertiesTab;
			}
		}

		public bool ShowClassProperties
		{
			get { return m_showClassProperties; }
			set
			{
				m_showClassProperties = value;
				tabItemClassProperties.Visible =
					m_showClassProperties;
			}
		}

		#endregion

		#region Constructor
		public PropertiesTab()
		{
			InitializeComponent();
			tabStripProperties.HideMenuGlyph = true;
			dbGridViewProperties = new dbDataGridView();

			tabStripProperties.AlwaysShowMenuGlyph = false;

		}



		#endregion

		#region Private Methods

		public void DisplayDatabaseProperties()
		{
			try
			{
				OMETrace.WriteFunctionStart();

				dbGridViewProperties.Size = panelDatabaseProperties.Size;
				dbGridViewProperties.Rows.Clear();
				dbGridViewProperties.Columns.Clear();

				dbGridViewProperties.PopulateDisplayGrid(Common.Constants.VIEW_DBPROPERTIES, null);

				dbGridViewProperties.Rows.Add(1);
				if (Helper.DbInteraction.GetTotalDbSize() == -1)
				{
					dbGridViewProperties.Rows[0].Cells[0].Value = "NA for Client/Server";
				}
				else
				{
					dbGridViewProperties.Rows[0].Cells[0].Value = Helper.DbInteraction.GetTotalDbSize().ToString() + " bytes";
				}

				dbGridViewProperties.Rows[0].Cells[1].Value = Helper.DbInteraction.NoOfClassesInDb().ToString();
				if (Helper.DbInteraction.GetFreeSizeOfDb() == -1)
				{
					dbGridViewProperties.Rows[0].Cells[2].Value = "NA for Client/Server";
				}
				else
				{
					dbGridViewProperties.Rows[0].Cells[2].Value = Helper.DbInteraction.GetFreeSizeOfDb().ToString() + " bytes";
				}

				if (!this.panelDatabaseProperties.Controls.Contains(dbGridViewProperties))
					this.panelDatabaseProperties.Controls.Add(dbGridViewProperties);

				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void DisplayClassProperties()
		{
			try
			{
				OMETrace.WriteFunctionStart();

				if (Helper.ClassName != null)
				{
					if (Helper.DbInteraction.GetCurrentRecentConnection() != null)
						if (Helper.DbInteraction.GetCurrentRecentConnection().ConnParam != null)
						{
							if (Helper.DbInteraction.GetCurrentRecentConnection().ConnParam.Host != null)
								buttonSaveIndex.Enabled = false;
							else
								buttonSaveIndex.Enabled = true;
							labelNoOfObjects.Text = "Number of objects : " + Helper.DbInteraction.NoOfObjectsforAClass(Helper.ClassName).ToString();
							dbGridViewProperties.Size = this.Size;
							dbGridViewProperties.Rows.Clear();
							dbGridViewProperties.Columns.Clear();

							ArrayList fieldPropertiesList = GetFieldsForAllClass();
							dbGridViewProperties.ReadOnly = false;
							dbGridViewProperties.PopulateDisplayGrid(Common.Constants.VIEW_CLASSPOPERTY
								, fieldPropertiesList);

							//Enable Disable IsIndexed Checkboxes
							foreach (DataGridViewRow row in dbGridViewProperties.Rows)
							{
								if (Helper.IsPrimitive(row.Cells[1].Value.ToString()))
								{
									row.Cells[2].ReadOnly = false;
								}
								//index should be disabled for arrays and collections
								else if (Helper.IsArrayOrCollection(row.Cells[1].Value.ToString()))
								{
									row.Cells[2].ReadOnly = true;
								}
								else //if a sub object field , index should be disabled
									row.Cells[2].ReadOnly = true;
							}

							if (!panelForClassPropTable.Controls.Contains(dbGridViewProperties))
								this.panelForClassPropTable.Controls.Add(dbGridViewProperties);
							dbGridViewProperties.Dock = DockStyle.Left;
						}
				}
				else
					buttonSaveIndex.Enabled = false;

				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private ArrayList GetFieldsForAllClass()
		{
			ClassPropertiesTable classPropTable = null;

			try
			{
				classPropTable = Helper.DbInteraction.GetClassProperties(Helper.ClassName);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

			return classPropTable.FieldEntries;
		}

		private void DisplayObjectProperties()
		{
			try
			{
				OMETrace.WriteFunctionStart();

				if (m_selectedObject != null)
				{
					ArrayList objectProperties = new ArrayList();
					ObjectPropertiesTable objTable = Helper.DbInteraction.GetObjectProperties(m_selectedObject);
					objectProperties.Add(objTable);

					dbGridViewProperties.Rows.Clear();
					dbGridViewProperties.Columns.Clear();
					dbGridViewProperties.Size = panelDatabaseProperties.Size;

					dbGridViewProperties.PopulateDisplayGrid(Common.Constants.VIEW_OBJECTPROPERTIES
						, objectProperties);

					if (!panelObjectProperties.Controls.Contains(dbGridViewProperties))
						this.panelObjectProperties.Controls.Add(dbGridViewProperties);

					dbGridViewProperties.Refresh();
				}

				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		#endregion

		#region Event Handlers
		private void tabControlProperties_Resize(object sender, EventArgs e)
		{
			try
			{
				if (dbGridViewProperties != null)
					this.dbGridViewProperties.Size = this.Size;
				if (panelDataGrid != null)
				{
					this.panelDataGrid.Height = this.Height - tableLayoutPanelClassProp.Height;
					this.panelDataGrid.Width = this.Width;
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

		}

		private void PropertiesTab_Load(object sender, EventArgs e)
		{
			try
			{
				PropertiesTab.CheckForIllegalCrossThreadCalls = false;
				dbGridViewProperties.Dock = DockStyle.Fill;
				tabItemObjectProperties.Visible = instance.m_showObjectPropertiesTab;
				if (!tabItemClassProperties.Visible)
					tabItemClassProperties.Visible = instance.m_showClassProperties;

				if (Helper.Tab_index.Equals(0))
				{
					DisplayDatabaseProperties();
				}
				else if (Helper.Tab_index.Equals(1))
				{
					DisplayClassProperties();
				}
				else if (Helper.Tab_index.Equals(2))
				{
					DisplayObjectProperties();
				}

				tabStripProperties.SelectedItem = (OMETabStripItem)tabStripProperties.Items[Helper.Tab_index];

				instance = this;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void tabStripProperties_TabStripItemSelectionChanged(TabStripItemChangedEventArgs e)
		{
			try
			{
				if (e.Item != null && e.ChangeType == OMETabStripItemChangeTypes.SelectionChanged)
					RefreshPropertiesTab(m_selectedObject);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		#endregion

		#region Public Methods
		public void RefreshPropertiesTab(object selectedObject)
		{
			try
			{
				m_selectedObject = selectedObject;

				if (tabItemDatabaseProperties.Visible == true &&
					tabStripProperties.SelectedItem.Equals(tabItemDatabaseProperties))
				{
					DisplayDatabaseProperties();
				}
				else if (tabItemClassProperties.Visible == true &&
					tabStripProperties.SelectedItem.Equals(tabItemClassProperties))
				{
					DisplayClassProperties();
				}
				else if (tabItemObjectProperties.Visible == true &&
					tabStripProperties.SelectedItem.Equals(tabItemObjectProperties))
				{
					DisplayObjectProperties();
				}
				else
					tabStripProperties.SelectedItem = (OMETabStripItem)tabStripProperties.Items[0];

				Helper.Tab_index = Convert.ToInt32(tabStripProperties.SelectedItem.Tag.ToString());
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
		#endregion



		private void SaveIndex()
		{
			try
			{
				SaveIndexClass saveIndexInstance = new SaveIndexClass();
				saveIndexInstance.Classname = Helper.ClassName;
				saveIndexInstance.Fieldname = new ArrayList();
				saveIndexInstance.Indexed = new ArrayList();
				foreach (DataGridViewRow row in dbGridViewProperties.Rows)
				{
					bool boolValue = Convert.ToBoolean(row.Cells[2].Value);
					saveIndexInstance.Fieldname.Add(row.Cells[0].Value.ToString());
					saveIndexInstance.Indexed.Add(boolValue);

				}
				foreach (Window w in ApplicationObject.ToolWindows.DTE.Windows)
				{
					if (Helper.HashClassGUID != null)
					{
						IDictionaryEnumerator eNum = Helper.HashClassGUID.GetEnumerator();
						if (eNum != null)
						{
							while (eNum.MoveNext())
							{
								string winId = w.ObjectKind.ToLower();
								string enumwinId = eNum.Value.ToString().ToLower();
								if (winId == enumwinId)
								{
									w.Close(vsSaveChanges.vsSaveChangesNo);
									break;
								}
							}

						}
					}
				}
				// 
				ConnParams conparam = Helper.DbInteraction.GetCurrentRecentConnection().ConnParam;
				Helper.DbInteraction.CloseCurrDb();
				saveIndexInstance.SaveIndex();
				//string clsName = Helper.ClassName;

				//foreach (DataGridViewRow row in dbGridViewProperties.Rows)
				//{
				//    bool boolValue = Convert.ToBoolean(row.Cells[2].Value);
				//    Helper.DbInteraction.SetIndexedConfiguration(row.Cells[0].Value.ToString(), clsName, boolValue);
				//}
				RecentQueries currRecentConnection = new RecentQueries(conparam);
				RecentQueries tempRc = currRecentConnection.ChkIfRecentConnIsInDb();
				if (tempRc != null)
					currRecentConnection = tempRc;
				currRecentConnection.Timestamp = DateTime.Now;
				Helper.DbInteraction.ConnectoToDB(currRecentConnection);
				Helper.DbInteraction.SetCurrentRecentConnection(currRecentConnection);

				if (ObjectBrowser.Instance.ToolStripButtonAssemblyView.Checked)
					ObjectBrowser.Instance.DbtreeviewObject.FindNSelectNode(ObjectBrowser.Instance.DbAssemblyTreeView.Nodes[0], saveIndexInstance.Classname, ObjectBrowser.Instance.DbAssemblyTreeView);

				else
					ObjectBrowser.Instance.DbtreeviewObject.FindNSelectNode(ObjectBrowser.Instance.DbtreeviewObject.Nodes[0], saveIndexInstance.Classname, ObjectBrowser.Instance.DbtreeviewObject);

				tabStripProperties.SelectedItem = tabItemClassProperties;
				MessageBox.Show("Index Saved Successfully!", Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION), MessageBoxButtons.OK);
			}
			catch (Exception oEx)
			{

				LoggingHelper.ShowMessage(oEx);
			}
		}


		private void buttonSaveIndex_Click(object sender, EventArgs e)
		{
			try
			{
				if (ListofModifiedObjects.Instance.Count > 0)
				{
					DialogResult dialogRes = MessageBox.Show("This will close all the Query result windows. Do you want to continue?", Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION), MessageBoxButtons.YesNo, MessageBoxIcon.Question);
					if (dialogRes == DialogResult.Yes)
					{
						SaveIndex();

					}
				}
				else
				{
					SaveIndex();
				}

			}
			catch (Exception e1)
			{
				LoggingHelper.ShowMessage(e1);
			}
		}




	}
}
