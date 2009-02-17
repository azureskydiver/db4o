/* Copyright (C) 2004 - 2009  db4objects Inc.  http://www.db4o.com */

using System;
using System.Collections;
using System.Collections.Generic;
using System.ComponentModel;
using System.Drawing;
using System.Runtime.InteropServices;
using System.Text;
using System.Threading;
using System.Windows.Forms;
using EnvDTE;
using EnvDTE80;
using OManager.BusinessLayer.Common;
using OManager.BusinessLayer.QueryManager;
using OManager.BusinessLayer.UIHelper;
using OManager.DataLayer.Reflection;
using OMControlLibrary.Common;
using OME.AdvancedDataGridView;
using OME.Logging.Common;
using OME.Logging.Tracing;
using Constants=OMControlLibrary.Common.Constants;
using Thread=System.Threading.Thread;

namespace OMControlLibrary
{
	[ComVisible(true)]
	public partial class QueryResult : ViewBase
	{
		#region Member Variables

		private string strstoreValue;
		private string strstoreTreeValue;
		internal string ClassName = Helper.BaseClass;

		internal ArrayList hierarchy;

		internal List<Hashtable> hashListResult = Helper.HashList;
		internal Hashtable listQueryAttributes;
		private dbDataGridView masterView;
		private TreeGridView treeview = new TreeGridView();
		private OMETabStrip tabControlObjHierarchy;

		internal OMQuery omQuery;
		internal long[] objectid;

		private Hashtable cellUpdated;

		//Constants
		private const string COLUMN_NUMBER = "No.";
		private const string CONST_DOT = ".";
		private const char CONST_SPACE = ' ';
		private const string CONST_TAB_CAPTION = "Object ";
		private const string CONST_TRUE = "true";
		private const string CONST_FALSE = "false";
		private const string CONST_COMMA = ",";

		private const int m_pagingStartIndex = 0;
		private int m_pageCount = 1;

		private readonly WindowVisibilityEvents windowsVisEvents;
		private readonly WindowEvents _windowsEvents;

		private List<long> lstObjIdLong;

		#endregion

		public void Setobjectid(long[] objectid)
		{
			this.objectid = objectid;
		}

		#region Constructor

		public QueryResult()
		{
			strstoreTreeValue = string.Empty;
			strstoreValue = string.Empty;
			try
			{
				SetStyle(ControlStyles.CacheText | ControlStyles.OptimizedDoubleBuffer, true);

				InitializeComponent();

				treeview.AllowDrop = true;

				omQuery = (OMQuery) Helper.OMResultedQuery[ClassName];
				listQueryAttributes = omQuery.AttributeList;

				Events events = ApplicationObject.Events;
				_windowsEvents = events.get_WindowEvents(null);
				_windowsEvents.WindowActivated += _windowsEvents_WindowActivated;
				Events2 event_1 = (Events2) ApplicationObject.Events;
				windowsVisEvents = event_1.get_WindowVisibilityEvents(Helper.QueryResultToolWindow);
				windowsVisEvents.WindowHiding += windowsVisEvents_WindowHiding;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		#endregion

		#region WindowEvents

		private static void _windowsEvents_WindowActivated(Window GotFocus, Window LostFocus)
		{
			if (LostFocus.Caption == "Closed")
				return;

			if (GotFocus.Caption != "Query Builder" && GotFocus.Caption != "db4o Browser" &&
			    GotFocus.Caption != "DataBase Properties" && GotFocus.Caption != "")
			{
				PropertiesTab.Instance.ShowObjectPropertiesTab = false;
				SelectTreeNodeInObjBrowser(GotFocus.Caption);
			}
		}


		private static void SelectTreeNodeInObjBrowser(string winCaptionArg)
		{
			string winCaption = winCaptionArg;
			foreach (DictionaryEntry entry in Helper.HashClassGUID)
			{
				string enumwinCaption = entry.Key.ToString();
				int index = enumwinCaption.LastIndexOf(',');
				string strClassName = enumwinCaption.Remove(0, index);

				string str = enumwinCaption.Remove(index);

				index = str.IndexOf('.');
				string caption = str.Remove(0, index + 1) + strClassName;

				if (winCaption != caption)
					continue;

				dbTreeView view = CurrentViewMode();
				ObjectBrowser.Instance.DbtreeviewObject.FindNSelectNode(view.Nodes[0], entry.Key.ToString(), view);
			}
		}

		private static dbTreeView CurrentViewMode()
		{
			return ObjectBrowser.Instance.ToolStripButtonAssemblyView.Checked
								? ObjectBrowser.Instance.DbAssemblyTreeView
								: ObjectBrowser.Instance.DbtreeviewObject;
		}

		public void windowsVisEvents_WindowHiding(Window Window)
		{
			string winCaption = Window.Caption;
			foreach (DictionaryEntry entry in Helper.HashClassGUID)
			{
				string enumwinCaption = entry.Key.ToString();
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

						bool checkforValueChanged = false;

						bool check = false;
						ListofModifiedObjects.SaveBeforeWindowHiding(ref check, ref checkforValueChanged, caption, db, 10);
						//TODO: Remove the hardcoded value of 10. istead there shud be alogic for counting level
						ListofModifiedObjects.Instance.Remove(enumwinCaption);
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
				CheckForIllegalCrossThreadCalls = false;
				InitializeTabControl();
				InitializeResultDataGridView();
				Hashtable hAttributes = new Hashtable();
				if (omQuery != null)
				{
					hAttributes = omQuery.AttributeList;
				}
				PagingData pagingData = new PagingData(m_pagingStartIndex);

				if (objectid != null)
				{
					lstObjIdLong = new List<long>(objectid);
					pagingData.ObjectId = lstObjIdLong;

					const int pageNumber = m_pagingStartIndex + 1;
					lblPageCount.Text = pagingData.GetPageCount().ToString();
					txtCurrentPage.Text = pageNumber.ToString();
					labelNoOfObjects.Text = pagingData.ObjectId.Count.ToString();
					if (lstObjIdLong.Count > 0)
					{
						hashListResult = Helper.DbInteraction.ReturnQueryResults(pagingData, true, omQuery.BaseClass, omQuery.AttributeList);
						masterView.SetDataGridColumnHeader(hashListResult, ClassName, omQuery.AttributeList);
						masterView.SetDatagridRowsWithIndex(hashListResult, ClassName, hAttributes, 1);
						ListofModifiedObjects.AddDatagrid(ClassName, masterView);
					}

					if (pagingData.ObjectId.Count <= PagingData.PAGE_SIZE)
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

		#endregion

		#region DataGridView Events

		private void masterView_CellEndEdit(object sender, DataGridViewCellEventArgs e)
		{
			try
			{
				OMETrace.WriteFunctionStart();

				if (treeview.SelectedRows.Count > 0)
					treeview.SelectedRows[0].Selected = false;

				DataGridViewCell cell = masterView[e.ColumnIndex, e.RowIndex];
				object currObj = cell.OwningRow.Tag;
				string headerText = masterView.Columns[e.ColumnIndex].HeaderText;
				object value = cell.Value;

			    IType type = (IType) cell.Tag;
			    if (Validations.ValidateDataType(type, ref value))
				{
					if (strstoreValue != value.ToString())
					{
						if (currObj != null)
						{
							UpdateMasterViewObjectEditedStatus(masterView.Rows[e.RowIndex], true);
							Helper.DbInteraction.EditObject(currObj, headerText, value.ToString());

                            masterView.Rows[e.RowIndex].Cells[e.ColumnIndex].Value = type.Cast(cell.Value);
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
                    masterView.Rows[e.RowIndex].Cells[e.ColumnIndex].Value = type.Cast(strstoreValue);
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

		private static void UpdateMasterViewObjectEditedStatus(DataGridViewRow row, bool edited)
		{
			row.Cells[Constants.QUERY_GRID_ISEDITED_HIDDEN].Value = edited;
		}

		private DataGridViewColumnSortMode sortStore;
		private void masterView_CellBeginEdit(object sender, DataGridViewCellCancelEventArgs e)
		{
			try
			{
				DataGridViewCell cell = ((DataGridView) sender).CurrentCell;

				dbInteraction db = new dbInteraction();
			    IType fieldType = db.GetFieldType(cell.OwningColumn.Tag.ToString(), cell.OwningColumn.HeaderText);
                if (!fieldType.IsEditable)
				{
					e.Cancel = true;
					return;
				}

				btnSave.Enabled = true;
				if (treeview.SelectedRows.Count > 0)
					treeview.SelectedRows[0].Selected = false;

				if (cell.Value != null)
					strstoreValue = cell.Value.ToString();

				sortStore = cell.OwningColumn.SortMode;
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void masterView_SelectionChanged(object sender, EventArgs e)
		{
			try
			{
				OMETrace.WriteFunctionStart();
				if (masterView.SelectedRows.Count > 0 && tabControlObjHierarchy.SelectedItem != null)
				{
					if (masterView.SelectedRows[0].Tag != null &&
					    masterView.SelectedRows[0].Tag.Equals(tabControlObjHierarchy.SelectedItem.Tag))
					{
						PropertiesTab.Instance.ShowObjectPropertiesTab = true;

						PropertiesTab.Instance.RefreshPropertiesTab(masterView.SelectedRows[0].Tag);

						return;
					}
				}
				if (masterView.SelectedRows.Count > 0)
				{
					DataGridViewRow row = masterView.SelectedRows[0];
					if (null == row)
						return;
					
					if (row.Tag != null)
					{
						OMETabStripItem foundTab = FindDetailsTabForObjectIndex(DetailsTabCaptionFor(row));
						if (null != foundTab)
						{
							tabControlObjHierarchy.SelectedItem = foundTab;
							return;
						}

						//TODO: we should either consider all current row object's subobjects
						//      or try to get rid with this dependency (Activating/Refreshing)
                        //      otherwise we will set activate to false which causes
                        //      the object (and all subjobjects) to be refreshed.
                        bool activate = IsObjectInMasterViewEdited(masterView.CurrentRow);
						treeview = Helper.DbInteraction.GetObjectHierarchy(row.Tag, ClassName, activate);
						treeview.Dock = DockStyle.Fill;

						Helper.SelectedObject = treeview.Nodes[0].Tag;
						OMETabStripItem tabPage = new OMETabStripItem(DetailsTabCaptionFor(row), treeview);
						tabPage.Tag = Helper.SelectedObject;
						tabPage.Name = tabPage.Title;
						tabControlObjHierarchy.AddTab(tabPage);

						RegisterTreeviewEvents();
						// This check helps in avoding recusrrion.
						if (masterView.SortOrder == SortOrder.None)
						{
							tabControlObjHierarchy.SelectedItem = tabPage;
						}
					}
					else
						row.Selected = false;
				}

				OMETrace.WriteFunctionEnd();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private static string DetailsTabCaptionFor(DataGridViewRow row)
		{
			return DetailsTabCaptionFor((int) row.Cells[COLUMN_NUMBER].Value);
		}

		private static string DetailsTabCaptionFor(int index)
		{
			return CONST_TAB_CAPTION + index;
		}

		private OMETabStripItem FindDetailsTabForObjectIndex(string caption)
		{
			foreach (OMETabStripItem tp in tabControlObjHierarchy.Items)
			{
				if (tp.Caption.Equals(caption))
				{
					return tp;
				}
			}
			return null;
		}

		private void RegisterTreeviewEvents()
		{
			treeview.Dock = DockStyle.Fill;
			treeview.NodeExpanded += treeview_NodeExpanded;
			treeview.CellBeginEdit += treeview_CellBeginEdit;
			treeview.CellEndEdit += treeview_CellEndEdit;
			treeview.OnContextMenuItemClicked += treeview_OnContextMenuItemClicked;
			treeview.OnContextMenuOpening += treeview_OnContextMenuOpening;
			treeview.Click += treeview_Click;
			treeview.Columns[0].Width = (treeview.Width - 2)/3;
			treeview.Columns[1].Width = (treeview.Width - 2)/3;
			treeview.Columns[2].Width = (treeview.Width - 2)/3;
		}

		private void treeview_Click(object sender, EventArgs e)
		{
			CheckForObjectPropertiesTab(treeview.Nodes[0].Tag);
		}

		private void treeview_OnContextMenuItemClicked(object sender, ContextItemClickedEventArg e)
		{
			try
			{
				if (treeview.SelectedRows.Count > 0)
				{
					treeview = e.Data as TreeGridView;
					treeview.ContextMenuStrip.Dispose();
					DialogResult dialogRes =
						MessageBox.Show("This will set the value to null in the database. Do you want to continue?",
						                Helper.GetResourceString(Constants.PRODUCT_CAPTION), MessageBoxButtons.YesNo,
						                MessageBoxIcon.Question);

					if (dialogRes == DialogResult.Yes)
					{
						TreeGridNode node = (TreeGridNode)treeview.SelectedCells[0].OwningRow;

						dbInteraction db = new dbInteraction();
						long id = db.GetLocalID(treeview.Nodes[0].Tag);

						db.SetFieldToNull(
								ParentObjectFor(node), 
								CommonValues.UndecorateFieldName(node.Cells[0].Value.ToString()));

						object obj = null;
						if (id != 0)
							obj = db.GetObjById(id);
						else
						{
							MessageBox.Show("This object is already deleted.", Helper.GetResourceString(Constants.PRODUCT_CAPTION),
							                MessageBoxButtons.OK, MessageBoxIcon.Information);
						}

						if (obj != null)
						{
							db.RefreshObject(obj, DepthFor(node));

							UpdateResultTable(treeview.SelectedCells[0].OwningRow.Cells[0],
							                  "null",
							                  (TreeGridNode) treeview.SelectedCells[0].OwningRow.Cells[0].OwningRow,
							                  (OMETabStripItem) treeview.Parent, false, true);

							treeview = Helper.DbInteraction.GetObjectHierarchy(obj, FieldTypeNameFor(treeview.Nodes[0]), false);
							tabControlObjHierarchy.SelectedItem.Controls.Clear();
							tabControlObjHierarchy.SelectedItem.Controls.Add(treeview);
							RegisterTreeviewEvents();
						}
						else //delete tab as teh obj is deleted and delete it from db grid view
						{
							int delIndex = tabControlObjHierarchy.SelectedItem.Title.LastIndexOf(" ");
							string strInd = tabControlObjHierarchy.SelectedItem.Title.Substring(delIndex + 1);
							delIndex = Convert.ToInt32(strInd);

							UpdateResultTable(treeview.SelectedCells[0].OwningRow.Cells[0],
							                  "null",
							                  (TreeGridNode) treeview.SelectedCells[0].OwningRow.Cells[0].OwningRow,
							                  (OMETabStripItem) treeview.Parent, true, false);

							tabControlObjHierarchy.SelectedItem.Controls.Clear();
							lstObjIdLong.Remove(id);

							int m_pageCount = CurrentPageNumber();
							int startIndex = (CurrentPageNumber() * PagingData.PAGE_SIZE) - PagingData.PAGE_SIZE;
							int endIndex = startIndex + PagingData.PAGE_SIZE;
							labelNoOfObjects.Text = lstObjIdLong.Count.ToString();

							PagingData pgData = new PagingData(startIndex, endIndex);
							pgData.ObjectId = lstObjIdLong;
							if (lstObjIdLong.Count > 0)
							{
								hashListResult = Helper.DbInteraction.ReturnQueryResults(pgData, false, omQuery.BaseClass, omQuery.AttributeList);
								Hashtable hAttributes = null;

								if (omQuery != null)
								{
									hAttributes = omQuery.AttributeList;
								}
								masterView.SetDatagridRowsWithIndex(hashListResult, ClassName,
								                                                   hAttributes, Helper.DbInteraction.runQuery.StartIndex + 1);
							}

							bool activate = ActivationRequired(tabControlObjHierarchy.SelectedItem);
							treeview = db.GetObjectHierarchy(tabControlObjHierarchy.SelectedItem.Tag, ClassName, activate);

							tabControlObjHierarchy.SelectedItem.Controls.Add(treeview);
							RegisterTreeviewEvents();

							foreach (OMETabStripItem tp in tabControlObjHierarchy.Items)
							{
								int tabIndex = ObjectIndexInMasterViewFor(tp);
								if (tabIndex > delIndex)
								{
									tp.Title = DetailsTabCaptionFor(tabIndex - 1);
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

		private static bool IsObjectInMasterViewEdited(DataGridViewRow row)
		{
			return row != null 
				? Convert.ToBoolean(row.Cells[Constants.QUERY_GRID_ISEDITED_HIDDEN].Value)
				: false;
		}

		private static bool ActivationRequired(Control item)
		{
			return item != null ? item.Name == CONST_TRUE : false;
		}

		private static int DepthFor(TreeGridNode node)
		{
			int depth = 0;
			if (node.Parent == null) 
				return depth;

			while (node.Parent.Tag != null)
			{
				depth++;
				node = node.Parent;
			}
			return depth;
		}

		private static object ParentObjectFor(TreeGridNode node)
		{
			return node.Parent.Tag;
		}

		private void treeview_OnContextMenuOpening(object sender, ContextItemClickedEventArg e)
		{
			try
			{
				treeview = e.Data as TreeGridView;
				treeview.EndEdit();

				CancelEventArgs args = e.CancelEventArguments;
				if (treeview.SelectedRows.Count > 0)
				{
					DataGridViewRow selectedRow = treeview.SelectedCells[0].OwningRow;
					TreeGridNode treeGridNode = selectedRow as TreeGridNode;

					args.Cancel = !IsSetToNullOperationValidFor(FieldTypeForObjectInRow(selectedRow), treeGridNode.Parent.Tag);
				}
				else
				{
					args.Cancel = true;
				}
				
			}
			catch (Exception ex)
			{
				LoggingHelper.ShowMessage(ex);
			}
		}

		private static bool IsSetToNullOperationValidFor(IType targetFieldType, object containingObject)
		{
			return (!targetFieldType.IsPrimitive || targetFieldType.IsNullable) && (containingObject != null);
		}

		private static string FieldTypeNameFor(DataGridViewRow fieldRow)
		{
			return FieldTypeForObjectInRow(fieldRow).FullName;
		}

		internal static IType FieldTypeForObjectInRow(DataGridViewRow fieldRow)
		{
			return (IType) fieldRow.Cells[2].Tag;
		}

		#endregion

		#region TreeView Events

		private static void treeview_NodeExpanded(object sender, ExpandedEventArgs e)
		{
			try
			{
				if (e.Node.Nodes.Count == 0)
					return;

				if (e.Node.Nodes[0].Cells[0].Value.ToString() != "dummy")
					return;

				e.Node.Nodes.RemoveAt(0);
				TreeGridView tree = e.Node.DataGridView as TreeGridView;
				Helper.DbInteraction.ExpandTreeNode(e.Node, ((tree.Parent)).Name == CONST_TRUE);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}


		private void treeview_CellEndEdit(object sender, DataGridViewCellEventArgs e)
		{
			try
			{
				OMETrace.WriteFunctionStart();

				if (masterView.SelectedRows.Count > 0)
					masterView.SelectedRows[0].Selected = false;

				DataGridViewCell cell = ((TreeGridView) sender).CurrentCell;

				object editValue = cell.Value;

				if (Validations.ValidateDataType(FieldTypeForObjectInRow(cell.OwningRow), ref editValue))
				{
					if (strstoreTreeValue != editValue.ToString())
					{
						hierarchy = new ArrayList();
						List<int> offset = new List<int>();
						List<string> nameList = new List<string>();
						List<IType> typeList = new List<IType>();
						try
						{
							TreeGridNode currNode = (TreeGridNode) cell.OwningRow;

							hierarchy.Add(currNode.Tag);
							nameList.Add((string) currNode.Cells[0].Value);
							offset.Add(-2);
							typeList.Add(FieldTypeForObjectInRow(currNode));

							TreeGridNode node = currNode;

							while (node.Parent.Tag != null)
							{
								hierarchy.Add(node.Parent.Tag);
								typeList.Add(FieldTypeForObjectInRow(node.Parent));
								int level = -1;

								if (Helper.DbInteraction.IsArray(node.Parent.Tag)
								    || Helper.DbInteraction.IsCollection(node.Parent.Tag))
								{
									string name = (string) node.Parent.Cells[0].Value;
									level = node.Parent.Nodes.IndexOf(node);
									int index = name.IndexOf(CONST_SPACE);
									name = name.Substring(0, index);
									nameList.Add(name);
								}
								else
								{
									nameList.Add((string) node.Parent.Cells[0].Value);
								}

								offset.Add(level);

								node = node.Parent;
							}
							hierarchy.Reverse();
							nameList.Reverse();
							typeList.Reverse();
							offset.Reverse();
							Helper.DbInteraction.UpdateCollection(hierarchy, offset, nameList, typeList, ValueType(typeList).Cast(editValue));

							tabControlObjHierarchy.SelectedItem.Name = CONST_TRUE;
							tabControlObjHierarchy.SelectedItem.Tag = ((TreeGridView) sender).Parent.Tag;

							OMETabStripItem pg = (OMETabStripItem) ((TreeGridView) sender).Parent;
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
			try
			{
				int pageIndex = OffsetInCurrentPageFor(ObjectIndexInMasterViewFor(pg));
				if (!toDelete)
				{
					string columnName;
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

					foreach (DataGridViewColumn col in masterView.Columns)
					{
						if (col.HeaderText == columnName)
						{
							masterView.Rows[pageIndex - 1].Cells[columnName].Value = editValue.ToString();
							break;
						}
					}

					masterView.Rows[pageIndex - 1].Cells[1].Selected = true;

					if (!updateToNull)
					{
						UpdateMasterViewObjectEditedStatus(masterView.Rows[pageIndex - 1], true);
						buttonSaveResult.Enabled = true;
						cell.Style.ForeColor = Color.Red;
						cell.Style.SelectionForeColor = Color.Red;
					}
				}
				else
				{
					tabControlObjHierarchy.RemoveTab(tabControlObjHierarchy.SelectedItem);
					masterView.Rows.RemoveAt(pageIndex - 1);
				}
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		private void treeview_CellBeginEdit(object sender, DataGridViewCellCancelEventArgs e)
		{
			try
			{
				buttonSaveResult.Enabled = true;
				if (masterView.SelectedRows.Count > 0)
					masterView.SelectedRows[0].Selected = false;

				DataGridViewCell cell = ((TreeGridView) sender).CurrentCell;
				strstoreTreeValue = cell.Value != null ? cell.Value.ToString() : string.Empty;
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

				foreach (DataGridViewRow row in masterView.Rows)
				{
					object obj = row.Tag;

					if (IsObjectInMasterViewEdited(row))
					{
						UpdateMasterViewObjectEditedStatus(row, false);
						Helper.DbInteraction.SaveObjects(obj);
						Helper.IsQueryResultUpdated = true;
					}
				}
				int startindex = (int) masterView.Rows[masterView.Rows.Count - 1].Cells[1].Value;
				int endindex = startindex + PagingData.PAGE_SIZE;
				if (endindex > lstObjIdLong.Count && startindex < lstObjIdLong.Count)
				{
					endindex = lstObjIdLong.Count;
				}
				PagingData pagingData = new PagingData(startindex, endindex);
				pagingData.ObjectId = lstObjIdLong;
				hashListResult = Helper.DbInteraction.ReturnQueryResults(pagingData, true, omQuery.BaseClass, omQuery.AttributeList);

				MakeAllElementsInGridBlack(masterView);
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
				dbInteraction dbI = new dbInteraction();
				if (masterView.SelectedRows.Count > 0)
				{
					DataGridViewRow row = masterView.SelectedRows[0];

					const string strShowMessage = "Do You want to CascadeonDelete?";
					DialogResult dialogRes = MessageBox.Show(strShowMessage, Helper.GetResourceString(Constants.PRODUCT_CAPTION),
					                                         MessageBoxButtons.YesNoCancel,
					                                         MessageBoxIcon.Question);
					long deletedId = dbI.GetLocalID(row.Tag);

					if (DialogResult.Cancel == dialogRes)
						return;

					Helper.IsQueryResultUpdated = true;
					if (dialogRes == DialogResult.Yes)
					{
						CascadeOndeleteobjects(row.Tag);
					}
					else if (dialogRes == DialogResult.No)
					{
						Helper.DbInteraction.DeleteObject(row.Tag, false);
					}

					int objectIndex = ObjectIndexInMasterViewFor(tabControlObjHierarchy.SelectedItem);
					RemoveObjectFromDetailsView(row.Tag);

					UpdateObjectDetailTablCaptions(objectIndex);

					lstObjIdLong.Remove(deletedId);

					const int pageNumber = m_pagingStartIndex + 1;

					PagingData pagData = PagingData.StartingAtPage(pageNumber);
					pagData.ObjectId = lstObjIdLong;

					lblPageCount.Text = pagData.GetPageCount().ToString();
					txtCurrentPage.Text = pageNumber.ToString();
					labelNoOfObjects.Text = pagData.ObjectId.Count.ToString();

					masterView.Rows.Clear();
					if (lstObjIdLong.Count > 0)
					{
						hashListResult = Helper.DbInteraction.ReturnQueryResults(pagData, true, omQuery.BaseClass, omQuery.AttributeList);
						Hashtable hAttributes = (omQuery != null) ? omQuery.AttributeList : null;
						masterView.SetDatagridRowsWithIndex(hashListResult, ClassName, hAttributes, Helper.DbInteraction.runQuery.StartIndex + 1);

						SetSelectedObjectInMasterView(Math.Min(objectIndex, lstObjIdLong.Count));
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

		private void UpdateObjectDetailTablCaptions(int startIndex)
		{
			foreach (OMETabStripItem tp in tabControlObjHierarchy.Items)
			{
				
				int tabIndex = ObjectIndexInMasterViewFor(tp);
				if (tabIndex > startIndex)
				{
					tp.Title = DetailsTabCaptionFor(tabIndex - 1);
				}
			}
		}

		private void RemoveObjectFromDetailsView(object obj)
		{
			foreach (OMETabStripItem pg in tabControlObjHierarchy.Items)
			{
				if (pg.Tag == obj)
				{
					tabControlObjHierarchy.RemoveTab(pg);
					break;
				}
			}
		}

		public void CascadeOndeleteobjects(object obj)
		{
			Thread t = new Thread(ShowDialogforProgressBar);
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
			catch (ThreadAbortException)
			{
				Thread.ResetAbort();
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
							Helper.DbInteraction.SaveCollection(pg.Tag, hierarchy != null ? hierarchy.Count : 1);

							PaintBlack((TreeGridView) pg.Controls[0]);
							pg.Name = CONST_FALSE;
							int objectIndexInMasterView = ObjectIndexInMasterViewFor(pg);
							int pageIndex = objectIndexInMasterView % PagingData.PAGE_SIZE;
							if (pageIndex == 0)
								pageIndex = PagingData.PAGE_SIZE;

							UpdateMasterViewObjectEditedStatus(masterView.Rows[pageIndex - 1], false);
						}
					}

					Helper.IsQueryResultUpdated = true;
					int startindex = (int) masterView.Rows[masterView.Rows.Count - 1].Cells[1].Value;
					int endindex = startindex + PagingData.PAGE_SIZE;
					if (endindex > lstObjIdLong.Count && startindex < lstObjIdLong.Count)
					{
						endindex = lstObjIdLong.Count;
					}
					PagingData pagingData = new PagingData(startindex, endindex);
					pagingData.ObjectId = lstObjIdLong;
					hashListResult = Helper.DbInteraction.ReturnQueryResults(pagingData, false, omQuery.BaseClass,
					                                                         omQuery.AttributeList);
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
				m_pageCount = PageCount();
				txtCurrentPage.Text = lblPageCount.Text;

				KeyEventArgs keyArgs = new KeyEventArgs(Keys.Enter);
				txtObjectNumber_KeyDown(txtCurrentPage, keyArgs);

				if (m_pageCount == PageCount())
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

				if (m_pageCount >= PageCount())
				{
					btnPrevious.Enabled = true;
					btnFirst.Enabled = true;
					btnLast.Enabled = false;
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


		private void RefreshPaging(ref bool check, ref DialogResult dialogRes, ref bool checkforValueChanged, DataGridView db)
		{
			try
			{
				checkforValueChanged = HasChangedData(db);
				if (checkforValueChanged)
				{
					dialogRes = MessageBox.Show("Do you want to save modified objects on this page, else they will be discarded.",
					                            Helper.GetResourceString(Constants.PRODUCT_CAPTION), MessageBoxButtons.YesNo,
					                            MessageBoxIcon.Question);
					if (dialogRes == DialogResult.Yes)
					{
						check = true;
						buttonSaveResult_Click(buttonSaveResult, null);
					}
					else
					{
						foreach (DataGridViewRow row in db.Rows)
						{
							if (IsObjectInMasterViewEdited(row))
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

		private static bool HasChangedData(DataGridView db)
		{
			foreach (DataGridViewRow row in db.Rows)
			{
				if (IsObjectInMasterViewEdited(row))
				{
					return true;
				}
			}
			return false;
		}

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
					if (masterView.SortedColumn != null)
						masterView.SortedColumn.HeaderCell.SortGlyphDirection = SortOrder.None;

					RefreshPaging(ref check, ref dialogRes, ref checkforValueChanged, masterView);

					if (CurrentPageNumber() > PageCount())
						txtCurrentPage.Text = lblPageCount.Text;
					else if (CurrentPageNumber() == 0)
						txtCurrentPage.Text = m_pagingStartIndex.ToString();


					if (!string.IsNullOrEmpty(txtCurrentPage.Text.Trim()) &&
					    CurrentPageNumber() <= PageCount())
					{
						m_pageCount = CurrentPageNumber();
						pagingData = PagingData.StartingAtPage(m_pageCount);
						pagingData.ObjectId = lstObjIdLong;
						if (lstObjIdLong.Count > 0)
						{
							hashListResult = Helper.DbInteraction.ReturnQueryResults(pagingData, true, omQuery.BaseClass,
							                                                         omQuery.AttributeList);

							if (omQuery != null)
							{
								hAttributes = omQuery.AttributeList;
							}

							masterView.SetDatagridRowsWithIndex(hashListResult, ClassName, hAttributes,
							                                                   pagingData.StartIndex + 1);

							ListofModifiedObjects.AddDatagrid(ClassName, masterView);
						}

						int totalPages = PageCount();

						if (m_pageCount == 1 && totalPages == 1)
						{
							btnPrevious.Enabled = false;
							btnLast.Enabled = false;
							btnFirst.Enabled = false;
							btnNext.Enabled = false;
							if (m_pageCount == 1 && PageCount() == 1)
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

		private void tabControlObjHierarchy_TabStripItemSelectionChanged(TabStripItemChangedEventArgs e)
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
				if (masterView.SortOrder == SortOrder.None)
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
			EnsureCurrentPageIs(PageNumberFor(objectIndex));
			DataGridViewRow row = masterView.Rows[OffsetInCurrentPageFor(objectIndex) - 1];
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

		private int PageCount()
		{
			return Convert.ToInt32(lblPageCount.Text);
		}

		private int CurrentPageNumber()
		{
			return Convert.ToInt32(txtCurrentPage.Text);
		}

		private static int PageNumberFor(int masterObjectIndex)
		{
			double pageNumber = (double) masterObjectIndex / PagingData.PAGE_SIZE;
			return Math.Max(Convert.ToInt32(Math.Ceiling(pageNumber)), 1);
		}

		private static int OffsetInCurrentPageFor(int masterViewObjectIndex)
		{
			int offset = masterViewObjectIndex % PagingData.PAGE_SIZE;
			return offset == 0 ? PagingData.PAGE_SIZE : offset;
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
			masterView.EndEdit();
			treeview.EndEdit();
		}

		private void panelResultGridOptions_SizeChanged(object sender, EventArgs e)
		{
			if (panelResultGridOptions.Width <= panelLeft.Width + panelRight.Width)
			{
				panelResultGridOptions.MinimumSize = new Size(panelLeft.Width + panelRight.Width, Height);
			}
		}

		private void txtObjectNumber_TextChanged(object sender, EventArgs e)
		{
			try
			{
				int result;
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
					const int pageNumber = m_pagingStartIndex + 1;
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
				buttonSaveResult.Text = Helper.GetResourceString(Constants.BUTTON_SAVE_CAPTION);
				btnSave.Text = Helper.GetResourceString(Constants.BUTTON_SAVE_CAPTION);
				btnDelete.Text = Helper.GetResourceString(Constants.BUTTON_DELETE_CAPTION);
				lblFechedObjects.Text = Helper.GetResourceString(Constants.LABEL_OBJECTS_NO);
				lblof.Text = Helper.GetResourceString(Constants.LABEL_OF);
				toolTipPagging.SetToolTip(btnFirst, Helper.GetResourceString(Constants.TOOLTIP_PAGE_FIRST));
				toolTipPagging.SetToolTip(btnPrevious, Helper.GetResourceString(Constants.TOOLTIP_PAGE_PREV));
				toolTipPagging.SetToolTip(btnNext, Helper.GetResourceString(Constants.TOOLTIP_PAGE_NEXT));
				toolTipPagging.SetToolTip(btnLast, Helper.GetResourceString(Constants.TOOLTIP_PAGE_LAST));
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
				masterView.Rows.Clear();
				masterView.Columns.Clear();

				tabControlObjHierarchy.Items.Clear();
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}

		internal static void CheckColor(TreeGridNode node)
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

		internal static void PaintBlack(TreeGridView treeobj)
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
			tabControlObjHierarchy.TabStripItemSelectionChanged += tabControlObjHierarchy_TabStripItemSelectionChanged;
			tabControlObjHierarchy.Click += tabControlObjHierarchy_Click;
			tabControlObjHierarchy.TabStripItemClosing += tabControlObjHierarchy_TabStripItemClosing;
		}

		private void tabControlObjHierarchy_Click(object sender, EventArgs e)
		{
			OMETabStripItem item = ((OMETabStrip) sender).SelectedItem;
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

		private void tabControlObjHierarchy_TabStripItemClosing(TabStripItemClosingEventArgs e)
		{
			if (tabControlObjHierarchy.Controls.Count == 1)
				e.Cancel = true;
		}

		private void InitializeResultDataGridView()
		{
			try
			{
				masterView = new dbDataGridView();
				masterView.Size = Size;
				masterView.ReadOnly = false;
				masterView.EditMode = DataGridViewEditMode.EditOnF2;
				masterView.ScrollBars = ScrollBars.Both;
				masterView.AllowUserToOrderColumns = true;
				masterView.AllowUserToResizeColumns = true;
				masterView.AllowDrop = true;
				masterView.Dock = DockStyle.Fill;
				masterView.SelectionChanged += masterView_SelectionChanged;
				masterView.CellEndEdit += masterView_CellEndEdit;
				masterView.CellBeginEdit += masterView_CellBeginEdit;
				masterView.Click += masterView_Click;
				tableLayoutPanelResultGrid.Controls.Add(masterView, 0, 0);
			}
			catch (Exception oEx)
			{
				LoggingHelper.ShowMessage(oEx);
			}
		}
        
		private void masterView_Click(object sender, EventArgs e)
		{
			masterView_SelectionChanged(sender, e);
		}

		private void UpdateDataTreeView(object objUpdated, DataGridViewBand row)
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
			string parentName;
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

		private static IType ValueType(IList<IType> types)
		{
			return types[types.Count - 1];
		}

		#endregion
	}
}