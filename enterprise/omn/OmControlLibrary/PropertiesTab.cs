using System;
using System.Collections;
using System.Windows.Forms;
using EnvDTE;
using OManager.BusinessLayer.Login;
using OManager.DataLayer.PropertyTable;
using OMControlLibrary.Common;
using OME.Logging.Common;
using OME.Logging.Tracing;
using Constants=OMControlLibrary.Common.Constants;

namespace OMControlLibrary
{
	public partial class PropertiesTab : ViewBase
	{
		#region Member Variables

		readonly dbDataGridView dbGridViewProperties;

		private bool m_showObjectPropertiesTab;
		private bool m_showClassProperties;

		private static PropertiesTab instance;
	    object m_selectedObject;

		#endregion

		#region Properties

		public void SelectDefaultTab()
		{
			tabStripProperties.SelectedItem = tabStripProperties.Items[0];
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
				tabItemObjectProperties.Visible = m_showObjectPropertiesTab;
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
			dbGridViewProperties.Dock = DockStyle.Fill;

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

				dbGridViewProperties.PopulateDisplayGrid(Constants.VIEW_DBPROPERTIES, null);

				dbGridViewProperties.Rows.Add(1);
				if (Helper.DbInteraction.GetTotalDbSize() == -1)
				{
					dbGridViewProperties.Rows[0].Cells[0].Value = "NA for Client/Server";
				}
				else
				{
					dbGridViewProperties.Rows[0].Cells[0].Value = Helper.DbInteraction.GetTotalDbSize() + " bytes";
				}

				dbGridViewProperties.Rows[0].Cells[1].Value = Helper.DbInteraction.NoOfClassesInDb().ToString();
				if (Helper.DbInteraction.GetFreeSizeOfDb() == -1)
				{
					dbGridViewProperties.Rows[0].Cells[2].Value = "NA for Client/Server";
				}
				else
				{
					dbGridViewProperties.Rows[0].Cells[2].Value = Helper.DbInteraction.GetFreeSizeOfDb() + " bytes";
				}

				if (!panelDatabaseProperties.Controls.Contains(dbGridViewProperties))
					panelDatabaseProperties.Controls.Add(dbGridViewProperties);

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
							buttonSaveIndex.Enabled = Helper.DbInteraction.GetCurrentRecentConnection().ConnParam.Host == null;

							labelNoOfObjects.Text = "Number of objects : " + Helper.DbInteraction.NoOfObjectsforAClass(Helper.ClassName);
							dbGridViewProperties.Size = Size;
							dbGridViewProperties.Rows.Clear();
							dbGridViewProperties.Columns.Clear();

							ArrayList fieldPropertiesList = GetFieldsForAllClass();
							dbGridViewProperties.ReadOnly = false;
							dbGridViewProperties.PopulateDisplayGrid(Constants.VIEW_CLASSPOPERTY
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
								panelForClassPropTable.Controls.Add(dbGridViewProperties);
							dbGridViewProperties.Dock = DockStyle.Fill;
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

		private static ArrayList GetFieldsForAllClass()
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

					dbGridViewProperties.PopulateDisplayGrid(Constants.VIEW_OBJECTPROPERTIES, objectProperties);

					if (!panelObjectProperties.Controls.Contains(dbGridViewProperties))
						panelObjectProperties.Controls.Add(dbGridViewProperties);

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
					dbGridViewProperties.Size = Size;
				if (panelDataGrid != null)
				{
					panelDataGrid.Height = Height - tableLayoutPanelClassProp.Height;
					panelDataGrid.Width = Width;
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
				CheckForIllegalCrossThreadCalls = false;
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

				tabStripProperties.SelectedItem = tabStripProperties.Items[Helper.Tab_index];

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

				if (tabItemDatabaseProperties.Visible &&
					tabStripProperties.SelectedItem.Equals(tabItemDatabaseProperties))
				{
					DisplayDatabaseProperties();
				}
				else if (tabItemClassProperties.Visible &&
					tabStripProperties.SelectedItem.Equals(tabItemClassProperties))
				{
					DisplayClassProperties();
				}
				else if (tabItemObjectProperties.Visible &&
					tabStripProperties.SelectedItem.Equals(tabItemObjectProperties))
				{
					DisplayObjectProperties();
				}
				else
					tabStripProperties.SelectedItem = tabStripProperties.Items[0];

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
				
				ConnParams conparam = Helper.DbInteraction.GetCurrentRecentConnection().ConnParam;
				Helper.DbInteraction.CloseCurrDb();
				saveIndexInstance.SaveIndex();

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
				MessageBox.Show("Index Saved Successfully!", Helper.GetResourceString(Constants.PRODUCT_CAPTION), MessageBoxButtons.OK);
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
					DialogResult dialogRes = MessageBox.Show("This will close all the Query result windows. Do you want to continue?", Helper.GetResourceString(Constants.PRODUCT_CAPTION), MessageBoxButtons.YesNo, MessageBoxIcon.Question);
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
