using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Text;
using System.Windows.Forms;
using System.Collections;
using EnvDTE;
using EnvDTE80;
using OMControlLibrary.Common;
using OManager.BusinessLayer.pagingData;
using OManager.BusinessLayer.QueryManager;
using System.Runtime.InteropServices;
using OME.AdvancedDataGridView;
using OME.Logging.Common;
using OME.Logging.Tracing;

namespace OMControlLibrary
{
	[ComVisibleAttribute(true)]

	public partial class QueryResult : ViewBase
	{
		#region Member Variables

		string strstoreValue = string.Empty;
		string strstoreTreeValue = string.Empty;
		internal string ClassName = Helper.BaseClass;

		internal ArrayList hierarchy;

		internal List<Hashtable> hashListResult = Helper.HashList;
		internal Hashtable listQueryAttributes;
		internal Hashtable hashObjectId = new Hashtable();
		dbDataGridView dbDataGridViewQueryResult;
		TreeGridView treeview = new TreeGridView();
		QueryBuilder queryBuilder = null;
		OMETabStrip tabControlObjHierarchy = null;
		OMETabStripItem tabPage = null;

		int intLevel = 1; //reqd for saving object (indicates obj depth to be saved)
		internal OMQuery omQuery = null;
		internal long[] objectid;

		Hashtable cellUpdated = null;


		delegate void deletgateCascadeonDelete(object obj);

		//Constants
		private const string COLUMN_NUMBER = "No.";
		private const string CONST_DOT = ".";
		private const char CONST_SPACE = ' ';
		private const string CONST_TAB_CAPTION = "Object ";
		private const string CONST_TRUE = "true";
		private const string CONST_FALSE = "false";
		private const string CONST_COMMA = ",";

		private const int m_defaultPageSize = 50;
		private const int m_pagingStartIndex = 0;
		private int m_pageCount = 1;

		readonly WindowVisibilityEvents windowsVisEvents;
		readonly WindowEvents _windowsEvents;

		List<long> lstObjIdLong;

		#endregion


		public void Setobjectid(long[] objectid)
		{
			this.objectid = objectid;
		}

		#region Constructor

		public QueryResult()
		{
			try
			{
				this.SetStyle(ControlStyles.CacheText |
						  ControlStyles.OptimizedDoubleBuffer, true);

				InitializeComponent();

				treeview.AllowDrop = true;

				omQuery = (OMQuery)Helper.OMResultedQuery[this.ClassName];
				this.listQueryAttributes = omQuery.AttributeList;

				EnvDTE.Events events = ApplicationObject.Events;
				_windowsEvents = (EnvDTE.WindowEvents)events.get_WindowEvents(null);
				_windowsEvents.WindowActivated += new _dispWindowEvents_WindowActivatedEventHandler(_windowsEvents_WindowActivated);
				EnvDTE80.Events2 event_1 = (EnvDTE80.Events2)ApplicationObject.Events;
				windowsVisEvents = (WindowVisibilityEvents)event_1.get_WindowVisibilityEvents(Helper.QueryResultToolWindow);
				windowsVisEvents.WindowHiding += new _dispWindowVisibilityEvents_WindowHidingEventHandler(windowsVisEvents_WindowHiding);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		#endregion

		#region WindowEvents
		void _windowsEvents_WindowActivated(Window GotFocus, Window LostFocus)
		{
			if (LostFocus.Caption == "Closed")
				return;
			if (GotFocus.Caption != "Query Builder" && GotFocus.Caption != "db4o Browser" && GotFocus.Caption != "DataBase Properties" && GotFocus.Caption != "")
			{

				PropertiesTab.Instance.ShowObjectPropertiesTab = false;
				SelectTreeNodeInObjBrowser(GotFocus.Caption);
			}
			else
			{


			}



		}


		private void SelectTreeNodeInObjBrowser(string winCaptionArg)
		{

			string winCaption = winCaptionArg;
			IDictionaryEnumerator eNum = Helper.HashClassGUID.GetEnumerator();

			if (eNum != null)
			{
				while (eNum.MoveNext())
				{

					string enumwinCaption = eNum.Key.ToString();
					int index = enumwinCaption.LastIndexOf(',');
					string strClassName = enumwinCaption.Remove(0, index);

					string str = enumwinCaption.Remove(index);

					index = str.IndexOf('.');
					string caption = str.Remove(0, index + 1) + strClassName;

					if (winCaption == caption)
					{
						if (ObjectBrowser.Instance.ToolStripButtonAssemblyView.Checked)
						{

							ObjectBrowser.Instance.DbtreeviewObject.FindNSelectNode(ObjectBrowser.Instance.DbAssemblyTreeView.Nodes[0], eNum.Key.ToString(), ObjectBrowser.Instance.DbAssemblyTreeView);


						}
						else
							ObjectBrowser.Instance.DbtreeviewObject.FindNSelectNode(ObjectBrowser.Instance.DbtreeviewObject.Nodes[0], eNum.Key.ToString(), ObjectBrowser.Instance.DbtreeviewObject);
						//    ObjectBrowser.Instance.DbtreeviewObject.FindTreeNodesClasses(Helper.DbInteraction.FetchAllStoredClassesForAssembly(), eNum.Key.ToString());
					}
				}
			}
		}

		public void windowsVisEvents_WindowHiding(Window Window)
		{
			string winCaption = Window.Caption;
			IDictionaryEnumerator eNum = Helper.HashClassGUID.GetEnumerator();

			if (eNum != null)
			{
				while (eNum.MoveNext())
				{

					string enumwinCaption = eNum.Key.ToString();
					int index = enumwinCaption.LastIndexOf(',');
					string strClassName = enumwinCaption.Remove(0, index);

					string str = enumwinCaption.Remove(index);

					index = str.IndexOf('.');
					string caption = str.Remove(0, index + 1) + strClassName;

					if (winCaption == caption)
					{
						if (ListofModifiedObjects.Instance.ContainsKey(enumwinCaption))
						{

							dbDataGridView db = ListofModifiedObjects.Instance[enumwinCaption] as dbDataGridView;

							bool check = false;
							DialogResult dialogRes = DialogResult.Ignore;
							bool checkforValueChanged = false;

							ListofModifiedObjects.SaveBeforeWindowHiding(ref check, ref dialogRes, ref checkforValueChanged, caption, db, 10); //TODO: Remove the hardcoded value of 10. istead there shud be alogic for counting level
							intLevel = 1;
							ListofModifiedObjects.Instance.Remove(enumwinCaption);

						}
					}
				}
			}
		}


		#endregion



		#region Query Result Events

		private void QueryResult_Load(object sender, EventArgs e)
		{
			try
			{
				OMETrace.WriteFunctionStart();
				QueryResult.CheckForIllegalCrossThreadCalls = false;
				InitializeTabControl();
				InitializeResultDataGridView();
				Hashtable hAttributes = new Hashtable();
				if (omQuery != null)
				{
					hAttributes = this.omQuery.AttributeList;
				}
				PagingData pagingData = new PagingData(m_pagingStartIndex, m_defaultPageSize);

				if (objectid != null)
				{
					lstObjIdLong = new List<long>(objectid);
					pagingData.ObjectId = lstObjIdLong;


					//int length = pagingData.ObjectId.Count; 

					int pageNumber = m_pagingStartIndex + 1;
					lblPageCount.Text = pagingData.GetPageCount().ToString();
					txtCurrentPage.Text = pageNumber.ToString();
					labelNoOfObjects.Text = pagingData.ObjectId.Count.ToString();
					if (lstObjIdLong.Count > 0)
					{

						hashListResult = Helper.DbInteraction.ReturnQueryResults(pagingData, true, omQuery.BaseClass, this.omQuery.AttributeList);
						dbDataGridViewQueryResult.SetDataGridColumnHeader(hashListResult, ClassName, omQuery.AttributeList);
						dbDataGridViewQueryResult.SetDatagridRowsWithIndex(hashListResult, ClassName, hAttributes, 1);
						ListofModifiedObjects.AddDatagrid(this.ClassName, dbDataGridViewQueryResult);

					}


					if (pagingData.ObjectId.Count <= 50)
					{
						btnPrevious.Enabled = false;
						btnNext.Enabled = false;
						btnFirst.Enabled = false;
						btnLast.Enabled = false;
					}
					else
					{
						btnPrevious.Enabled = false;
						btnFirst.Enabled = false;
					}

					SetLiterals();

					OMETrace.WriteFunctionEnd();
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

		}


		private void QueryResult_Resize(object sender, EventArgs e)
		{
			try
			{
				//if (dbDataGridViewQueryResult != null)
				//    this.dbDataGridViewQueryResult.Size = this.Size;

				//if (dbDataGridViewQueryResult != null)
				//{

				//    int w = dbDataGridViewQueryResult.Width;
				//    if (dbDataGridViewQueryResult.Columns[2].Width * (dbDataGridViewQueryResult.Columns.Count - 2) < dbDataGridViewQueryResult.Width)
				//    {
				//        for (int i = 2; i < dbDataGridViewQueryResult.Columns.Count; i++)
				//        {
				//            dbDataGridViewQueryResult.Columns[i].Width = (w + 100) / dbDataGridViewQueryResult.Columns.Count - 2;
				//        }
				//    }
				//}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		#endregion

		#region DataGridView Events

		private void dbDataGridViewQueryResult_CellEndEdit(object sender, DataGridViewCellEventArgs e)
		{
			try
			{

				OMETrace.WriteFunctionStart();

				if (treeview.SelectedRows.Count > 0)
					treeview.SelectedRows[0].Selected = false;

				DataGridViewCell cell = dbDataGridViewQueryResult[e.ColumnIndex, e.RowIndex];
				object currObj = cell.OwningRow.Tag;
				//cell.OwningColumn.SortMode = sort; 
				string headerText = dbDataGridViewQueryResult.Columns[e.ColumnIndex].HeaderText;
				string strclass = dbDataGridViewQueryResult.Columns[e.ColumnIndex].Tag.ToString();
				object obj = dbDataGridViewQueryResult.Rows[e.RowIndex].Cells[e.ColumnIndex].Value;
				string strAttribName = Helper.ReturnAttributeName(headerText);
				string dataType = Helper.DbInteraction.GetDatatype(strclass, strAttribName);
				object value = cell.Value;

				bool check = Validations.ValidateDataType(dataType,
							ref value);

				if (check == true)
				{
					if (strstoreValue != value.ToString())
					{
						if (currObj != null)
						{
							dbDataGridViewQueryResult.Rows[e.RowIndex].Cells[Common.Constants.QUERY_GRID_ISEDITED_HIDDEN].Value = Convert.ToBoolean(true);
							Helper.DbInteraction.EditObject(currObj, headerText,
								value.ToString());

							dbDataGridViewQueryResult.Rows[e.RowIndex].Cells[e.ColumnIndex].Value = Helper.GetValue(dataType, cell.Value);
							cellUpdated = new Hashtable();
							cellUpdated.Add(headerText, value);

							if (strstoreValue != value.ToString())
							{
								cell.Style.ForeColor = Color.Red;
								cell.Style.SelectionForeColor = Color.Red;

								tabControlObjHierarchy.SelectedItem.Name = CONST_TRUE;

								UpdateDataTreeView(cell.OwningRow.Tag, cell.OwningRow);


							}
						}

						btnSave.Enabled = true;
					}
					cell.OwningColumn.SortMode = sortStore;

					OMETrace.WriteFunctionEnd();



				}
				else
				{
					dbDataGridViewQueryResult.Rows[e.RowIndex].Cells[e.ColumnIndex].Value = Helper.GetValue(dataType, strstoreValue);
					strstoreValue = string.Empty;
					cell.Style.ForeColor = Color.Black;
					cell.Style.SelectionForeColor = Color.White;
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
		DataGridViewColumnSortMode sortStore;
		private void dbDataGridViewQueryResult_CellBeginEdit(object sender, DataGridViewCellCancelEventArgs e)
		{
			try
			{
				DataGridViewCell cell = ((DataGridView)sender).CurrentCell;

				dbInteraction db = new dbInteraction();
				string dataType = db.GetDatatype(cell.OwningColumn.Tag.ToString(), cell.OwningColumn.HeaderText);

				if (dataType != typeof(string).ToString() && (cell.Value != null && cell.Value.ToString() == "null"))
				{
					e.Cancel = true;
					return;
				}

				btnSave.Enabled = true;
				if (treeview.SelectedRows.Count > 0)
					treeview.SelectedRows[0].Selected = false;

				if (cell.Value != null)
					strstoreValue = cell.Value.ToString();

				//if (cell != null)
				//    cell.OwningColumn.SortMode = DataGridViewColumnSortMode.NotSortable;

				sortStore = cell.OwningColumn.SortMode;

			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void dbDataGridViewQueryResult_SelectionChanged(object sender, EventArgs e)
		{
			try
			{
				OMETrace.WriteFunctionStart();

				if (dbDataGridViewQueryResult.SelectedRows.Count > 0 && tabControlObjHierarchy.SelectedItem != null)
				{
					if (dbDataGridViewQueryResult.SelectedRows[0].Tag != null && dbDataGridViewQueryResult.SelectedRows[0].Tag.Equals(tabControlObjHierarchy.SelectedItem.Tag))
					{
						PropertiesTab.Instance.ShowObjectPropertiesTab = true;

						PropertiesTab.Instance.RefreshPropertiesTab(dbDataGridViewQueryResult.SelectedRows[0].Tag);

						return;
					}
				}
				if (dbDataGridViewQueryResult.SelectedRows.Count > 0)
				{
					DataGridViewRow row = dbDataGridViewQueryResult.SelectedRows[0];
					if (row != null)
					{
						if (row.Tag != null)
						{
							if (dbDataGridViewQueryResult.CurrentRow != null)
							{
								if ((bool)dbDataGridViewQueryResult.CurrentRow.Cells[Common.Constants.QUERY_GRID_ISEDITED_HIDDEN].Value == true)
								{
									treeview = Helper.DbInteraction.GetObjectHierarchy(row.Tag, ClassName, true);
								}

								else
								{
									treeview = Helper.DbInteraction.GetObjectHierarchy(row.Tag, ClassName, false);
								}
							}
							else
							{
								treeview = Helper.DbInteraction.GetObjectHierarchy(row.Tag, ClassName, false);
							}
							treeview.Dock = DockStyle.Fill;
							tabPage = new OMETabStripItem();
							tabPage.Controls.Add(treeview);

							bool add = true;

							foreach (OMETabStripItem tp in tabControlObjHierarchy.Items)
							{
								if (tp.Caption.Equals(CONST_TAB_CAPTION + row.Cells[COLUMN_NUMBER].Value.ToString()))
								{
									tabControlObjHierarchy.SelectedItem = tp;
									add = false;
									break;
								}
							}

							Helper.SelectedObject = treeview.Nodes[0].Tag;
							tabPage.Tag = Helper.SelectedObject;
							tabPage.Title = tabPage.Name = CONST_TAB_CAPTION + row.Cells[COLUMN_NUMBER].Value.ToString();
							if (add)
							{
								tabControlObjHierarchy.AddTab(tabPage);
							}
							else return;

							RegisterTreeviewEvents();
							// This check helps in avoding recusrrion.
							if (dbDataGridViewQueryResult.SortOrder == SortOrder.None)
							{
								tabControlObjHierarchy.SelectedItem = tabPage;
							}
						}
						else
							row.Selected = false;
					}
				}

				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		void RegisterTreeviewEvents()
		{
			treeview.Dock = DockStyle.Fill;
			treeview.NodeExpanded += new ExpandedEventHandler(treeview_NodeExpanded);
			treeview.CellBeginEdit += new DataGridViewCellCancelEventHandler(treeview_CellBeginEdit);
			treeview.CellEndEdit += new DataGridViewCellEventHandler(treeview_CellEndEdit);
			treeview.OnContextMenuItemClicked += new EventHandler<ContextItemClickedEventArg>(treeview_OnContextMenuItemClicked);
			treeview.OnContextMenuOpening += new EventHandler<ContextItemClickedEventArg>(treeview_OnContextMenuOpening);
			treeview.Click += new EventHandler(treeview_Click);
			treeview.Columns[0].Width = (treeview.Width - 2) / 3;
			treeview.Columns[1].Width = (treeview.Width - 2) / 3;
			treeview.Columns[2].Width = (treeview.Width - 2) / 3;
		}

		void treeview_Click(object sender, EventArgs e)
		{
			CheckForObjectPropertiesTab(treeview.Nodes[0].Tag);
		}

		void treeview_OnContextMenuItemClicked(object sender, ContextItemClickedEventArg e)
		{
			try
			{
				if (treeview.SelectedRows.Count > 0)
				{
					treeview = e.Data as TreeGridView;
					treeview.ContextMenuStrip.Dispose();
					DialogResult dialogRes = MessageBox.Show("This will set the value to null in the database. Do you want to continue?",
						Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION), MessageBoxButtons.YesNo, MessageBoxIcon.Question);
					if (dialogRes == DialogResult.Yes)
					{
						long id = 0;
						dbInteraction db = new dbInteraction();
						bool isArr = db.IsArray(((TreeGridNode)treeview.SelectedRows[0]).Tag);
						bool isdateTime = db.CheckForDateTimeOrString(((TreeGridNode)treeview.SelectedRows[0]).Tag);
						bool isColl = db.IsCollection(((TreeGridNode)treeview.SelectedRows[0]).Tag);

						if (isArr || isColl)
						{
							object objToDelete1 = ((TreeGridNode)treeview.SelectedCells[0].OwningRow).Parent.Tag;
							string field = treeview.SelectedCells[0].OwningRow.Cells[0].Value.ToString();
							int index = field.IndexOf('(');
							field = field.Remove(index - 1, field.Length - index + 1);
							id = db.GetLocalID(treeview.Nodes[0].Tag);
							db.SetCollectionsToNull(objToDelete1, field);
						}
						else if (isdateTime)
						{
							object objToDelete2 = ((TreeGridNode)treeview.SelectedCells[0].OwningRow).Parent.Tag;
							string field = treeview.SelectedCells[0].OwningRow.Cells[0].Value.ToString();
							id = db.GetLocalID(treeview.Nodes[0].Tag);
							db.SetCollectionsToNull(objToDelete2, field);
						}
						else
						{
							object objToDelete = ((TreeGridNode)treeview.SelectedCells[0].OwningRow).Tag;
							id = db.GetLocalID(treeview.Nodes[0].Tag);
							db.DeleteObject(objToDelete, true);
						}

						TreeGridNode node = (TreeGridNode)treeview.SelectedCells[0].OwningRow;
						int depth = 0;
						if (node.Parent != null)
						{
							while (node.Parent.Tag != null)
							{
								depth++;
								node = node.Parent;
							}
						}
						object obj = null;

						if (id != 0)

							obj = db.GetObjById(id);
						else
						{
							MessageBox.Show("This object is already deleted.", Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION), MessageBoxButtons.OK, MessageBoxIcon.Information);
						}
						if (obj != null)
						{
							db.RefreshObject(obj, depth);

							UpdateResultTable(treeview.SelectedCells[0].OwningRow.Cells[0],
							   "null",
							   (TreeGridNode)treeview.SelectedCells[0].OwningRow.Cells[0].OwningRow, (OMETabStripItem)treeview.Parent, false, true);
							treeview = Helper.DbInteraction.GetObjectHierarchy(obj, treeview.Nodes[0].Cells[2].Value.ToString(), false);
							tabControlObjHierarchy.SelectedItem.Controls.Clear();
							tabControlObjHierarchy.SelectedItem.Controls.Add(treeview);
							RegisterTreeviewEvents();

						}
						else//delete tab as teh obj is deleted and delete it from db grid view
						{
							int delIndex = tabControlObjHierarchy.SelectedItem.Title.LastIndexOf(" ");
							string strInd = tabControlObjHierarchy.SelectedItem.Title.Substring(delIndex + 1);
							delIndex = Convert.ToInt32(strInd);

							UpdateResultTable(treeview.SelectedCells[0].OwningRow.Cells[0],
							"null",
							(TreeGridNode)treeview.SelectedCells[0].OwningRow.Cells[0].OwningRow, (OMETabStripItem)treeview.Parent, true, false);

							tabControlObjHierarchy.SelectedItem.Controls.Clear();
							lstObjIdLong.Remove(id);
							//long[] objectid = Helper.DbInteraction.ExecuteQueryResults(omQuery, 0);


							int m_pageCount = Convert.ToInt32(txtCurrentPage.Text);
							int startIndex = (Convert.ToInt32(txtCurrentPage.Text) * m_defaultPageSize) - m_defaultPageSize;
							int endIndex = startIndex + m_defaultPageSize;
							labelNoOfObjects.Text = lstObjIdLong.Count.ToString();

							PagingData pgData = new PagingData(startIndex, endIndex);
							pgData.ObjectId = lstObjIdLong;
							if (lstObjIdLong.Count > 0)
							{
								hashListResult = Helper.DbInteraction.ReturnQueryResults(pgData, false, omQuery.BaseClass, this.omQuery.AttributeList);
								Hashtable hAttributes = null;

								if (omQuery != null)
								{
									hAttributes = this.omQuery.AttributeList;
								}
								dbDataGridViewQueryResult.SetDatagridRowsWithIndex(hashListResult, this.ClassName,
									hAttributes, Helper.DbInteraction.runQuery.StartIndex + 1);

							}
							if (tabControlObjHierarchy.SelectedItem != null)
							{
								if (tabControlObjHierarchy.SelectedItem.Name == CONST_TRUE)
								{
									treeview = db.GetObjectHierarchy(tabControlObjHierarchy.SelectedItem.Tag, ClassName, true);
								}

								else
								{
									treeview = db.GetObjectHierarchy(tabControlObjHierarchy.SelectedItem.Tag, ClassName, false);
								}
							}
							else
							{
								treeview = db.GetObjectHierarchy(tabControlObjHierarchy.SelectedItem.Tag, ClassName, false);
							}

							tabControlObjHierarchy.SelectedItem.Controls.Add(treeview);
							RegisterTreeviewEvents();

							foreach (OMETabStripItem tp in tabControlObjHierarchy.Items)
							{

								int tabIndex = Convert.ToInt32(tp.Title.Split(' ')[1]);
								if (tabIndex > delIndex)
								{
									int newIndex = tabIndex - 1;
									string title = CONST_TAB_CAPTION + "" + newIndex.ToString();
									tp.Title = title;
								}
							}
						}
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		void treeview_OnContextMenuOpening(object sender, ContextItemClickedEventArg e)
		{
			try
			{
				treeview = e.Data as TreeGridView;
				CancelEventArgs cancelEventArgs = e.CancelEventArguments;

				dbInteraction db = new dbInteraction();

				treeview.EndEdit();

				if (treeview.SelectedRows.Count > 0)
				{

					TreeGridNode treeGridNode = treeview.SelectedCells[0].OwningRow as TreeGridNode;
					bool isPrim = db.IsPrimitive(treeGridNode.Tag);
					bool isArr = db.IsArray(treeGridNode.Tag);
					bool isdateString = db.CheckForDateTimeOrString(treeGridNode.Tag);
					bool isColl = db.IsCollection(treeGridNode.Tag);

					bool isObj = !isPrim && !isArr && !isColl;

					if ((!isObj && !isArr && !isColl && !isdateString) || treeGridNode.Tag == null || treeGridNode.Parent.Tag == null)
						cancelEventArgs.Cancel = true;
				}
				else
					cancelEventArgs.Cancel = true;
			}
			catch (Exception ex)
			{

				LoggingHelper.ShowMessage(ex);
			}
		}

		#endregion

		#region TreeView Events

		void treeview_ItemDrag(object sender, ItemDragEventArgs e)
		{
			try
			{
				DoDragDrop(e.Item, DragDropEffects.Move);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		void treeview_NodeExpanded(object sender, ExpandedEventArgs e)
		{
			try
			{
				if (e.Node.Nodes.Count > 0)
				{
					if (e.Node.Nodes[0].Cells[0].Value.ToString() == "dummy")
					{
						e.Node.Nodes.RemoveAt(0);
						TreeGridView tree = e.Node.DataGridView as TreeGridView;
						if (((OMETabStripItem)(tree.Parent)).Name == CONST_TRUE)
						{
							Helper.DbInteraction.ExpandTreeNode(e.Node, true);
						}
						else
							Helper.DbInteraction.ExpandTreeNode(e.Node, false);
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}


		void treeview_CellEndEdit(object sender, DataGridViewCellEventArgs e)
		{
			try
			{
				OMETrace.WriteFunctionStart();

				if (dbDataGridViewQueryResult.SelectedRows.Count > 0)
					dbDataGridViewQueryResult.SelectedRows[0].Selected = false;

				DataGridViewCell cell = ((TreeGridView)sender).CurrentCell;
				DataGridViewCell typecell = cell.OwningRow.Cells[2];
				object editValue = cell.Value;

				bool check = Validations.ValidateDataType(typecell.Value.ToString(), ref editValue);

				if (check == true)
				{
					if (strstoreTreeValue != editValue.ToString())
					{
						string[] tabName = ((TreeGridView)sender).Parent.Text.Split(CONST_SPACE);

						TreeGridNode currNode = null;
						hierarchy = new ArrayList();
						ArrayList offset = new ArrayList();
						ArrayList nameList = new ArrayList();
						ArrayList typeList = new ArrayList();
						try
						{
							currNode = (TreeGridNode)cell.OwningRow;
							hierarchy.Add(currNode.Tag);
							nameList.Add(currNode.Cells[0].Value);
							offset.Add(-2);
							typeList.Add(currNode.Cells[2].Value);

							TreeGridNode node = currNode;

							while (node.Parent.Tag != null)
							{
								hierarchy.Add(node.Parent.Tag);
								string name = string.Empty;
								typeList.Add(node.Parent.Cells[2].Value);
								int level = -1;

								if (Helper.DbInteraction.IsArray(node.Parent.Tag)
									|| Helper.DbInteraction.IsCollection(node.Parent.Tag))
								{
									name = (string)node.Parent.Cells[0].Value;
									level = node.Parent.Nodes.IndexOf(node);
									int index = name.IndexOf(CONST_SPACE);
									name = name.Substring(0, index);
									nameList.Add(name);
								}
								else
								{
									nameList.Add(node.Parent.Cells[0].Value);
								}

								offset.Add(level);

								node = node.Parent;
							}
							intLevel = hierarchy.Count;
							hierarchy.Reverse();
							nameList.Reverse();
							typeList.Reverse();
							offset.Reverse();
							Helper.DbInteraction.UpdateCollection(hierarchy, offset, nameList, typeList, editValue);

							tabControlObjHierarchy.SelectedItem.Name = CONST_TRUE;
							tabControlObjHierarchy.SelectedItem.Tag = ((TreeGridView)sender).Parent.Tag;

							OMETabStripItem pg = (OMETabStripItem)((TreeGridView)sender).Parent;
							pg.Name = CONST_TRUE;




							UpdateResultTable(cell, editValue, currNode, pg, false, false);
							cell.OwningRow.Selected = true;

						}
						catch (Exception ex)
						{
							LoggingHelper.ShowMessage(ex);
						}
					}
				}
				else
				{
					cell.Style.ForeColor = Color.Black;
					cell.Style.SelectionForeColor = Color.White;
					cell.Value = strstoreTreeValue;
					strstoreTreeValue = string.Empty;
				}

				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

		}

		private void UpdateResultTable(DataGridViewCell cell, object editValue, TreeGridNode currNode, OMETabStripItem pg, bool toDelete, bool updateToNull)
		{

			string columnName = string.Empty;

			//Update the edited value to the Result DataGridView
			try
			{
				int rowIndex = Convert.ToInt32(pg.Title.Split(CONST_SPACE)[1].ToString());
				int pageIndex = rowIndex % m_defaultPageSize;
				if (pageIndex == 0)
					pageIndex = m_defaultPageSize;
				if (!toDelete)
				{
					if (currNode.Parent.Cells[1].Value != null && omQuery.AttributeList.Count > 0)
					{
						columnName = GetFullPath(currNode);
					}
					else
					{
						columnName = currNode.Cells[0].Value.ToString();
					}
					//This fix is applied cause (G) also contains "(" and therefore next line will not fail
					//If (G) is removed from the columnname.

					if (columnName.Contains("(G)"))
					{
						int index1 = columnName.IndexOf("(G)");
						columnName = columnName.Remove(index1, 3);
					}

					if (columnName.Contains("("))
					{
						int index = columnName.IndexOf('(');
						columnName = columnName.Remove(index - 1, columnName.Length - index + 1);
					}

					foreach (DataGridViewColumn col in dbDataGridViewQueryResult.Columns)
					{
						if (col.HeaderText == columnName)
						{
							dbDataGridViewQueryResult.Rows[pageIndex - 1].Cells[columnName].Value = editValue.ToString();
							break;
						}
					}

					dbDataGridViewQueryResult.Rows[pageIndex - 1].Cells[1].Selected = true;

					if (!updateToNull)
					{
						dbDataGridViewQueryResult.Rows[pageIndex - 1].Cells[Common.Constants.QUERY_GRID_ISEDITED_HIDDEN].Value = true;
						buttonSaveResult.Enabled = true;
						cell.Style.ForeColor = Color.Red;
						cell.Style.SelectionForeColor = Color.Red;
					}
				}
				else
				{
					tabControlObjHierarchy.RemoveTab(tabControlObjHierarchy.SelectedItem);
					dbDataGridViewQueryResult.Rows.RemoveAt(pageIndex - 1);

				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		void treeview_CellBeginEdit(object sender, DataGridViewCellCancelEventArgs e)
		{
			try
			{
				buttonSaveResult.Enabled = true;
				if (dbDataGridViewQueryResult.SelectedRows.Count > 0)
					dbDataGridViewQueryResult.SelectedRows[0].Selected = false;

				DataGridViewCell cell = ((TreeGridView)sender).CurrentCell;
				DataGridViewCell typecell = cell.OwningRow.Cells[2];


				if (cell.Value != null)
					strstoreTreeValue = cell.Value.ToString();
				else
					strstoreTreeValue = string.Empty;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		void treeview_NodeMouseClick(object sender, TreeNodeMouseClickEventArgs e)
		{
			try
			{
				if (e.Node.Tag != null)
				{
					Helper.SelectedObject = e.Node.Tag;
					Helper.Depth = e.Node.Level;
					Helper.CreatePropertiesPaneToolWindow(false);
				}
				else
				{
					Helper.SelectedObject = null;
					Helper.ClassName = null;
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		#endregion

		#region Buttons Events

		private void btnSave_Click(object sender, EventArgs e)
		{
			try
			{
				OMETrace.WriteFunctionStart();

				foreach (DataGridViewRow row in dbDataGridViewQueryResult.Rows)
				{
					object obj = row.Tag;

					if (Convert.ToBoolean(row.Cells[Common.Constants.QUERY_GRID_ISEDITED_HIDDEN].Value) == true)
					{
						row.Cells[Common.Constants.QUERY_GRID_ISEDITED_HIDDEN].Value = false;
						Helper.DbInteraction.SaveObjects(obj);
						Helper.IsQueryResultUpdated = true;
					}
				}
				int startindex = (int)dbDataGridViewQueryResult.Rows[dbDataGridViewQueryResult.Rows.Count - 1].Cells[1].Value;
				int endindex = startindex + m_defaultPageSize;
				if (endindex > lstObjIdLong.Count && startindex < lstObjIdLong.Count)
				{
					endindex = lstObjIdLong.Count;
				}
				PagingData pagingData = new PagingData(startindex, endindex);
				pagingData.ObjectId = lstObjIdLong;
				hashListResult = Helper.DbInteraction.ReturnQueryResults(pagingData, true, omQuery.BaseClass, this.omQuery.AttributeList);

				MakeAllElementsInGridBlack(dbDataGridViewQueryResult);
				btnSave.Enabled = false;
				buttonSaveResult.Enabled = false;

				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void btnDelete_Click(object sender, EventArgs e)
		{
			try
			{
				OMETrace.WriteFunctionStart();
				int m_pageCount = Convert.ToInt32(txtCurrentPage.Text);
				long deletedId;
				dbInteraction dbI = new dbInteraction();
				if (dbDataGridViewQueryResult.SelectedRows.Count > 0)
				{
					DataGridViewRow row = dbDataGridViewQueryResult.SelectedRows[0];

					const string strShowMessage = "Do You want to CascadeonDelete?";
					DialogResult dialogRes = MessageBox.Show(strShowMessage, Helper.GetResourceString(Common.Constants.PRODUCT_CAPTION), MessageBoxButtons.YesNoCancel,
						MessageBoxIcon.Question);
					deletedId = dbI.GetLocalID(row.Tag);
					if (dialogRes == DialogResult.Yes)
					{
						Helper.IsQueryResultUpdated = true;
						deletgateCascadeonDelete deldelete = new deletgateCascadeonDelete(CascadeOndeleteobjects);
						deldelete.Invoke(row.Tag);

					}
					else if (dialogRes == DialogResult.No)
					{
						Helper.IsQueryResultUpdated = true;
						Helper.DbInteraction.DeleteObject(row.Tag, false);
					}
					else
					{
						return;
					}
					int delIndex = tabControlObjHierarchy.SelectedItem.Title.LastIndexOf(" ");
					string ind1 = tabControlObjHierarchy.SelectedItem.Title.Substring(delIndex + 1);
					delIndex = Convert.ToInt32(ind1);



					foreach (OMETabStripItem pg in tabControlObjHierarchy.Items)
					{
						if (pg.Tag == row.Tag)
						{
							tabControlObjHierarchy.RemoveTab(pg);
							break;
						}
					}
					foreach (OMETabStripItem tp in tabControlObjHierarchy.Items)
					{

						int tabIndex = Convert.ToInt32(tp.Title.Split(' ')[1]);
						if (tabIndex > delIndex)
						{
							int newIndex = tabIndex - 1;
							string title = CONST_TAB_CAPTION + "" + newIndex;
							tp.Title = title;

						}
					}

					lstObjIdLong.Remove(deletedId);

					int pageNumber = m_pagingStartIndex + 1;
					int startIndex = (m_pageCount * m_defaultPageSize) - m_defaultPageSize;
					int endIndex = startIndex + m_defaultPageSize;
					PagingData pagData = new PagingData(startIndex, endIndex);
					pagData.ObjectId = lstObjIdLong;

					lblPageCount.Text = pagData.GetPageCount().ToString();
					txtCurrentPage.Text = pageNumber.ToString();
					labelNoOfObjects.Text = pagData.ObjectId.Count.ToString();

					dbDataGridViewQueryResult.Rows.Clear();
					if (lstObjIdLong.Count > 0)
					{
						hashListResult = Helper.DbInteraction.ReturnQueryResults(pagData, true, omQuery.BaseClass, omQuery.AttributeList);
						Hashtable hAttributes = null;

						if (omQuery != null)
						{
							hAttributes = omQuery.AttributeList;
						}
						dbDataGridViewQueryResult.SetDatagridRowsWithIndex(hashListResult, this.ClassName, hAttributes, Helper.DbInteraction.runQuery.StartIndex + 1);

						int rowIndex = Convert.ToInt32(tabControlObjHierarchy.SelectedItem.Title.Split(CONST_SPACE)[1]);

						int pageIndex = rowIndex % m_defaultPageSize;
						if (pageIndex == 0)
							pageIndex = m_defaultPageSize - 1;

						//If the last object is deleted then it tries to select the next row which does not exist
						//therfore this check
						if (rowIndex - 1 != lstObjIdLong.Count)
						{
							dbDataGridViewQueryResult.Rows[pageIndex - 1].Selected = true;
							dbDataGridViewQueryResult.Rows[pageIndex - 1].Cells[1].Selected = true;
						}
					}

					Helper.SelectedObject = null;
					buttonSaveResult.Enabled = false;
				}
				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		public void CascadeOndeleteobjects(object obj)
		{

			System.Threading.Thread t = new System.Threading.Thread(ShowDialogforProgressBar);
			t.Start();
			Helper.DbInteraction.DeleteObject(obj, true);
			t.Abort();

		}

		private static void ShowDialogforProgressBar()
		{
			try
			{
				ProgressBar p = new ProgressBar();
				p.Text = "CascadeonDelete in progress...";
				p.ShowDialog();
			}
			catch (System.Threading.ThreadAbortException)
			{
				System.Threading.Thread.ResetAbort();

			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void buttonSaveResult_Click(object sender, EventArgs e)
		{
			try
			{
				OMETrace.WriteFunctionStart();
				if (tabControlObjHierarchy != null)
				{
					foreach (OMETabStripItem pg in tabControlObjHierarchy.Items)
					{
						if (pg.Name.Equals(CONST_TRUE))
						{
							if (hierarchy != null)
								Helper.DbInteraction.SaveCollection(pg.Tag, hierarchy.Count);
							else
								Helper.DbInteraction.SaveCollection(pg.Tag, 1);

							PaintBlack((TreeGridView)pg.Controls[0]);
							pg.Name = CONST_FALSE;
							int rowIndex = Convert.ToInt32(pg.Title.Split(CONST_SPACE)[1]);
							int pageIndex = rowIndex % m_defaultPageSize;
							if (pageIndex == 0)
								pageIndex = m_defaultPageSize;
							dbDataGridViewQueryResult.Rows[pageIndex - 1].Cells[Common.Constants.QUERY_GRID_ISEDITED_HIDDEN].Value = false;
						}
					}

					Helper.IsQueryResultUpdated = true;
					int startindex = (int)dbDataGridViewQueryResult.Rows[dbDataGridViewQueryResult.Rows.Count - 1].Cells[1].Value;
					int endindex = startindex + m_defaultPageSize;
					if (endindex > lstObjIdLong.Count && startindex < lstObjIdLong.Count)
					{
						endindex = lstObjIdLong.Count;
					}
					PagingData pagingData = new PagingData(startindex, endindex);
					pagingData.ObjectId = lstObjIdLong;
					hashListResult = Helper.DbInteraction.ReturnQueryResults(pagingData, false, omQuery.BaseClass, omQuery.AttributeList);
					btnSave.Enabled = false;
					buttonSaveResult.Enabled = false;
				}
				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void btnPrevious_Click(object sender, EventArgs e)
		{
			try
			{
				m_pageCount--;
				if (m_pageCount <= 0)
					m_pageCount = m_pagingStartIndex + 1;

				txtCurrentPage.Text = m_pageCount.ToString();
				KeyEventArgs keyArgs = new KeyEventArgs(Keys.Enter);
				txtObjectNumber_KeyDown(txtCurrentPage, keyArgs);

				if (m_pageCount == 1)
				{
					btnPrevious.Enabled = false;
					btnLast.Enabled = true;
					btnFirst.Enabled = false;
					btnNext.Enabled = true;
				}
				else
				{
					btnPrevious.Enabled = true;
					btnNext.Enabled = true;
					btnLast.Enabled = true;
					btnFirst.Enabled = true;
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

		}

		private void btnLast_Click(object sender, EventArgs e)
		{

			try
			{

				m_pageCount = Convert.ToInt32(lblPageCount.Text);
				txtCurrentPage.Text = lblPageCount.Text;

				KeyEventArgs keyArgs = new KeyEventArgs(Keys.Enter);
				txtObjectNumber_KeyDown(txtCurrentPage, keyArgs);

				if (m_pageCount == Convert.ToInt32(lblPageCount.Text))
				{
					btnPrevious.Enabled = true;
					btnLast.Enabled = false;
					btnFirst.Enabled = true;
					btnNext.Enabled = false;
				}
				else
				{
					btnPrevious.Enabled = true;
					btnNext.Enabled = true;
					btnLast.Enabled = true;
					btnFirst.Enabled = true;
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);

			}
		}

		private void btnNext_Click(object sender, EventArgs e)
		{

			try
			{

				m_pageCount++;

				txtCurrentPage.Text = m_pageCount.ToString();
				KeyEventArgs keyArgs = new KeyEventArgs(Keys.Enter);
				txtObjectNumber_KeyDown(txtCurrentPage, keyArgs);

				if (m_pageCount >= Convert.ToInt32(lblPageCount.Text))
				{
					btnPrevious.Enabled = true;
					btnLast.Enabled = false;
					btnFirst.Enabled = true;
					btnNext.Enabled = false;
				}
				else
				{
					btnPrevious.Enabled = true;
					btnNext.Enabled = true;
					btnLast.Enabled = true;
					btnFirst.Enabled = true;
				}

			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}


		private void RefreshPaging(ref bool check, ref DialogResult dialogRes, ref bool checkforValueChanged, dbDataGridView db)
		{
			try
			{

				foreach (DataGridViewRow row in db.Rows)
				{


					if (Convert.ToBoolean(row.Cells[Common.Constants.QUERY_GRID_ISEDITED_HIDDEN].Value) == true)
					{
						checkforValueChanged = true;
						break;
					}
				}
				if (checkforValueChanged == true)
				{
					dialogRes = MessageBox.Show("Do you want to save modified objects on this page, else they will be discarded.", Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION), MessageBoxButtons.YesNo, MessageBoxIcon.Question);
					if (dialogRes == DialogResult.Yes)
					{
						check = true;
						buttonSaveResult_Click(buttonSaveResult, null);


					}
					else
					{
						foreach (DataGridViewRow row in db.Rows)
						{


							if (Convert.ToBoolean(row.Cells[Common.Constants.QUERY_GRID_ISEDITED_HIDDEN].Value) == true)
							{
								if (hierarchy == null)
								{
									Helper.DbInteraction.RefreshObject(row.Tag, 1);
								}
								else
								{
									Helper.DbInteraction.RefreshObject(row.Tag, hierarchy.Count);
								}
								UpdateDataTreeView(row.Tag, row);

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

		//private void SaveBeforeWindowHiding(ref bool check, ref DialogResult dialogRes, ref bool checkforValueChanged, dbDataGridView db, string Caption)
		//{
		//    try
		//    {

		//        foreach (DataGridViewRow row in db.Rows)
		//        {


		//            if (Convert.ToBoolean(row.Cells[Common.Constants.QUERY_GRID_ISEDITED_HIDDEN].Value) == true)
		//            {
		//                checkforValueChanged = true;
		//                break;
		//            }
		//        }
		//        if (checkforValueChanged == true)
		//        {
		//            dialogRes = MessageBox.Show("Window '"+Caption +"' had few modified objects,do you want to save them?", Helper.GetResourceString(OMControlLibrary.Common.Constants.PRODUCT_CAPTION), MessageBoxButtons.YesNo, MessageBoxIcon.Question);
		//            if (dialogRes == DialogResult.Yes)
		//            {
		//                foreach (DataGridViewRow row in db.Rows)
		//                {


		//                    if (Convert.ToBoolean(row.Cells[Common.Constants.QUERY_GRID_ISEDITED_HIDDEN].Value) == true)
		//                    {

		//                        if (hierarchy != null)
		//                            Helper.DbInteraction.SaveCollection(row.Tag, hierarchy.Count);
		//                        else
		//                            Helper.DbInteraction.SaveCollection(row.Tag, 1);
		//                    }
		//                }                       


		//            }
		//            else
		//            {
		//                foreach (DataGridViewRow row in db.Rows)
		//                {


		//                    if (Convert.ToBoolean(row.Cells[Common.Constants.QUERY_GRID_ISEDITED_HIDDEN].Value) == true)
		//                    {
		//                        if (hierarchy == null)
		//                        {
		//                            Helper.DbInteraction.RefreshObject(row.Tag, 1);
		//                        }
		//                        else
		//                        {
		//                            Helper.DbInteraction.RefreshObject(row.Tag, hierarchy.Count);
		//                        }


		//                    }
		//                }

		//            }
		//        }

		//    }
		//    catch (Exception ex)
		//    {
		//        LoggingHelper.ShowMessage(ex);
		//    }

		//}




		private void btnFirst_Click(object sender, EventArgs e)
		{

			try
			{


				m_pageCount = m_pagingStartIndex + 1;
				txtCurrentPage.Text = m_pageCount.ToString();

				KeyEventArgs keyArgs = new KeyEventArgs(Keys.Enter);
				txtObjectNumber_KeyDown(txtCurrentPage, keyArgs);


				if (m_pageCount == 1)
				{
					btnPrevious.Enabled = false;
					btnLast.Enabled = true;
					btnFirst.Enabled = false;
					btnNext.Enabled = true;
				}
				else
				{
					btnPrevious.Enabled = true;
					btnNext.Enabled = true;
					btnLast.Enabled = true;
					btnFirst.Enabled = true;
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void txtObjectNumber_KeyDown(object sender, KeyEventArgs e)
		{
			try
			{
			    Hashtable hAttributes = null;
				bool check = false;
				DialogResult dialogRes = DialogResult.Ignore;
				PagingData pagingData;
			    bool checkforValueChanged = false;

				if (e.Modifiers == Keys.Control)
				{
					e.Handled = true;
				}

				if (e.KeyCode == Keys.Enter)
				{
					if (dbDataGridViewQueryResult.SortedColumn != null)
						dbDataGridViewQueryResult.SortedColumn.HeaderCell.SortGlyphDirection = SortOrder.None;

					RefreshPaging(ref check, ref dialogRes, ref checkforValueChanged, dbDataGridViewQueryResult);

					if (Convert.ToInt32(txtCurrentPage.Text) > Convert.ToInt32(lblPageCount.Text))
						txtCurrentPage.Text = lblPageCount.Text;
					else if (Convert.ToInt32(txtCurrentPage.Text) == 0)
						txtCurrentPage.Text = m_pagingStartIndex.ToString();


					if (!string.IsNullOrEmpty(txtCurrentPage.Text.Trim()) &&
						Convert.ToInt32(txtCurrentPage.Text) <= Convert.ToInt32(lblPageCount.Text))
					{
						m_pageCount = Convert.ToInt32(txtCurrentPage.Text);
						int startIndex = (Convert.ToInt32(txtCurrentPage.Text) * m_defaultPageSize) - m_defaultPageSize;
						int endIndex = startIndex + m_defaultPageSize;
						//objectid = Helper.DbInteraction.ExecuteQueryResults(omQuery,1);
						pagingData = new PagingData(startIndex, endIndex);
						pagingData.ObjectId = this.lstObjIdLong;
						if (lstObjIdLong.Count > 0)
						{
							hashListResult = Helper.DbInteraction.ReturnQueryResults(pagingData, true, omQuery.BaseClass, this.omQuery.AttributeList);

							if (omQuery != null)
							{
								hAttributes = this.omQuery.AttributeList;
							}

							dbDataGridViewQueryResult.SetDatagridRowsWithIndex(hashListResult, this.ClassName, hAttributes, pagingData.StartIndex + 1);

							ListofModifiedObjects.AddDatagrid(this.ClassName, dbDataGridViewQueryResult);

						}

						int totalPages = Convert.ToInt32(lblPageCount.Text);

						if (m_pageCount == 1 && totalPages == 1)
						{
							btnPrevious.Enabled = false;
							btnLast.Enabled = false;
							btnFirst.Enabled = false;
							btnNext.Enabled = false;
							if (m_pageCount == 1 && Convert.ToInt32(lblPageCount.Text) == 1)
								btnLast.Enabled = false;
							btnNext.Enabled = false;
							return;
						}

						if (m_pageCount >= totalPages)
						{
							btnPrevious.Enabled = true;
							btnLast.Enabled = false;
							btnFirst.Enabled = true;
							btnNext.Enabled = false;
						}
						else if (m_pageCount < totalPages && m_pageCount > 1)
						{
							btnPrevious.Enabled = true;
							btnNext.Enabled = true;
							btnLast.Enabled = true;
							btnFirst.Enabled = true;
						}
						else if (m_pageCount <= 1)
						{
							btnPrevious.Enabled = false;
							btnLast.Enabled = true;
							btnFirst.Enabled = false;
							btnNext.Enabled = true;
						}
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void txtObjectNumber_KeyPress(object sender, KeyPressEventArgs e)
		{
			try
			{
				char c = e.KeyChar;

				//Allow only numeric charaters in filter textbox.
				if (!Helper.IsNumeric(c.ToString()))
					e.Handled = true;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		#endregion

		#region Other Events

		void tabControlObjHierarchy_TabStripItemSelectionChanged(TabStripItemChangedEventArgs e)
		{
			FinishPendingEdits();

			if (IsEmptySelectionChange(e))
				return;

			OMETabStripItem item = e.Item;
			if (string.IsNullOrEmpty(item.Title))
				return;

			try
			{
				int objectIndex = ObjectIndexInMasterViewFor(item);
				EnsureCurrentPageIs(PageNumberFor(objectIndex));

				//This check helps avaoiding recurssion.
				if (dbDataGridViewQueryResult.SortOrder == SortOrder.None)
				{
					SetSelectedObjectInMasterView(objectIndex);
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void SetSelectedObjectInMasterView(int objectIndex)
		{
			DataGridViewRow row = dbDataGridViewQueryResult.Rows[OffsetInCurrentPageFor(objectIndex) - 1];
			row.Selected = true;
			row.Cells[1].Selected = true;
		}

		private void EnsureCurrentPageIs(int pageNumber)
		{
			if (CurrentPageNumber() != pageNumber)
			{
				SetCurrentPageTo(pageNumber);
			}
		}

		private void SetCurrentPageTo(int pageNumber)
		{
			txtCurrentPage.Text = pageNumber.ToString();
			txtObjectNumber_KeyDown(txtCurrentPage, new KeyEventArgs(Keys.Enter));
		}

		private int CurrentPageNumber()
		{
			return Convert.ToInt32(txtCurrentPage.Text);
		}

		private static int PageNumberFor(int masterObjectIndex)
		{
			double pageNumber = (double) masterObjectIndex / m_defaultPageSize;
			return Math.Max(Convert.ToInt32(Math.Ceiling(pageNumber)), 1);
		}

		private static int OffsetInCurrentPageFor(int masterViewObjectIndex)
		{
			int offset = masterViewObjectIndex % m_defaultPageSize;
			return offset == 0 ? m_defaultPageSize : offset;
		}

		private static int ObjectIndexInMasterViewFor(OMETabStripItem item)
		{
			return Convert.ToInt32(item.Title.Split(CONST_SPACE)[1]);
		}

		private static bool IsEmptySelectionChange(TabStripItemChangedEventArgs e)
		{
			return e.Item == null || (e.ChangeType != OMETabStripItemChangeTypes.SelectionChanged);
		}

		private void FinishPendingEdits()
		{
			dbDataGridViewQueryResult.EndEdit();
			treeview.EndEdit();
		}

		private void panelResultGridOptions_SizeChanged(object sender, EventArgs e)
		{
			if (panelResultGridOptions.Width <= panelLeft.Width + panelRight.Width)
			{
				//this.Dock = DockStyle.None;
				panelResultGridOptions.MinimumSize = new Size(panelLeft.Width + panelRight.Width, this.Height);
			}
		}

		private void txtObjectNumber_TextChanged(object sender, EventArgs e)
		{
			try
			{
				int result = 0;
				if (!Int32.TryParse(txtCurrentPage.Text.Trim(), out result))
				{
					if (result <= 0)
					{
						int pageNo = result + 1;
						txtCurrentPage.Text = pageNo.ToString();
					}
					else
						txtCurrentPage.Text = result.ToString();

					txtCurrentPage.SelectAll();
				}

				if (string.IsNullOrEmpty(txtCurrentPage.Text) || txtCurrentPage.Text == "0")
				{
					int pageNumber = m_pagingStartIndex + 1;
					txtCurrentPage.Text = pageNumber.ToString();
					txtCurrentPage.SelectAll();
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		#endregion

		#region Public and Internal Helper Methods

		public override void SetLiterals()
		{
			base.SetLiterals();

			try
			{
				buttonSaveResult.Text = Helper.GetResourceString(Common.Constants.BUTTON_SAVE_CAPTION);
				btnSave.Text = Helper.GetResourceString(Common.Constants.BUTTON_SAVE_CAPTION);
				btnDelete.Text = Helper.GetResourceString(Common.Constants.BUTTON_DELETE_CAPTION);
				lblFechedObjects.Text = Helper.GetResourceString(Common.Constants.LABEL_OBJECTS_NO);
				lblof.Text = Helper.GetResourceString(Common.Constants.LABEL_OF);
				toolTipPagging.SetToolTip(btnFirst, Helper.GetResourceString(Common.Constants.TOOLTIP_PAGE_FIRST));
				toolTipPagging.SetToolTip(btnPrevious, Helper.GetResourceString(Common.Constants.TOOLTIP_PAGE_PREV));
				toolTipPagging.SetToolTip(btnNext, Helper.GetResourceString(Common.Constants.TOOLTIP_PAGE_NEXT));
				toolTipPagging.SetToolTip(btnLast, Helper.GetResourceString(Common.Constants.TOOLTIP_PAGE_LAST));
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		internal void ClearResult()
		{
			try
			{
				dbDataGridViewQueryResult.Rows.Clear();
				dbDataGridViewQueryResult.Columns.Clear();

				tabControlObjHierarchy.Items.Clear();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		internal Hashtable GetQueryAttributes()
		{
			Hashtable list = new Hashtable();

			this.listQueryAttributes = queryBuilder.GetSelectedAttributes();//this.listQueryAttributes;
			IDictionaryEnumerator enumerator = this.listQueryAttributes.GetEnumerator();

			try
			{
				while (enumerator.MoveNext())
				{
					string fieldName = enumerator.Key.ToString();
					string className = enumerator.Value.ToString();

					list.Add(fieldName, className);
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

			return list;
		}

		internal void CheckColor(TreeGridNode node)
		{
			try
			{
				if (node.Cells[1].Style.ForeColor == Color.Red)
				{
					node.Cells[1].Style.ForeColor = Color.Black;
					node.Cells[1].Style.SelectionForeColor = Color.White;

				}
				if (node.HasChildren)
				{
					foreach (TreeGridNode currnode in node.Nodes)

						CheckColor(currnode);
				}

			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		internal void PaintBlack(TreeGridView treeobj)
		{
			try
			{
				TreeGridNode node = treeobj.Nodes[0];
				CheckColor(node);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

		}

		#endregion

		#region Private Helper Methods

		private void InitializeTabControl()
		{
			tabControlObjHierarchy = new OMETabStrip();

			tabControlObjHierarchy.Dock = DockStyle.Fill;
			tabControlObjHierarchy.Show();
			tableLayoutPanelResult.Controls.Add(tabControlObjHierarchy, 0, 0);
			tabControlObjHierarchy.TabStripItemSelectionChanged +=
				new TabStripItemChangedHandler(tabControlObjHierarchy_TabStripItemSelectionChanged);
			tabControlObjHierarchy.Click += new EventHandler(tabControlObjHierarchy_Click);
			tabControlObjHierarchy.TabStripItemClosing += new TabStripItemClosingHandler(tabControlObjHierarchy_TabStripItemClosing);
		}

		void tabControlObjHierarchy_Click(object sender, EventArgs e)
		{

			OMETabStripItem item = ((OMETabStrip)sender).SelectedItem;
			if (item != null)
				CheckForObjectPropertiesTab(item.Tag);

		}

		private void CheckForObjectPropertiesTab(object SelectedObject)
		{
			try
			{
				if (tabControlObjHierarchy.Items.Count >= 1)
				{
					PropertiesTab propertiesTab = PropertiesTab.Instance;
					if (propertiesTab.ShowObjectPropertiesTab == false)
					{
						propertiesTab.ShowObjectPropertiesTab = true;
						propertiesTab.RefreshPropertiesTab(SelectedObject);
					}

				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		void tabControlObjHierarchy_TabStripItemClosing(TabStripItemClosingEventArgs e)
		{
			if (tabControlObjHierarchy.Controls.Count == 1)
				e.Cancel = true;
		}

		private void InitializeResultDataGridView()
		{
			try
			{
				dbDataGridViewQueryResult = new dbDataGridView();
				dbDataGridViewQueryResult.Size = this.Size;
				dbDataGridViewQueryResult.ReadOnly = false;
				dbDataGridViewQueryResult.EditMode = DataGridViewEditMode.EditOnF2;
				dbDataGridViewQueryResult.ScrollBars = ScrollBars.Both;
				dbDataGridViewQueryResult.AllowUserToOrderColumns = true;
				dbDataGridViewQueryResult.AllowUserToResizeColumns = true;
				this.dbDataGridViewQueryResult.AllowDrop = true;
				this.dbDataGridViewQueryResult.Dock = System.Windows.Forms.DockStyle.Fill;
				this.dbDataGridViewQueryResult.SelectionChanged += new EventHandler(dbDataGridViewQueryResult_SelectionChanged);
				this.dbDataGridViewQueryResult.CellEndEdit += new DataGridViewCellEventHandler(dbDataGridViewQueryResult_CellEndEdit);
				this.dbDataGridViewQueryResult.CellBeginEdit += new DataGridViewCellCancelEventHandler(dbDataGridViewQueryResult_CellBeginEdit);
				// this.dbDataGridViewQueryResult.MouseDoubleClick += new MouseEventHandler(dbDataGridViewQueryResult_MouseDoubleClick);
				this.dbDataGridViewQueryResult.Click += new EventHandler(dbDataGridViewQueryResult_Click);
				tableLayoutPanelResultGrid.Controls.Add(dbDataGridViewQueryResult, 0, 0);

			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}




		private void dbDataGridViewQueryResult_Click(object sender, EventArgs e)
		{
			dbDataGridViewQueryResult_SelectionChanged(sender, e);
		}

		private void UpdateDataTreeView(object objUpdated, DataGridViewRow row)
		{
			try
			{
				OMETrace.WriteFunctionStart();

				if (objUpdated != null)
				{
					foreach (OMETabStripItem tp in tabControlObjHierarchy.Items)
					{
						if (tp.Tag.Equals(row.Tag))
						{
							tp.Controls.Clear();
							treeview = Helper.DbInteraction.GetObjectHierarchy(objUpdated, ClassName, true);
							treeview.Dock = DockStyle.Fill;

							tp.Controls.Add(treeview);
							treeview.Visible = true;
							RegisterTreeviewEvents();
							Helper.SelectedObject = treeview.Nodes[0].Tag;
							tp.Name = CONST_TRUE;
							break;
						}
					}
				}

				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private static void MakeAllElementsInGridBlack(DataGridView gridView)
		{
			try
			{
				foreach (DataGridViewRow row in gridView.Rows)
				{
					foreach (DataGridViewCell cell in row.Cells)
					{
						if (cell.Style.ForeColor == Color.Red)
						{
							cell.Style.ForeColor = Color.Black;
							cell.Style.SelectionForeColor = Color.White;

						}
					}
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}


	    private static string GetFullPath(TreeGridNode treenode)
		{
			StringBuilder fullpath = new StringBuilder(string.Empty);
			TreeGridNode treenodeParent;
			List<string> stringParent = new List<string>();
			string parentName = string.Empty;
			string assemplyName = string.Empty;
			string fillpathString = string.Empty;

			try
			{
				OMETrace.WriteFunctionStart();

				treenodeParent = treenode.Parent;
				while (treenodeParent != null)
				{

					if (treenodeParent.Cells[0].Value.ToString().IndexOf(CONST_COMMA) != -1)
					{
						//Set the base class name for selected field
						Helper.BaseClass = treenodeParent.Cells[0].Value.ToString();

						parentName = treenodeParent.Cells[0].Value.ToString().Split(CONST_COMMA.ToCharArray())[0];
					    int classIndex = parentName.LastIndexOf(CONST_DOT);
						parentName = parentName.Substring(classIndex + 1, parentName.Length - classIndex - 1);
					}
					else
						parentName = treenodeParent.Cells[0].Value.ToString();

					stringParent.Add(parentName);

					if (treenodeParent.Parent.RowIndex != -1)
					{
						treenodeParent = treenodeParent.Parent;
					}
					else
						break;
				}

				for (int i = stringParent.Count; i > 0; i--)
				{
					string parent = stringParent[i - 1] + ".";
					fullpath.Append(parent);
				}

				fillpathString = fullpath.Append(treenode.Cells[0].Value.ToString()).ToString();

				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}

			return fillpathString;
		}

		#endregion

		private void tableLayoutPanelResultGrid_Resize(object sender, EventArgs e)
		{
			ResizeColumnWidth();
		}

		private void ResizeColumnWidth()
		{
			//if (dbDataGridViewQueryResult != null)
			//{
			//    int w = tableLayoutPanelResultGrid.Width;
			//    if (dbDataGridViewQueryResult.Columns.Count > 0)
			//    {
			//        if (dbDataGridViewQueryResult.Columns[2].Width * (dbDataGridViewQueryResult.Columns.Count - 2) < dbDataGridViewQueryResult.Width)
			//        {
			//            for (int i = 2; i < dbDataGridViewQueryResult.Columns.Count; i++)
			//            {
			//                dbDataGridViewQueryResult.Columns[i].Width = (w + 100) / dbDataGridViewQueryResult.Columns.Count - 2;
			//            }
			//        }
			//    }

			//    dbDataGridViewQueryResult.ScrollBars = ScrollBars.Both;
			//}
		}
	}
}
